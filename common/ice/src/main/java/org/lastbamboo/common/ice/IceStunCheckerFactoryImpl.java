package org.lastbamboo.common.ice;

import org.lastbamboo.common.ice.transport.IceUdpStunChecker;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.transaction.StunTransactionTracker;
import org.littleshoot.mina.common.IoSession;

/**
 * Class for creating STUN checker factories for both UDP and TCP.  Each
 * media stream requires its own factory because the checkers are coupled to
 * data for that specific stream.
 */
public class IceStunCheckerFactoryImpl implements IceStunCheckerFactory
    {

    private final StunTransactionTracker<StunMessage> m_transactionTracker;

    /**
     * Creates a new factory.  The checks the factory creates can be either
     * for UDP or TCP.
     * @param transactionTracker The class that keeps track of STUN 
     * transactions.
     */
    public IceStunCheckerFactoryImpl(
        final StunTransactionTracker<StunMessage> transactionTracker)
        {
        m_transactionTracker = transactionTracker;
        }

    public IceStunChecker newChecker(final IoSession session)
        {
        return new IceUdpStunChecker(session, m_transactionTracker);
        }

    }
