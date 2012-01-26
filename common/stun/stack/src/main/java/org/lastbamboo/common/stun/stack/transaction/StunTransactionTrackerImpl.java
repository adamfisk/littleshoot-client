package org.lastbamboo.common.stun.stack.transaction;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.id.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.stun.stack.message.StunMessage;

/**
 * Class for matching requests and responses to their associated transactions.
 */
public class StunTransactionTrackerImpl 
    implements StunTransactionTracker<StunMessage>, StunTransactionListener
    {
    
    private final Logger LOG = LoggerFactory.getLogger(StunTransactionTrackerImpl.class);
    
    private final Map<UUID, StunClientTransaction<StunMessage>> m_transactions = 
        new ConcurrentHashMap<UUID, StunClientTransaction<StunMessage>>();
    
    public void addTransaction(final StunMessage request, 
        final StunTransactionListener listener, 
        final InetSocketAddress localAddress, 
        final InetSocketAddress remoteAddress)
        {
        final List<StunTransactionListener> transactionListeners = 
            new LinkedList<StunTransactionListener>();
        transactionListeners.add(listener);
        
        final StunClientTransaction<StunMessage> ct = 
            new StunClientTransactionImpl(request, transactionListeners,
                remoteAddress);
        trackTransaction(ct);
        }

    private void trackTransaction(final StunClientTransaction<StunMessage> ct)
        {
        LOG.debug("Tracking transaction...");
        final StunMessage message = ct.getRequest();
        final UUID key = getTransactionKey(message);
        this.m_transactions.put(key, ct);
        ct.addListener(this);
        }

    public StunClientTransaction<StunMessage> getClientTransaction(
        final StunMessage message)
        {
        LOG.debug("Accessing client transaction...");
        final UUID key = getTransactionKey(message);
        final StunClientTransaction<StunMessage> ct = 
            this.m_transactions.get(key);
        if (ct == null)
            {
            // This will happen fairly often with STUN using UDP because
            // multiple requests and responses could be sent.  We should just
            // silently ignore it.
            LOG.debug("Nothing known about transaction: "+key);
            LOG.debug("Known transactions: "+this.m_transactions.keySet());
            }
        return ct;
        }

    private UUID getTransactionKey(final StunMessage message)
        {
        return message.getTransactionId();
        }

    public Object onTransactionFailed(final StunMessage request,
        final StunMessage response)
        {
        LOG.debug("Transaction failed...");
        return removeTransaction(request);
        }

    public Object onTransactionSucceeded(final StunMessage request, 
        final StunMessage response)
        {
        LOG.debug("Transaction succeeded...");
        return removeTransaction(request);
        }

    private Object removeTransaction(final StunMessage message)
        {
        // We now consider the transaction completed and remove the 
        // transaction.
        final UUID key = getTransactionKey(message);
        
        LOG.debug("Removing transaction with key '" + key + "'");
        this.m_transactions.remove(key);
        return null;
        }
    }
