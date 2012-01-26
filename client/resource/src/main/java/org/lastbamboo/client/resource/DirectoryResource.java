package org.lastbamboo.client.resource;


/**
 * Interface for shared directory resources.
 */
public interface DirectoryResource extends VisitableResource
    {

    /**
     * Accessor for the directory.
     * 
     * @return The directory.
     */
    String getDir();
    
    /**
     * Sets the shared directory.
     * 
     * @param dir The shared directory.
     */
    void setDir(final String dir);
    
    /**
     * Gets the tags for the directory.
     * 
     * @return The tags for the directory.
     */
    String getTags();
    
    /**
     * Sets the tags for the directory.
     * @param tags The tags for the directory.
     */
    void setTags(String tags);
    
    /**
     * Accessor for the user ID.
     * 
     * @return The user ID.
     */
    long getUserId();
    
    /**
     * Mutator for the user ID.
     * @param userId The user ID.
     */
    void setUserId(long userId);
    
    /**
     * Accessor for the title of the directory.
     * @return The title of the directory.
     */
    String getTitle();
    
    /**
     * Sets the title of the directory.
     * @param title The title of the directory.
     */
    void setTitle(String title);
    
    /**
     * Accessor for the number of files in the directory, excluding 
     * subdirectories.
     * @return The number of files in the directory, excluding subdirectories.
     */
    int getNumFiles();

    /**
     * Sets the number of files in the directory, excluding subdirectories.
     * 
     * @param numFiles The number of files in the directory, excluding 
     * subdirectories.
     */
    void setNumFiles(int numFiles);

    /**
     * Updates the data for the directory.
     */
    void updateData();
        
    }