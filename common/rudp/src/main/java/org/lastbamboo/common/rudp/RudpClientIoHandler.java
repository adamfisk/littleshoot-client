package org.lastbamboo.common.rudp;

import org.littleshoot.mina.common.IdleStatus;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoHandlerAdapter;
import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.rudp.segment.Segment;
import org.littleshoot.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IoHandler} for RUDP clients.
 */
public class RudpClientIoHandler extends IoHandlerAdapter
    {
    /**
     * The logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final RudpManager m_rudpManager;

    /**
     * Creates a new RUDP client {@link IoHandler}.
     * 
     * @param service The manager for RUDP traffic.
     */
    public RudpClientIoHandler(final RudpService service)
        {
        m_rudpManager = service.getManager();
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceived (final IoSession session, final Object msg)
        {
        m_log.debug ("RUDP client received message: {}", msg);
        this.m_rudpManager.handle (RudpUtils.toId (session), (Segment) msg);
        }
    
    @Override
    public void sessionClosed(final IoSession session) 
        {
        m_log.debug("Session closed: " + session + " {}", 
            ThreadUtils.dumpStack());
        this.m_rudpManager.notifyClosed(RudpUtils.toId(session));
        }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status)
        {
        m_log.debug("Session idle " + session + " status: "+status);
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(final IoSession session, final Throwable cause)
        {
        m_log.warn("Caught RUDP client exception.", cause);
        m_log.warn("Session is: {}", session);
        session.close();
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void messageSent(final IoSession session, final Object msg)  
        {
        m_log.debug ("RUDP client sent message: {}", msg);
        }
    }
