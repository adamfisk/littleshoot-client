package org.lastbamboo.client.services;

import java.io.File;
import java.io.IOException;

/**
 * Class that refreshes local resource data as resources change on disk.
 */
public interface FileRefresher
    {

    /**
     * Adds a directory to be refreshed.
     * 
     * @param dir The new directory to refresh.
     * @param tags The tags for the directory.
     * @throws IOException If there's an error adding the directory to the
     * database of directories.
     */
    void addDirectory(File dir, String tags) throws IOException;
    
    /**
     * Removes the specified directory from directories that are automatically
     * refreshed.
     * 
     * @param dir The directory to remove.
     */
    void removeDirectory(File dir);


    }
