package org.lastbamboo.common.ice.candidate;


/**
 * Factory for creating ICE candidate pairs.
 */
public interface IceCandidatePairFactory
    {

    IceCandidatePair newPair(IceCandidate localCandidate, 
        IceCandidate remoteCandidate);

    }
