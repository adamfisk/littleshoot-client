package org.lastbamboo.client.resource;

import java.util.Collection;

/**
 * Interface for classes containing data about results in addition to the
 * results themselves.  This can be useful for eliminating multiple database
 * accesses using scroll queries, for example.
 */
public interface DirectoryResourceResult
    {

    /**
     * Acessor for the {@link Collection} of resources.
     * 
     * @return The {@link Collection} of resources.
     */
    Collection<DirectoryResource> getResources();

    /**
     * Accessor for the total number of results.
     * @return The total number of results available.
     */
    int getTotalResults();

    }
