package org.lastbamboo.common.ice.candidate;

import java.net.InetSocketAddress;

import org.lastbamboo.common.ice.IceTransportProtocol;

/**
 * Peer reflexive ICE TCP candidate.
 */
public class IceTcpPeerReflexiveCandidate extends AbstractIceCandidate
    {

    /**
     * Creates a new TCP ICE candidate for the server peer candidate.
     * 
     * @param peerReflexiveAddress The address of the peer reflexive 
     * candidate.
     * @param baseCandidate The local base candidate.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     * @param priority The priority of the candidate.
     */
    public IceTcpPeerReflexiveCandidate(
        final InetSocketAddress peerReflexiveAddress,
        final IceCandidate baseCandidate, 
        final boolean controlling, final long priority)
        {
        super(peerReflexiveAddress, 
            IceFoundationCalculator.calculateFoundation(IceCandidateType.PEER_REFLEXIVE, 
               baseCandidate.getSocketAddress().getAddress(), 
               IceTransportProtocol.TCP_PASS), 
            IceCandidateType.PEER_REFLEXIVE, 
            IceTransportProtocol.TCP_PASS, priority, controlling, 
            DEFAULT_COMPONENT_ID, baseCandidate, 
            baseCandidate.getSocketAddress().getAddress(), 
            baseCandidate.getSocketAddress().getPort());
        }
    
    /**
     * Creates a new TCP ICE candidate for the server peer candidate.
     * 
     * @param peerReflexiveAddress The address of the peer reflexive 
     * candidate.
     * @param foundation The foundation.
     * @param componentId The component ID.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     * @param priority The priority for the candidate.
     */
    public IceTcpPeerReflexiveCandidate(
        final InetSocketAddress peerReflexiveAddress,
        final String foundation, final int componentId,
        final boolean controlling, final long priority)
        {
        super(peerReflexiveAddress, foundation, 
            IceCandidateType.PEER_REFLEXIVE, IceTransportProtocol.TCP_PASS, 
            priority, controlling, componentId, null, null, -1);
        }

    public <T> T accept(final IceCandidateVisitor<T> visitor)
        {
        return visitor.visitTcpPeerReflexiveCandidate(this);
        }

    }
