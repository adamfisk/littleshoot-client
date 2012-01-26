package org.lastbamboo.client.search;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.SearchRequestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the class that processes incoming search results.  This
 * inserts results into the database for later retrieval from webapp requests.
 * 
 * NOTE: We only store the session mappings separately here (outside the 
 * session attribute map itself) because otherwise we'd have to pass the
 * session data down through all the search classes (since they make
 * asynchronous callbacks).  This is a little simpler.
 * 
 * @param <T> Class extending {@link RestResult}
 */
public final class SearchResultManager<T extends RestResult> 
    implements SearchResultProcessor<T>, SearchResultAccessor<T>
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass()); 
    
    /**
     * We only support a few sessions to preserve memory.
     */
    private final int MAX_SESSIONS = 3;
    
    private final Map<String, SessionResults<T>> m_sessionMap = 
        Collections.synchronizedMap(new LinkedHashMap<String, SessionResults<T>>()
            {
            private static final long serialVersionUID = 
                -196782790713546383L;
        
            @Override
            protected boolean removeEldestEntry(
                final Map.Entry<String, SessionResults<T>> eldest) 
                {
                // This makes the map automatically purge the least used
                // entry.  We don't support that many sessions because we
                // can quickly run out of memory.
                final boolean remove = size() > MAX_SESSIONS;
                
                if (remove)
                    {
                    m_messageIdToSessionId.remove(eldest.getKey());
                    }
                return remove;
                }
            });
    
    private final Map<UUID, String> m_messageIdToSessionId =
        new ConcurrentHashMap<UUID, String>();

    public synchronized void processResults(final UUID messageId, 
        final RestResults<T> results)
        {
        final String sessionId = m_messageIdToSessionId.get(messageId);
        final SessionResults<T> sessionResults = 
            this.m_sessionMap.get(sessionId);
        
        if (sessionResults == null)
            {
            // This can happen if results come in after the session has
            // expired.
            m_log.debug("Session expired!");
            return;
            }
        sessionResults.addResults(messageId, results);
        }

    public void sessionCreated(final HttpSessionEvent hse)
        {
        // Ignored for now.
        }

    public synchronized void sessionDestroyed(final HttpSessionEvent hse)
        {
        m_log.debug("Session destroyed...");
        final String sessionId = hse.getSession().getId();
        if (this.m_sessionMap.containsKey(sessionId))
            {
            m_log.debug("Removing session from map...");
            this.m_sessionMap.remove(sessionId);
            }
        
        removeSessionId(sessionId);
        }

    /**
     * Utility method for removing all entries for the given session ID from
     * the map.
     * @param sessionId The session ID to remove.
     */
    private void removeSessionId(final String sessionId)
        {
        final Set<Entry<UUID, String>> entries = 
            this.m_messageIdToSessionId.entrySet();
        final Collection<UUID> idsToRemove = new LinkedList<UUID>();
        
        for (final Entry<UUID, String> entry : entries)
            {
            if (entry.getValue().equals(sessionId))
                {
                idsToRemove.add(entry.getKey());
                }
            }
        for (final UUID id : idsToRemove)
            {
            this.m_messageIdToSessionId.remove(id);
            }
        }
    
    public synchronized void addMapping(final String sessionId, 
        final UUID messageId, final SearchRequestBean request)
        {
        final SessionResults<T> results;
        if (!m_sessionMap.containsKey(sessionId))
            {
            results = new SessionResultsImpl<T>(sessionId);
            m_sessionMap.put(sessionId, results);
            }
        else
            {
            results = m_sessionMap.get(sessionId);
            }
        
        results.addSearch(request, messageId);
        m_messageIdToSessionId.put(messageId, sessionId);
        }

    public synchronized SessionResults<T> getResults(final String sessionId, 
        final UUID messageId)
        {
        return m_sessionMap.get(sessionId);
        }
    
    public boolean hasMapping(final String sessionId, final String messageId)
        {
        if (StringUtils.isBlank(messageId))
            {
            m_log.warn("Blank message ID!!");
            return false;
            }
        final SessionResults<T> sr = m_sessionMap.get(sessionId);
        if (sr == null)
            {
            synchronized (m_sessionMap)
                {
                m_log.warn("No match for session ID: "+sessionId+
                    "  We have:\n"+m_sessionMap.keySet());
                }
            return false;
            }
        final boolean hasMessageId =  sr.hasMapping(new UUID(messageId));
        if (!hasMessageId)
            {
            m_log.warn("No message ID!!!");
            }
        return hasMessageId;
        }
    }
