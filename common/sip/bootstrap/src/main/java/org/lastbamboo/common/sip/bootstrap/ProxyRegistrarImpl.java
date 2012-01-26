package org.lastbamboo.common.sip.bootstrap;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.offer.answer.OfferAnswer;
import org.lastbamboo.common.offer.answer.OfferAnswerFactory;
import org.lastbamboo.common.offer.answer.OfferAnswerListener;
import org.lastbamboo.common.sip.client.CrlfDelayCalculator;
import org.lastbamboo.common.sip.client.DefaultCrlfDelayCalculator;
import org.lastbamboo.common.sip.client.SipClient;
import org.lastbamboo.common.sip.client.SipClientImpl;
import org.lastbamboo.common.sip.client.SipClientTracker;
import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.lastbamboo.common.sip.stack.IdleSipSessionListener;
import org.lastbamboo.common.sip.stack.message.SipMessageFactory;
import org.lastbamboo.common.sip.stack.transaction.client.SipTransactionTracker;
import org.lastbamboo.common.sip.stack.transport.SipTcpTransportLayer;
import org.lastbamboo.common.sip.stack.util.UriUtils;

/**
 * An implementation of the proxy registrar interface.
 */
public final class ProxyRegistrarImpl implements ProxyRegistrar
    {
    /**
     * The log for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger (ProxyRegistrarImpl.class);

    /**
     * The client to register.
     */
    private final URI m_client;

    /**
     * The client to register.
     */
    private final URI m_proxy;

    /**
     * The listener to be notified of registration events.
     */
    private final ProxyRegistrationListener m_listener;

    private final SipMessageFactory m_messageFactory;

    private final SipClientTracker m_sipClientTracker;

    private final UriUtils m_uriUtils;

    private final SipTransactionTracker m_transactionTracker;

    private final SipTcpTransportLayer m_transportLayer;

    private final OfferAnswerFactory m_offerAnswerFactory;

    private final OfferAnswerListener m_offerAnswerListener;

    private final IdleSipSessionListener m_idleSipSessionListener;

    /**
     * Creates a new class for registering with an individual SIP proxy.
     * 
     * @param uriUtils Utilities for manipulating SIP URIs.
     * @param client The URI of the client.
     * @param proxy The URI of the proxy to connect to.
     * @param listener The listener for registration events, necessary for 
     * initiating new registrations as necessary.
     * @param messageFactory The factory for creating new SIP messages.
     * @param transportLayer The class for actually sending SIP messages to 
     * the transport layer.
     * @param transactionTracker The class for keeping track of SIP client
     * transactions.
     * @param offerAnswerFactory The class for creating {@link OfferAnswer}
     * instances capable of processing offers and answers for an offer/answer
     * protocol.
     * @param offerAnswerListener The listener for offer/answer events.
     * @param clientTracker The class for keeping track of SIP clients.
     * @param idleSipSessionListener Listener for idle SIP sessions.
     */
    public ProxyRegistrarImpl (final UriUtils uriUtils, final URI client,
        final URI proxy, final ProxyRegistrationListener listener,
        final SipMessageFactory messageFactory,
        final SipTcpTransportLayer transportLayer,
        final SipTransactionTracker transactionTracker,
        final OfferAnswerFactory offerAnswerFactory,
        final OfferAnswerListener offerAnswerListener,
        final SipClientTracker clientTracker, 
        final IdleSipSessionListener idleSipSessionListener)
        {
        this.m_client = client;
        this.m_proxy = proxy;
        this.m_listener = listener;
        this.m_uriUtils = uriUtils;
        this.m_messageFactory = messageFactory;
        this.m_offerAnswerFactory = offerAnswerFactory;
        this.m_offerAnswerListener = offerAnswerListener;
        this.m_sipClientTracker = clientTracker;
        this.m_transactionTracker = transactionTracker;
        this.m_transportLayer = transportLayer;
        this.m_idleSipSessionListener = idleSipSessionListener;
        }

    /**
     * {@inheritDoc}
     */
    public void register ()
        {
        final CrlfDelayCalculator calculator = new DefaultCrlfDelayCalculator();
        try
            {
            final SipClient client = 
                new SipClientImpl(this.m_client, this.m_proxy, 
                    this.m_messageFactory, this.m_transactionTracker, 
                    this.m_offerAnswerFactory, this.m_offerAnswerListener,
                    this.m_uriUtils, this.m_transportLayer, 
                    this.m_sipClientTracker, calculator,
                    this.m_idleSipSessionListener);
            
            client.connect();
            client.register();
            
            
            LOG.debug("Adding SIP client!!");
            this.m_sipClientTracker.addSipClient(client, this.m_listener);
            this.m_listener.registered(this.m_client, this.m_proxy);
            }
        catch (final IOException e)
            {
            // This will frequently happen when, for example, the user has
            // lost his or her network connection.
            LOG.debug("Could not register!!", e);
            this.m_listener.registrationFailed(this.m_client, this.m_proxy);
            }
        }
    }
