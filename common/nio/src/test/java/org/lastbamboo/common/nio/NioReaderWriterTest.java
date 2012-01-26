package org.lastbamboo.common.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.easymock.MockControl;
import org.lastbamboo.common.nio.AcceptorListener;
import org.lastbamboo.common.nio.NioReaderWriter;
import org.lastbamboo.common.nio.NioServer;
import org.lastbamboo.common.nio.NioServerImpl;
import org.lastbamboo.common.nio.SelectorManager;
import org.lastbamboo.common.nio.SelectorManagerImpl;
import org.lastbamboo.common.nio.stub.ProtocolHandlerStub;
import org.littleshoot.util.NetworkUtils;

/**
 * Tests the class for reading and writing data using NIO.
 */
public final class NioReaderWriterTest extends TestCase 
    implements AcceptorListener
    {

    /**
     * Logger for the test.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NioReaderWriterTest.class);
    
    private volatile int m_writes;

    private static final int EXPECTED_WRITES = 100;

    private SocketChannel m_socketChannel;
    
    /**
     * Tests the method for creating a reader/writer from a thread other than
     * the selector thread.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    
    public void testReaderWriterConstructorFromNonSelectorThread() 
        throws Exception
        {
        final SelectorManager selector1 = new SelectorManagerImpl("test1");
        selector1.start();
        
        final SelectorManager selector2 = new SelectorManagerImpl("test2");
        selector2.start();
        
        final int port = 4859;
        final NioServer server = new NioServerImpl(port, selector2, this);
        server.startServer();
        
        this.m_socketChannel = null;
        final Socket client = new Socket();
        client.setSoTimeout(6000);
        final InetSocketAddress host = 
            new InetSocketAddress(NetworkUtils.getLocalHost(), port);
        client.connect(host);
        
        synchronized(this)
            {
            if (this.m_socketChannel == null)
                {
                wait(6000);
                }
            }
        
        assertNotNull(this.m_socketChannel);
        assertFalse(this.m_socketChannel.isRegistered());
        
        new NioReaderWriter(this.m_socketChannel, selector1, 
            new ProtocolHandlerStub(), false);
        
        assertTrue(this.m_socketChannel.isRegistered());
        }

    /**
     * Tests the method for writing data from another thread while making 
     * sure there's free space.
     */
    public void testWriteLater() throws Exception
        {
        final SelectorProvider provider = new TestSelectorProvider();
        final SocketChannel channel = new TestSocketChannel(provider);
        final MockControl selectorControl =
            MockControl.createControl(SelectorManager.class);
        final SelectorManager selector = 
            (SelectorManager) selectorControl.getMock();
        
        selector.addChannelInterestLater(null, 3);
        selectorControl.setMatcher(MockControl.ALWAYS_MATCHER);
        selectorControl.setVoidCallable(MockControl.ONE_OR_MORE);
        selector.registerChannelNow(null, 3, null);
        selectorControl.setMatcher(MockControl.ALWAYS_MATCHER);
        selector.removeChannelInterestLater(channel, SelectionKey.OP_WRITE);
        selectorControl.setVoidCallable(1);
        selectorControl.replay();
        
        assertEquals(0, this.m_writes);
        
        final ByteBuffer buf = ByteBuffer.allocate(1);
        
        final NioReaderWriter rw = new NioReaderWriter(channel, selector);
        
        final Collection buffers = new LinkedList();
        for (int i = 0; i < EXPECTED_WRITES; i++)
            {
            buffers.add(buf);
            }
        
        LOG.trace("About to write..");
        rw.writeLater(buffers);
        
        
        LOG.trace("About to run threaded write...");
        threadedWrite(rw);

        rw.writeLater(buffers);
        
        synchronized (this)
            {
            if (this.m_writes < EXPECTED_WRITES)
                {
                wait(10000);
                }
            }
        assertEquals(EXPECTED_WRITES, this.m_writes);
        
        // We need to sleep because the call to disable writing on the 
        // selector happens *after* the call to channel writes, so we could
        // be notified before write interest has been disabled, which would
        // cause the selector control verification to break!
        Thread.sleep(500);
        selectorControl.verify();
        }

    private void threadedWrite(final NioReaderWriter rw)
        {
        final Runnable writeRunner = new Runnable()
            {
            public void run()
                {
                LOG.trace("Running write...");
                for (int i = 0; i < 100; i++)
                    {
                    try
                        {
                        Thread.sleep(100);
                        }
                    catch (final InterruptedException e)
                        {
                        // Should never happen.
                        }
                    rw.handleKey(new TestSelectionKey());
                    }
                
                }
            };
            
        LOG.trace("Starting thread...");
        final Thread writeThread = new Thread(writeRunner, "write-test-thread");
        writeThread.setDaemon(true);
        writeThread.start();
        }

    private static class TestSelectorProvider extends SelectorProvider
        {

        public DatagramChannel openDatagramChannel() throws IOException
            {
            // TODO Auto-generated method stub
            return null;
            }

        public Pipe openPipe() throws IOException
            {
            // TODO Auto-generated method stub
            return null;
            }

        public ServerSocketChannel openServerSocketChannel() throws IOException
            {
            // TODO Auto-generated method stub
            return null;
            }

        public SocketChannel openSocketChannel() throws IOException
            {
            // TODO Auto-generated method stub
            return null;
            }

        public AbstractSelector openSelector() throws IOException
            {
            // TODO Auto-generated method stub
            return null;
            }
    
        }
    
    private final class TestSocketChannel extends SocketChannel
        {

        protected TestSocketChannel(final SelectorProvider sp)
            {
            super(sp);
            }

        public boolean finishConnect() 
            {
            // TODO Auto-generated method stub
            return false;
            }

        public boolean isConnected()
            {
            // TODO Auto-generated method stub
            return false;
            }

        public boolean isConnectionPending()
            {
            // TODO Auto-generated method stub
            return false;
            }

        public Socket socket()
            {
            return new Socket();
            }

        public boolean connect(SocketAddress arg0)
            {
            // TODO Auto-generated method stub
            return false;
            }

        public int read(ByteBuffer arg0)
            {
            // TODO Auto-generated method stub
            return 0;
            }

        public int write(final ByteBuffer buf)
            {
            m_writes++;
            if (m_writes == EXPECTED_WRITES)
                {
                synchronized (NioReaderWriterTest.this)
                    {
                    NioReaderWriterTest.this.notifyAll();
                    }
                }
            buf.position(buf.limit());
            return buf.capacity();
            }

        public long read(ByteBuffer[] arg0, int arg1, int arg2)
            {
            // TODO Auto-generated method stub
            return 0;
            }

        public long write(ByteBuffer[] arg0, int arg1, int arg2) 
            {
            // TODO Auto-generated method stub
            return 0;
            }

        protected void implCloseSelectableChannel() 
            {
            // TODO Auto-generated method stub
            
            }

        protected void implConfigureBlocking(boolean arg0)
            {
            }
    
        }
       
    private static final class TestSelectionKey extends SelectionKey
        {
        
        public int interestOps()
            {
            LOG.trace("Accessing interest ops...");
            return OP_WRITE;
            }

        public int readyOps()
            {
            LOG.trace("Accessing ready ops...");
            return OP_WRITE;
            }

        public void cancel()
            {
            // TODO Auto-generated method stub
            
            }

        public boolean isValid()
            {
            return true;
            }

        public SelectableChannel channel()
            {
            // TODO Auto-generated method stub
            return null;
            }

        public SelectionKey interestOps(int arg0)
            {
            // TODO Auto-generated method stub
            return null;
            }

        public Selector selector()
            {
            // TODO Auto-generated method stub
            return null;
            }
    
        }
  
    
    public void onAccept(final SocketChannel sc)
        {
        LOG.trace("Accepted channel!!");
        this.m_socketChannel = sc;
        synchronized(this)
            {
            notifyAll();
            }
        }
    }
