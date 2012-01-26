package org.lastbamboo.common.nio;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Event queue for I/O events raised by a selector. This class receives the
 * lower level events raised by a Selector and dispatches them to the
 * appropriate handler. It also manages all other operations on the selector,
 * like registering and unregistering channels, or updating the events of
 * interest for each monitored socket.
 * 
 * This class is inspired on the java.awt.EventQueue and follows a similar
 * model. The EventQueue class is responsible for making sure that all
 * operations on AWT objects are performed on a single thread, the one managed
 * internally by EventQueue. Theis class performs a similar task.
 * In particular:
 *  - Only the thread created by instances of this class should be allowed to
 * access the selector and all sockets managed by it. This means that all I/O
 * operations on the sockets should be peformed on the corresponding selector's
 * thread. If some other thread wants to access objects managed by this
 * selector, then it should use <code>invokeLater()</code> or the
 * <code>invokeAndWait()</code> to dispatch a runnable to this thread.
 *  - This thread should not be used to perform lenghty operations. In
 * particular, it should never be used to perform blocking I/O operations. To
 * perform a time consuming task use a worker thread.
 * 
 * 
 * This architecture is required for two main reasons:
 * 
 * The first, is to make synchronization in the objects of a connection
 * unnecessary. This is good for performance and essential for keeping the
 * complexity low. Getting synchronization right within the objects of a
 * connection would be extremely tricky.
 * 
 * The second is to make sure that all actions over the selector, its keys and
 * related sockets are carried in the same thread. My personal experience with
 * selectors is that they don't work well when being accessed concurrently by
 * several threads. This is mostly the result of bugs in some of the version of
 * Sun's Java SDK (these problems were found with version 1.4.2_02). Some of the
 * bugs have already been identified and fixed by Sun. But it is better to work
 * around them by avoiding multithreaded access to the selector.
 */
