// Tools for decryption 
// based on a java implementation of Ma Bingyao optimized for 16 byte data
 

 /*
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; version 2.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

 */

package obp.tools;


public class Tools {
    
    //Decrypt data with key.
    public static byte[] decrypt(byte[] data, int[] key) {
        if (data.length == 0) {
            return data;
        }
        
        return toByteArray(decrypt(toIntArray(data, false),key), true);
    }

    // Decrypt data with key.
    public static int[] decrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        if (k.length < 4) {
            int[] key = new int[4];

            System.arraycopy(k, 0, key, 0, k.length);
            k = key;
        }
        int z = v[n];
        int y = v[0];
        int e;
        int p;
        int q = 6 + 52 / (n + 1);
        int delta = 0x9E3779B9;
        int sum = q * delta;
        
        while (sum != 0) {
            e = sum >>> 2 & 3;
            for (p = n; p > 0; p--) {
                z = v[p - 1];
                y = v[p] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4)
                        ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
            }
            z = v[n];
            y = v[0] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4)
                    ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
            sum = sum - delta;
        }
        
        return v;
    }

    private static int[] toIntArray(byte[] data, boolean includeLength) {
        int n = (((data.length & 3) == 0)
                ? (data.length >>> 2)
                : ((data.length >>> 2) + 1));
        int[] result;

        if (includeLength) {
            result = new int[n + 1];
            result[n] = data.length;
        } else {
            result = new int[n];
        }
        
        n = data.length;
        for (int i = 0; i < n; i++) {
            result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
        }
        return result;
    }

     //Convert int array[16] to byte array.
    
    private static byte[] toByteArray(int[] data, boolean includeLength) {
        int n = data.length << 2;
        byte[] result = new byte[n];

        for (int i = 0; i < n; i++) {
            result[i] = (byte) ((data[i >>> 2] >>> ((i & 3) << 3)) & 0xff);
        }
        return result;
    }
    
    public static byte[] flipArray(byte[] input) {
		int INT_SIZE = 4;
		int i, j;
		byte flipped_array[] = new byte[input.length];

		for (i = 0; i < input.length / INT_SIZE; i++) {
			for (j = 0; j < INT_SIZE; j++) {
				flipped_array[(i + 1) * INT_SIZE - j - 1] = input[i * INT_SIZE + j];
			}
		}
		return flipped_array;
	}
}