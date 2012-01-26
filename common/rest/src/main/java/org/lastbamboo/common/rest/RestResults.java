package org.lastbamboo.common.rest;

import java.util.Collection;

/**
 * Interface for a set of results from a REST search provider.  This includes
 * metadata about the results, such as the total results available and the
 * currently available result set.
 * 
 * @param <T> The class extending {@link RestResult}.
 */
public interface RestResults<T extends RestResult>
    {
    
    /**
     * Accessor for the metadata for this result set.
     * 
     * @return The metadata for this result set.
     */
    RestResultsMetadata<T> getMetadata();

    /**
     * Accessor for the currently active result set.
     * 
     * @return The currently active result set.
     */
    Collection<T> getCurrentResults();

    /**
     * Adds the specified results.
     * 
     * @param results The results to add.
     */
    void addResults(Collection<T> results);

    /**
     * Returns whether or not there are more results available from this source.
     * 
     * @return <code>true</code> if there are more results available from this
     * source, otherwise <code>false</code>.
     */
    boolean hasMoreResults();

    /**
     * Returns whether or not this searcher considers itself complete.  
     * 
     * @return <code>true</code> if this searcher is complete, otherwise
     * <code>false</code>;
     */
    boolean isComplete();

    }
