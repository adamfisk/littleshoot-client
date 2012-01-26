package org.lastbamboo.common.ice;

import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorFactory;
import org.lastbamboo.common.stun.stack.transaction.StunTransactionTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating ICE connectivity checkers.
 * 
 * @param <T> This is also a message visitor for visiting messages on the port
 * we're checking.  T is the class the visitors return.
 */
public class IceStunConnectivityCheckerFactoryImpl<T> implements
    StunMessageVisitorFactory<T>
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final IceAgent m_iceAgent;
    private final StunTransactionTracker<T> m_transactionTracker;
    private final IceStunCheckerFactory m_checkerFactory;
    private final IceBindingRequestTracker m_bindingRequestTracker =
        new IceBindingRequestTrackerImpl();

    /**
     * Creates a new factory for creating connectivity checkers.
     * 
     * @param iceAgent The agent performing the checks.
     * @param transactionTracker The class that keeps track of STUN transactions 
     * during the checks.
     * @param checkerFactory The class that creates new checkers.
     */
    public IceStunConnectivityCheckerFactoryImpl(final IceAgent iceAgent, 
        final StunTransactionTracker<T> transactionTracker, 
        final IceStunCheckerFactory checkerFactory)
        {
        m_iceAgent = iceAgent;
        m_transactionTracker = transactionTracker;
        m_checkerFactory = checkerFactory;
        }

    public StunMessageVisitor<T> createVisitor(final IoSession session)
        {
        m_log.debug("Creating new message visitor for session: {}", session);
        return new IceStunConnectivityCheckerImpl<T>(this.m_iceAgent, 
            session, this.m_transactionTracker, 
            this.m_checkerFactory, m_bindingRequestTracker);
        }

    }
