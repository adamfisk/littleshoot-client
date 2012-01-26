package org.lastbamboo.common.rudp;

import java.net.InetSocketAddress;

import org.littleshoot.mina.common.IdleStatus;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoHandlerAdapter;
import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.rudp.segment.Segment;
import org.littleshoot.util.F1;
import org.littleshoot.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IoHandler} for RUDP.  This can be the {@link IoHandler} for either
 * the client or the server depending on the state of a controlling class.
 */
public class RudpServerIoHandler extends IoHandlerAdapter
    {
    /**
     * The logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final RudpManager m_rudpManager;

    /**
     * Creates a new {@link RudpServerIoHandler}.
     * 
     * @param service The class that manages RUDP connections.
     */
    public RudpServerIoHandler(final RudpService service)
        {
        this.m_rudpManager = service.getManager();
        }

    @Override
    public void messageReceived (final IoSession session, final Object msg)
        {
        m_log.debug ("Received RUDP on server: {}", msg);
        
        final InetSocketAddress localAddress =
            (InetSocketAddress) session.getLocalAddress ();
        
        final RudpListeningConnectionId id = 
            new RudpListeningConnectionIdImpl (localAddress);
        
        final InetSocketAddress remoteAddress =
            (InetSocketAddress) session.getRemoteAddress ();
        
        // TODO: We should ideally synchronize to make sure the server socket 
        // is listening before we handle a message, but the change to make 
        // this a separate class makes that trickier.
        //synchronized (this)
          //  {
        m_rudpManager.handle (id, remoteAddress, (Segment) msg,
          getWriteF (session));
            //}
        }
    
    @Override
    public void messageSent(final IoSession session, final Object msg) 
        throws Exception
        {
        m_log.debug("Sent message: {}", msg);
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(final IoSession session, final Throwable cause)
        {
        m_log.warn("Caught RUDP server exception on session: "+session, cause);
        session.close();
        }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionOpened(final IoSession session)  
        {
        m_log.debug("RUDP server established session: {}", session);
        }

    @Override
    public void sessionClosed(final IoSession session)  
        {
        if (m_log.isDebugEnabled())
            {
            m_log.debug("RUDP server session closed: {}", session);
            }

        // We notify the manager of the close event using the connection ID, 
        // not the listening connection ID, as the server side should stay
        // up for future connections.  It should be closed elsewhere.
        m_rudpManager.notifyClosed(RudpUtils.toId(session));
        }
    
    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status)
        {
        m_log.debug("Session idle " + session + " status: "+status);
        }
    
    @Override
    public void sessionCreated(final IoSession session)  
        {
        m_log.debug("RUDP server created session: {}", session);
        }
    
    /**
     * Returns a write function that is used to write UDP messages over a given
     * session.
     * 
     * @param session The session.
     * @return The write function.
     */
    private static F1<Segment,Void> getWriteF (final IoSession session)
        {
        final F1<Segment,Void> f = new F1<Segment,Void> ()
            {
            public Void run (final Segment segment)
                {
                session.write (segment);
                return null;
                }
            };
            
        return f;
        }
    }
