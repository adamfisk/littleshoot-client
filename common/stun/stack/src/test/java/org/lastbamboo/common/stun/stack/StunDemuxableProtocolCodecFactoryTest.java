package org.lastbamboo.common.stun.stack;

import static org.junit.Assert.*;

import org.littleshoot.mina.common.ByteBuffer;
import org.junit.Test;

public class StunDemuxableProtocolCodecFactoryTest
    {

    @Test public void testCanDecode() throws Exception
        {
        final StunDemuxableProtocolCodecFactory factory = 
            new StunDemuxableProtocolCodecFactory();
        
        ByteBuffer in = ByteBuffer.allocate(4);
        in.putInt(0x00000000);
        in.flip();
        assertFalse(factory.enoughData(in));
        
        boolean canDecode = false;
        try
            {
            canDecode = factory.canDecode(in);
            fail("Should have thrown an exception!!");
            }
        catch (final IllegalArgumentException e)
            {
            // Expected because there's not enough data.
            }
        
        assertFalse(canDecode);
        
        in = ByteBuffer.allocate(8);
        in.putInt(0x00000000);
        
        // This is the STUN magic cookie.
        in.putInt(0x2112A442);
        in.flip();
        assertTrue(factory.enoughData(in));
        
        
        try
            {
            canDecode = factory.canDecode(in);
            }
        catch (final IllegalArgumentException e)
            {
            fail("Should have been able to determine if it's a STUN message");
            }
        
        assertTrue(canDecode);
        }
    }
