package org.lastbamboo.common.ice.sdp;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.lastbamboo.common.ice.IcePriorityCalculator;
import org.lastbamboo.common.ice.IceTransportProtocol;
import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidateType;
import org.lastbamboo.common.ice.candidate.IceCandidateVisitor;
import org.lastbamboo.common.ice.candidate.IceTcpActiveCandidate;
import org.lastbamboo.common.ice.candidate.IceTcpHostPassiveCandidate;
import org.lastbamboo.common.ice.candidate.IceTcpPeerReflexiveCandidate;
import org.lastbamboo.common.ice.candidate.IceTcpRelayPassiveCandidate;
import org.lastbamboo.common.ice.candidate.IceTcpServerReflexiveSoCandidate;
import org.lastbamboo.common.ice.candidate.IceUdpHostCandidate;
import org.lastbamboo.common.ice.candidate.IceUdpPeerReflexiveCandidate;
import org.lastbamboo.common.ice.candidate.IceUdpRelayCandidate;
import org.lastbamboo.common.ice.candidate.IceUdpServerReflexiveCandidate;
import org.littleshoot.util.mina.MinaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test for the class for creating ICE candidates.
 */
public class IceCandidateSdpDecoderTest extends TestCase 
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(IceCandidateSdpDecoderTest.class);
    
    /**
     * Tests the method for creating the candidates.
     * @throws Exception If there's any unexpected error.
     */
    public void testCreateCandidates() throws Exception
        {
        final String tcpLocalHostString = "192.168.1.6";
        final String tcpRelayString = "72.3.139.235";
        final String udpHostString = "69.203.29.241";
        final int tcpLocalPort = 8107;
        final int tcpPort = 54684;
        final int udpPort = 8107;
        final long udpHostPriority = 
            IcePriorityCalculator.calculatePriority(IceCandidateType.HOST, 
                IceTransportProtocol.UDP);
        final long tcpRelayPriority = 
            IcePriorityCalculator.calculatePriority(IceCandidateType.RELAYED, 
                IceTransportProtocol.TCP_PASS);
        final long tcpHostPriority = 
            IcePriorityCalculator.calculatePriority(IceCandidateType.HOST, 
                IceTransportProtocol.TCP_PASS);
        final String candidateString = 
            "v=0\r\n" +
            "o=- 0 0 IN IP4 192.168.2.34\r\n" +
            "s=-\r\n" +
            "t=0 0\r\n" +
            "m=message 8107 udp http\r\n" +
            "c=IN IP4 " + udpHostString + "\r\n" +
            "a=candidate:1 1 UDP "+udpHostPriority+" "+udpHostString+" "+ udpPort+" typ host\r\n" + 
            
            // TURN address
            "m=message 54684 tcp http\r\n" +
            "c=IN IP4 " + tcpRelayString + "\r\n" +
            "a=candidate:1 1 tcp-pass "+tcpRelayPriority+" "+tcpRelayString+" "+ tcpPort+" typ relay raddr 10.0.1.1 rport 8998\r\n" + 
            "a=setup:passive\r\n" +
            "a=connection:new\r\n" +
            
            // local address
            "m=message 8107 tcp http\r\n"+
            "c=IN IP4 "+ tcpLocalHostString + "\r\n"+
            "a=candidate:1 1 tcp-pass "+tcpHostPriority+" "+tcpLocalHostString+" "+ tcpLocalPort+" typ host\r\n" + 
            "a=setup:passive\r\n"+
            "a=connection:new";
        
        final IceCandidateSdpDecoder decoder = 
            new IceCandidateSdpDecoderImpl();
        final Collection<IceCandidate> candidates = 
            decoder.decode(MinaUtils.toBuf(candidateString), false);
        assertEquals("Unexpected number of candidates", 3, candidates.size());
        
        final TestIceCandidateVisitor visitor = new TestIceCandidateVisitor();
        visitor.visitCandidates(candidates);
        
        final Collection<IceCandidate> sortedCandidates = 
            visitor.m_candidates;
        
        final Iterator<IceCandidate> sortedIter = sortedCandidates.iterator();
        
        final IceCandidate c1 = sortedIter.next();
        final IceCandidate c2 = sortedIter.next();
        final IceCandidate c3 = sortedIter.next();
        
        assertTrue("Unexpected candidate "+c1+" with priority: "+c1.getPriority(), 
            (c1 instanceof IceTcpHostPassiveCandidate));
        assertTrue("Unexpected candidate "+c2+" with priority: "+c2.getPriority(), 
            (c2 instanceof IceUdpHostCandidate));
        assertTrue("Unexpected candidate "+c3+" with priority: "+c3.getPriority(), 
            (c3 instanceof IceTcpRelayPassiveCandidate));
        final IceTcpHostPassiveCandidate tcpLocalCandidate = 
            (IceTcpHostPassiveCandidate) c1;
        final IceUdpHostCandidate udpCandidate = 
            (IceUdpHostCandidate) c2;
        final IceTcpRelayPassiveCandidate tcpCandidate =
            (IceTcpRelayPassiveCandidate) c3;
        
        //inal Iterator tcpIter = tcpCandidates.iterator();

        //final IceTcpRelayPassiveCandidate tcpCandidate = 
          //  (IceTcpRelayPassiveCandidate) tcpIter.next();
        //final IceTcpHostPassiveCandidate tcpLocalCandidate = 
          //  (IceTcpHostPassiveCandidate) tcpIter.next();

        
        //final Iterator udpIter = udpCandidates.iterator();
        //final IceCandidate udpCandidate = (IceCandidate) udpIter.next();
        
        assertEquals("tcp-pass", tcpLocalCandidate.getTransport().getName());
        assertEquals("tcp-pass", tcpCandidate.getTransport().getName());
        assertEquals("udp", udpCandidate.getTransport().getName());
        
        final InetSocketAddress tcpLocalSocketAddress = 
            new InetSocketAddress(tcpLocalHostString, tcpLocalPort);
        final InetSocketAddress tcpSocketAddress = 
            new InetSocketAddress(tcpRelayString, tcpPort);
        final InetSocketAddress udpSocketAddress = 
            new InetSocketAddress(udpHostString, udpPort);
        
        assertEquals(tcpLocalSocketAddress, tcpLocalCandidate.getSocketAddress());
        assertEquals(tcpSocketAddress, tcpCandidate.getSocketAddress());
        assertEquals(udpSocketAddress, udpCandidate.getSocketAddress());
        }
    
    private final class TestIceCandidateVisitor 
        implements IceCandidateVisitor<Object>
        {
        
        private final Set<IceCandidate> m_candidates = 
            new TreeSet<IceCandidate>();

        public void visitCandidates(Collection<IceCandidate> candidates)
            {
            for (final IceCandidate candidate : candidates)
                {
                candidate.accept(this);
                }
            }

        public Object visitTcpActiveCandidate(IceTcpActiveCandidate candidate)
            {
            // TODO Auto-generated method stub
            return null;
            }

        public Object visitTcpHostPassiveCandidate(
            final IceTcpHostPassiveCandidate candidate)
            {
            LOG.debug("Adding candidate with priority: "+candidate.getPriority());
            this.m_candidates.add(candidate);
            return null;
            }

        public Object visitTcpRelayPassiveCandidate(
            final IceTcpRelayPassiveCandidate candidate)
            {
            LOG.debug("Adding candidate with priority: "+candidate.getPriority());
            this.m_candidates.add(candidate);
            return null;
            }

        public Object visitTcpServerReflexiveSoCandidate(IceTcpServerReflexiveSoCandidate candidate)
            {
            // TODO Auto-generated method stub
            return null;
            }

        public Object visitUdpHostCandidate(IceUdpHostCandidate candidate)
            {
            LOG.debug("Adding candidate with priority: "+candidate.getPriority());
            this.m_candidates.add(candidate);
            return null;
            }

        public Object visitUdpPeerReflexiveCandidate(IceUdpPeerReflexiveCandidate candidate)
            {
            // TODO Auto-generated method stub
            return null;
            }

        public Object visitUdpRelayCandidate(IceUdpRelayCandidate candidate)
            {
            // TODO Auto-generated method stub
            return null;
            }

        public Object visitUdpServerReflexiveCandidate(IceUdpServerReflexiveCandidate candidate)
            {
            // TODO Auto-generated method stub
            return null;
            }

        public Object visitTcpPeerReflexiveCandidate(IceTcpPeerReflexiveCandidate candidate)
            {
            // TODO Auto-generated method stub
            return null;
            }
    
        }

    }
