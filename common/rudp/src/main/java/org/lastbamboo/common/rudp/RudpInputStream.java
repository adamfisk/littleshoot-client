package org.lastbamboo.common.rudp;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.support.ByteBufferHexDumper;
import org.littleshoot.util.IoExceptionWithCause;
import org.littleshoot.util.RuntimeIoException;
import org.littleshoot.util.RuntimeSocketException;
import org.littleshoot.util.RuntimeSocketTimeoutException;
import org.littleshoot.util.SocketExceptionWithCause;
import org.littleshoot.util.SocketTimeoutExceptionWithCause;
import org.littleshoot.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The input stream that gets its data from a reliable UDP connection.
 */
public final class RudpInputStream extends InputStream
    { 
    /**
     * The current message buffer received by the reliable UDP layer.
     */
    private byte[] m_current = new byte[0];
    
    /**
     * The connection identifier of the reliable UDP connection.
     */
    private final RudpConnectionId m_id;
    
    /**
     * The index into the current message buffer received by the reliable UDP
     * layer.
     */
    private int m_index;
    
    /**
     * The logger for this class.
     */
    private final Logger m_log;
    
    /**
     * The reliable UDP service to use for reliable UDP interaction.
     */
    private final RudpService m_service;

    private volatile boolean m_closing = false;

    private final RudpSocket m_socket;

    private volatile boolean m_closed = false;

    /**
     * Constructs a new input stream that gets its data from a reliable UDP
     * connection.  Currently, the connection identifier should identify a
     * connection that is known to be ready for reading.
     * 
     * @param service The reliable UDP service to use for reliable UDP 
     *  interaction.
     * @param id The connection identifier of the reliable UDP connection.
     * @param socket The RUDP socket associated with this stream.
     */
    public RudpInputStream (final RudpService service, 
        final RudpConnectionId id, final RudpSocket socket)
        {
        m_socket = socket;
        m_log = LoggerFactory.getLogger (RudpInputStream.class);
        m_service = service;
        m_id = id;
        m_index = 0;
        }
    
    @Override
    public void close ()
        {
        m_log.debug("Closing input stream: \n"+ThreadUtils.dumpStack());
        
        System.out.println("Dumping stack at: "+
            DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date()));
        Thread.dumpStack();
        if (this.m_closed || this.m_closing)
            {
            return;
            }
        
        this.m_closing = true;
        
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

    // This is slightly modified from the default InputStream read method.
    @Override
    public int read (final byte b[], final int off, final int len) 
        throws IOException
        {
        if (b == null)
            {
            m_log.error("Null buffer!!");
            throw new NullPointerException();
            }
        else if ((off < 0) || (off > b.length) || (len < 0)
                || ((off + len) > b.length) || ((off + len) < 0))
            {
            m_log.error("Index out of bounds.  " +
                "off: "+off+" len: "+len+" b.length: "+b.length);
            throw new IndexOutOfBoundsException();
            }
        else if (len == 0)
            {
            return 0;
            }

        int c = read();
        if (c == -1)
            {
            return -1;
            }
        b[off] = (byte) c;

        int i = 1;
        for (; i < len; i++)
            {
            c = nonBlockingRead();
            if (c == -77)
                {
                break;
                }
            if (b != null)
                {
                b[off + i] = (byte) c;
                }
            }
        return i;
        }
    
    /**
     * This method is necessary because bulk read calls can't block until
     * all the data in the buffer is read, as many times there simply won't
     * be enough data available to fill the bulk byte array.  So, this 
     * method simply supplies as much data as we've got in our cache, waiting
     * for the next pass to query the RUDP layer for more data if necessary.
     * 
     * @return The next byte, or -77 if there's no byte immediately available
     * in our buffer.  The -77 is chosen randomly -- we just avoid returning
     * -1 to avoid confusion with EOF.   
     */
    private int nonBlockingRead()
        {
        if (m_index >= m_current.length)
            {
            return -77;
            }
        else
            {
            // We need to return an unsigned byte int equivalent.
            return m_current[m_index++] & 0x000000FF;
            }
        }
    
    @Override
    public int available ()
        {
        return (m_current.length - m_index);
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int read () throws IOException
        {
        // This matches SocketInputStream behavior, in particular the 
        // native Solaris implementation in SocketInputStream.c called from
        // SocketInputStream.java, as well as likely other native 
        // implementations.
        if (this.m_closing || this.m_closed)
            {
            throw new SocketException("Socket closed");
            }
        if (m_index >= m_current.length)
            {
            try
                {
                m_current = m_service.receive (m_id);
                }
            catch (final RuntimeSocketException e)
                {
                throw new SocketExceptionWithCause(e.getMessage(), e);
                }
            catch (final RuntimeSocketTimeoutException e)
                {
                throw new SocketTimeoutExceptionWithCause(e.getMessage(), e);
                }
            catch (final RuntimeIoException e)
                {
                throw new IoExceptionWithCause(e.getMessage(), e);
                }
            m_index = 0;
            }
        // We need to return an unsigned byte int equivalent.
        return m_current[m_index++] & 0x000000FF;
        }
    
    @Override
    public String toString()
        {
        return getClass().getSimpleName() + 
            "\nAvailable: " + (m_current.length - m_index) +
            "\nArray length: "+m_current.length+
            "\nIndex: "+m_index +
            "\nBuf: "+ByteBufferHexDumper.getHexdump(ByteBuffer.wrap(m_current));
        }
    }
