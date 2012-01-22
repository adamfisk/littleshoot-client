package org.lastbamboo.client.resource;


/**
 * Interface for a specialized visitor for visiting resource instances.
 * @param <T> The type visitor methods return.
 */
public interface ResourceVisitor<T>
    {
    
    /**
     * Visits the specified file resource instance.
     * 
     * @param fr the <code>LocalFileResource</code> to visit.
     * @return The type visitor methods return.
     */
    T visitLocalFileResource(LocalFileResource fr);
    
    /**
     * Visits the specified audio file resource.
     * 
     * @param afr the <code>AudioFileResource</code> to visit.
     * @return The type visitor methods return.
     */
    T visitAudioFileResource(AudioFileResource afr);

    /**
     * Visits the specified file resource.
     * 
     * @param resource The file resource to visit.
     * @return The type visitor methods return.
     */
    T visitFileResource(FileResource resource);

    /**
     * Visits a directory resource.
     * 
     * @param dr The directory resource to visit.
     * @return The type visitor methods return.
     */
    T visitDirectoryResource(DirectoryResource dr);

    }
