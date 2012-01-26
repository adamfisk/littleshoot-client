package org.lastbamboo.server.resource;

import java.util.Date;
import java.util.Set;

/**
 * Interface containing metadata for a specific file on the network, such as
 * all of the source for that file, all of the tags for that file, etc.
 */
public interface MetaFileResource extends VisitableResource
    {

    /**
     * Adds the specified user to the set of users who have this resource.
     * 
     * @param ur The user with the resource.
     */
    void addInstance(OnlineInstance ur);
    
    /**
     * Removes the specified user from the set of users who have this resource.
     * 
     * @param ur The user with the resource.
     */
    void removeInstance(OnlineInstance ur);
    
    /**
     * Accessor for the number of instances online who have the file.
     * 
     * @return The number of users online who have the file.
     */
    public int getNumOnlineInstances();
    
    /**
     * Sets the number of users online who have the file.
     * 
     * @param numOnlineInstances The number of instances online who have the 
     * file.
     */
    void setNumOnlineInstances(int numOnlineInstances);
    
    
    Set<OnlineInstance> getInstances();
    
    void setInstances(Set<OnlineInstance> instances);
    
    String getSha1Urn();

    void setSha1Urn(String urn);
    
    void setUri(String uri);

    String getUri();
    
    long getSize();
    
    void setSize(long size);
    
    boolean isTakenDown();

    void setTakenDown(boolean takenDown);

    String getTitle();

    String getMimeType();
    
    void setMimeType(String mimeType);
    
    void setTitle(String title);

    void addTags(String tags);

    void addTitle(String title);
    
    void setNumDownloads(int numDownloads);

    int getNumDownloads();
    
    void setPublishTime(Date publishTime);

    Date getPublishTime();
    
    void setPermission(int permission);

    int getPermission();

    String getTags();
    

    void setNumRatings(int numRatings);

    int getNumRatings();

    void setAverageRating(double averageRating);

    double getAverageRating();
    
    }
