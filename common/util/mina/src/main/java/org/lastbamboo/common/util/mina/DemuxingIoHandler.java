package org.lastbamboo.common.util.mina;

import org.littleshoot.util.ThreadUtils;
import org.littleshoot.mina.common.IdleStatus;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoHandlerAdapter;
import org.littleshoot.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IoHandler} that allows multiple protocols to run over the same 
 * {@link IoSession}.
 *  
 * @param <T> The type of the message {@link Class} for the first protocol.
 * @param <Z> The type of the message {@link Class} for the second protocol.
 */
public class DemuxingIoHandler<T, Z> extends IoHandlerAdapter
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final Class<T> m_class1;
    private final Class<Z> m_class2;
    private final IoHandler m_ioHandler1;
    private final IoHandler m_ioHandler2;

    /**
     * Creates a new {@link IoHandler} that demultiplexes encoded and decoded
     * messages between STUN and another protocol.
     * 
     * @param class1 The message class for the first protocol.
     * @param ioHandler1 The {@link IoHandler} for the first protocol.
     * @param class2 The message class for the second protocol.
     * @param ioHandler2 The {@link IoHandler} for the second protocol.
     */
    public DemuxingIoHandler(final Class<T> class1, final IoHandler ioHandler1,
        final Class<Z> class2, final IoHandler ioHandler2)
        {
        if (class1 == null)
            {
            throw new NullPointerException("Null first class");
            }
        if (class2 == null)
            {
            throw new NullPointerException("Null second class");
            }
        if (ioHandler1 == null)
            {
            throw new NullPointerException("Null first handler");
            }
        if (ioHandler2 == null)
            {
            throw new NullPointerException("Null second handler");
            }
        m_class1 = class1;
        m_class2 = class2;
        m_ioHandler1 = ioHandler1;
        m_ioHandler2 = ioHandler2;
        }
    
    @Override
    public void exceptionCaught(final IoSession session, final Throwable cause)
        throws Exception
        {
        m_log.debug("Caught exception", cause);
        m_log.debug("Cause trace: "+ThreadUtils.dumpStack(cause.getCause()));
        this.m_ioHandler1.exceptionCaught(session, cause);
        this.m_ioHandler2.exceptionCaught(session, cause);
        }

    @Override
    public void messageReceived(final IoSession session, final Object message)
        throws Exception
        {
        m_log.debug("Received message...");
        final IoHandler handler = getHandlerForMessage(message);
        if (handler != null)
            {
            handler.messageReceived(session, message);
            }
        }

    @Override
    public void messageSent(final IoSession session, final Object message) 
        throws Exception
        {
        m_log.debug("Sent message...");
        final IoHandler handler = getHandlerForMessage(message);
        if (handler != null)
            {
            handler.messageSent(session, message);
            }
        }

    private IoHandler getHandlerForMessage(final Object message)
        {
        if (this.m_class1.isAssignableFrom(message.getClass()))
            {
            return this.m_ioHandler1;
            }
        else if (this.m_class2.isAssignableFrom(message.getClass()))
            {
            return this.m_ioHandler2;
            }
        else
            {
            m_log.warn("Could not find IoHandler for message: {}", message);
            m_log.warn("Existing classes: " +m_class1+" and "+m_class2);
            return null;
            }
        }

    @Override
    public void sessionClosed(final IoSession session) throws Exception
        {
        this.m_ioHandler1.sessionClosed(session);
        this.m_ioHandler2.sessionClosed(session);
        }

    @Override
    public void sessionCreated(final IoSession session) throws Exception
        {
        this.m_ioHandler1.sessionCreated(session);
        this.m_ioHandler2.sessionCreated(session);
        }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status)
        throws Exception
        {
        this.m_ioHandler1.sessionIdle(session, status);
        this.m_ioHandler2.sessionIdle(session, status);
        }

    @Override
    public void sessionOpened(final IoSession session) throws Exception
        {
        this.m_ioHandler1.sessionOpened(session);
        this.m_ioHandler2.sessionOpened(session);
        }

    }
