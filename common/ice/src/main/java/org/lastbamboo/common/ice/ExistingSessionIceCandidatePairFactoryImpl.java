package org.lastbamboo.common.ice;

import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidatePair;
import org.lastbamboo.common.ice.candidate.IceUdpCandidatePair;
import org.littleshoot.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pair factory for when there's already a session established for this pair.
 */
public class ExistingSessionIceCandidatePairFactoryImpl 
    implements ExistingSessionIceCandidatePairFactory
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final IceStunCheckerFactory m_checkerFactory;

    /**
     * Creates a new pair factory that uses an already-established session for
     * the pair.
     * 
     * @param checkerFactory The class that performs STUN checks.
     */
    public ExistingSessionIceCandidatePairFactoryImpl(
        final IceStunCheckerFactory checkerFactory)
        {
        m_checkerFactory = checkerFactory;
        }

    public IceCandidatePair newUdpPair(final IceCandidate localCandidate,
        final IceCandidate remoteCandidate, final IoSession ioSession)
        {
        if (ioSession == null)
            {
            m_log.error("No IO Session");
            throw new NullPointerException("Null IO Session");
            }
        return new IceUdpCandidatePair(localCandidate, 
            remoteCandidate, ioSession, this.m_checkerFactory);
        }
    }
