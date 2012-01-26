package org.lastbamboo.client.search;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.rest.SearchRequestBean;

/**
 * Interface for all search implementations.
 */
public interface Searcher 
    {

    /**
     * Performs the search.
     * 
     * @param request The specific parameters of the current search.
     * 
     * @return The unique ID for the query.
     */
    UUID search(final SearchRequestBean request);

    }
