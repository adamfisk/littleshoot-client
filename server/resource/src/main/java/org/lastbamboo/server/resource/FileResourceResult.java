package org.lastbamboo.server.resource;

import java.util.Collection;

/**
 * Interface for {@link FileResource} data from the database.
 */
public interface FileResourceResult
    {

    /**
     * Accessor for the {@link Collection} of resources.
     * 
     * @return The {@link Collection} of resources.
     */
    Collection<FileResource> getResources();

    /**
     * Accessor for the total number of results.
     * @return The total number of results available.
     */
    int getTotalResults();
    }
