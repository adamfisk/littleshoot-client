package org.lastbamboo.server.db;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.FileResourceResult;

/**
 * Interface for the DAO class for accessing file resource data.
 */
public interface FileResourceDao
    {

    /**
     * Inserts a new resource into the repository.
     * 
     * @param fr The file resource to insert.
     * @throws IOException If there's an error adding the resource.
     */
    void insertResource(FileResource fr) throws IOException;
    
    /**
     * Deletes a resource from the repository.
     * 
     * @param instanceId The ID of the instance.
     * @param sha1 The SHA-1 URN of the resource.
     * 
     * @throws IOException If there's an error adding the resource.
     */
    void deleteResource(long instanceId, String sha1) throws IOException;

    /**
     * Accessor for the file resource results.
     * 
     * @param pageIndex The start index of the page.
     * @param resultsPerPage The results to include per page.
     * @param instanceId The ID of the instance.
     * @return The result data.
     */
    FileResourceResult getFileResources(final int pageIndex, 
        final int resultsPerPage, final long instanceId);
    
    /**
     * Accessor for the file resource results for a specific group.
     * 
     * @param pageIndex The start index of the page.
     * @param resultsPerPage The results to include per page.
     * @param instanceId The ID of the instance.
     * @param groupName The name of the group.
     * @return The result data.
     */
    FileResourceResult getFileResources(int pageIndex, int resultsPerPage,
        long instanceId, String groupName);

    /**
     * Accessor for a single file resource.
     * 
     * @param instanceId The ID of the instance.
     * @param sha1 The SHA-1 of the resource.
     * @return The {@link FileResource} or <code>null</code> if it doesn't exist.
     */
    FileResource getFileResource(long instanceId, String sha1);

    /**
     * Edits the file resource.
     * 
     * @param instanceId The instance ID of the resource.
     * @param sha1 The SHA-1 for the resource.
     * @param tags The tags.
     * @param url The URL.
     * @throws FileNotFoundException If we could not locate the resource.
     */
    void editResource(long instanceId, String sha1, String tags, String url)
        throws FileNotFoundException;

    }
