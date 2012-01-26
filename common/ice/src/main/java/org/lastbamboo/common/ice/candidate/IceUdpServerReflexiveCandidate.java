package org.lastbamboo.common.ice.candidate;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.lastbamboo.common.ice.IcePriorityCalculator;
import org.lastbamboo.common.ice.IceTransportProtocol;

/**
 * Server reflexive ICE UDP candidate.
 */
public class IceUdpServerReflexiveCandidate extends AbstractIceCandidate
    {

    /**
     * Creates a new UDP ICE candidate for the server reflexive candidate.
     * 
     * @param serverReflexiveAddress The address of the server reflexive 
     * candidate.
     * @param baseCandidate The base candidate.
     * @param stunServerAddress The address of the STUN server.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     */
    public IceUdpServerReflexiveCandidate(
        final InetSocketAddress serverReflexiveAddress, 
        final IceCandidate baseCandidate, final InetAddress stunServerAddress,
        final boolean controlling)
        {
        super(serverReflexiveAddress, 
            IceFoundationCalculator.calculateFoundation(
                IceCandidateType.SERVER_REFLEXIVE, 
                baseCandidate.getSocketAddress().getAddress(), 
                IceTransportProtocol.UDP, stunServerAddress), 
            IceCandidateType.SERVER_REFLEXIVE, 
            IceTransportProtocol.UDP, 
            IcePriorityCalculator.calculatePriority(
                IceCandidateType.SERVER_REFLEXIVE, 
                IceTransportProtocol.UDP), controlling, 
            DEFAULT_COMPONENT_ID, baseCandidate, 
            baseCandidate.getSocketAddress().getAddress(), 
            baseCandidate.getSocketAddress().getPort());
        }

    /**
     * Creates a new UDP ICE candidate for the server reflexive candidate.
     * 
     * @param serverReflexiveAddress The address of the server reflexive 
     * candidate.
     * the candidate address.
     * @param foundation The foundation.
     * @param relatedAddress The address related to this candidate.  In this
     * case, the base address.
     * @param relatedPort The port related to this candidate.  In this
     * case, the base port.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     * @param priority The priority of the candidate.
     * @param componentId The component ID. 
     */
    public IceUdpServerReflexiveCandidate(
        final InetSocketAddress serverReflexiveAddress, final String foundation, 
        final InetAddress relatedAddress, final int relatedPort,
        final boolean controlling, final long priority, final int componentId)
        {
        super(serverReflexiveAddress, foundation, 
            IceCandidateType.SERVER_REFLEXIVE, IceTransportProtocol.UDP, 
            priority, controlling, 
            componentId, null, relatedAddress, relatedPort);
        }

    public <T> T accept(final IceCandidateVisitor<T> visitor)
        {
        return visitor.visitUdpServerReflexiveCandidate(this);
        }

    }
