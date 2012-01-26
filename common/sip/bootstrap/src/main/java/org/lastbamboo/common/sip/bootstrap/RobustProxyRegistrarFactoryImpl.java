package org.lastbamboo.common.sip.bootstrap;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.lastbamboo.common.sip.stack.util.UriUtils;
import org.littleshoot.util.CandidateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the robust proxy registrar factory implementation.
 */
public final class RobustProxyRegistrarFactoryImpl
    implements RobustProxyRegistrarFactory
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * The default transport to use to connect to this host.
     */
    private static final String DEFAULT_TRANSPORT = "tcp";

    private final UriUtils m_uriUtils;
    
    /**
     * The candidate provider that provides candidate registrars for
     * registration.
     */
    private final CandidateProvider<URI> m_candidateProvider;

    /**
     * The registrar factory that provides registrars for single registrations.
     */
    private final ProxyRegistrarFactory m_registrarFactory;

    /**
     * Constructs a new robust proxy registrar factory.
     * 
     * @param uriUtils Utilities for creating SIP URIs.
     * @param sipCandidateProvider The candidate provider that provides candidate 
     * registrars for registration.
     * @param registrarFactory The registrar factory that provides registrars 
     * for single registrations.
     */
    public RobustProxyRegistrarFactoryImpl (final UriUtils uriUtils,
        final CandidateProvider<InetSocketAddress> sipCandidateProvider,
        final ProxyRegistrarFactory registrarFactory)
        {
        this.m_uriUtils = uriUtils;
        this.m_candidateProvider = new CandidateProvider<URI>() 
            {
            public URI getCandidate() 
                {
                final Collection<URI> candidates = getCandidates();
                if (candidates.isEmpty()) return null;
                return candidates.iterator().next();
                }

            public Collection<URI> getCandidates() 
                {
                m_log.debug("Accessing SIP servers...");
                final Collection<URI> candidates = new LinkedList<URI>();
                
                final Collection<InetSocketAddress> addresses =
                    sipCandidateProvider.getCandidates();
                
                m_log.info("Connecting to servers: {}", addresses);
                
                for (final InetSocketAddress isa : addresses)
                    {
                    m_log.info("Processing candidate address: {}", isa);
                    final InetAddress address = isa.getAddress();
                    
                    // The URI we are given is the public address (and SIP port) of
                    // this host. To convert to the URI of the proxy we are running,
                    // we replace the port with the proxy port.
                    final String host = address.getHostAddress();
                
                    final URI uri = m_uriUtils.getSipUri(host, isa.getPort(), 
                        DEFAULT_TRANSPORT);
                    candidates.add(uri);
                    }
                
                return candidates;
                }
            };
            
        this.m_registrarFactory = registrarFactory;
        }

    /**
     * {@inheritDoc}
     */
    public RobustProxyRegistrar getRegistrar (final URI client,
        final ProxyRegistrationListener listener)
        {
        return (new RobustProxyRegistrarImpl (client, 
            this.m_candidateProvider, this.m_registrarFactory, listener));
        }
    }
