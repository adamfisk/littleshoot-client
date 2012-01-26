package org.lastbamboo.common.ice.transport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.id.uuid.UUID;
import org.littleshoot.mina.common.CloseFuture;
import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.ice.IceStunChecker;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.CanceledStunMessage;
import org.lastbamboo.common.stun.stack.message.NullStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.transaction.StunTransactionListener;
import org.lastbamboo.common.stun.stack.transaction.StunTransactionTracker;
import org.littleshoot.util.RuntimeIoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for STUN connectivity checkers.  This performs STUN checks
 * and notifies the callers of responses.  Subclasses supply the transport.
 */
public abstract class AbstractIceStunChecker implements IceStunChecker,
    StunTransactionListener
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    protected final IoSession m_ioSession;

    protected volatile int m_writeCallsForChecker = 0;
    
    protected final Map<UUID, StunMessage> m_idsToResponses =
        new ConcurrentHashMap<UUID, StunMessage>();

    protected final StunTransactionTracker<StunMessage> m_transactionTracker;

    protected final Object m_requestLock = new Object();
    
    /**
     * TODO: Review if this works!!
     */
    protected volatile boolean m_transactionCanceled = false;

    protected volatile boolean m_closed = false;
    
    public AbstractIceStunChecker(final IoSession ioSession,
        final StunTransactionTracker<StunMessage> transactionTracker)
        {
        if (ioSession == null)
            {
            throw new NullPointerException("Null session!!");
            }
        m_transactionTracker = transactionTracker;
        m_ioSession = ioSession;
        }
    
    public StunMessage write(final BindingRequest bindingRequest, 
        final long rto)
        {
        m_log.debug("Writing Binding Request...");
        this.m_writeCallsForChecker++;
        if (this.m_writeCallsForChecker > 1)
            {
            m_log.debug("Second call to checker!!");
            throw new RuntimeIoException("Too many calls to checker: "+
                this.m_writeCallsForChecker);
            }
        
        // If the pair is requesting a write, *that* pair hasn't been canceled.
        // STUN checkers can be used for multiple pairs because, for example,
        // local peer reflexive candidates often have the same base as local
        // host candidates, so there could otherwise be bind collisions
        // (since the remote candidates can be the same).
        //this.m_transactionCanceled = false;
        
        if (this.m_transactionCanceled ||
            this.m_closed || 
            this.m_ioSession.isClosing())
            {
            m_log.debug("Already closed");
            return new CanceledStunMessage();
            }
        try
            {
            return writeInternal(bindingRequest, rto);
            }
        catch (final Throwable t)
            {
            m_log.error("Could not write Binding Request", t);
            return new NullStunMessage();
            }
        }
    
    protected abstract StunMessage writeInternal(BindingRequest bindingRequest, 
        long rto);
    
    protected final void waitIfNoResponse(final BindingRequest request, 
        final long waitTime)
        {
        if (waitTime == 0L) return;
        if (!m_idsToResponses.containsKey(request.getTransactionId()))
            {
            try
                {
                m_requestLock.wait(waitTime);
                }
            catch (final InterruptedException e)
                {
                m_log.error("Unexpected interrupt", e);
                }
            }
        }

    public void cancelTransaction()
        {
        m_log.debug("Cancelling transaction!!");
        this.m_transactionCanceled = true;
        synchronized (m_requestLock)
            {
            m_requestLock.notifyAll();
            }
        }
    
    public Object onTransactionFailed(final StunMessage request,
        final StunMessage response)
        {
        m_log.warn("Transaction failed");
        return notifyWaiters(request, response);
        }
    
    public Object onTransactionSucceeded(final StunMessage request, 
        final StunMessage response)
        {
        return notifyWaiters(request, response);
        }

    private Object notifyWaiters(final StunMessage request, 
        final StunMessage response)
        {
        synchronized (m_requestLock)
            {
            this.m_idsToResponses.put(request.getTransactionId(), response);
            m_requestLock.notifyAll();
            }
        return null;
        }
    
    public void close()
        {
        if (this.m_ioSession == null)
            {
            m_log.debug("Can't close null session");
            return;
            }
        final CloseFuture future = this.m_ioSession.close();
        future.join();
        this.m_closed = true;
        }
    }
