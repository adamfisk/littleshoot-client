package org.lastbamboo.common.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collection;

/**
 * Interface for classes that read and write protocol data.
 */
public interface ReaderWriter
    {

    /**
     * Sets the protocol handler to use for this connection.
     * @param protocolHandler Handler for reading messages for a specific
     * protocol.
     */
    void setProtocolHandler(final ProtocolHandler protocolHandler);
    
    /**
     * Requests to write the specified <code>ByteBuffer</code> to the 
     * underlying channel.  This call is non-blocking.
     * 
     * @param buffer The <code>ByteBuffer</code> to write.
     * @throws IOException If we could not enable writing on this channel.
     */
    void write(final ByteBuffer buffer) throws IOException;

    /**
     * Requests to write the specified <code>ByteBuffer</code> to the 
     * underlying channel.  This call is non-blocking.
     * 
     * @param buffer The <code>ByteBuffer</code> to write.
     * @param listener The listener to notify when the buffer is written.
     * @throws IOException If we could not enable writing on this channel.
     */
    void write(final ByteBuffer buffer, final WriteListener listener) 
        throws IOException;
    
    /**
     * Writes the specified <code>Collection</code> of <code>ByteBuffer</code>s
     * to the underlying write mechanism.  This call is non-blocking.
     * @param buffers The <code>Collection</code> of <code>ByteBuffer</code>s
     * to write.
     * @throws IOException If we could not enable writing on this channel.
     */
    void write(final Collection buffers) throws IOException;

    /**
     * Closes the handler.
     */
    void close();

    /**
     * Adds the specified listener for when the reader/writer closes.
     * @param listener The listener to notify on close events.
     */
    void addCloseListener(final CloseListener listener);

    /**
     * Writes the specified <code>Collection</code> of <code>ByteBuffer</code>s
     * from a thread other than the selector thread.  This queues up the
     * write to write on the selector thread whenever it's ready.
     * 
     * @param buffers The <code>ByteBuffer</code>s to write.
     */
    void writeLater(final Collection buffers);

    /**
     * Writes the specified <code>ByteBuffer</code> from a thread other than 
     * the selector thread.  This queues up the write to write on the selector 
     * thread whenever it's ready.
     * 
     * @param data The <code>ByteBuffer</code> to write.
     */
    void writeLater(final ByteBuffer data);

    /**
     * Writes the specified <code>ByteBuffer</code> from a thread other than 
     * the selector thread.  This queues up the write to write on the selector 
     * thread whenever it's ready.  This also notifies the specified listener
     * when all the data is written.
     * 
     * @param data The <code>ByteBuffer</code> to write.
     * @param listener The listener to notify when the data is completely 
     * written.
     */
    void writeLater(final ByteBuffer data, final WriteListener listener);

    /**
     * Accessor for the <code>InetSocketAddress</code> of the remote endpoint
     * for this reader/writer.
     * 
     * @return The <code>InetSocketAddress</code> for the remote host.
     */
    InetSocketAddress getRemoteSocketAddress();

    /**
     * Accessor for the local <code>InetSocketAddress</code> of this 
     * reader/writer.
     * 
     * @return The local <code>InetSocketAddress</code>.
     */
    InetSocketAddress getLocalSocketAddress();

    /**
     * Determines whether or not the connection is closed.
     * 
     * @return <code>true</code> if the connection is closed, otherwise 
     * <code>false</code>.
     */
    boolean isClosed();

    /**
     * Accessor for the {@link SocketChannel}.
     * 
     * @return The {@link SocketChannel}, or <code>null</code> if there is no
     * {@link SocketChannel} for this type of {@link ReaderWriter}.
     */
    SocketChannel getSocketChannel();

    }
