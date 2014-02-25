/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package obs.service.sighting;

import static org.junit.Assert.*;

import java.net.DatagramPacket;

import obs.service.Constants;
import obs.service.sighting.TagSighting;
import obs.service.tools.Tools;

import org.junit.Test;

/**
 * Test class for testing tag data encoded using protocol 16 (RFBPROTO_BEACONTRACKER_OLD).
 * 
 * FIXME: This test only tests the protocol itself, based on the original code from 
 * OpenBeacon. Validate the test with actual data from a tag using this protocol!
 * 
 * @author Björn Behrens <uol@btech.de>
 * @version 1.0
 **/
public class TestTagSightingProtocol16 {
	private static final long[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	
	/**
	 * Test method for {@link odp.service.sighting.tagsighting.TagSighting#TagSighting(java.net.DatagramPacket, int[])}.
	 */
	@Test
	public final void testTagSighting() {
		int tagCRCValue = 0;
		int envelopeCRCValue = 0;
		
		// Create envelope
		byte[] packetData = new byte[32];
		packetData[0] = 0;				// Envelope CRC (1) - updated below!
		packetData[1] = 0;				// Envelope CRC (2) - updated below!
		packetData[2] = Constants.ENVELOPE_PROTOCOL_BEACONLOG_SIGHTING;	// Envelope protocol
		packetData[3] = (byte)0x01;		// Antenna
		packetData[4] = (byte)0x04;		// Reader (1), 1268
		packetData[5] = (byte)0xf4;		// Reader (2)
		packetData[6] = (byte)0x00;		// Size (1), currently always 32 bytes -> 0x20
		packetData[7] = (byte)0x20;		// Size (2)
		packetData[8] = 0;				// Sequence (1), some value
		packetData[9] = 0;				// Sequence (2)
		packetData[10] = (byte)0x01;	// Sequence (3)
		packetData[11] = (byte)0xb0;	// Sequence (4)
		packetData[12] = 0;				// Time stamp (1), some value
		packetData[13] = (byte)0x11;	// Time stamp (2)
		packetData[14] = (byte)0x2e;	// Time stamp (3)
		packetData[15] = (byte)0x97;	// Time stamp (4)
		
		// Create payload
		byte[] payload = new byte[16];
		
		payload[0] = (byte)Constants.RFBPROTO_BEACONTRACKER_OLD;	// Tag protocol
		payload[1] = (byte)Constants.RFBPROTO_BEACONTRACKER_OLD2;	// Tag protocol2
		payload[2] = (byte)Constants.RFBFLAGS_SENSOR;				// Flags, button pressed
		payload[3] = (byte)0x02;							// Strength: 2
		payload[4] = 0;										// Sequence (1), some value
		payload[5] = 0;										// Sequence (2)
		payload[6] = (byte)0x02;							// Sequence (3)
		payload[7] = (byte)0x01;							// Sequence (4)
		payload[8] = 0;										// Tag id (1)
		payload[9] = 0;										// Tag id (2)
		payload[10] = (byte)0x04;							// Tag id (3)
		payload[11] = (byte)0x73;							// Tag id (4)
		payload[12] = 0;									// Reserved (1)
		payload[13] = 0;									// Reserved (2)
		
		// Calculate CRC values of the unencrypted data
		tagCRCValue = Tools.calculateCRC(payload, 0, 14);
		
		payload[14] = (byte) (0xff & (tagCRCValue >> 8));		// Tag CRC (2)
		payload[15] = (byte) (0xff & tagCRCValue); 				// Tag CRC (1)
		
		// Encrypt the payload an copy it into the envelope
		payload = Tools.flipArray(payload);
		payload = Tools.encrypt(payload, key);
		payload = Tools.flipArray(payload);
		
		// Copy payload into packetData array
		System.arraycopy(payload, 0, packetData, 16, 16);
		
		// Calculate the CRC values of the envelope
		envelopeCRCValue = Tools.calculateLongCRC(packetData, 2, 30);
		
		packetData[0] = (byte) (0xff & (envelopeCRCValue >> 8));	// Envelope CRC (2)
		packetData[1] = (byte) (0xff & envelopeCRCValue); 			// Envelope CRC (1)
		
		TagSighting tagSighting = new TagSighting(new DatagramPacket(packetData, 32), key);
		
		assertEquals(envelopeCRCValue, tagSighting.getEnvelopeCRC());
		assertTrue(tagSighting.hasValidEnvelopeCRC());
		assertEquals(1, tagSighting.getProtocol());
		assertEquals(1, tagSighting.getInterface());
		assertEquals(1268, tagSighting.getReaderId());
		assertEquals("R1268", tagSighting.getReaderKey());
		assertEquals(32, tagSighting.getSize());
		assertEquals(432, tagSighting.getSequence());
		assertEquals(1126039, tagSighting.getTimestamp());
		assertTrue(tagSighting.hasValidTagData());
		assertEquals(1139, tagSighting.getTagId());
		assertEquals("T1139", tagSighting.getTagKey());
		assertTrue(tagSighting.isTagButtonPressed());
		assertEquals(16, tagSighting.getTagProtocol());
		assertEquals(2, tagSighting.getFlags());
		assertEquals(2, tagSighting.getStrength());
		assertNull(tagSighting.getProximitySightings());
		assertEquals(-1, tagSighting.getTagTime());
		assertEquals(-1, tagSighting.getTagBattery());
		assertEquals(513, tagSighting.getTagSequence());
		assertEquals(tagCRCValue, tagSighting.getTagCRC());
		assertTrue(tagSighting.hasValidTagCRC());
		assertTrue(tagSighting.isValid());
		
		for (int i = 0; i < packetData.length; i++) {
			try {
				assertEquals(packetData[i], tagSighting.getRawData()[i]);
			} catch (AssertionError e) {
				System.out.printf("PacketData failed at %d (expected: %d, got: %d)\n", i, packetData[i], tagSighting.getRawData()[i]);
			}
		}
		
		assertFalse(tagSighting.getTagData() == null);
		assertEquals(16, tagSighting.getTagData().length);
		
		assertEquals("122,208,1,1,4,244,0,32,0,0,1,176,0,17,46,151,73,242,65,211,156,68,214,179,165,145,152,3,195,243,162,106",
					tagSighting.toString(true));
	} // testTagSighting

	/**
	 * Test method for {@link odp.service.sighting.tagsighting.TagSighting#TagSighting(java.net.DatagramPacket, int[])},
	 * returning invalid tag data status, as a non-existing protocol 2 is given.
	 */
	@Test
	public final void testTagSightingInvalidProto2() {
		int tagCRCValue = 0;
		int envelopeCRCValue = 0;
		
		// Create envelope
		byte[] packetData = new byte[32];
		packetData[0] = 0;				// Envelope CRC (1) - updated below!
		packetData[1] = 0;				// Envelope CRC (2) - updated below!
		packetData[2] = Constants.ENVELOPE_PROTOCOL_BEACONLOG_SIGHTING;	// Envelope protocol
		packetData[3] = (byte)0x01;		// Antenna
		packetData[4] = (byte)0x04;		// Reader (1), 1268
		packetData[5] = (byte)0xf4;		// Reader (2)
		packetData[6] = (byte)0x00;		// Size (1), currently always 32 bytes -> 0x20
		packetData[7] = (byte)0x20;		// Size (2)
		packetData[8] = 0;				// Sequence (1), some value
		packetData[9] = 0;				// Sequence (2)
		packetData[10] = (byte)0x01;	// Sequence (3)
		packetData[11] = (byte)0xb0;	// Sequence (4)
		packetData[12] = 0;				// Time stamp (1), some value
		packetData[13] = (byte)0x11;	// Time stamp (2)
		packetData[14] = (byte)0x2e;	// Time stamp (3)
		packetData[15] = (byte)0x97;	// Time stamp (4)
		
		// Create payload
		byte[] payload = new byte[16];
		
		payload[0] = (byte)Constants.RFBPROTO_BEACONTRACKER_OLD;	// Tag protocol
		payload[1] = (byte)0x05;									// Tag protocol2 (0x05 = not supported!)
		payload[2] = (byte)Constants.RFBFLAGS_SENSOR;				// Flags, button pressed
		payload[3] = (byte)0x02;									// Strength: 2
		payload[4] = 0;												// Sequence (1), some value
		payload[5] = 0;												// Sequence (2)
		payload[6] = (byte)0x02;									// Sequence (3)
		payload[7] = (byte)0x01;									// Sequence (4)
		payload[8] = 0;												// Tag id (1)
		payload[9] = 0;												// Tag id (2)
		payload[10] = (byte)0x04;									// Tag id (3)
		payload[11] = (byte)0x73;									// Tag id (4)
		payload[12] = 0;											// Reserved (1)
		payload[13] = 0;											// Reserved (2)
		
		// Calculate CRC values of the unencrypted data
		tagCRCValue = Tools.calculateCRC(payload, 0, 14);
		
		payload[14] = (byte) (0xff & (tagCRCValue >> 8));			// Tag CRC (2)
		payload[15] = (byte) (0xff & tagCRCValue); 					// Tag CRC (1)
		
		// Encrypt the payload an copy it into the envelope
		payload = Tools.flipArray(payload);
		payload = Tools.encrypt(payload, key);
		payload = Tools.flipArray(payload);
		
		// Copy payload into packetData array
		System.arraycopy(payload, 0, packetData, 16, 16);
		
		// Calculate the CRC values of the envelope
		envelopeCRCValue = Tools.calculateLongCRC(packetData, 2, 30);
		
		packetData[0] = (byte) (0xff & (envelopeCRCValue >> 8));	// Envelope CRC (2)
		packetData[1] = (byte) (0xff & envelopeCRCValue); 			// Envelope CRC (1)
		
		TagSighting tagSighting = new TagSighting(new DatagramPacket(packetData, 32), key);
		
		assertEquals(envelopeCRCValue, tagSighting.getEnvelopeCRC());
		assertTrue(tagSighting.hasValidEnvelopeCRC());
		assertEquals(1, tagSighting.getProtocol());
		assertEquals(1, tagSighting.getInterface());
		assertEquals(1268, tagSighting.getReaderId());
		assertEquals("R1268", tagSighting.getReaderKey());
		assertEquals(32, tagSighting.getSize());
		assertEquals(432, tagSighting.getSequence());
		assertEquals(1126039, tagSighting.getTimestamp());
		assertFalse(tagSighting.hasValidTagData());
		assertEquals(-1, tagSighting.getTagId());
		assertNull(tagSighting.getTagKey());
		assertNull(tagSighting.isTagButtonPressed());
		assertEquals(16, tagSighting.getTagProtocol());
		assertEquals(-1, tagSighting.getFlags());
		assertEquals(-1, tagSighting.getStrength());
		assertNull(tagSighting.getProximitySightings());
		assertEquals(-1, tagSighting.getTagTime());
		assertEquals(-1, tagSighting.getTagBattery());
		assertEquals(-1, tagSighting.getTagSequence());
		assertEquals(-1, tagSighting.getTagCRC());
		assertFalse(tagSighting.hasValidTagCRC());
		assertFalse(tagSighting.isValid());
		
		for (int i = 0; i < packetData.length; i++) {
			try {
				assertEquals(packetData[i], tagSighting.getRawData()[i]);
			} catch (AssertionError e) {
				System.out.printf("PacketData failed at %d (expected: %d, got: %d)\n", i, packetData[i], tagSighting.getRawData()[i]);
			}
		}
		
		assertFalse(tagSighting.getTagData() == null);
		assertEquals(16, tagSighting.getTagData().length);
		
		assertEquals("230,216,1,1,4,244,0,32,0,0,1,176,0,17,46,151,214,208,237,219,147,173,120,125,26,47,222,60,26,155,123,58",
					tagSighting.toString(true));
	} // testData
}