package org.lastbamboo.common.ice.candidate;

import org.lastbamboo.common.ice.IceStunCheckerFactory;
import org.lastbamboo.common.ice.transport.IceUdpConnector;

/**
 * Factory for creating ICE candidate pairs.
 */
public class IceCandidatePairFactoryImpl implements IceCandidatePairFactory
    {
    
    private final IceStunCheckerFactory m_checkerFactory;
    private final IceUdpConnector m_udpConnector;

    /**
     * Creates a new pair factory.
     * 
     * @param checkerFactory The factory for creating connectivity checkers.
     * @param udpConnector The class for creating UDP "connections" for pairs.
     */
    public IceCandidatePairFactoryImpl(
        final IceStunCheckerFactory checkerFactory,
        final IceUdpConnector udpConnector)
        {
        m_checkerFactory = checkerFactory;
        m_udpConnector = udpConnector;
        }

    public IceCandidatePair newPair(final IceCandidate localCandidate,
        final IceCandidate remoteCandidate)
        {
        return new IceUdpCandidatePair(localCandidate, remoteCandidate, 
            this.m_checkerFactory, this.m_udpConnector);
        }
    }
