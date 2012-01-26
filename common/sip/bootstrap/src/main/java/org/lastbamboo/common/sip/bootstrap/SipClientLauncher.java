package org.lastbamboo.common.sip.bootstrap;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.offer.answer.NoAnswerException;
import org.lastbamboo.common.offer.answer.OfferAnswerFactory;
import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
import org.lastbamboo.common.p2p.DefaultTcpUdpSocket;
import org.lastbamboo.common.p2p.P2PClient;
import org.lastbamboo.common.p2p.TcpUdpSocket;
import org.lastbamboo.common.sip.client.SipClient;
import org.lastbamboo.common.sip.client.SipClientTracker;
import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.lastbamboo.common.sip.stack.SipUriFactory;

/**
 * This class kicks off all SIP client services.
 */
public final class SipClientLauncher implements P2PClient
    {

    /**
     * The log for this class.
     */
    private static final Logger LOG = 
        LoggerFactory.getLogger (SipClientLauncher.class);

    /**
     * The factory used to create SIP URIs.
     */
    private final SipUriFactory m_sipUriFactory;

    /**
     * The object for maintaining a registration with a SIP proxy.
     */
    private final RobustProxyRegistrarFactory m_registrarFactory;

    private final SipClientTracker m_sipClientTracker;

    private final OfferAnswerFactory m_offerAnswerFactory;

    private final int m_relayWaitTime;

    /**
     * Launches a SIP client.
     * 
     * @param sipClientTracker Keeps track of SIP clients.
     * @param registrarFactory The object for maintaining a registration with a 
     * SIP proxy.
     * @param sipUriFactory The factory for creating SIP URIs from user IDs.
     * @param offerAnswerFactory Factory for creating offers and answers.
     * @param relayWaitTime The time to wait before using a relay.
     */
    public SipClientLauncher(final SipClientTracker sipClientTracker, 
        final RobustProxyRegistrarFactory registrarFactory,
        final SipUriFactory sipUriFactory, 
        final OfferAnswerFactory offerAnswerFactory, final int relayWaitTime)
        {
        this.m_sipClientTracker = sipClientTracker;
        this.m_registrarFactory = registrarFactory;
        this.m_sipUriFactory = sipUriFactory;
        this.m_offerAnswerFactory = offerAnswerFactory;
        this.m_relayWaitTime = relayWaitTime;
        }

    /**
     * Registers a given user ID with SIP proxies so that other people can
     * connect to her.
     *
     * @param userId The identifier of the user to register.
     */
    public void register(final long userId)
        {
        LOG.debug("Registering...");
        // Set up the URI used as the 'From' for SIP messages.
        final URI sipUri = m_sipUriFactory.createSipUri (userId);
        register(sipUri);
        }

    /**
     * Registers a given user ID with SIP proxies so that other people can
     * connect to her.
     *
     * @param userId The identifier of the user to register.
     */
    public void register(final URI sipUri) 
        {
        // Register with the SIP network.
        final RobustProxyRegistrar registrar =
            m_registrarFactory.getRegistrar
                (sipUri, new NoOpRegistrationListener());

        registrar.register ();
        }

    /**
     * Registers a given user ID with SIP proxies so that other people can
     * connect to her.
     *
     * @param userId The identifier of the user to register.
     */
    public void register(final String id)
        {
        LOG.debug("Registering...");
        // Set up the URI used as the 'From' for SIP messages.
        final URI sipUri = m_sipUriFactory.createSipUri (id);
        register(sipUri);
        }
    
    private static final class NoOpRegistrationListener 
        implements ProxyRegistrationListener
        {

        public void reRegistered(URI client, URI proxy)
            {
            LOG.debug("Got re-registered");
            }

        public void registered(URI client, URI proxy)
            {
            LOG.debug("Got registered");
            }

        public void registrationFailed(URI client, URI proxy)
            {
            LOG.debug("Got registration failed.");
            }

        public void unregistered(URI client, URI proxy)
            {
            LOG.debug("Got unregistered");
            }
        
        }

    public void offer(final URI sipUri, final byte[] offer,
        final OfferAnswerTransactionListener transactionListener) 
        {
        LOG.error("Offer not supported");
        throw new UnsupportedOperationException("Offer not supported");
        }

    public String login(final String user, final String password) 
        {
        LOG.error("Login not supported");
        throw new UnsupportedOperationException("Login not supported");
        }
    
    public String login(final String user, final String password, 
        final String id) throws IOException 
        {
        LOG.error("Login not supported");
        throw new UnsupportedOperationException("Login not supported");
        }

    public Socket newSocket (final URI sipUri) throws IOException, 
        NoAnswerException
        {
        LOG.trace ("Creating SIP socket for URI: {}", sipUri);
        final SipClient client = this.m_sipClientTracker.getSipClient();
        if (client == null)
            {
            LOG.warn("No available SIP clients!!");
            throw new IOException (
                "No available connections to SIP proxies!!");
            }
        
        final TcpUdpSocket tcpUdpSocket = 
            new DefaultTcpUdpSocket(client, this.m_offerAnswerFactory,
                this.m_relayWaitTime);
        
        return tcpUdpSocket.newSocket(sipUri);
        }

    }
