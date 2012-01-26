package org.lastbamboo.client.resource;

import java.io.File;
import java.io.IOException;

/**
 * Factory for creating file resource instances.
 */
public interface FileResourceFactory
    {


    /**
     * Creates a new file resource from the specified <code>File</code> on disk.
     * 
     * @param file the <code>File</code> on disk to use for generating a new
     * file resource.
     * @return a new <code>LocalFileResource</code> instance.
     * @throws IOException If there's any IO error creating the resource.
     */
    LocalFileResource createFileResource(File file) throws IOException;

    /**
     * Creates a new file resource from the specified <code>File</code> on disk
     * and the specified tags.
     * 
     * @param file the <code>File</code> on disk to use for generating a new
     * file resource.
     * @param tags The tags for the file.
     * @return a new <code>LocalFileResource</code> instance.
     * @throws IOException If there's any IO error creating the resource.
     */
    LocalFileResource createFileResource(File file, String tags) 
        throws IOException;
    }
