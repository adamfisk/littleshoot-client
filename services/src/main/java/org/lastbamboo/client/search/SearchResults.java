package org.lastbamboo.client.search;

import java.util.Collection;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.SearchRequestBean;

/**
 * Interface for the search results for a single search.
 * 
 * @param <T> A class extending {@link RestResult}.
 */
public interface SearchResults<T extends RestResult> 
    {

    /**
     * Adds the given results to the results for this search.  
     * 
     * @param results The results to add.
     */
    void addResults(final RestResults<T> results);

    /**
     * Accesses the search results at the specified index with the specified
     * number of results in the set.
     * 
     * @param numResults The number of results to return.
     * @param resultsIndex The starting index of the results.
     * @return The {@link Collection} of results.
     */
    Collection<T> getResults(int numResults, int resultsIndex);

    /**
     * Accessor for the total number of search results.
     * 
     * @return The total number of search results available.
     */
    int getTotalResults();

    /**
     * Returns whether or not all the search sources have returned their 
     * results.
     * 
     * @return <code>true</code> if all the search sources have returned 
     * their results, otherwise <code>false</code>.
     */
    boolean isComplete();
    
    /**
     * Returns the original search request.
     * 
     * @return The original search request.
     */
    SearchRequestBean getRequestBean();
    
    /**
     * Accessor for the GUID.
     * 
     * @return The search GUID.
     */
    UUID getGuid();

    }
