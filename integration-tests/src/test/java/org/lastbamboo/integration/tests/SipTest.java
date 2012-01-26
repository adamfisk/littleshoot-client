package org.lastbamboo.integration.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lastbamboo.common.offer.answer.IceMediaStreamDesc;
import org.lastbamboo.common.offer.answer.OfferAnswer;
import org.lastbamboo.common.offer.answer.OfferAnswerConnectException;
import org.lastbamboo.common.offer.answer.OfferAnswerFactory;
import org.lastbamboo.common.offer.answer.OfferAnswerListener;
import org.lastbamboo.common.offer.answer.OfferAnswerMessage;
import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
import org.lastbamboo.common.online.OnlineStatusUpdater;
import org.lastbamboo.common.online.RobustOnlineStatusUpdater;
import org.lastbamboo.common.sdp.api.SdpFactory;
import org.lastbamboo.common.sdp.api.SessionDescription;
import org.lastbamboo.common.sip.client.CrlfDelayCalculator;
import org.lastbamboo.common.sip.client.SipClient;
import org.lastbamboo.common.sip.client.SipClientImpl;
import org.lastbamboo.common.sip.client.SipClientTrackerImpl;
import org.lastbamboo.common.sip.proxy.LastBambooLocationService;
import org.lastbamboo.common.sip.proxy.LocationService;
import org.lastbamboo.common.sip.proxy.LocationServiceChain;
import org.lastbamboo.common.sip.proxy.SipConstants;
import org.lastbamboo.common.sip.proxy.SipProxyImpl;
import org.lastbamboo.common.sip.proxy.SipRegistrarImpl;
import org.lastbamboo.common.sip.proxy.SipRequestAndResponseForwarder;
import org.lastbamboo.common.sip.proxy.SipRequestForwarder;
import org.lastbamboo.common.sip.proxy.stateless.ExternalDomainForwarder;
import org.lastbamboo.common.sip.proxy.stateless.StatelessSipProxy;
import org.lastbamboo.common.sip.proxy.stateless.UnregisteredUriForwarder;
import org.lastbamboo.common.sip.stack.SipUriFactory;
import org.lastbamboo.common.sip.stack.message.SipMessageFactoryImpl;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactoryImpl;
import org.lastbamboo.common.sip.stack.transaction.client.SipTransactionFactory;
import org.lastbamboo.common.sip.stack.transaction.client.SipTransactionFactoryImpl;
import org.lastbamboo.common.sip.stack.transaction.client.SipTransactionTrackerImpl;
import org.lastbamboo.common.sip.stack.transport.SipTcpTransportLayerImpl;
import org.lastbamboo.common.sip.stack.util.UriUtilsImpl;
import org.lastbamboo.common.sipturn.OnlineStatusRegistrationListener;
import org.lastbamboo.integration.tests.stubs.OfferAnswerStub;
import org.littleshoot.util.NetworkUtils;
import org.littleshoot.util.SessionSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration test for SIP client and server.
 */
