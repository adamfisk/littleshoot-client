package org.lastbamboo.common.ice.candidate;

import java.net.InetAddress;
import java.util.Collection;


/**
 * Interface for classes that gather ICE candidates.
 */
public interface IceCandidateGatherer
    {

    /**
     * Gathers ICE candidates.
     * 
     * @return The {@link Collection} of gathered candidates.
     */
    Collection<IceCandidate> gatherCandidates();

    /**
     * Close any resources associated with the gatherer.
     */
    void close();
    
    /**
     * Accessor for the public address of the machine.
     * 
     * @return The public address of the machine.
     */
    InetAddress getPublicAddress();

    }
