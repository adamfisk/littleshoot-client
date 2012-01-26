package org.lastbamboo.common.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manages a non-blocking connection attempt to a remote host.
 */
public final class ConnectorImpl implements Connector
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ConnectorImpl.class);

    /**
     * The socket being connected.
     */
    private SocketChannel m_socketChannel;

    /**
     * The address of the remote endpoint.
     */
    private final InetSocketAddress m_remoteAddress;

    /**
     * The m_selectorManager used for receiving events.
     */
    private final SelectorManager m_selectorManager;

    /**
     * The m_listener for the callback events.
     */
    private final ConnectionListener m_listener;

    /**
     * Creates a new instance.
     *
     * @param remoteAddress The remote endpoint to connect to.
     * @param listener The listener for connection events.
     * @param selectorManager The manager to use.
     */
    public ConnectorImpl(final SelectorManager selectorManager,
        final InetSocketAddress remoteAddress,
        final ConnectionListener listener)
        {
        this.m_selectorManager = selectorManager;
        this.m_remoteAddress = remoteAddress;
        this.m_listener = listener;
        }

    /* (non-Javadoc)
     * @see org.lastbamboo.shoot.nio.Connector#connect()
     */
    public void connect() throws IOException
        {
        this.m_socketChannel = SocketChannel.open();

        // Very important. Set to non-blocking. Otherwise a call
        // to connect will block until the connection attempt fails
        // or succeeds.
        this.m_socketChannel.configureBlocking(false);
        this.m_socketChannel.connect(this.m_remoteAddress);

        // Registers itself to receive the connect event.
        this.m_selectorManager.registerChannelLater(this.m_socketChannel,
            SelectionKey.OP_CONNECT, this);
        }

    /**
     * Called by the selector thread when the connection is ready to
     * be completed.
     */
    private void handleConnect()
        {
        try
            {
            if (!this.m_socketChannel.finishConnect())
                {
                LOG.warn("Did not finish connect -- not sure what to do...");
                return;
                }
            // Connection succeeded.
            LOG.trace("Connected successfully...");
            this.m_listener.onConnect(this.m_socketChannel);
            }
        catch (final IOException e)
            {
            // Could not connect.
            LOG.debug("Could not connect to '" + this.m_remoteAddress + "'", e);
            this.m_listener.onConnectFailed(this.m_remoteAddress);
            }
        }

    /* (non-Javadoc)
     * @see org.lastbamboo.shoot.nio.Connector#handleKey(java.nio.channels.SelectionKey)
     */
    public void handleKey(final SelectionKey sk)
        {
        if (sk.isConnectable())
            {
            // A connection is ready to be accepted.
            handleConnect();
            }
        }

    /* (non-Javadoc)
     * @see org.lastbamboo.shoot.nio.Connector#close()
     */
    public void close()
        {
        try
            {
            this.m_socketChannel.close();
            }
        catch (final IOException e)
            {
            // Ignore.
            LOG.debug("Exception closing socket", e);
            }
        }

    }
