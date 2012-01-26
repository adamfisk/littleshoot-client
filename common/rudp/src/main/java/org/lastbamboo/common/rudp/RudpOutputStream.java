package org.lastbamboo.common.rudp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.time.DateFormatUtils;
import org.littleshoot.util.IoExceptionWithCause;
import org.littleshoot.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An output stream backed by a reliable UDP connection.
 */
public final class RudpOutputStream extends OutputStream
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * The buffer holding the data to be send in a reliable UDP message.
     * This is actually limited by the MTU.
     */
    private final byte[] m_buffer = new byte[1200];

    /**
     * The connection identifier of the reliable UDP connection.
     */
    private final RudpConnectionId m_id;
    
    /**
     * The index into the buffer that we are writing.  Each byte written
     * increments this index.  Whenever a message is sent with the current
     * buffer, this gets reset.
     */
    private volatile int m_index;
    
    /**
     * The reliable UDP service to use for reliable UDP interaction.
     */
    private final RudpService m_service;
    
    /**
     * The timer used to schedule automatic flushing of this stream.
     */
    private final Timer m_timer;

    private volatile boolean m_closing = false;
    
    private volatile boolean m_closed = false;

    private final RudpSocket m_socket;

    /**
     * Constructs a new output stream that is connected to reliable UDP
     * connection.  Currently, the connection identifier should identify a
     * connection that is known to be ready for writing.
     * 
     * @param service The reliable UDP service to use for reliable UDP 
     *  interaction.
     * @param id The connection identifier of the reliable UDP connection.
     * @param socket The enclosing RUDP socket. 
     */
    public RudpOutputStream (final RudpService service,
        final RudpConnectionId id, final RudpSocket socket)
        {
        m_service = service;
        m_id = id;
        m_socket = socket;
        
        final TimerTask flushTask = new TimerTask ()
            {
            @Override
            public void run ()
                {
                synchronized (RudpOutputStream.this)
                    {
                    if (m_closed || m_closing)
                        {
                        m_log.debug("In the process of closing...");
                        return;
                        }
                    try
                        {
                        flush ();
                        }
                    catch (final IOException e)
                        {
                        // TODO: There was an error flushing.  This should
                        // probably render this stream closed.
                        m_log.debug("Exception flushing stream...", e);
                        }
                    catch (final Throwable t)
                        {
                        m_log.warn("Unexpected exception flushing os...", t);
                        }
                    }
                }
            };
            
        m_timer = new Timer ("RudpOutputStream-" + id, true);
        m_timer.schedule (flushTask, 0, 800);
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void close () 
        {
        m_log.debug("Closing output stream: \n"+ThreadUtils.dumpStack());

        System.out.println("Dumping stack at: "+
            DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date()));
        Thread.dumpStack();
        if (this.m_closed || this.m_closing)
            {
            return;
            }
        
        this.m_closing = true;
        m_timer.cancel ();
        try
            {
            flush();
            }
        catch (final IOException e)
            {
            m_log.debug("Exception flushing...");
            }
        
        m_log.debug("Closing socket...");
        if (!this.m_socket.isClosed())
            {
            try
                {
                this.m_socket.close();
                }
            catch (final IOException e)
                {
                m_log.debug("Exception closing socket", e);
                }
            }
        this.m_closed = true;
        this.m_closing = false;
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush () throws IOException
        {
        synchronized (this)
            {
            if (m_index > 0)
                {
                final byte[] slice = new byte[m_index];
                System.arraycopy (m_buffer, 0, slice, 0, m_index);
                send(slice);
                }
            else
                {
                // There is no data to flush.  Do nothing.
                }
            }
        }
    
    @Override
    public void write (final byte b[]) throws IOException
        {
        write(b, 0, b.length);
        }
    
    @Override
    public void write (final byte b[], final int off, final int len) 
        throws IOException
        {
        if (b == null)
            {
            m_log.error("Throwing null pointer!!");
            throw new NullPointerException();
            }
        else if ((off < 0) || (off > b.length) || (len < 0) || 
                ((off + len) > b.length) || ((off + len) < 0))
            {
            m_log.error("IOOBE");
            throw new IndexOutOfBoundsException();
            }
        else if (len == 0)
            {
            return;
            }
        for (int i = 0; i < len; i++)
            {
            write(b[off + i]);
            }
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write (final int i) throws IOException
        {
        synchronized (this)
            {
            if (m_index == m_buffer.length)
                {
                final byte[] dataCopy = new byte[m_buffer.length];
                System.arraycopy (m_buffer, 0, dataCopy, 0, m_buffer.length);
                send (dataCopy);
                }
            
            // Narrowing conversions simply discard the high-order bits, which
            // is exactly what we want here.  No need for a bit-wise and.
            m_buffer[m_index++] = (byte)i;
            }
        }

    private void send(final byte[] data) throws IOException
        {
        // This matches SocketOutputStream behavior, in particular the 
        // native Solaris implementation in SocketOutputStream.c called from
        // SocketOutputStream.java, as well as likely other native 
        // implementations.
        if (this.m_closing || this.m_closed)
            {
            throw new SocketException("Socket closed");
            }
        try
            {
            m_log.debug("Sending data of length: {}", data.length);
            m_service.send (m_id, data);
            }
        catch (final RudpNotOpenException e)
            {
            m_log.debug("Got RUDP not open", e);
            close();
            throw new SocketException("Socket closed!");
            }
        catch (final RuntimeException e)
            {
            m_log.warn("Could not send data", e);
            close();
            throw new IoExceptionWithCause("Could not send data", e);
            }
        m_index = 0;
        }
    }
