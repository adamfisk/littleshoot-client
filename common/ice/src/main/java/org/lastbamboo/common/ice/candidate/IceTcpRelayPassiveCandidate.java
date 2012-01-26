package org.lastbamboo.common.ice.candidate;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.lastbamboo.common.ice.IcePriorityCalculator;
import org.lastbamboo.common.ice.IceTransportProtocol;

/**
 * ICE passive TCP candidate for relayed hosts.
 */
public class IceTcpRelayPassiveCandidate extends AbstractIceCandidate
    {

    /**
     * Creates a new TCP passive ICE candidate for relayed hosts.
     * 
     * @param socketAddress The address of the relayed candidate.
     * @param stunServerAddress The address of the STUN server.
     * @param relatedAddress The address related to this candidate.  In this
     * case, the mapped address received in the Allocate Response.
     * @param relatedPort The port related to this candidate.  In this
     * case, the port in the mapped address received in the Allocate Response.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     */
    public IceTcpRelayPassiveCandidate(final InetSocketAddress socketAddress,
        final InetAddress stunServerAddress,
        final InetAddress relatedAddress, final int relatedPort,
        final boolean controlling)
        {
        super(socketAddress, 
            IceFoundationCalculator.calculateFoundation(IceCandidateType.RELAYED, 
                socketAddress.getAddress(), 
                IceTransportProtocol.TCP_PASS, stunServerAddress), 
            IceCandidateType.RELAYED, IceTransportProtocol.TCP_PASS, 
            IcePriorityCalculator.calculatePriority(IceCandidateType.RELAYED, 
                IceTransportProtocol.TCP_PASS), 
            controlling, DEFAULT_COMPONENT_ID, 
            null, relatedAddress, relatedPort);
        }
    
    /**
     * Creates a new TCP passive ICE candidate for relayed hosts.
     * 
     * @param socketAddress The address of the relayed candidate.
     * @param foundation The foundation.
     * @param relatedAddress The address related to this candidate.  In this
     * case, the mapped address received in the Allocate Response.
     * @param relatedPort The port related to this candidate.  In this
     * case, the port in the mapped address received in the Allocate Response.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     * @param priority The priority of the candidate.
     * @param componentId The component ID.
     */
    public IceTcpRelayPassiveCandidate(final InetSocketAddress socketAddress,
        final String foundation, final InetAddress relatedAddress, 
        final int relatedPort, final boolean controlling, final long priority,
        final int componentId)
        {
        super(socketAddress, foundation, IceCandidateType.RELAYED, 
            IceTransportProtocol.TCP_PASS, priority, controlling, 
            componentId, null, relatedAddress, relatedPort);
        }

    public <T> T accept(final IceCandidateVisitor<T> visitor)
        {
        return visitor.visitTcpRelayPassiveCandidate(this);
        }

    }
