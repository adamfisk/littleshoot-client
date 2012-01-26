package org.lastbamboo.common.tcp.frame;

import java.io.IOException;
import java.io.OutputStream;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.util.mina.AbstractIoSessionOutputStream;
import org.littleshoot.util.mina.MinaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link OutputStream} for {@link IoSession}s that also wraps all data in
 * {@link TcpFrame} messages.
 */
public final class TcpFrameIoSessionOutputStream 
    extends AbstractIoSessionOutputStream<TcpFrame> 
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private volatile long m_rawBytesWritten = 0;

    /**
     * Creates a new {@link OutputStream} for {@link TcpFrame}s.
     * 
     * @param session The MINA {@link IoSession} to write to.
     */
    public TcpFrameIoSessionOutputStream(final IoSession session) 
        {
        super(session);
        m_log.debug("Created new TCP frame output stream");
        }
    
    @Override
    public void write(final byte[] b, final int off, final int len) 
        throws IOException
        {
        // This override is key because OutputStream typically calls write(byte)
        // for each byte here.  We need to overwrite that because otherwise
        // we'd wrap every single byte in a TCP frame, so this takes care of
        // most cases.  Most code will generally use the bulk write methods,
        // so we should be in fairly good shape.
        final byte[] subArray = 
            MinaUtils.toByteArray(ByteBuffer.wrap(b, off, len));
        if (m_log.isDebugEnabled())
            {
            //final String dataString = new String(subArray, "US-ASCII");
            //m_log.debug("Sending data:\n{}", dataString);
            m_log.debug("Data length is: "+subArray.length);
            m_rawBytesWritten += subArray.length;
            m_log.debug("Raw bytes written: {}", m_rawBytesWritten);
            }
        write(new TcpFrame(subArray));
        }

    @Override
    public void write(final int b) throws IOException
        {
        m_log.warn("Wrapping single byte in TCP frame");
        final byte[] bytes = new byte[1];
        write(new TcpFrame(bytes));
        }

    }
