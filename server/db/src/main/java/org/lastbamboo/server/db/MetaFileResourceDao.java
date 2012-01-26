package org.lastbamboo.server.db;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.MetaFileResource;

/**
 * Interface for containing metadata for an individual file resource.  The
 * metadata includes all the information in a specific {@link FileResource}
 * instance, but it also includes other information like all the user 
 * containing that resource, all the tags for that resource, etc.
 */
public interface MetaFileResourceDao
    {

    /**
     * Inserts the specified file resource.  If there's already an entry for
     * this resource (identified by SHA-1), this method adds any relevant 
     * metadata from the new resource to the meta resource.  If there's not 
     * already a MetaFileResource for the specified SHA-1, this adds it.
     * 
     * @param fr The resource to insert.
     * @return The new or existing {@link MetaFileResource} for the file.
     * @throws IOException If the resource cannot be published for any 
     * reason.
     */
    MetaFileResource insertResource(FileResource fr) throws IOException;

    /**
     * Deletes a resource from the repository.
     * 
     * @param instanceId The ID of the LittleShoot instance.
     * @param sha1 The SHA-1 URN of the resource.
     */
    void deleteResource(long instanceId, String sha1);

    /**
     * Marks the resource with the specified URN as "taken down", presumably
     * with a DMCA takedown notice.
     * 
     * @param sha1Urn The URN of the file to take down.
     * @param takeDown Whether to take the file down or to bring it up.
     */
    void takeDown(String sha1Urn, boolean takeDown);

    /**
     * Edit the specified resource.
     * 
     * @param sha1 The SHA-1 for the resource.
     * @param tags The tags.
     * @param url The associated URL.
     * @throws FileNotFoundException If we could not locate the resource.
     */
    void editResource(String sha1, String tags, String url)
        throws FileNotFoundException;
        
    }
