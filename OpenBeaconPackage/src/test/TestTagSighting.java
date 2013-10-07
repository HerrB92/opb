/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.net.DatagramPacket;

import obp.tag.TagSighting;

import org.junit.Before;
import org.junit.Test;

/**
 * @author bbehrens
 *
 */
public class TestTagSighting {
	
	private TagSighting tagSighting;
	private static final int[] key = {0x00112233, 0x44556677, 0x8899aabb, 0xccddeeff};
	private static final byte[] packetData = 
		{-37,67,1,1,5,111,0,32,0,0,55,16,0,19,-64,-72,-29,-123,26,-31,-45,59,98,-127,-93,-100,106,53,38,-10,56,97};
	
	@Before
	public void setUp() throws Exception {
		tagSighting = new TagSighting(new DatagramPacket(packetData, 32), key);
	}

	/**
	 * Test method for {@link obp.tag.TagSighting#TagSighting(java.net.DatagramPacket, int[])}.
	 */
	@Test
	public final void testTagSighting() {		
		
	}

	/**
	 * Test method for {@link obp.tag.TagSighting#checkCRC()}.
	 */
	@Test
	public final void testCheckCRC() {
		assertTrue(tagSighting.hasValidEnvelopeCRC());
		assertTrue(tagSighting.hasValidTagCRC());
	}
}