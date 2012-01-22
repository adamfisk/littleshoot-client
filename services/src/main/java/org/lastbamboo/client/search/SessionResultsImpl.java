package org.lastbamboo.client.search;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.SearchRequestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains search results for a specific session ID.  This handles grouping 
 * search results according to SHA-1 hash, filtering out duplicates, etc.
 * 
 * @param <T> Class extending {@link RestResult}.
 */
public final class SessionResultsImpl<T extends RestResult> 
    implements SessionResults<T>
    {

    /**
     * Limit for the number of searches to allow per session.
     */
    private static final int PER_SESSION_SEARCH_LIMIT = 5;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * Maps message IDs to the corresponding group of results for 
     * that query.
     */
    private final Map<UUID, SearchResults<T>> m_messageIdsToResults = 
        Collections.synchronizedMap(new LinkedHashMap<UUID, SearchResults<T>>()
            {
            private static final long serialVersionUID = -5039081869525524192L;

            @Override
            protected boolean removeEldestEntry(
                final Map.Entry<UUID, SearchResults<T>> eldest) 
                {
                // This makes the map automatically lose the least used
                // entry.  We don't support that many sessions because we
                // can quickly run out of memory.
                final boolean remove = size() > PER_SESSION_SEARCH_LIMIT;
                
                if (remove)
                    {
                    m_messageIdsToSearches.remove(eldest.getKey());
                    }
                return remove;
                } 
            });
    
    /**
     * <code>Map</code> of message <code>UUID</code>s to 
     * <code>SearchRequestBean</code>s.
     */
    private final Map<UUID, SearchRequestBean> m_messageIdsToSearches = 
        Collections.synchronizedMap(new LinkedHashMap<UUID, SearchRequestBean>());

    private UUID m_latestMessageId;

    private final String m_sessionId;
    
    /**
     * Creates a new set of search results for the given session.
     * 
     * @param sessionId The ID of the session.
     */
    public SessionResultsImpl(final String sessionId)
        {
        m_log.debug("Creating new session results for ID: {}", sessionId);
        this.m_sessionId = sessionId;
        }
    
    public void addSearch(final SearchRequestBean request, final UUID messageId)
        {
        m_log.trace("Adding search for ID: "+messageId);
        m_log.trace("Size of messageIdsToSearches : " +
            this.m_messageIdsToSearches.size());

        this.m_latestMessageId = messageId;
        this.m_messageIdsToSearches.put(messageId, request);
        
        // Add empty search results.
        final SearchResults<T> results = 
            new SearchResultsImpl<T>(request, messageId);
        this.m_messageIdsToResults.put(messageId, results);
        }
    
    public synchronized void addResults(final UUID messageId, 
        final RestResults<T> results)
        {
        m_log.trace("Adding results...");
        if (messageId == null)
            {
            m_log.warn("Null message ID");
            throw new NullPointerException("null message id");
            }
        
        if (results == null)
            {
            m_log.warn("Null result set.");
            throw new NullPointerException("null results");
            }
        final SearchResults<T> messageIdResults = 
            this.m_messageIdsToResults.get(messageId);
        
        if (messageIdResults != null)
            {
            messageIdResults.addResults(results);
            }
        }

    public Collection<T> getLatest(final int numResults, 
        final int resultsIndex)
        {
        return getResults(this.m_latestMessageId, numResults, resultsIndex);
        }
    
    public Collection<T> getResults(final UUID uuid, final int numResults, 
        final int resultsIndex)
        {
        final SearchResults<T> results = 
            this.m_messageIdsToResults.get(uuid);
        return results.getResults(numResults, resultsIndex);
        }

    public int getTotalResults(final UUID guid)
        {
        final SearchResults<T> results = this.m_messageIdsToResults.get(guid);
        return results.getTotalResults();
        }

    public boolean isComplete(final UUID guid)
        {
        final SearchResults<T> results = this.m_messageIdsToResults.get(guid);
        return results.isComplete();
        }

    public UUID getLatestGuid()
        {
        return this.m_latestMessageId;
        }

    public boolean hasMapping(final UUID messageId)
        {
        return this.m_messageIdsToResults.containsKey(messageId);
        }

    public synchronized Collection<SearchResults<T>> getAllResults()
        {
        final Collection<SearchResults<T>> results = 
            new LinkedList<SearchResults<T>>();
        
        final Collection<SearchResults<T>> values = 
            this.m_messageIdsToResults.values();
        
        results.addAll(values);
        return results;
        }

    public String getSessionId()
        {
        return m_sessionId;
        }

    }
