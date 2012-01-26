package org.lastbamboo.common.ice;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.StunMessage;

/**
 * Keeps track of Binding Requests we've seen to avoid processing a request
 * for the same transaction twice.
 */
public class IceBindingRequestTrackerImpl implements IceBindingRequestTracker
    {
    
    private final Collection<UUID> m_transactionIds = 
        Collections.synchronizedSet(new LinkedHashSet<UUID>());
    
    private static final int UUIDS_TO_STORE = 200;
    
    private final Map<UUID, StunMessage> m_requestsToResponses = 
        Collections.synchronizedMap(new LinkedHashMap<UUID, StunMessage>()
            {

            private static final long serialVersionUID = 48319719L;

            @Override
            protected boolean removeEldestEntry(
                final Map.Entry<UUID, StunMessage> eldest) 
                {
                // This makes the map automatically purge the least used
                // entry.  
                final boolean remove = size() > 200;
                return remove;
                }
            });
    
    public void add(final BindingRequest request)
        {
        synchronized (m_transactionIds)
            {
            if (this.m_transactionIds.size() >= UUIDS_TO_STORE)
                {
                final UUID lastIn = this.m_transactionIds.iterator().next();
                this.m_transactionIds.remove(lastIn);
                }
            this.m_transactionIds.add(request.getTransactionId());
            }
        }

    public boolean recentlyProcessed(final BindingRequest request)
        {
        return this.m_transactionIds.contains(request.getTransactionId());
        }

    public void addResponse(final BindingRequest request, 
        final StunMessage response) 
        {
        this.m_requestsToResponses.put(request.getTransactionId(), response);
        }

    public StunMessage getResponse(final BindingRequest request) 
        {
        return m_requestsToResponses.get(request.getTransactionId());
        }

    }
