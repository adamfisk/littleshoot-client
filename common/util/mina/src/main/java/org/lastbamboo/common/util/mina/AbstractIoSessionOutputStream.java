package org.lastbamboo.common.util.mina;

import java.io.IOException;
import java.io.OutputStream;

import org.littleshoot.util.ThreadUtils;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.common.WriteFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract utility class for creating {@link OutputStream}s from 
 * MINA {@link IoSession}s for arbitrary message types.  This allows users
 * to create streams from specialized methods other than {@link ByteBuffer}s.
 *
 * @param <T> The type of message it's an {@link OutputStream} for.
 */
public abstract class AbstractIoSessionOutputStream<T> extends OutputStream 
    {
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    protected final IoSession m_ioSession;

    protected WriteFuture m_lastWriteFuture;

    protected AbstractIoSessionOutputStream(final IoSession session) 
        {
        this.m_ioSession = session;
        }

    @Override
    public void close() throws IOException 
        {
        m_log.debug("Closing output stream from: "+ThreadUtils.dumpStack());
        try
            {
            flush();
            }
        finally
            {
            m_ioSession.close().join();
            }
        }

    private void checkClosed() throws IOException
        {
        if (!m_ioSession.isConnected())
            {
            throw new IOException("The session has been closed.");
            }
        }

    protected synchronized void write(final T message) throws IOException
        {
        m_log.debug("Writing message: {}", message);
        checkClosed();
        m_lastWriteFuture = m_ioSession.write(message);
        m_lastWriteFuture.join(m_ioSession.getWriteTimeoutInMillis());
        m_log.debug("Finshing writing message...");
        }

    @Override
    public synchronized void flush() throws IOException
        {
        m_log.debug("Flushing IoSession output stream...");
        if (m_lastWriteFuture == null)
            {
            return;
            }

        m_lastWriteFuture.join();
        if (!m_lastWriteFuture.isWritten())
            {
            throw new IOException(
                    "The bytes could not be written to the session");
            }
        }
    }
