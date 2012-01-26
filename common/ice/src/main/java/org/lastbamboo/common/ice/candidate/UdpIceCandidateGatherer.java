package org.lastbamboo.common.ice.candidate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;

import org.lastbamboo.common.ice.IceMediaStreamDesc;
import org.lastbamboo.common.stun.client.StunClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gathers UDP ICE candidates.
 */
public class UdpIceCandidateGatherer implements IceCandidateGatherer
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final StunClient m_iceUdpStunPeer;
    
    private final boolean m_controlling;

    private final IceMediaStreamDesc m_desc;

    private InetSocketAddress m_udpServerReflexiveAddress;

    /**
     * Creates a new class for gathering ICE candidates.
     * 
     * @param udpStunClient The client for connecting to the STUN server.  This
     * will also handle peer reflexive connectivity checks. 
     * @param controlling Whether or not this is the controlling agent at the
     * start of processing.
     * @param desc The description of the media stream to create.
     */
    public UdpIceCandidateGatherer(final StunClient udpStunClient,
            final boolean controlling, final IceMediaStreamDesc desc) {
        if (desc.isUdp() && udpStunClient == null) {
            throw new IllegalArgumentException("No UDP client with UDP active");
        }
        this.m_iceUdpStunPeer = udpStunClient;
        this.m_controlling = controlling;
        this.m_desc = desc;
    }

    public Collection<IceCandidate> gatherCandidates() {
        final Collection<IceCandidate> candidates = 
            new ArrayList<IceCandidate>();

        if (this.m_desc.isUdp()) {
            final Collection<IceCandidate> udpCandidates = 
                createUdpCandidates(this.m_iceUdpStunPeer);

            // 4.1.3. Eliminating Redundant Candidates.
            eliminateRedundantCandidates(udpCandidates);
            candidates.addAll(udpCandidates);
        }

        return candidates;
    }
    
    /**
     * Section 4.1.3.
     */
    private void eliminateRedundantCandidates(
            final Collection<IceCandidate> candidates) {
        final Map<InetSocketAddress, IceCandidate> addressesToCandidates = 
            new HashMap<InetSocketAddress, IceCandidate>();
        for (final Iterator<IceCandidate> iter = candidates.iterator(); iter
                .hasNext();) {
            final IceCandidate candidate = iter.next();
            m_log.debug("Checking: {}", candidate);
            final InetSocketAddress address = candidate.getSocketAddress();
            if (addressesToCandidates.containsKey(address)) {
                final IceCandidate existingCandidate = addressesToCandidates
                        .get(address);
                final IceCandidate base = existingCandidate.getBaseCandidate();
                if (base.equals(candidate.getBaseCandidate())) {
                    m_log.debug("Removing redundant candidate!!!!");
                    iter.remove();
                }
            } else {
                addressesToCandidates.put(address, candidate);
            }
        }
    }

    private Collection<IceCandidate> createUdpCandidates(
        final StunClient client) {
        final Collection<IceCandidate> candidates = new ArrayList<IceCandidate>();

        final InetAddress stunServerAddress = client.getStunServerAddress();

        // Add the host candidate. Note the host candidate is also used as
        // the BASE candidate for the server reflexive candidate below.
        final InetSocketAddress hostAddress = client.getHostAddress();

        final IceUdpHostCandidate hostCandidate = new IceUdpHostCandidate(
                hostAddress, this.m_controlling);

        // We don't want the local UDP candidate to be included, as we'll just
        // use the TCP candidate in this case. See LS-460.
        candidates.add(hostCandidate);

        try {
            this.m_udpServerReflexiveAddress = 
                client.getServerReflexiveAddress();
            m_log.info("Got server reflexive: {}", 
                this.m_udpServerReflexiveAddress);
        } catch (final IOException e) {
            m_log.error("Could not get UDP server reflexive candidate", e);
            return candidates;
        }

        final IceUdpServerReflexiveCandidate serverReflexiveCandidate = 
            new IceUdpServerReflexiveCandidate(
                m_udpServerReflexiveAddress, hostCandidate, stunServerAddress,
                this.m_controlling);

        candidates.add(serverReflexiveCandidate);

        return candidates;
    }

    public InetAddress getPublicAddress() {
        if (this.m_udpServerReflexiveAddress == null) {
            return null;
        }
        return this.m_udpServerReflexiveAddress.getAddress();
    }

    public void close() {
        if (this.m_iceUdpStunPeer != null) {
            this.m_iceUdpStunPeer.close();
        }
    }
}
