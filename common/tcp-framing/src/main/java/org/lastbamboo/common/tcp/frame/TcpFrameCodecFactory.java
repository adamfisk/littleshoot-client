package org.lastbamboo.common.tcp.frame;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.littleshoot.util.mina.DecodingStateMachine;
import org.littleshoot.util.mina.DemuxableProtocolCodecFactory;
import org.littleshoot.util.mina.DemuxableProtocolDecoder;
import org.littleshoot.util.mina.DemuxingStateMachineProtocolDecoder;

/**
 * {@link DemuxableProtocolCodecFactory} for framed TCP data as defined in 
 * RFC 4571. 
 */
public class TcpFrameCodecFactory
    implements DemuxableProtocolCodecFactory<TcpFrame>
    {
    
    public boolean canDecode(final ByteBuffer in)
        {
        // We rely on something else, such as STUN, to differentiate packets.
        // The TCP framing mechanism is too simple to meaningfully differentiate
        // it from any other protocol.
        return true;
        }
    
    public boolean enoughData(final ByteBuffer in)
        {
        // We don't use TCP frame messages to differentiate protocols, so we 
        // always have "enough" data.
        return true;
        }

    public Class<TcpFrame> getClassToEncode()
        {
        return TcpFrame.class;
        }

    public DemuxableProtocolDecoder newDecoder()
        {
        final DecodingStateMachine startState = 
            new TcpFrameDecodingState();
        return new DemuxingStateMachineProtocolDecoder(startState);
        }

    public ProtocolEncoder newEncoder()
        {
        return new TcpFrameProtocolEncoder();
        }

    public String toString()
        {
        return getClass().getSimpleName();
        }
    }
