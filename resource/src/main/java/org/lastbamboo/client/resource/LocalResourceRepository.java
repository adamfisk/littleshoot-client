package org.lastbamboo.client.resource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;


/**
 * Interface for classes that are repositories for resources.  These 
 * could be different remote server implementations, a local database, or 
 * anything else that might be interested in storing resource data.
 */
public interface LocalResourceRepository
    {

    /**
     * Inserts a new file resource.
     * 
     * @param file The resource to insert.
     * @param tags The tags for the resource.
     * @return <code>true</code> if the resource was newly inserted, otherwise
     * <code>false</code>.
     * @throws IOException If there's any error inserting the resource into
     * the repository.
     */
    boolean insertResource(File file, String tags) throws IOException;
    
    /**
     * Deletes a resource from the repository.
     * 
     * @param file The file resource to delete
     * @throws IOException If there's an error deleting the resource.
     */
    void deleteResource(File file) throws IOException;
    
    /**
     * Updates the specified file's data.  The file resides in a shared
     * directory.
     * 
     * @param file The resource to update.
     * @param tags The tags for the file.
     * @return <code>true</code> if the resource was updated, otherwise 
     * <code>false</code>.
     * @throws IOException If there was an error updating the resource.
     */
    boolean updateDirectoryFile(File file, String tags) throws IOException;


    /**
     * Accesses a local resource by URN.
     * 
     * @param urn The SHA-1 URN for the local resource.
     * @return The local resource, or <code>null</code> if it does not exist.
     */
    LocalFileResource getResourceByUrn(final URI urn);

    /**
     * Accessor shared file resources at the specified page.
     * 
     * @param pageIndex The index of the page to return.
     * @param resultsPerPage The number of results to return for a page.
     * @return The shared file resources for the specified page.
     */
    LocalFileResourceResult getLocalResources(int pageIndex, 
        int resultsPerPage);
  
    /**
     * Accessor for all local shared file resources.  This does not include
     * resources in shared directories.
     * 
     * @return All local shared file resources.
     */
    Collection<LocalFileResource> getLocalResources();
   
    /**
     * Accessor for all local shared file resources, including resources in
     * shared directories and downloaded files.
     * 
     * @return All local shared file resources.
     */
    Collection<LocalFileResource> getAllLocalResources();

    /**
     * Accessor for all stored directory resources.
     * 
     * @return All stored directory resources.
     */
    Collection<DirectoryResource> getDirectoryResources();
    
    /**
     * Accessor for directory resources by page.
     * 
     * @param pageIndex The index of the page we're currently on.
     * @param resultsPerPage The number of results to display per page.
     * @return The {@link Collection} of directory resources.
     */
    DirectoryResourceResult getDirectoryResources(int pageIndex, 
        int resultsPerPage);

    /**
     * Removes the directory resource.
     * 
     * @param dir The directory resource to remove.
     */
    void removeDirectoryResource(File dir);

    /**
     * Inserts an already-constructed resource.
     * 
     * @param lfr The resource to insert.
     * @return <code>true</code> if the resource was inserted, otherwise 
     * <code>false</code>.
     */
    boolean insertResource(LocalFileResource lfr);
    

    /**
     * Inserts a new directory resource.
     * 
     * @param file The resource to insert.
     * @param tags The tags for the resource.
     * @return <code>true</code> if the resource was newly inserted, otherwise
     * <code>false</code>.
     * @throws IOException If there's any error inserting the resource into
     * the repository.
     */
    boolean insertDirectoryResource(final File file, final String tags) 
        throws IOException;

    /**
     * Accessor for the directory resource for the specified directory.
     * 
     * @param dir The directory on disk.
     * @return The associated {@link DirectoryResource} or <code>null</code> 
     * if the directory does not exist or is not shared.
     */
    DirectoryResource getDirectoryResource(File dir);

    /**
     * Updates data for the directory.  This does not update data for all
     * the enclosing files.
     * 
     * @param dir The directory to update.
     * @throws IOException If there's an IO error updating the directory.
     */
    void updateDirectoryData(File dir) throws IOException;

    }
