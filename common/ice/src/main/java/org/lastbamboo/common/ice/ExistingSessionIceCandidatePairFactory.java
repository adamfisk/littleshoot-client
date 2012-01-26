package org.lastbamboo.common.ice;

import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidatePair;

/**
 * Factory for creating ICE candidate pairs.
 */
public interface ExistingSessionIceCandidatePairFactory
    {

    IceCandidatePair newUdpPair(IceCandidate localCandidate, 
        IceCandidate remoteCandidate, IoSession ioSession);

    }
