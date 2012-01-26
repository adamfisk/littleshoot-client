package org.lastbamboo.client.search;

import java.util.Collection;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.SearchRequestBean;

/**
 * Interface for classes maintaining search results for a given session.
 * 
 * @param <T> Class extending {@link RestResult}.
 */
public interface SessionResults<T extends RestResult> 
    {


    /**
     * Adds the specified <code>List</code> of results for the specified 
     * unique message ID.
     * 
     * @param messageId The ID of the results to access.
     * @param results The search results to add.
     */
    void addResults(final UUID messageId, final RestResults<T> results);

    /**
     * Adds a search for this session.  This stores data for the UI 
     * regarding the search, such as the search terms themselves.
     * 
     * @param request The search request.
     * @param messageId The ID of the search message.
     */
    void addSearch(final SearchRequestBean request, final UUID messageId);

    /**
     * Accessor for the results for the most recent search.
     * 
     * @param numResults The number of results to return.
     * @param resultsIndex The starting index of the results to return.
     * @return The {@link Collection} of results.
     */
    Collection<T> getLatest(int numResults, int resultsIndex);

    /**
     * Accessor for the results for the specified search ID.
     * 
     * @param uuid The unique ID for the search to retrieve.
     * @param numResults The number of results to return.
     * @param resultsIndex The starting index of the results to return.
     * @return The {@link Collection} of results.
     */
    Collection<T> getResults(UUID uuid, int numResults, int resultsIndex);
    
    /**
     * Accessor for the total results available for the latest search.
     * 
     * @param guid The ID of the search to get the total results of.
     * @return The total results available for the latest search.
     */
    int getTotalResults(UUID guid);

    /**
     * Returns whether or not the most recent search has completed.
     * 
     * @param guid The ID of the search to check for completeness.
     * @return <code>true</code> if the most recent search has completed,
     * otherwise <code>false</code>.
     */
    boolean isComplete(UUID guid);

    /**
     * Returns the unique ID for the last search performed.
     * 
     * @return The unique ID for the last search performed.
     */
    UUID getLatestGuid();

    boolean hasMapping(UUID messageId);

    Collection<SearchResults<T>> getAllResults();
    
    /**
     * The ID of the session.
     * 
     * @return The ID of the session.
     */
    String getSessionId();

    
    }
