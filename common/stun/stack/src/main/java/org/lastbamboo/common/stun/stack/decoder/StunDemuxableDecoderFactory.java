package org.lastbamboo.common.stun.stack.decoder;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.util.mina.DecodingStateMachine;
import org.littleshoot.util.mina.DemuxableDecoderFactory;
import org.littleshoot.util.mina.DemuxableProtocolDecoder;
import org.littleshoot.util.mina.DemuxingStateMachineProtocolDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MINA decoder for demultiplexing between STUN messages and messages for 
 * another protocol working in conjunction with STUN.
 */
public class StunDemuxableDecoderFactory implements DemuxableDecoderFactory
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public boolean canDecode(final ByteBuffer in)
        {
        final int pos = in.position();
        final int limit = in.limit();
        try
            {
            final int firstByte = in.getUnsigned();
            
            // The first 2 bits of STUN messages are always zero.
            final int masked = firstByte & 0xc0;
            if (masked > 0)
                {
                return false;
                }
            else
                {
                // OK, it could be a STUN message.  Let's check the 
                // STUN magic cookie field to make sure.
                final long magicCookie = 0x2112A442;
                final long secondFourBytes = in.getUnsignedInt(pos + 4);

                final boolean magicCookieMatches = 
                    secondFourBytes == magicCookie;
                
                m_log.debug("Magic cookie matches: "+
                    magicCookieMatches);
                return magicCookieMatches;
                }
            }
        finally
            {
            // Make sure we reset the buffer!
            in.position(pos);
            in.limit(limit);
            }
        }

    public DemuxableProtocolDecoder newDecoder()
        {
        final DecodingStateMachine startState = 
            new StunMessageDecodingState();
        return new DemuxingStateMachineProtocolDecoder(startState);
        }
    
    public String toString()
        {
        return "STUN Decoder Factory";
        }

    }
