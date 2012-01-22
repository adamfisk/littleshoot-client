package org.lastbamboo.client.services;

import java.io.File;
import java.net.URI;
import java.util.Collection;

/**
 * Interface that maps file URNs to file paths.
 */
public interface FileMapper
    {

    /**
     * Maps the specified SHA-1 to the specified file.
     * 
     * @param uri The SHA-1 for the file.
     * @param file The file for the SHA-1.
     */
    void map(URI uri, File file);

    /**
     * Gets the file for the specified SHA-1.
     * 
     * @param sha1 The SHA-1 URN for a file.
     * @return The file mapped to the given SHA-1 URN.
     */
    File getFile(URI sha1);

    /**
     * Maps the specified file, computing its SHA-1 URN.
     * 
     * @param file The file to map.
     */
    void map(File file);

    Collection<File> getAllFiles();

    /**
     * Removes a file when all we know is the file itself.
     * 
     * @param onDisk The file on disk.
     */
    void removeFile(File onDisk);

    /**
     * Removes a file by URN.
     * 
     * @param sha1 The SHA-1 of the file.
     */
    void removeFile(URI sha1);

    boolean updateDirectoryFile(File file);

    /**
     * Accesses the hash for a file.  This is useful if the file no longer
     * exists, for example, but we still need to remove it from the database.
     * 
     * @param file The file.
     * @return The SHA-1 for the file.
     */
    URI getUri(File file);

    /**
     * Returns whether or not the file is already mapped just using the path
     * (not calculating the SHA-1).
     * 
     * @param file The file to check.
     * @return <code>true</code> if the file is mapped, otherwise 
     * <code>false</code>.
     */
    boolean hasFile(File file);

    /**
     * Clears all mappings. Particularly useful for tests.
     */
    void clear();
    
    /**
     * Returns whether or not the mapper has a file for the specified URI.
     * 
     * @param uri The URI identifying the file.
     * @return <code>true</code> if the mapper has the file, otherwise
     * <code>false</code>.
     */
    boolean hasFile(URI uri);

    }
