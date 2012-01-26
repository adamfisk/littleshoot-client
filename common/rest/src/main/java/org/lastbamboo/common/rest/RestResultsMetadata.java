package org.lastbamboo.common.rest;


/**
 * Interface for classes containing metadata about search results.
 */
public interface RestResultsMetadata<T extends RestResult> 
    {

    /**
     * Accessor for the source identifier.
     * 
     * @return The source identifier.
     */
    RestResultSources getSource();
    
    /**
     * Returns the total number of available results.
     * 
     * @return The total number of results available.
     */
    int getTotalResults();

    RestSearcher<T> getSearcher();

    }
