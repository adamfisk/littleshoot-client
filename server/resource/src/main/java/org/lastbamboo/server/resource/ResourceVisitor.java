package org.lastbamboo.server.resource;


/**
 * Interface for a specialized visitor for visiting resource instances.
 * 
 * @param <T> The return type for this visitor.
 */
public interface ResourceVisitor<T>
    {
    
    /**
     * Visits the specified audio file resource.
     * 
     * @param afr the <code>AudioFileResource</code> to visit.
     * @return The return type for this visitor.
     */
    T visitAudioFileResource(AudioFileResource afr);

    /**
     * Visits the specified file resource.
     * 
     * @param resource The file resource to visit.
     * @return The return type for this visitor.
     */
    T visitFileResource(FileResource resource);

    /**
     * Visits the specified file resource.
     * 
     * @param resource The file resource to visit.
     * @return The return type for this visitor.
     */
    T visitMetaFileResource(MetaFileResource resource);

    }
