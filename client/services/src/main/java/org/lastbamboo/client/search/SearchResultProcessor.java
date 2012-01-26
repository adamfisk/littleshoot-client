package org.lastbamboo.client.search;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResultProcessor;
import org.lastbamboo.common.rest.SearchRequestBean;

/**
 * Processes incoming search results, inserting them into the database as 
 * appropriate.
 * @param <T> Class extending {@link RestResult}.
 */
public interface SearchResultProcessor<T extends RestResult> 
    extends RestResultProcessor<T>
    {

    /**
     * Adds a mapping from the given unique search message ID to the given 
     * webapp session ID.
     * 
     * @param sessionId The ID of the session.
     * @param request The search request.
     * @param messageId The search message ID to map.
     * @throws NullPointerException If any argument is <code>null</code>.
     */
    void addMapping(String sessionId, UUID messageId, 
        SearchRequestBean request);
    

    }
