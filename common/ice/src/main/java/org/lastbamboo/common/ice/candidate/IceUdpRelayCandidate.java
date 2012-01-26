package org.lastbamboo.common.ice.candidate;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.lastbamboo.common.ice.IcePriorityCalculator;
import org.lastbamboo.common.ice.IceTransportProtocol;

/**
 * Relay ICE UDP candidate.
 */
public class IceUdpRelayCandidate extends AbstractIceCandidate
    {

    /**
     * Creates a new UDP ICE candidate for a relay candidate.  Note the base
     * candidate for relays is the candidate itself.
     * 
     * @param relayAddress The address of the relay candidate.
     * @param stunServerAddress The address of the STUN server.
     * @param relatedAddress The address related to this candidate.  In this
     * case, the mapped address received in the Allocate Response.
     * @param relatedPort The port related to this candidate.  In this
     * case, the port in the mapped address received in the Allocate Response.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     */
    public IceUdpRelayCandidate(final InetSocketAddress relayAddress, 
        final InetAddress stunServerAddress,
        final InetAddress relatedAddress, final int relatedPort,
        final boolean controlling)
        {
        super(relayAddress, 
            IceFoundationCalculator.calculateFoundation(IceCandidateType.RELAYED, 
                relayAddress.getAddress(), IceTransportProtocol.UDP, 
                stunServerAddress), 
            IceCandidateType.RELAYED, IceTransportProtocol.UDP, 
            IcePriorityCalculator.calculatePriority(IceCandidateType.RELAYED, 
                IceTransportProtocol.UDP), 
            controlling, DEFAULT_COMPONENT_ID, null, 
            relatedAddress, relatedPort);
        }

    public IceUdpRelayCandidate(final InetSocketAddress relayAddress, 
        final String foundation, final long priority, 
        final boolean controlling, final int componentId, 
        final InetAddress relatedAddress, final int relatedPort)
        {
        super(relayAddress, foundation, IceCandidateType.RELAYED, 
            IceTransportProtocol.UDP, priority, controlling, 
            componentId, null, relatedAddress, relatedPort);
        }

    public <T> T accept(IceCandidateVisitor<T> visitor)
        {
        return visitor.visitUdpRelayCandidate(this);
        }

    }
