package org.lastbamboo.common.rest;

import java.net.URI;

import org.json.JSONObject;

/**
 * Interface for generic results from REST interfaces.
 */
public interface RestResult
    {

    /**
     * Accessor for the size of the file.  Some REST APIs don't provide the 
     * size in bytes.  
     * 
     * @return The file size in bytes.
     */
    long getFileSize();

    /**
     * Accessor for the title of the result.
     * 
     * @return The title of the result.
     */
    String getTitle();
    
    /**
     * Accessor for the URL of the result.
     * 
     * @return The URL of the result.
     */
    URI getUrl();
    
    /**
     * Accessor for the URL of the thumbnail if there is one.  Can be 
     * <code>null</code>.
     * 
     * @return The URL of the thumbnail.  Can be <code>null</code>.
     */
    URI getThumbnailUrl();
    
    /**
     * Accessor for the source of the result.
     * 
     * @return The source of the results.
     */
    String getSource();

    /**
     * Accessor for the SHA-1 URN for this resource.  This can be 
     * <code>null</code>.
     * 
     * @return The SHA-1 URN for this resource.  This can be 
     * <code>null</code>.
     */
    URI getSha1Urn();
    
    /**
     * Returns the user ID for the result.  Returns -1 if there is no ID
     * associated with the result.
     * 
     * @return The user ID for the result.  Returns -1 if there is no ID
     * associated with the result.
     */
    long getUserId();

    /**
     * Returns the MIME type for the file.
     * 
     * @return The MIME type for the file.
     */
    String getMimeType();
    
    /**
     * Returns the general media type for the file.
     * 
     * @return The general media type for the file.
     */
    String getMediaType();

    int getNumSources();
    
    int getThumbnailWidth();

    int getThumbnailHeight();
    
    String getDescription();

    String getAuthor();
    
    int getLengthSeconds();
    
    float getRating();

    byte[] getJson();
    
    }
