package org.lastbamboo.common.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.protocol.CloseListener;
import org.lastbamboo.common.protocol.ProtocolHandler;
import org.lastbamboo.common.protocol.ReaderWriter;
import org.lastbamboo.common.protocol.WriteListener;

/**
 * This class follows the facade pattern for writing and reading data to and
 * from the network, delegating those functions to more specialized classes.
 * This class handles all read/write events from the selector.
 */
public final class NioReaderWriter implements SelectorHandler, ReaderWriter
    {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NioReaderWriter.class);
    
    /** 
     * The associated m_selectorManager. 
     */
    private final SelectorManager m_selectorManager;

    /** 
     * The socket where read and write operations are performed. 
     */
    private final SocketChannel m_socketChannel;
    
    private final ReadHandlerImpl m_readHandler;
    private final WriteHandlerImpl m_writeHandler;
    
    /**
     * <code>List</code> of listeners for close events.
     */
    private final List m_closeListeners = 
        Collections.synchronizedList(new LinkedList());

    /**
     * Flag for whether or not this has already been closed.
     */
    private transient boolean m_closed = false;

    /**
     * Creates a new handler for reading and writing.  This enables read 
     * handling by default.  You should enable write handling when there's 
     * data that needs writing.
     * 
     * @param socketChannel Socket to be wrapped.
     * @param selectorManager Selector to be used for managing IO events.
     * @throws IOException If we could not register this channel for reading.
     */
    public NioReaderWriter(final SocketChannel socketChannel,
        final SelectorManager selectorManager) throws IOException
        {
        this(socketChannel, selectorManager, true);
        }
    
    /**
     * Creates a new handler for reading and writing.  This enables read 
     * handling by default.  You should enable write handling when there's 
     * data that needs writing.
     * 
     * @param socketChannel Socket to be wrapped.
     * @param selectorManager Selector to be used for managing IO events.
     * @param onSelectorThread Specifies whether or not this contructor is
     * being called from the thread of the specified selector.  If it is, we
     * just register with that selector right away.  If not, we register from
     * another thread and block until the registration actually takes place.
     * @throws IOException If we could not register this channel for reading.
     */
    private NioReaderWriter(final SocketChannel socketChannel,
        final SelectorManager selectorManager, final boolean onSelectorThread) 
        throws IOException
        {
        this.m_selectorManager = selectorManager;
        this.m_socketChannel = socketChannel;
        
        this.m_writeHandler = 
            new WriteHandlerImpl(this.m_selectorManager, this.m_socketChannel);
        
        this.m_readHandler = new ReadHandlerImpl(this.m_socketChannel);
        registerRead(onSelectorThread);
        }
 
    /**
     * Creates a new handler for reading and writing.  This enables read 
     * handling by default.  You should enable write handling when there's 
     * data that needs writing.  This sets the protocol handler on 
     * construction.
     * 
     * @param socketChannel Socket to be wrapped.
     * @param selectorManager Selector to be used for managing IO events.
     * @param protocolHandler The handler for the specific protocol.
     * @throws IOException If we could not register this channel for reading.
     */
    public NioReaderWriter(final SocketChannel socketChannel, 
        final SelectorManager selectorManager, 
        final ProtocolHandler protocolHandler) throws IOException
        {
        this(socketChannel, selectorManager, protocolHandler, true);
        }
    
    
    /**
     * Creates a new handler for reading and writing.  This enables read 
     * handling by default.  You should enable write handling when there's 
     * data that needs writing.  This sets the protocol handler on 
     * construction.
     * 
     * @param socketChannel Socket to be wrapped.
     * @param selectorManager Selector to be used for managing IO events.
     * @param protocolHandler The handler for the specific protocol.
     * @param onSelectorThread Specifies whether or not this contructor is
     * being called from the thread of the specified selector.  If it is, we
     * just register with that selector right away.  If not, we register from
     * another thread and block until the registration actually takes place.
     * @throws IOException If we could not register this channel for reading.
     */
    public NioReaderWriter(final SocketChannel socketChannel, 
        final SelectorManager selectorManager, 
        final ProtocolHandler protocolHandler, final boolean onSelectorThread) 
        throws IOException
        {
        this(socketChannel, selectorManager, onSelectorThread);
        setProtocolHandler(protocolHandler);
        }    

    /**
     * Adds a listener for close events.
     * @param listener The listener to add.
     */
    public void addCloseListener(final CloseListener listener)
        {
        this.m_closeListeners.add(listener);
        }

    public void close()
        {
        LOG.debug("Closing socket...");
        if (this.m_closed)
            {
            // Ignore multiple close calls.
            LOG.debug("Ignoring close call on closed socket...");
            return;
            }
        this.m_closed = true;
        this.m_writeHandler.close();
        try
            {
            this.m_socketChannel.close();
            }
        catch (final IOException e)
            {
            // Ignore
            }
        
        notifyCloseListeners();
        }

    /**
     * Notifies any listeners that this handler has closed.
     */
    private void notifyCloseListeners()
        {
        synchronized (this.m_closeListeners)
            {
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Closing "+this.m_closeListeners.size()+" listeners");
                }
            for (final Iterator iter = this.m_closeListeners.iterator(); 
                iter.hasNext();)
                {
                final CloseListener listener = (CloseListener) iter.next();
                if (LOG.isDebugEnabled())
                    {
                    LOG.debug("Notifying listener: "+listener);
                    }
                listener.onClose(this);
                }
            }
        }

    public void handleKey(final SelectionKey sk)
        {
        LOG.trace("Handling read/write key...");
        
        // Readable or writable              
        if (sk.isReadable())
            {
            LOG.trace("Handling read...");
            // It is possible to read
            try
                {
                this.m_readHandler.read();
                }
            catch (final IOException e)
                {
                // This will happen when the connection has closed.
                LOG.debug("Exception on "+this+" while reading...", e);
                close();
                return;
                }
            }
        
        // Check if the key is still valid, since it might 
        // have been invalidated in the read handler 
        // (for instance, the socket might have been closed)
        if (sk.isValid() && sk.isWritable())
            {
            LOG.trace("Handling write...");
            try
                {
                this.m_writeHandler.write();
                }
            catch (final IOException e)
                {
                // This will happen when the connection has closed.
                LOG.debug("Exception on "+this+" while reading...", e);
                close();
                }
            }
        
        if (!sk.isValid())
            {
            LOG.trace("Closing socket");
            close();
            }
        }

    private void registerRead(final boolean onSelectorThread) throws IOException
        {
        LOG.trace("Registering read for: "+this);
        if (onSelectorThread)
            {
            this.m_selectorManager.registerChannelNow(this.m_socketChannel, 
                SelectionKey.OP_READ, this);
            }
        else
            {
            this.m_selectorManager.registerChannelLater(this.m_socketChannel, 
                SelectionKey.OP_READ, this);
            
            // Now, block until we're registered.  This is not ideal, but it's
            // about the best we can do!
            while (!this.m_socketChannel.isRegistered())
                {
                LOG.trace("Waiting for channel to register...");
                try
                    {
                    Thread.sleep(200);
                    }
                catch (final InterruptedException e)
                    {
                    // Should never happen.
                    LOG.error("Unexpected interrupt", e);
                    }
                }
            }
        }

    /**
     * Sets the protocol handler to use for this connection.
     * @param protocolHandler Handler for reading messages for a specific
     * protocol.
     */
    public void setProtocolHandler(final ProtocolHandler protocolHandler)
        {
        this.m_readHandler.setProtocolHandler(protocolHandler);
        }

    public void write(final ByteBuffer buffer) throws IOException
        {
        this.m_writeHandler.write(buffer);
        }
    
    public void write(final ByteBuffer buffer, final WriteListener listener) 
        throws IOException
        {
        this.m_writeHandler.write(buffer, listener);
        }

    public void write(final Collection buffers) throws IOException
        {
        this.m_writeHandler.write(buffers);
        }
 
    public void writeLater(final Collection buffers)
        {
        this.m_writeHandler.writeLater(buffers);
        }
    
    public void writeLater(final ByteBuffer data)
        {
        this.m_writeHandler.writeLater(data);
        }
    
    public void writeLater(final ByteBuffer data, final WriteListener listener)
        {
        this.m_writeHandler.writeLater(data, listener);
        }
    
    public InetSocketAddress getRemoteSocketAddress()
        {
        return (InetSocketAddress) 
            this.m_socketChannel.socket().getRemoteSocketAddress();
        }
    
    public InetSocketAddress getLocalSocketAddress()
        {
        return (InetSocketAddress) 
            this.m_socketChannel.socket().getLocalSocketAddress();
        }
    
    public boolean isClosed()
        {
        return this.m_closed;
        }
    
    public SocketChannel getSocketChannel()
        {
        return this.m_socketChannel;
        }
    
    public int hashCode()
        {
        return 47 * this.m_socketChannel.hashCode();
        }
    
    public boolean equals(final Object obj)
        {
        if (obj == this)
            {
            return true;
            }
        if (!(obj instanceof NioReaderWriter))
            {
            return false;
            }
        final NioReaderWriter readerWriter = (NioReaderWriter) obj;
        return readerWriter.m_socketChannel.equals(this.m_socketChannel);
        }
    
    public String toString()
        {
        return "NIO Reader/Writer to: " + this.m_socketChannel.socket();
        }
    }