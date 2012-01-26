package org.lastbamboo.common.tcp.frame;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.util.mina.MinaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encoder for framed TCP messages, as defined in RFC 4571. 
 */
public class TcpFrameEncoder
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * Encodes the TCP frame into a {@link ByteBuffer}.
     * 
     * @param frame The frame to encode.
     * @return The encoded frame in a {@link ByteBuffer}.
     */
    public ByteBuffer encode(final TcpFrame frame)
        {
        final int length = frame.getLength();
        final ByteBuffer buf = ByteBuffer.allocate(2 + length);
        MinaUtils.putUnsignedShort(buf, length);
        buf.put(frame.getData());
        buf.flip();
        //m_log.debug("Encoded TCP Frame as buffer: {}", buf);
        return buf;
        }

    }
