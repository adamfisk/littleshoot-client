package org.lastbamboo.client.resource;

import java.io.IOException;

/**
 * Interface for a remote repository of resources.
 */
public interface RemoteResourceRepository
    {

    /**
     * Inserts the specified resource.
     * 
     * @param lfr The resource to insert.
     * @throws IOException If there's an IO error accessing the server.
     */
    void insertResource(LocalFileResource lfr) throws IOException;

    /**
     * Deletes the specified resource.
     * 
     * @param lfr The resource to delete.
     * @throws IOException If there's an IO error accessing the server.
     */
    void deleteResource(LocalFileResource lfr) throws IOException;

    }
