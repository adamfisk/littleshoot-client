package org.lastbamboo.client.search;

import javax.servlet.http.HttpSessionListener;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.rest.RestResult;

/**
 * Accessor for stored search results.
 * 
 * @param <T> Class extending {@link RestResult}.
 */
public interface SearchResultAccessor<T extends RestResult> 
    extends HttpSessionListener
    {
    
    /**
     * Determines whether or not there is a mapping for the session ID.
     * @param sessionId The ID of the session.
     * @param searchId The ID of the search.
     * @return <code>true</code> if there is a mapping for the given session,
     * otherwise <code>false</code>.
     * @throws IllegalArgumentException If any string argument is 
     * <code>null</code> or only whitespace.
     */
    boolean hasMapping(final String sessionId, String searchId);

    /**
     * Accesses results for the given message ID.
     * 
     * @param sessionId The ID of the session. 
     * @param searchId The ID of the search to access.
     * @return The results for the search with the given ID.
     */
    SessionResults<T> getResults(String sessionId, UUID searchId);

    }
