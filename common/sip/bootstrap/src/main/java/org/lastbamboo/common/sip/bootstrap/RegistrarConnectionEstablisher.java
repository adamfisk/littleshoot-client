package org.lastbamboo.common.sip.bootstrap;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.littleshoot.util.ConnectionEstablisher;
import org.littleshoot.util.ConnectionMaintainerListener;

/**
 * The connection establisher used to establish connections with SIP
 * registrars.
 */
public final class RegistrarConnectionEstablisher
    implements ConnectionEstablisher<URI,URI>
    {
    /**
     * The log for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger (getClass());

    /**
     * The client to register.
     */
    private final URI m_client;

    /**
     * The registrar factory that provides registrars for single registrations.
     */
    private final ProxyRegistrarFactory m_registrarFactory;

    /**
     * The listener to be notified of registration events.
     */
    private final ProxyRegistrationListener m_listener;

    /**
     * The map of registrars to the connection maintainer listeners listening
     * for events on those registrars.
     */
    private final Map<URI,ConnectionMaintainerListener<URI>>
        m_connectionMaintainerListeners;

    /**
     * The listener we use to listen for registration events.
     */
    private final ProxyRegistrationListener m_myListener;

    /**
     * Constructs a new registrar connection establisher.
     *
     * @param client The client to register.
     * @param registrarFactory The registrar factory that provides registrars 
     * for single registrations.
     * @param listener The listener to be notified of registration events.
     */
    public RegistrarConnectionEstablisher (final URI client,
        final ProxyRegistrarFactory registrarFactory,
        final ProxyRegistrationListener listener)
        {
        this.m_client = client;
        this.m_registrarFactory = registrarFactory;
        this.m_listener = listener;

        this.m_connectionMaintainerListeners =
                new HashMap<URI,ConnectionMaintainerListener<URI>> ();
        
        this.m_myListener = new MyListener ();
        }

    /**
     * The registration listener that is notified of single registrations.
     */
    private class MyListener implements ProxyRegistrationListener
        {
        public void registered (final URI client, final URI proxy)
            {
            final ConnectionMaintainerListener<URI> connectionMaintainerListener =
                RegistrarConnectionEstablisher.this.getConnectionMaintainerListener (proxy);
            connectionMaintainerListener.connected (proxy);
            RegistrarConnectionEstablisher.this.m_listener.registered (client, proxy);
            }

        public void reRegistered (final URI client, final URI proxy)
            {
            final ConnectionMaintainerListener<URI> connectionMaintainerListener =
                RegistrarConnectionEstablisher.this.getConnectionMaintainerListener (proxy);
            connectionMaintainerListener.reconnected ();
            RegistrarConnectionEstablisher.this.m_listener.reRegistered (client, proxy);
            }

        public void registrationFailed (final URI client, final URI proxy)
            {
            final ConnectionMaintainerListener<URI> connectionMaintainerListener =
                    RegistrarConnectionEstablisher.this.getConnectionMaintainerListener (proxy);
            connectionMaintainerListener.connectionFailed ();
            RegistrarConnectionEstablisher.this.stopNotifying (proxy);
            RegistrarConnectionEstablisher.this.m_listener.registrationFailed (client, proxy);
            }

        public void unregistered (final URI client, final URI proxy)
            {
            final ConnectionMaintainerListener<URI> connectionMaintainerListener =
                RegistrarConnectionEstablisher.this.getConnectionMaintainerListener (proxy);

            connectionMaintainerListener.disconnected ();
            RegistrarConnectionEstablisher.this.stopNotifying (proxy);
            RegistrarConnectionEstablisher.this.m_listener.unregistered (client, proxy);
            }
        }

    /**
     * Signals us to start notifying a given connection maintainer listener of
     * connection events related to a given registrar.
     *
     * @param registrar The URI of the registrar.
     * @param listener The listener to notify.
     */
    private void startNotifying (final URI registrar,
        final ConnectionMaintainerListener<URI> listener)
        {
        synchronized (this.m_connectionMaintainerListeners)
            {
            // We should not yet have a listener for the given registrar.
            this.m_connectionMaintainerListeners.put (registrar, listener);
            }
        }

    /**
     * Signals us to stop notifying the connection maintainer listener of
     * connection events related to a given registrar.
     *
     * @param registrar The URI of the registrar.
     */
    private void stopNotifying (final URI registrar)
        {
        synchronized (this.m_connectionMaintainerListeners)
            {
            this.m_connectionMaintainerListeners.remove (registrar);
            }
        }

    /**
     * Returns the connection maintainer listener that is listening for
     * connection events related to a given registrar.
     *
     * @param registrar The URI of the registrar.
     *
     * @return The connection maintainer listener that is listening for 
     * connection events related to a given registrar.
     */
    private ConnectionMaintainerListener<URI> getConnectionMaintainerListener(
        final URI registrar)
        {
        return this.m_connectionMaintainerListeners.get (registrar);
        }

    /**
     * {@inheritDoc}
     */
    public void establish (final URI serverId,
        final ConnectionMaintainerListener<URI> listener)
        {
        LOG.debug ("Registering with: " + serverId);

        this.startNotifying (serverId, listener);

        try
            {
            final ProxyRegistrar registrar =
                this.m_registrarFactory.getRegistrar (this.m_client, serverId,
                                                 this.m_myListener);

            registrar.register ();
            }
        catch (final RuntimeException e)
            {
            LOG.warn("Could not either access the registrar or register", e);
            this.stopNotifying (serverId);

            throw (e);
            }
        }
    }
