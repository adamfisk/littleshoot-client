package org.lastbamboo.client.resource;

/**
 * Interface for all file resources.
 */
public interface LocalFileResource extends FileResource
    {
    
    /**
     * Accessor for the file path.
     * 
     * @return the file path.
     */
    String getPath();
    
    /**
     * Sets the file path.
     * @param path The path of the file on disk.
     */
    void setPath(final String path);

    /**
     * Sets whether or not this resource is in a shared directory.
     * 
     * @param inSharedDirectory Whether or not this resource is in a shared
     * directory.
     */
    void setInSharedDirectory(boolean inSharedDirectory);
    
    /**
     * Accessor for whether or not this resource is in a shared directory.
     * 
     * @return <code>true</code> if this file is in a shared directory, 
     * otherwise <code>false</code>.
     */
    boolean isInSharedDirectory();
    
    String getCountry();

    void setCountry(String country);

    String getLanguage();

    void setLanguage(String language);

    String getTimeZone();

    void setTimeZone(String timeZone);

    /**
     * Sets whether or not this file was downloaded, as opposed to published.
     * 
     * @param downloaded Whether or not this file was downloaded, as opposed
     * to published.
     */
    void setDownloaded(boolean downloaded);
    
    /**
     * Gets whether or not this file was downloaded as opposed to published.
     * 
     * @return Whether or not this file was downloaded as opposed to published.
     */
    boolean getDownloaded();

    }
