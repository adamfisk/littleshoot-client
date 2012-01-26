package org.lastbamboo.common.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.protocol.WriteData;
import org.lastbamboo.common.protocol.WriteListener;
import org.littleshoot.util.ByteBufferUtils;

/**
 * Handles writes to a given channel.
 */
final class WriteHandlerImpl 
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(WriteHandlerImpl.class);
    
    /**
     * Channel for writing data to.
     */
    private final SocketChannel m_socketChannel;
    
    /**
     * Selector manager allowing us to register for write events as necessary.
     */
    private final SelectorManager m_selectorManager;

    /**
     * <code>List</code> of <code>ByteBuffer</code>s to write.  This can be
     * queud if there's a backlog of data to write.
     */
    private final List m_writeBuffers = Collections.synchronizedList(
        new LinkedList());

    /**
     * Flag for whether or not this writer is closed.  Once a writer has closed,
     * it cannot be reopened.
     */
    private volatile boolean m_closed;
    
    private static final WriteListener NO_OP_LISTENER = new NoOpWriteListener();

    /**
     * Creates a new writing handler with the given manager for the selector
     * and for the given socket channel.
     * @param selectorManager The manager for the selector that notifies us 
     * of write events.
     * @param socketChannel The channel for the socket to write on.
     */
    public WriteHandlerImpl(final SelectorManager selectorManager, 
        final SocketChannel socketChannel)
        {
        this.m_selectorManager = selectorManager;
        this.m_socketChannel = socketChannel;
        }

    public void write(final ByteBuffer buffer, final WriteListener listener) 
        throws IOException
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Queuing buffer of length: "+buffer.remaining());
            }
        if (this.m_closed)
            {
            LOG.trace("Ignoring write to closed writer...");
            throw new IOException("Connection closed!!");
            }
        if (buffer == null)
            {
            throw new NullPointerException("null buffer");
            }
        if (!buffer.hasRemaining())
            {
            // If there's nothing remaining in the buffer, don't bother with
            // enabling writing and adding the buffer.
            return;
            }
        final List buffers = new LinkedList();
        buffers.add(buffer);
        final WriteData data = new BuffersAndListener(buffers, listener, 
            this.m_writeBuffers.size());
        synchronized (this.m_writeBuffers)
            {
            this.m_writeBuffers.add(data);
            enableWrite();
            }
        }
    
    public void write(final ByteBuffer buffer) throws IOException
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Queuing buffer of length: "+buffer.remaining());
            }
        write(buffer, NO_OP_LISTENER);
        }
    
    public void write(final Collection buffers) throws IOException
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Queuing "+buffers.size()+" for writing...");
            }
        if (this.m_closed)
            {
            LOG.trace("Ignoring write to closed writer...");
            throw new IOException("Connection closed!!");
            }
        checkForNull(buffers);
        final WriteData data = new BuffersAndListener(buffers);
        synchronized (this.m_writeBuffers)
            {
            this.m_writeBuffers.add(data);
            enableWrite();
            }
        }
    
    public void writeLater(final Collection buffers)
        {
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Queuing "+buffers.size()+" for writing...");
            }
        if (this.m_closed)
            {
            LOG.trace("Ignoring write to closed writer...");
            return;
            }
        checkForNull(buffers);
        final WriteData data = new BuffersAndListener(buffers);
        synchronized (this.m_writeBuffers)
            {
            this.m_writeBuffers.add(data);
            enableWriteLater();
            }
        }
    
    public void writeLater(final ByteBuffer buffer)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Queuing buffer for writing...");
            }
        writeLater(buffer, NO_OP_LISTENER);
        }
    
    public void writeLater(final ByteBuffer buffer, 
        final WriteListener listener)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Queuing buffer of length: "+buffer.remaining());
            }
        if (this.m_closed)
            {
            LOG.trace("Ignoring write to closed writer...");
            return;
            }
        if (buffer == null)
            {
            throw new NullPointerException("null buffer");
            }
        if (!buffer.hasRemaining())
            {
            // If there's nothing remaining in the buffer, don't bother with
            // enabling writing and adding the buffer.
            return;
            }
        final List buffers = new LinkedList();
        buffers.add(buffer);
        final WriteData data = new BuffersAndListener(buffers, listener, 
            this.m_writeBuffers.size());
        
        synchronized (this.m_writeBuffers)
            {
            this.m_writeBuffers.add(data);
            enableWriteLater();
            }
        }
    
    /**
     * Utility method that throws a <code>NullPointerException</code> if 
     * any of the buffers in the collection is <code>null</code>.
     * @param buffers The <code>Collection</code> of <code>ByteBuffer</code>s
     * to check.
     */
    private void checkForNull(final Collection buffers)
        {
        if (CollectionUtils.exists(buffers, PredicateUtils.nullPredicate()))
            {
            throw new NullPointerException("Cannot accept null buffers");
            }
        }

    public boolean write() throws IOException
        {
        if (LOG.isDebugEnabled())
            {
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Writing "+this.m_writeBuffers.size()+" buffers...");
                }
            }
        
        if (this.m_closed)
            {
            LOG.trace("Ignoring write to closed writer...");
            return true;
            }
        
        if (this.m_writeBuffers.isEmpty())
            {
            // This can happen in tests, but should never happen in the field.
            LOG.warn("Called write when there are no write buffers!!!");
            return true;
            }
        // Writes to the socket as much as possible. Since this is a
        // non-blocking operation, we don't know in advance how many
        // bytes will actually be written.
        synchronized (this.m_writeBuffers)
            {
            for (final Iterator iter = this.m_writeBuffers.iterator(); 
                iter.hasNext();)
                {
                final BuffersAndListener bl = (BuffersAndListener) iter.next();
                final Collection buffers = bl.getBuffers();
                for (final Iterator iterator = buffers.iterator(); 
                    iterator.hasNext();)
                    {
                    final ByteBuffer buffer = (ByteBuffer) iterator.next();
                    if (writeBuffer(buffer))
                        {
                        // Make sure we remove this buffer from the list.
                        iterator.remove();
                        }
                    else 
                        {
                        return false;
                        }
                    }
                
                // If we get here, we've written all the data for the 
                // current message, so notify the listener.
                final WriteListener listener = bl.getListener();
                listener.onWrite(bl);
                iter.remove();
                }
            
            // We have no more data to write, so stop listening for write 
            // events. 
            disableWriteLater();
            }
        
        return true;
        }
    
    /**
     * Writes a single buffer to the channel.
     * 
     * @param buffer The buffer to write.
     * @return <code>true</code> if the buffer was completely written, 
     * otherwise <code>false</code>.
     * @throws IOException If there's a write error writing to the channel.
     */
    private boolean writeBuffer(final ByteBuffer buffer) throws IOException
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing "+buffer.remaining()+" bytes to "+
                this.m_socketChannel.socket().getInetAddress());
            
            ByteBufferUtils.logBufferToWrite(buffer);
            }
        this.m_socketChannel.write(buffer);
        return !buffer.hasRemaining();
        }

    /**
     * Activates interest in writing from the selector thread.
     * @throws IOException If we could not enable write on the channel 
     * because it's closed.
     */
    private void enableWrite() throws IOException
        {
        this.m_selectorManager.addChannelInterestNow(this.m_socketChannel, 
            SelectionKey.OP_WRITE);
        }
    
    /**
     * Activates interest in writing from a thread other than the selector
     * thread.
     */
    private void enableWriteLater()
        {
        this.m_selectorManager.addChannelInterestLater(this.m_socketChannel, 
            SelectionKey.OP_WRITE);
        }
    
    /**
     * Disables interest in writing.  This is typically called if we've written
     * everything we can and have no more data to write.
     */
    private void disableWriteLater()
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Removing writing interest...");
            }
        this.m_selectorManager.removeChannelInterestLater(this.m_socketChannel,
            SelectionKey.OP_WRITE);
        }

    public void close()
        {
        this.m_writeBuffers.clear();
        this.m_closed = true;
        }
    
    private static final class NoOpWriteListener implements WriteListener
        {

        public void onWrite(final WriteData data)
            {
            // Noop.
            }
    
        }
    
    private static final class BuffersAndListener implements WriteData
        {

        private final Collection m_buffers;
        private final WriteListener m_listener;
        private final long m_startTime;
        private final long m_totalBytes;
        private final int m_numQueued;

        private BuffersAndListener(final Collection buffers, 
            final WriteListener listener, final int numQueued)
            {
            this.m_buffers = buffers;
            this.m_listener = listener;
            this.m_numQueued = numQueued;
            this.m_startTime = System.currentTimeMillis();
            
            long totalBytes = 0L;
            for (final Iterator iter = buffers.iterator(); iter.hasNext();)
                {
                final ByteBuffer buf = (ByteBuffer) iter.next();
                totalBytes += buf.remaining();
                }
            
            this.m_totalBytes = totalBytes;
            }

        private BuffersAndListener(final Collection buffers)
            {
            this(buffers, NO_OP_LISTENER, 0);
            }

        private Collection getBuffers()
            {
            return m_buffers;
            }

        private WriteListener getListener()
            {
            return m_listener;
            }

        public long getStartTime()
            {
            return m_startTime;
            }

        public long getTotalBytes()
            {
            return this.m_totalBytes;
            }

        public int getNumQueued()
            {
            return this.m_numQueued;
            }
        }
    }
