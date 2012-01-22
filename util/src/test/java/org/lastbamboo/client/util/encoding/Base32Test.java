package org.lastbamboo.client.util.encoding;

import java.util.Arrays;

import org.lastbamboo.client.util.encoding.Base32;

import junit.framework.TestCase;

/**
 * Unit tests for Base32 class.
 */
public final class Base32Test extends TestCase 
    {


    /**
     * Simple test for encoding and decoding.
     * 
     * @throws Exception if an unexpected error occurs.
     */
    public void testBasic() throws Exception 
        {
        final byte[] testBytes = new byte[5];
        testBytes[0] = (byte) 0xF0;
        testBytes[1] = (byte) 0x10;
        testBytes[2] = (byte) 0x24;
        testBytes[3] = (byte) 0xA5;
        testBytes[4] = (byte) 0x18;
        final String encoded = Base32.encode(testBytes);
        assertEquals(8, encoded.length());
        assertEquals("6AICJJIY", encoded);
        
        final byte[] decodedBytes = Base32.decode(encoded);
        assertTrue(Arrays.equals(testBytes, decodedBytes));
        }
    }
