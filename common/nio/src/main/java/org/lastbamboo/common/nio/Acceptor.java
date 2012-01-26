package org.lastbamboo.common.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for incoming connections from clients, using a selector to receive
 * connect events. Therefore, instances of this class don't have an associated
 * thread. When a connection is established, it notifies a listener using a
 * callback.
 */
public final class Acceptor implements SelectorHandler
    {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Acceptor.class);
    
    /**
     * Channel for receiving incoming connections.
     */ 
    private ServerSocketChannel m_serverSocketChannel;

    /**
     * Class that manages the selector for network events.
     */
    private final SelectorManager m_selectorManager;

    /**
     * The port to listen on.
     */
    private final int m_listeningPort;

    /**
     * The listener for newly accepted sockets.
     */
    private final AcceptorListener m_acceptorListener;

    /**
     * Creates a new instance. No server socket is created. Use
     * openServerSocket() to start listening.
     * 
     * @param listeningPort The port to open.
     * @param selectorManager The m_selectorManager thread.
     * @param acceptorListener The listener for incoming sockets.
     */
    public Acceptor(final int listeningPort, 
        final SelectorManager selectorManager,
        final AcceptorListener acceptorListener)
        {
        this.m_selectorManager = selectorManager;
        this.m_listeningPort = listeningPort;
        this.m_acceptorListener = acceptorListener;
        }

    /**
     * Starts listening for incoming connections. This method does not block
     * waiting for connections. Instead, it registers itself with the selector
     * to receive connect events.
     * 
     * @throws IOException If we could not open the server socket.
     */
    private void openServerSocket() throws IOException
        {
        LOG.trace("Opening server socket on port: "+this.m_listeningPort);
        this.m_serverSocketChannel = ServerSocketChannel.open();
        final InetSocketAddress isa = 
            new InetSocketAddress(this.m_listeningPort);
        this.m_serverSocketChannel.socket().bind(isa, 100);

        // This method might be called from any thread. We must use
        // the xxxLater methods so that the actual register operation
        // is done by the selector's thread. No other thread should 
        // access the selector directly.
        this.m_selectorManager.registerChannelLater(this.m_serverSocketChannel, 
            SelectionKey.OP_ACCEPT, this);
        }

    /**
     * Called by the selector when the underlying server socket is ready to
     * accept a connection. This method should not be called from anywhere else.
     */
    private void handleAccept()
        {
        try
            {
            final SocketChannel sc = this.m_serverSocketChannel.accept();      
            this.m_acceptorListener.onAccept(sc);
            }
        catch (final IOException e)
            {
            LOG.warn("Could not accept socket", e);
            close();
            }
        }

    /**
     * Closes the socket. Returns only when the socket has been closed.
     */
    public void close()
        {
        LOG.debug("Closing acceptor!");
        
        // Must wait for the socket to be closed.
        this.m_selectorManager.invokeLater(new Runnable()
            {
            public void run()
                {
                try
                    {
                    LOG.trace("Closing server socket channel...");
                    m_serverSocketChannel.close();
                    LOG.trace("Closed server socket channel...");
                    }
                catch (final IOException e)
                    {
                    // Ignore.
                    LOG.warn("Could not close server socket channel", e);
                    }
                }
            });
        }

    public void handleKey(final SelectionKey sk)
        {
        if (sk.isAcceptable())
            {
            handleAccept();
            }
        }

    public void start() throws IOException
        {
        openServerSocket();
        }
    }