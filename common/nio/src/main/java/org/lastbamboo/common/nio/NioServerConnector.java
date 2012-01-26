package org.lastbamboo.common.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.protocol.ReadWriteConnector;
import org.lastbamboo.common.protocol.ReadWriteConnectorListener;
import org.lastbamboo.common.protocol.ServerConnector;

/**
 * Uses NIO to connect to servers.  This notifies listeners when the server
 * connection is established.
 */
public final class NioServerConnector implements ServerConnector
    {

    /**
     * Logger for this class.
     */
    private static final Log LOG = 
        LogFactory.getLog(NioServerConnector.class);
    
    /**
     * The class that manages NIO select events.
     */
    private final SelectorManager m_selectorManager;
    
    /**
     * Creates a new class for connecting to a TURN server.
     * 
     * @param selector The selector for registering for connect events.
     */
    public NioServerConnector(final SelectorManager selector)
        {
        this.m_selectorManager = selector;        
        }
    
    public void connect(final Collection servers, 
        final ReadWriteConnectorListener listener)
        {
        CollectionUtils.forAllDo(servers, 
            new ConnectClosure(this.m_selectorManager, listener));
        }
    
    /**
     * Utility class that performs the connection for each server.
     */
    private static final class ConnectClosure implements Closure
        {
        
        private final ReadWriteConnectorListener m_connectionListener;
        private final SelectorManager m_selectorManager;

        private ConnectClosure(final SelectorManager manager, 
            final ReadWriteConnectorListener listener)
            {
            this.m_selectorManager = manager;
            this.m_connectionListener = listener;
            }

        public void execute(final Object obj)
            {
            final InetSocketAddress socketAddress = (InetSocketAddress) obj;
            LOG.trace("Connecting to: "+socketAddress);
            final ReadWriteConnector connector =
                new ReadWriteConnectorImpl(this.m_selectorManager, 
                    socketAddress, this.m_connectionListener);
            try
                {
                connector.connect();
                }
            catch (final IOException e)
                {
                LOG.warn("Could not connect to server: "+socketAddress, e);
                this.m_connectionListener.onConnectFailed(socketAddress);
                }
            }
    
        }

    }
