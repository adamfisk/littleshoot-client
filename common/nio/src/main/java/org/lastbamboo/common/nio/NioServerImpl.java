package org.lastbamboo.common.nio;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts a generic, protocol-agnostic NIO server.  You can simply implement
 * a protocol handler to use this NIO server with your protocol.
 */
public final class NioServerImpl implements NioServer
    {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NioServerImpl.class);
    
    private final SelectorManager m_selectorManager;
    
    private final Acceptor m_acceptor;

    /**
     * Starts the server.
     * 
     * @param port The port where to listen for incoming connections.
     * @param selector The selector for processing sockets for this server.
     * @param acceptorListener The listener for newly accepted sockets.
     */
    public NioServerImpl(final int port, final SelectorManager selector,
        final AcceptorListener acceptorListener)
        {
        LOG.trace("Opening NIO server on port: "+port);
        this.m_selectorManager = selector;
        this.m_acceptor = new Acceptor(port, this.m_selectorManager, 
            acceptorListener);
        }

    public void startServer() throws IOException
        {
        this.m_selectorManager.start();
        this.m_acceptor.start();
        }

    public void close()
        {
        LOG.trace("Closing server...");
        this.m_acceptor.close();
        }
    }
