package org.lastbamboo.server.resource;

import java.util.Collection;

/**
 * Interface for beans containing result data for a query for file resources.
 * This will include metadata such as the total number of results available.
 */
public interface MetaFileResourceResult
    {

    /**
     * Get the actual results {@link Collection} for the search.
     * 
     * @return The {@link Collection} of matching results.
     */
    Collection<MetaFileResource> getResults();
    
    /**
     * Get the total number of results available.
     * 
     * @return The total number of results available.
     */
    int getTotalResults();
    
    }
