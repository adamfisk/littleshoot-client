package org.lastbamboo.common.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.protocol.ReadWriteConnector;
import org.lastbamboo.common.protocol.ReadWriteConnectorListener;
import org.lastbamboo.common.protocol.ReaderWriter;

/**
 * Utility class that performs an NIO connection but that also generates a 
 * handler for reading and writing instead of simply notifying listeners of the
 * raw socket channel for the new connection.  This frees callers from having 
 * to generate read/write handlers on their own.
 */
public final class ReadWriteConnectorImpl implements ReadWriteConnector, 
    ConnectionListener
    {

    /**
     * Logger for this class.
     */
    private static final Log LOG = 
        LogFactory.getLog(ReadWriteConnectorImpl.class);
    
    /**
     * The address of the remote endpoint.
     */
    private final InetSocketAddress m_remoteAddress;

    /**
     * The m_listener for the callback events.
     */
    private final ReadWriteConnectorListener m_listener;

    private final Connector m_connector;

    private final SelectorManager m_selectorManager;

    /**
     * Creates a new instance.  
     * 
     * @param remoteAddress The remote endpoint to connect to.
     * @param listener The listener for connection events.
     * @param selectorManager The manager to use.
     */
    public ReadWriteConnectorImpl(final SelectorManager selectorManager, 
        final InetSocketAddress remoteAddress, 
        final ReadWriteConnectorListener listener)
        {
        this.m_connector = 
            new ConnectorImpl(selectorManager, remoteAddress, this);
        this.m_selectorManager = selectorManager;
        this.m_remoteAddress = remoteAddress;
        this.m_listener = listener;
        }
    
    public void connect() throws IOException
        {
        this.m_connector.connect();
        }

    public void onConnect(final SocketChannel sc)
        {
        LOG.trace("Received connection...");
        try
            {
            final ReaderWriter readerWriter = 
                new NioReaderWriter(sc, this.m_selectorManager);
            this.m_listener.onConnect(readerWriter);
            }
        catch (final IOException e)
            {
            LOG.debug("Could not connect...", e);
            onConnectFailed(this.m_remoteAddress);
            }
        }

    public void onConnectFailed(final InetSocketAddress socketAddress)
        {
        this.m_listener.onConnectFailed(socketAddress);
        }

    }