public class SipTest 
    {

    private static final Logger LOG = LoggerFactory.getLogger(SipTest.class);
    
    private static final int NUM_INVITES = 40;

    private static SipRegistrarImpl s_registrar;

    private static SipProxyImpl s_sipProxy;

    private static SipMessageFactoryImpl s_messageFactory;

    private static SipTransactionTrackerImpl s_transactionTracker;

    private static SipTcpTransportLayerImpl s_transportLayer;

    private static UriUtilsImpl s_uriUtils;

    private static SipClientTrackerImpl s_sipClientTracker;
    
    @BeforeClass public static void establishTest() throws Exception
        {
        LOG.debug("Starting test...");
        s_uriUtils = new UriUtilsImpl();
        s_sipClientTracker = new SipClientTrackerImpl();
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        s_messageFactory = new SipMessageFactoryImpl();
        s_transactionTracker = 
            new SipTransactionTrackerImpl();
        final SipTransactionFactory transactionFactory = 
            new SipTransactionFactoryImpl(s_transactionTracker, s_messageFactory, 500);
        s_transportLayer = 
            new SipTcpTransportLayerImpl(transactionFactory, headerFactory, 
                    s_messageFactory);
        s_registrar = 
            new SipRegistrarImpl(s_messageFactory, s_transportLayer);
        final LocationService locationService = 
            new LocationServiceChain(
                Arrays.asList(new LastBambooLocationService(s_uriUtils)));
        final SipRequestForwarder unregisteredUriForwarder =
            new UnregisteredUriForwarder(locationService, s_transportLayer, 
                s_uriUtils, s_messageFactory, s_registrar);
        final SipRequestForwarder externalDomainForwarder =
            new ExternalDomainForwarder();
        final SipRequestAndResponseForwarder forwarder = 
            new StatelessSipProxy(s_transportLayer, s_registrar, 
                unregisteredUriForwarder, externalDomainForwarder, 
                s_uriUtils, s_messageFactory);
        s_sipProxy = 
            new SipProxyImpl(forwarder, s_registrar, headerFactory, s_messageFactory, s_transportLayer);
        }
    
    @AfterClass public static void afterTest() throws Exception
        {
        LOG.debug("Test complete!");
        }
    
    @Test public void testAllSipStacks() throws Exception
        {
        LOG.debug("Starting test...");
        startSipServerThread();
        
        // Wait a second for the server to start. We can't lock here
        // for now.
        Thread.sleep(400);
        
        

        final long userId1 = 111111L;
        final long userId2 = 222222L;
        
        final SipClient client1 = createSipClient(userId1);
        final SipClient client2 = createSipClient(userId2);
        
        final URI uri1 = SipUriFactory.createSipUri(userId1);
        final URI uri2 = SipUriFactory.createSipUri(userId2);
        final SdpFactory sdpFactory = SdpFactory.getInstance();
        final SessionDescription sdp = sdpFactory.createSessionDescription();
        final TestSipTransactionListener stl1 = 
            new TestSipTransactionListener();
        final TestSipTransactionListener stl2 = 
            new TestSipTransactionListener();
        
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < NUM_INVITES; i++)
            {
            client1.writeCrlfKeepAlive();
            client1.offer(uri2, sdp.toBytes(), stl1, null);
            client1.writeCrlfKeepAlive();
            //client2.writeCrlfKeepAlive();
            client2.offer(uri1, sdp.toBytes(), stl2, null);
            Thread.sleep((long) (Math.random() * 200));
            assertEquals(0, stl1.getTransactionsFailed());
            assertEquals(0, stl2.getTransactionsFailed());
            }
        
        synchronized (this)
            {
            while (stl1.getTransactionsSucceeded() < NUM_INVITES ||
                stl2.getTransactionsSucceeded() < NUM_INVITES)
                {
                assertEquals("\n"+
                    "Transactions succeeded: "+stl1.getTransactionsSucceeded()+"\n"+
                    "Transactions failed:    "+stl1.getTransactionsFailed()+"\n"+
                    "Transactions total:     "+NUM_INVITES+"\n", 
                    0, stl1.getTransactionsFailed());
                assertEquals("\n"+
                    "Transactions succeeded: "+stl2.getTransactionsSucceeded()+"\n"+
                    "Transactions failed:    "+stl2.getTransactionsFailed()+"\n"+
                    "Transactions total:     "+NUM_INVITES+"\n", 
                    0, stl2.getTransactionsFailed());
                
                // Might be on slower systems, so wait awhile.  We'll get
                // notified, so it typically won't take this long.
                wait(2 * 1000);
                }
            }
        assertEquals(NUM_INVITES, stl1.getTransactionsSucceeded());
        assertEquals(NUM_INVITES, stl2.getTransactionsSucceeded());
        
        assertEquals(0, stl1.getTransactionsFailed());
        assertEquals(0, stl2.getTransactionsFailed());
        
        final long endTime = System.currentTimeMillis();
        final long totalTime = endTime - startTime;
        }

    private void startSipServerThread() throws IOException {

        final OnlineStatusUpdater updater = new RobustOnlineStatusUpdater();
        updater.setUpdateActive(false);

        final OnlineStatusRegistrationListener listener = 
            new OnlineStatusRegistrationListener(updater);
        s_registrar.addRegistrationListener(listener);

        s_sipProxy.start();
        LOG.debug("Started server: " + s_sipProxy);
        LOG.debug("Loaded context...");
    }

    
    /**
     * Creates a SIP client for testing.
     * 
     * @return The test SIP client.
     * @throws Exception If any unexpected error occurs.
     */
    private SipClient createSipClient(final long userId) throws Exception
        {
        LOG.debug("Loaded contexts...");
        
        final URI clientUri = SipUriFactory.createSipUri (userId);

        final InetAddress localHost = NetworkUtils.getLocalHost();
        final String localHostIp = localHost.getHostAddress();
        final URI proxyUri = 
            new URI("sip:"+localHostIp+":"+SipConstants.SIP_PORT+";transport=tcp");

        // We're testing SIP here, not ICE or STUN or TURN.  So just create
        // some dummy SDP for answers to offers.
        final String sdp =
           "v=0\r\n"+
           "o=jdoe 2890844526 2890842807 IN IP4 10.0.1.1\r\n"+
           "s=\r\n"+
           "c=IN IP4 192.0.2.3\r\n"+
           "t=0 0\r\n"+
           "a=ice-pwd:asd88fgpdd777uzjYhagZg\r\n"+
           "a=ice-ufrag:8hhY\r\n"+
           "m=audio 45664 RTP/AVP 0\r\n"+
           "b=RS:0\r\n"+
           "b=RR:0\r\n"+
           "a=rtpmap:0 PCMU/8000\r\n"+
           "a=candidate:1 1 UDP 2130706431 10.0.1.1 8998 typ host\r\n"+
           "a=candidate:2 1 UDP 1694498815 192.0.2.3 45664 typ srflx raddr\r\n"+
            "10.0.1.1 rport 8998\r\n";
        
        final byte[] sdpBytes = sdp.getBytes("US-ASCII");
        final OfferAnswerFactory offerAnswerFactory = new OfferAnswerFactory() {

            public OfferAnswer createAnswerer(OfferAnswerListener listener,
                final boolean useRelay)
                    throws OfferAnswerConnectException {
                return new OfferAnswerStub();
            }

            public OfferAnswer createOfferer(OfferAnswerListener arg0,
                    IceMediaStreamDesc arg1) throws OfferAnswerConnectException {
                return new OfferAnswerStub();
            }

            public int getMappedPort() {
                // TODO Auto-generated method stub
                return 0;
            }

            public boolean isAnswererPortMapped() {
                // TODO Auto-generated method stub
                return false;
            }
        };
            
        
        // Create a client that sense CRLF keep alive messages all the time
        // to make sure the server handles them successfully!
        final CrlfDelayCalculator calculator = new CrlfDelayCalculator() {
            public int calculateDelay() {
                // Send a bunch of double CRLF keep alives.
                return 75;
            }
        };

        final SessionSocketListener sl = new SessionSocketListener() {

            public void onSocket(String arg0, Socket arg1) throws IOException {
                // TODO Auto-generated method stub
                
            }
        };
            
        // OK for this to be a dummy address.
        final InetSocketAddress serverAddress = new InetSocketAddress(2894);
        final SipClient client = 
            new SipClientImpl(clientUri, proxyUri, 
                s_messageFactory, s_transactionTracker, 
                offerAnswerFactory, serverAddress, sl, s_uriUtils,
                s_transportLayer, s_sipClientTracker, calculator, null);
        
        client.connect();
        client.register();
        return client;
    }

    private class TestSipTransactionListener implements
            OfferAnswerTransactionListener {

        private int m_transactionsSucceeded;
        private int m_transactionsFailed;

        public void onTransactionFailed(final OfferAnswerMessage message) {
            this.m_transactionsFailed++;
            synchronized (SipTest.this) {
                SipTest.this.notify();
            }
        }

        public void onTransactionSucceeded(final OfferAnswerMessage message) {
            LOG.debug("Transaction succeeded!!");
            this.m_transactionsSucceeded++;
            if (this.m_transactionsSucceeded == SipTest.NUM_INVITES) {
                synchronized (SipTest.this) {
                    SipTest.this.notify();
                }
            }
        }

        public int getTransactionsFailed() {
            return m_transactionsFailed;
        }

        public int getTransactionsSucceeded() {
            return m_transactionsSucceeded;
        }
    }
}
