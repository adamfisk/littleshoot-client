package org.lastbamboo.common.ice.candidate;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.lastbamboo.common.ice.IcePriorityCalculator;
import org.lastbamboo.common.ice.IceTransportProtocol;

/**
 * ICE simultaneous open TCP candidate for server reflexive hosts.
 */
public class IceTcpServerReflexiveSoCandidate extends AbstractIceCandidate
    {

    /**
     * Creates a new ICE simultaneous open TCP candidate for server reflexive 
     * hosts.
     * 
     * @param socketAddress The address of the server reflexive candidate.
     * @param baseCandidate The local base candidate.
     * @param stunServerAddress The address of the STUN server.
     * @param controlling Whether or not this candidate is the controlling
     * candidate.
     */
    public IceTcpServerReflexiveSoCandidate(
        final InetSocketAddress socketAddress, final IceCandidate baseCandidate, 
        final InetAddress stunServerAddress, final boolean controlling)
        {
        super(socketAddress, 
            IceFoundationCalculator.calculateFoundation(
                IceCandidateType.SERVER_REFLEXIVE, 
                baseCandidate.getSocketAddress().getAddress(), 
                IceTransportProtocol.TCP_SO, stunServerAddress), 
            IceCandidateType.SERVER_REFLEXIVE, IceTransportProtocol.TCP_SO, 
            IcePriorityCalculator.calculatePriority(
                IceCandidateType.SERVER_REFLEXIVE, IceTransportProtocol.TCP_SO), 
            controlling, DEFAULT_COMPONENT_ID, baseCandidate, 
            baseCandidate.getSocketAddress().getAddress(),
            baseCandidate.getSocketAddress().getPort());
        }
    
    /**
     * Creates a new ICE simultaneous open TCP candidate for server reflexive 
     * hosts.
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
    public IceTcpServerReflexiveSoCandidate(
        final InetSocketAddress socketAddress, final String foundation, 
        final InetAddress relatedAddress, final int relatedPort, 
        final boolean controlling, final long priority, final int componentId)
        {
        super(socketAddress, foundation, IceCandidateType.SERVER_REFLEXIVE, 
            IceTransportProtocol.TCP_SO, priority, controlling, 
            componentId, null, relatedAddress, relatedPort);
        }


    public <T> T accept(IceCandidateVisitor<T> visitor)
        {
        return visitor.visitTcpServerReflexiveSoCandidate(this);
        }
    }
