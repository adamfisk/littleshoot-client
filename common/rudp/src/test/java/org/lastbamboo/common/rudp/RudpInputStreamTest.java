package org.lastbamboo.common.rudp;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.lastbamboo.common.rudp.stubs.RudpConnectionIdStub;
import org.lastbamboo.common.rudp.stubs.RudpServiceStub;

/**
 * Test for RUDP input streams.
 */
public class RudpInputStreamTest
    {

    @Test public void testStream() throws Exception
        {
        final RudpService service = new RudpServiceStub()
            {
            public byte[] receive(final RudpConnectionId id)
                {
                final byte[] data = new byte[100];
                Arrays.fill(data, (byte)7);
                return data;
                }
            };
        final RudpConnectionId id = new RudpConnectionIdStub();
        final RudpSocket sock = new RudpSocket(service, id, null);
        final RudpInputStream stream = 
            new RudpInputStream(service, id, sock);
        
        assertEquals(0, stream.available());
        
        final int b = stream.read();
        assertEquals(7, b);
        assertEquals(99, stream.available());
        
        final byte[] data = new byte[100];
        
        int bytesRead = stream.read(data);
        assertEquals(99, bytesRead);
        assertEquals((byte)0, data[99]);
        for (int i = 0; i < data.length-1; i++)
            {
            assertEquals((byte)7, data[i]);
            }
        
        assertEquals(0, stream.available());
        
        final byte[] small = new byte[1];
        stream.read(small);
        assertEquals(99, stream.available());
        
        final long skipped = stream.skip(10);
        assertEquals(10L, skipped);
        
        assertEquals(89, stream.available());
        }
    }
