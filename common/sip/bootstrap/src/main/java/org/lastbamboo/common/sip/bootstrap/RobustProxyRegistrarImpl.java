package org.lastbamboo.common.sip.bootstrap;

import java.net.URI;

import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.util.ConnectionEstablisher;
import org.littleshoot.util.ConnectionMaintainer;
import org.littleshoot.util.ConnectionMaintainerImpl;
import org.littleshoot.util.Optional;

/**
 * A proxy registrar that attempts to be robust by maintaining registrations to
 * multiple SIP proxies.
 */
public class RobustProxyRegistrarImpl implements RobustProxyRegistrar
    {

    /**
     * The connection maintainer used to maintain connections to multiple
     * registrars.
     */
    private final ConnectionMaintainer<URI> m_connectionMaintainer;

    /**
     * Constructs a new robust proxy registrar.
     *
     * @param client The client to register.
     * @param candidateProvider The candidate provider that provides candidate 
     *  registrars for registration.
     * @param registrarFactory The registrar factory that provides registrars 
     *  for single registrations.
     * @param listener The listener to be notified of registration events.
     */
    public RobustProxyRegistrarImpl (final URI client,
        final CandidateProvider<URI> candidateProvider,
        final ProxyRegistrarFactory registrarFactory,
        final ProxyRegistrationListener listener)
        {
        final ConnectionEstablisher<URI,URI> establisher =
            new RegistrarConnectionEstablisher (client, registrarFactory,
                listener);

        this.m_connectionMaintainer =
            new ConnectionMaintainerImpl<URI,URI> (establisher, 
                candidateProvider, 1);
        }

    /**
     * {@inheritDoc}
     */
    public void register ()
        {
        this.m_connectionMaintainer.start ();
        }

    /**
     * {@inheritDoc}
     */
    public Optional<URI> mostRecentlyActive ()
        {
        return this.m_connectionMaintainer.getMostRecentlyActive ();
        }
    }
