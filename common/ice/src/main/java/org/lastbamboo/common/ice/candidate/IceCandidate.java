package org.lastbamboo.common.ice.candidate;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.lastbamboo.common.ice.IceTransportProtocol;

/**
 * This is an interface for an ICE "candidate" as defined in 
 * the IETF draft "draft-ietf-mmusic-ice-05.txt".  A candidate is typically
 * a media-level attributed in SDP data transferred using SIP, but a 
 * node can learn of candidates using any other offer/answere protocol or
 * mode of describing the media.
 */
public interface IceCandidate
    {
    
    /**
     * Accessor for the address and port of the candidate.
     * @return The address and port of the candidate.
     */
    InetSocketAddress getSocketAddress();

    /**
     * Accessor for the priority of the candidate.
     * 
     * @return The priority of the candidate.
     */
    long getPriority();

    /**
     * Accessor for the type of transport of this candidate, such as TCP or
     * UDP.
     * 
     * @return The transport for this candidate.
     */
    IceTransportProtocol getTransport();

    /**
     * Accepts the specified visitor to an ICE candidate.
     * 
     * @param <T> The class to return.
     * @param visitor The visitor to accept.
     * @return The class the visitor created. 
     */
    <T> T accept(IceCandidateVisitor<T> visitor);

    /**
     * Gets the type of the ICE candidate.
     * 
     * @return The type of the ICE candidate.
     */
    IceCandidateType getType();

    /**
     * Accessor for the component ID of this candidate.  A component of a
     * candidate is the number of the component of the media stream it 
     * represents.  Many media streams will only have one component, starting
     * with "1", but others might have two or more, such as a media stream 
     * with RTP and RTCP.
     * 
     * @return The component ID.
     */
    int getComponentId();
    
    /**
     * Accessor for the candidate's foundation.
     * 
     * @return The candidate's foundation.
     */
    String getFoundation();
    
    /**
     * Accessor for the base candidate for this candidate.  For host and relay
     * candidates, the base candidate is the same as the candidate itself.  For
     * server reflexive candidates, the candidate is the host candidate used
     * to determine the server reflexive address.  
     * 
     * @return The base candidate.
     */
    IceCandidate getBaseCandidate();

    /**
     * Returns whether or not this peer is the controlling peer.
     * 
     * @return <code>true</code> if this peer is the controlling peer,
     * otherwise <code>false</code>.
     */
    boolean isControlling();
    
    InetAddress getRelatedAddress();
    
    int getRelatedPort();

    /**
     * Sets the controlling status of this candidate.
     * 
     * @param controlling The controlling status.
     */
    void setControlling(boolean controlling);

    boolean isUdp();

    }
