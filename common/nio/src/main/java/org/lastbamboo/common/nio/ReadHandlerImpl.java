package org.lastbamboo.common.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.protocol.ProtocolHandler;

/**
 * Reads incoming NIO data for a single <code>SocketChannel</code>.
 */
final class ReadHandlerImpl
    {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ReadHandlerImpl.class);

    private final ByteBuffer m_readBuffer;

    private final SocketChannel m_socketChannel;

    private ProtocolHandler m_protocolHandler;

    private final InetSocketAddress m_inetSocketAddress;

    /**
     * Creates a new read handler for the specified socket and using the 
     * specified handler for the specific protocol.
     * @param socketChannel The channel to read data from.
     * @throws SocketException If we could not access socket data.
     */
    public ReadHandlerImpl(final SocketChannel socketChannel) 
        throws SocketException
        {
        this.m_socketChannel = socketChannel;
        this.m_inetSocketAddress = 
            (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
        
        // Send the protocol handle the TCP receive buffer size so it can 
        // use it in determining the size of buffers to create.  We cannot
        // read more data than the receive buffer size at any one time.
        this.m_readBuffer = ByteBuffer.allocateDirect(
            this.m_socketChannel.socket().getReceiveBufferSize());
        }

    /**
     * Reads from the socket channel into the buffer.
     * 
     * @throws IOException If we reach an end-of-stream on the connection.
     */
    public void read() throws IOException
        {
        LOG.trace("Handling read...");
        // Reads from the socket
        // Returns -1 if it has reached end-of-stream
        final long readBytes = this.m_socketChannel.read(this.m_readBuffer);
        
        // Have we reached the end of the stream?
        if (readBytes == -1)
            {
            LOG.debug("Reached end of stream, closing channel...");
            // End of stream. Throw an exception to close the channel.
            throw new IOException("end of stream...");
            }

        // Anything to read?
        if (readBytes != 0)
            {
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Reading "+readBytes+" bytes of data...");
                }
            // There is some data in the buffer. Process it.
            processReadBuffer();
            }
        }

    /**
     * Processes the internal buffer, converting it into packets if enough data
     * is available.
     * @throws IOException If the handling of the message results in a read or
     * write error.
     */
    private void processReadBuffer() throws IOException
        {
        LOG.trace("Processing input buffer...");        
        this.m_protocolHandler.handleMessages(this.m_readBuffer, 
            this.m_inetSocketAddress);
        
        // We've read all the data, so clear the buffer.
        this.m_readBuffer.clear();
        }

    /**
     * Sets the protocol handler to use for this connection.
     * @param protocolHandler Handler for reading messages for a specific
     * protocol.
     */
    public void setProtocolHandler(final ProtocolHandler protocolHandler)
        {
        this.m_protocolHandler = protocolHandler;
        }
    
    public String toString()
        {
        return this.getClass().getName() + " to: "+this.m_inetSocketAddress;
        }
    }
