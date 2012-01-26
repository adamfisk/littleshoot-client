package org.lastbamboo.common.tcp.frame;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.littleshoot.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ProtocolEncoder} for framed TCP messages. 
 */
public class TcpFrameProtocolEncoder implements ProtocolEncoder
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    public void dispose(final IoSession session) throws Exception
        {
        m_log.debug("Disposing of sessoin: {}", session);
        }

    public void encode(final IoSession session, final Object message,
            ProtocolEncoderOutput out) throws Exception
        {
        final TcpFrameEncoder encoder = new TcpFrameEncoder();
        
        final TcpFrame frame = (TcpFrame) message;
        final ByteBuffer buf = encoder.encode(frame);
        out.write(buf);
        }

    }
