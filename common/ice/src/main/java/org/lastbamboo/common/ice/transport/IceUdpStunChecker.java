package org.lastbamboo.common.ice.transport;

import java.net.InetSocketAddress;

import org.apache.commons.id.uuid.UUID;
import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.CanceledStunMessage;
import org.lastbamboo.common.stun.stack.message.NullStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.transaction.StunTransactionTracker;
import org.littleshoot.util.RuntimeIoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that performs STUN connectivity checks for ICE over UDP.  Each 
 * ICE candidate pair has its own connectivity checker. 
 */
public class IceUdpStunChecker extends AbstractIceStunChecker
    {

    private static final Logger m_log = 
        LoggerFactory.getLogger(IceUdpStunChecker.class);

    /**
     * Creates a new UDP STUN connectivity checker.
     * 
     * @param session The MINA session over which the checks will take place.
     * @param transactionTracker The class for keeping track of STUN 
     * transactions for checks.
     */
    public IceUdpStunChecker(final IoSession session, 
        final StunTransactionTracker<StunMessage> transactionTracker)
        {
        super(session, transactionTracker);
        }
    
    @Override
    protected StunMessage writeInternal(final BindingRequest bindingRequest, 
        final long rto)
        {
        if (this.m_transactionCanceled)
            {
            return new CanceledStunMessage();
            }
        if (this.m_writeCallsForChecker > 1)
            {
            throw new RuntimeIoException("Too many write calls: "+
                this.m_writeCallsForChecker);
            }
        if (bindingRequest == null)
            {
            throw new NullPointerException("Null Binding Request");
            }
        
        // This method will retransmit the same request multiple times because
        // it's being sent unreliably.  All of these requests will be 
        // identical, using the same transaction ID.
        final UUID id = bindingRequest.getTransactionId();
        final InetSocketAddress localAddress = 
            (InetSocketAddress) this.m_ioSession.getLocalAddress();
        final InetSocketAddress remoteAddress =
            (InetSocketAddress) this.m_ioSession.getRemoteAddress();
        
        this.m_transactionTracker.addTransaction(bindingRequest, this, 
            localAddress, remoteAddress);
        
        synchronized (m_requestLock)
            {   
            int requests = 0;
            
            long waitTime = 0L;

            while (!m_idsToResponses.containsKey(id) && requests < 7 &&
                !this.m_transactionCanceled)
                {
                waitIfNoResponse(bindingRequest, waitTime);
                
                // If the transaction was canceled during the wait, then
                // don't send the request.
                if (this.m_transactionCanceled)
                    {
                    break;
                    }
                
                // See draft-ietf-behave-rfc3489bis-06.txt section 7.1.  We
                // continually send the same request until we receive a 
                // response, never sending more that 7 requests and using
                // an expanding interval between requests based on the 
                // estimated round-trip-time to the server.  This is because
                // some requests can be lost with UDP.
                
                // We only send if its connected because another thread might
                // have close the session (if ICE processing has finished and
                // this pair is not being used, for example).
                if (this.m_ioSession.isConnected())
                    {
                    this.m_ioSession.write(bindingRequest);
                    }
                
                // Wait a little longer with each send.
                waitTime = (2 * waitTime) + rto;
                
                requests++;
                m_log.debug("Wrote Binding Request number: {}", requests);
                }
            
            // Now we wait for 1.6 seconds after the last request was sent.
            // If we still don't receive a response, then the transaction 
            // has failed.  
            if (!this.m_transactionCanceled)
                {
                waitIfNoResponse(bindingRequest, 1600);
                }

            // Even if the transaction was canceled, we still may have 
            // received a successful response.  If we did, we process it as
            // usual.  This is specified in 7.2.1.4.
            if (m_idsToResponses.containsKey(id))
                {
                final StunMessage response = this.m_idsToResponses.remove(id);
                m_log.debug("Received STUN response: {}", response);
                return response;
                }
            
            if (this.m_transactionCanceled || 
                this.m_closed || 
                this.m_ioSession.isClosing())
                {
                m_log.debug("The transaction was canceled!");
                return new CanceledStunMessage();
                }
            
            else
                {
                // This will happen quite often, such as when we haven't
                // yet successfully punched a hole in the firewall.
                m_log.debug("Did not get response on: {}", this.m_ioSession);
                return new NullStunMessage();
                }
            }
        }
    }
