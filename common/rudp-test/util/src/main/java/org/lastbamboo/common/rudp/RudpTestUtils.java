package org.lastbamboo.common.rudp;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;

/**
 * Reliable UDP test utilities.
 */
public class RudpTestUtils
    {
    /**
     * Returns the test data to use for testing.
     * 
     * @param numBytes
     *      The number of bytes of test data to return.
     *      
     * @return
     *      A byte array containing test data with the requested number of bytes.
     */
    public static byte[] getTestData
            (final int numBytes)
        {
        final byte[] data = new byte[numBytes];
        
        for (int i = 0; i < data.length; ++i)
            {
            data[i] = (byte) i;
            }
        
        return data;
        }
    
    /**
     * Checks that the bytes retrieved from a given inputs stream match test
     * data.
     * 
     * @param logger    
     *      The logger used to log events.
     * @param numBytes
     *      The number of bytes expected to come in on the input stream.
     * @param is
     *      The input stream.
     */
    public static void check
            (final Logger logger,
             final int numBytes,
             final InputStream is)
        {
        final byte[] data = getTestData (numBytes);
        
        for (int i = 0; i < data.length; ++i)
            {
            try
                {
                final byte b = (byte) is.read ();
                
                if (b != data[i])
                    {
                    throw new RuntimeException
                           	("Data mismatch (" + i + "): '" +
                                   	b + "' != '" +
                                   	data[i] + "'");
                    }
                
                if ((i % 100) == 0)
                    {
                    logger.debug ("Byte '" + i + "' checked");
                    }
                }
            catch (final IOException e)
                {
                throw new RuntimeException (e);
                }
            }
        }
    }
