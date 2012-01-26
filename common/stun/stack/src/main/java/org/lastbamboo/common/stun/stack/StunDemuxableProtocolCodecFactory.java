package org.lastbamboo.common.stun.stack;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.lastbamboo.common.stun.stack.decoder.StunMessageDecodingState;
import org.lastbamboo.common.stun.stack.encoder.StunProtocolEncoder;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.littleshoot.util.mina.DecodingStateMachine;
import org.littleshoot.util.mina.DemuxableProtocolCodecFactory;
import org.littleshoot.util.mina.DemuxableProtocolDecoder;
import org.littleshoot.util.mina.DemuxingStateMachineProtocolDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DemuxableProtocolCodecFactory} for STUN.
 */
public class StunDemuxableProtocolCodecFactory 
    implements DemuxableProtocolCodecFactory<StunMessage>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public boolean canDecode(final ByteBuffer in)
        {
        if (!enoughData(in))
            {
            throw new IllegalArgumentException(
                "Not enough data to determine if we can decode it or not!!");
            }
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

    public Class<StunMessage> getClassToEncode()
        {
        return StunMessage.class;
        }

    public DemuxableProtocolDecoder newDecoder()
        {
        final DecodingStateMachine startState = 
            new StunMessageDecodingState();
        return new DemuxingStateMachineProtocolDecoder(startState);
        }

    public ProtocolEncoder newEncoder()
        {
        return new StunProtocolEncoder();
        }

    public boolean enoughData(final ByteBuffer in)
        {
        //0                   1                   2                   3
        //0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
        //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        //|0 0|     STUN Message Type     |         Message Length        |
        //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        //|                         Magic Cookie                          |
        //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        //|                                                               |
        //|                     Transaction ID (96 bits)                  |
        //|                                                               |
        //+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        
        // As you can see from the above diagram, we need 8 bytes total to
        // read the magic cookie.  Anything less than this, and we can't 
        // reliably determine whether or not it's a STUN message.
        return in.remaining() >= 8;
        }

    }
