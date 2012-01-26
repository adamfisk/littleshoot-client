package org.lastbamboo.common.ice;

import java.net.InetSocketAddress;

import org.lastbamboo.common.stun.client.UdpStunClient;
import org.lastbamboo.common.stun.stack.StunConstants;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.util.SrvCandidateProvider;
import org.littleshoot.util.SrvUtil;
import org.littleshoot.util.SrvUtilImpl;

import junit.framework.TestCase;

public class IceUdpStunClientTest extends TestCase
    {

    public void testClient() throws Exception
        {
        final SrvUtil srv = new SrvUtilImpl();
        final CandidateProvider<InetSocketAddress> stunCandidateProvider =
            new SrvCandidateProvider(srv, "_stun._udp.littleshoot.org", 
                new InetSocketAddress("stun.littleshoot.org", 
                    StunConstants.STUN_PORT));
        final UdpStunClient stunClient = 
            new UdpStunClient(stunCandidateProvider);
        stunClient.connect();
        assertNotNull("null host address", stunClient.getHostAddress());
        assertNotNull("null server reflexive address", 
            stunClient.getServerReflexiveAddress());
        assertNotNull("Null STUN server address", 
            stunClient.getStunServerAddress());
        }
    }
