package org.lastbamboo.common.ice.candidate;

import java.net.InetSocketAddress;

import org.lastbamboo.common.ice.IceTransportProtocol;

/**
 * ICE UDP candidate for the local host.
 */
public class IceUdpHostCandidate extends AbstractIceCandidate
    {

    /**
     * Creates a new UDP ICE candidate for the local host.
     * 
     * @param hostAddress The candidate address and port.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     */
    public IceUdpHostCandidate(final InetSocketAddress hostAddress,
        final boolean controlling)
        {
        super (hostAddress, hostAddress.getAddress(), IceCandidateType.HOST, 
             IceTransportProtocol.UDP, controlling);
        }


    /**
     * Creates a new UDP ICE candidate for the local host.
     * 
     * @param socketAddress The address of the local host.
     * @param foundation The foundation for the candidate.
     * @param priority The priority.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     * @param componentId The component ID.
     */
    public IceUdpHostCandidate(final InetSocketAddress socketAddress, 
        final String foundation, final long priority, final boolean controlling,
        final int componentId)
        {
        super(socketAddress, foundation,
            IceCandidateType.HOST, IceTransportProtocol.UDP,
            priority, controlling, componentId, null, null, -1);
        }

    public <T> T accept(final IceCandidateVisitor<T> visitor)
        {
        return visitor.visitUdpHostCandidate(this);
        }

    }