public final class SelectorManagerImpl implements SelectorManager, Runnable
    {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SelectorManagerImpl.class);
    
    /** 
     * Selector used for I/O multiplexing. 
     */
    private Selector m_selector;

    /** 
     * The thread associated with this manager. 
     */
    private Thread m_selectorThread;

    /**
     * Flag telling if this object should terminate, that is, if it should close
     * the selector and kill the associated thread. Used for graceful
     * termination.
     */
    private volatile boolean m_closeRequested = false;

    /**
     * List of tasks to be executed in the selector thread. Submitted using
     * invokeLater() and executed in the main select loop.
     */
    private final List m_pendingInvocations = new ArrayList(32);

    private final String m_name;

    
    /**
     * Creates a new selector with no name for debugging.  This should typically
     * only be used in testing.
     */
    public SelectorManagerImpl()
        {
        this.m_name = "unnamed";
        }
    
    /**
     * Creates a new selector.
     * 
     * @param name The name of the selector so we can differentiate between
     * muliple selector threads.
     */
    public SelectorManagerImpl(final String name)
        {
        this.m_name = name;
        }
    
    /**
     * Raises an internal flag that will result on this thread dying the next
     * time it goes through the dispatch loop. The thread executes all pending
     * tasks before dying.
     */
    public void requestClose()
        {
        this.m_closeRequested = true;
        // Nudges the selector.
        this.m_selector.wakeup();
        }

    public void addChannelInterestNow(final SelectableChannel channel, 
        final int interest) throws IOException
        {
        if (Thread.currentThread() != this.m_selectorThread)
            {
            // Programming error -- throw an unchecked exception.
            throw new IllegalStateException(
                "Method can only be called from selector thread");
            }
      
        if (!channel.isOpen())
            {
            LOG.trace("Channel is already closed...");
            return;
            }
        LOG.debug("Getting key for channel: "+channel);
        final SelectionKey sk = channel.keyFor(this.m_selector);
        changeKeyInterest(sk, sk.interestOps() | interest);
        }

    public void addChannelInterestLater(final SelectableChannel channel,
        final int interest)
        {
        // Add a new runnable to the list of tasks to be executed in the
        // selector thread
        invokeLater(new Runnable()
            {
            public void run()
                {
                LOG.trace("About to add channel interest...");
                try
                    {
                    addChannelInterestNow(channel, interest);
                    }
                catch (final IOException e)
                    {
                    LOG.warn("Could not add channel interest", e);
                    }
                }
            });
        }

    public void removeChannelInterestNow(final SelectableChannel channel, 
        final int interest) throws IOException
        {
        if (Thread.currentThread() != this.m_selectorThread)
            {
            throw new IOException(
                "Method can only be called from selector thread");
            }
        if (!channel.isOpen())
            {
            LOG.trace("Channel is already closed...");
            return;
            }
        final SelectionKey sk = channel.keyFor(this.m_selector);
        changeKeyInterest(sk, sk.interestOps() & ~interest);
        }

    public void removeChannelInterestLater(final SelectableChannel channel,
        final int interest)
        {
        invokeLater(new Runnable()
            {
            public void run()
                {
                try
                    {
                    removeChannelInterestNow(channel, interest);
                    }
                catch (final IOException e)
                    {
                    // TODO: Remove connection??
                    LOG.warn("Could not remove channel interest", e);
                    }
                }
            });
        }

    /**
     * Updates the interest set associated with a selection key. The old
     * interest is discarded, being replaced by the new one.
     * 
     * @param key The key to be updated.
     * @param newInterest The new interest to register the key for.
     * @throws IOException If the key is cancelled by the time interest ops 
     * are modified.
     */
    private void changeKeyInterest(final SelectionKey key, 
        final int newInterest) throws IOException
        {
        // This method might throw two unchecked exceptions: 1.
        // IllegalArgumentException - Should never happen. It is a bug if it
        // happens 2. CancelledKeyException - Might happen if the channel is
        // closed while a packet is being dispatched.
        try
            {
            key.interestOps(newInterest);
            if (LOG.isDebugEnabled())
                {
                LOG.trace("Set interest ops, now read "+
                    ((key.interestOps() & SelectionKey.OP_READ) != 0)+
                    " and write "+
                    ((key.interestOps() & SelectionKey.OP_WRITE) != 0));
                }
            }
        catch (final CancelledKeyException e)
            {
            final IOException ioe = new IOException(
                "Failed to change channel interest.");
            ioe.initCause(e);
            throw ioe;
            }
        }

    public void registerChannelLater(final SelectableChannel channel,
        final int selectionKeys, final SelectorHandler handler)
        {
        LOG.trace("Registering channel later...");
        invokeLater(new Runnable()
            {
            public void run()
                {
                try
                    {
                    registerChannelNow(channel, selectionKeys, handler);
                    }
                catch (final IOException e)
                    {
                    // TODO: What to do if we can't register the channel??
                    LOG.warn("Could not register channel", e);
                    }
                }
            });
        }

    public void registerChannelNow(final SelectableChannel channel, 
        final int selectionKeys, final SelectorHandler handler) 
        throws IOException 
        {
        LOG.trace("Registering channel...");
        if (Thread.currentThread() != m_selectorThread) 
            {
            // Programming error -- throw an unchecked exception.
            throw new IllegalStateException(
                "Method can only be called from selector thread");
            }
  
        if (!channel.isOpen()) 
            { 
            throw new IOException("Channel is not open.");
            }

        if (channel.isRegistered()) 
            {
            final SelectionKey sk = channel.keyFor(this.m_selector);
            Assert.notNull(sk, 
                "Channel is already registered with other selector");        
            sk.interestOps(selectionKeys);
            final Object previousAttach = sk.attach(handler);
            Assert.notNull(previousAttach);
            } 
        else 
            {  
            channel.configureBlocking(false);
            channel.register(this.m_selector, selectionKeys, handler);      
            }  
        }
    
    public void invokeLater(final Runnable task)
        {
        LOG.trace("Invoking task later...");
        synchronized (this.m_pendingInvocations)
            {
            this.m_pendingInvocations.add(task);
            }
        LOG.trace("Waking selector: "+this);
        this.m_selector.wakeup();
        }

    /**
     * Executes all tasks queued for execution on the selector's thread.
     */
    private void doInvocations()
        {
        LOG.trace("Processing pending invocations...");
        synchronized (this.m_pendingInvocations)
            {
            for (final Iterator iter = this.m_pendingInvocations.iterator(); 
                iter.hasNext();)
                {
                final Runnable task = (Runnable) iter.next();
                task.run();
                }
            this.m_pendingInvocations.clear();
            }
        }

    /**
     * Starts the selector.
     */
    public void run()
        {
        runSelector();
        }
    
    private void runSelector()
        {
        LOG.trace("Running selector...");
        // Here's where everything happens. The select method will
        // return when any operations registered above have occurred, the
        // thread has been interrupted, etc.
        while (true)
            {
            // Execute all the pending tasks.
            doInvocations();

            // Should we close the selector?
            if (this.m_closeRequested)
                {
                LOG.warn("Why the heck is the selector closed??");
                return;
                }

            int selectedKeys = 0;
            try
                {
                LOG.trace(this + " selecting...");
                selectedKeys = this.m_selector.select();
                LOG.trace("Out of select...");
                }
            catch (final IOException e)
                {
                // Select should never throw an exception under normal
                // operation. If this happens, print the error and try to
                // continue working.
                LOG.error("Unexpected error", e);
                continue;
                }

            if (selectedKeys == 0)
                {
                LOG.trace("No selected keys...");
                continue;
                }

            // Process the selected keys.
            handleSelectedKeys(this.m_selector);
            }
        }

    private void handleSelectedKeys(final Selector selector)
        {
        final Iterator it = selector.selectedKeys().iterator();
        // Walk through the collection of ready keys and dispatch
        // any active event.
        while (it.hasNext())
            {
            final SelectionKey sk = (SelectionKey) it.next();
            it.remove();
            try
                {
                handleKey(sk);
                }
            catch (final Throwable t)
                {
                LOG.error("Unexpected error", t);
                // No exceptions should be thrown in the previous block!
                // So kill everything if one is detected. 
                // Makes debugging easier.
                closeSelectorAndChannels();
                return;
                }
            }
        }

    private void handleKey(final SelectionKey sk)
        {
        LOG.trace("Handling key...");
        
        // Obtain the interest of the key
        //final int readyOps = sk.readyOps();
        
        // Disable the interest for the operation that is ready.
        // This prevents the same event from being raised multiple times.
        //sk.interestOps(sk.interestOps() & ~readyOps);
        final SelectorHandler handler = (SelectorHandler) sk.attachment();

        LOG.trace("Found attached handler: "+handler);
        handler.handleKey(sk);
        }
        
    /**
     * Closes all channels registered with the selector. Used to
     * clean up when the selector dies and cannot be recovered.
     */
    private void closeSelectorAndChannels()
        {
        LOG.warn("Closing selector...");
        final Set keys = this.m_selector.keys();
        for (final Iterator iter = keys.iterator(); iter.hasNext();)
            {
            final SelectionKey key = (SelectionKey) iter.next();
            try
                {
                key.channel().close();
                }
            catch (final IOException e)
                {
                // Ignore.
                LOG.warn("Could not close channel", e);
                }
            }
        try
            {
            this.m_selector.close();
            }
        catch (final IOException e)
            {
            // Ignore.
            LOG.warn("Could not close selector", e);
            }
        }
    
    public void close()
        {
        LOG.warn("Closing selector!");
        requestClose();
        closeSelectorAndChannels();
        }

    public void start() throws IOException
        {
        // Ignore the call if the selector has already started.
        if (this.m_selector != null)
            {
            return;
            }
        this.m_selector = Selector.open();
        this.m_selectorThread = 
            new Thread(this, "Selector-Thread-"+this.m_name+"-"+hashCode());
        this.m_selectorThread.setDaemon(true);
        this.m_selectorThread.start();
        }
    }