package org.lastbamboo.common.util.mina;

import java.io.IOException;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.handler.support.IoSessionOutputStream;

/**
 * An {@link IoSessionOutputStream} for byte buffers.
 */
public final class ByteBufferIoSessionOutputStream 
    extends AbstractIoSessionOutputStream<ByteBuffer> 
    {

    /**
     * Creates a new {@link ByteBufferIoSessionOutputStream} for the specified
     * session.
     * 
     * @param session The {@link IoSession}.
     */
    public ByteBufferIoSessionOutputStream(final IoSession session) 
        {
        super(session);
        }

    @Override
    public void write(final byte[] b, final int off, 
        final int len) throws IOException
        {
        write(ByteBuffer.wrap(b.clone(), off, len));
        }

    @Override
    public void write(final int b) throws IOException
        {
        final ByteBuffer buf = ByteBuffer.allocate(1);
        buf.put((byte) b);
        buf.flip();
        write(buf);
        }
    }
