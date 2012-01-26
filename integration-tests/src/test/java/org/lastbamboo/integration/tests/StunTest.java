package org.lastbamboo.integration.tests;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.littleshoot.mina.common.IoSession;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lastbamboo.common.stun.client.StunClient;
import org.lastbamboo.common.stun.client.UdpStunClient;
import org.lastbamboo.common.stun.server.StunServer;
import org.lastbamboo.common.stun.server.StunServerMessageVisitor;
import org.lastbamboo.common.stun.server.UdpStunServer;
import org.littleshoot.stun.stack.StunConstants;
import org.littleshoot.stun.stack.message.BindingRequest;
import org.littleshoot.stun.stack.message.StunMessageVisitor;
import org.littleshoot.stun.stack.message.StunMessageVisitorAdapter;
import org.littleshoot.stun.stack.message.StunMessageVisitorFactory;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.util.NetworkUtils;
import org.littleshoot.util.SrvCandidateProvider;
import org.littleshoot.util.SrvUtil;
import org.littleshoot.util.SrvUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the STUN client and server.
 */
public class StunTest
    {

    private static final Logger LOG = LoggerFactory.getLogger(StunTest.class);
    
    @BeforeClass public static void establishTest() throws Exception
        {
        LOG.debug("Starting test...");
        }
    
    @AfterClass public static void afterTest() throws Exception
        {
        LOG.debug("Test complete!");
        }
    
    /**
     * Tests STUN roundtrip with a test proxy server that ignores some 
     * messages to simulate UDP message loss.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    @Test
    public void testStun() throws Exception
        {
        launchServer(1);
        
        LOG.debug("Server started...running clients...");
        
        final InetAddress localStunServerAddress = NetworkUtils.getLocalHost();
        final SrvUtil srv = new SrvUtilImpl();
        final CandidateProvider<InetSocketAddress> stunCandidateProvider =
            new SrvCandidateProvider(srv, "", 
                new InetSocketAddress(localStunServerAddress, 
                    StunConstants.STUN_PORT));
        final InetSocketAddress isa = 
            new InetSocketAddress(localStunServerAddress, 3478); 
        final StunClient client = new UdpStunClient(stunCandidateProvider); 
        client.connect();
        
        final InetSocketAddress localAddress = client.getHostAddress();
        
        final InetSocketAddress mappedAddress = 
            client.getServerReflexiveAddress();
        LOG.debug("Got mapped address: "+mappedAddress);
        
        Assert.assertNotNull(mappedAddress);
        
        // The port here should just equal the local port because we're using
        // a locally-running server.
        final int port = mappedAddress.getPort();
        if (localAddress.getPort() == port)
            {
            LOG.debug("Got expected port for: "+port);
            }
        Assert.assertTrue("Unexpected port: "+port, localAddress.getPort() == port);
        }
    
    /**
     * Launches the server without filtering any requests.
     * @throws IOException 
     */
    private void launchServer() throws IOException
        {
        launchServer(0);
        }
    
    /**
     * Launches the server.
     * @throws IOException 
     */
    private void launchServer(final int requestsToFilter) throws IOException
        {
        final StunMessageVisitorFactory messageVisitorFactory = 
            new TestServerVisitoryFactory(requestsToFilter);
        final StunServer server = 
            new UdpStunServer(messageVisitorFactory, "STUN-Test-Server");
        server.start();
        }
    
    private static final class TestServerVisitoryFactory 
        implements StunMessageVisitorFactory
        {

        private final int m_requestsToFilter;

        private TestServerVisitoryFactory(final int requestsToFilter)
            {
            m_requestsToFilter = requestsToFilter;
            }

        public StunMessageVisitor createVisitor(final IoSession session)
            {
            LOG.debug("Creating new server message visitor!!");
            return new StunServerMessageVisitorProxy(m_requestsToFilter, session);
            }

        public StunMessageVisitor createVisitor(IoSession session, Object attachment)
            {
            return createVisitor(session);
            }
        
        }
    
    /**
     * Message visitor proxy class that simulates a server that doesn't 
     * respond to some messages.  This should be the same as UDP messages
     * being lost on the network, requiring the client to resend them.
     */
    private static final class StunServerMessageVisitorProxy 
        extends StunMessageVisitorAdapter<Object>
        implements StunMessageVisitor<Object>
        {
        
        private static int s_numBindingRequests = 0;
        private StunServerMessageVisitor m_proxiedVisitor;
        private final int m_requestsToFilter;

        private StunServerMessageVisitorProxy(final int requestsToFilter, 
            final IoSession session)
            {
            this.m_requestsToFilter = requestsToFilter;
            this.m_proxiedVisitor = new StunServerMessageVisitor(session);
            }

        public Object visitBindingRequest(final BindingRequest binding)
            {
            // Ignore the first few requests.
            LOG.debug("Received binding request!!");
            if (s_numBindingRequests > this.m_requestsToFilter)
                {
                this.m_proxiedVisitor.visitBindingRequest(binding);
                }
            s_numBindingRequests++;
            return null;
            }
        }
    }
