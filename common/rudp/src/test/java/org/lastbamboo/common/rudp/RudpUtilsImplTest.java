package org.lastbamboo.common.rudp;

import org.littleshoot.mina.common.ByteBuffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.littleshoot.util.mina.ByteBufferExtImpl;

/**
 * A test for RUDP utilities.
 */
public final class RudpUtilsImplTest
    {
    /**
     * The RUDP utilities to test.
     */
    //private RudpUtils m_utils;
    
    /**
     * Sets up this test.
     */
    @Before
    public void setUp
            ()
        {
        //m_utils = new RudpUtils ();
        }
    
    /**
     * Tests the <code>getChecksum</code> method.
     */
    @Test
    public void testGetChecksum
            ()
        {
        final byte headerLength = 22;
        final ByteBuffer bb = ByteBuffer.allocate (headerLength);
        
        bb.put (RudpConstants.SYN);
        bb.put (headerLength);
        bb.putShort ((short) 0);
        bb.putInt (567);
        bb.putInt (234);
        bb.putInt (0);
        bb.putShort ((short) 17);
        bb.putShort ((short) 40000);
        bb.putShort ((short) 0);
        
        bb.flip ();
        
        bb.putInt (RudpConstants.CHECKSUM_INDEX,
                   RudpUtils.getChecksum (bb).toInt ());
        
        Assert.assertEquals (true,
                RudpUtils.isChecksumOk (new ByteBufferExtImpl (bb)));
        
        bb.putInt (12, 17);
        
        Assert.assertEquals (false,
                RudpUtils.isChecksumOk (new ByteBufferExtImpl (bb)));
        }
    }
