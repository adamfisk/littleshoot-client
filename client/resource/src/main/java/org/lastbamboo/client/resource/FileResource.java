package org.lastbamboo.client.resource;

import java.util.Date;


/**
 * Generic file resource interface.
 */
public interface FileResource extends VisitableResource
    {
    
    /**
     * Accessor for the name of this file resource.  The name is the name of
     * the file itself, which can differ from the title.
     * 
     * @return the name of this file resource.
     */
    String getName();
    
    /**
     * Sets the file name for the resource.  The name is the name of
     * the file itself, which can differ from the title.
     * 
     * @param name The name to use.
     */
    void setName(String name);
    
    /**
     * Accessor for the tags for the resource.
     * 
     * @return The tags for the resource.
     */
    String getTags();
    
    /**
     * Sets the tags for the file.
     * 
     * @param tags The tags for the file.
     */
    void setTags(String tags);
    
    /**
     * Accessor for the size of the file in bytes.
     * @return the size of the file in bytes.
     */
    long getSize();
    
    /**
     * Sets the size in bytes.
     * 
     * @param size The size to set.
     */
    void setSize(long size);
    
    /**
     * Accessor for the URI for this resource.  This will typically be either 
     * a SIP URI or an HTTP URL.  This cannot be <code>null</code>.  It is a 
     * string for easier database storage.
     * 
     * @return The URI for the resource.
     */
    String getUri();
    
    /**
     * Sets the URI.  This will typically be either a SIP URI or an HTTP URL.
     * 
     * @param uri The URI to use.
     */
    void setUri(String uri);
    
    /**
     * Returns the identifier of the person who owns this file resource.
     * 
     * @return The identifier of the person who owns this file resource.
     */
    long getUserId ();
    
    /**
     * Sets this file resource's owner to be the person identified by the given
     * identifier.
     * 
     * @param userId The identifier of the person who owns this file resource.
     */
    void setUserId (long userId);

    /**
     * Accessor for the title of the resource.
     * 
     * @return the title of the resource.
     */
    String getTitle();
    
    /**
     * Sets the title for this work.
     * 
     * @param title The title to set.
     */
    void setTitle(String title);

    /**
     * Accessor for the creator of this resource.
     * 
     * @return the creator of this resource, such as the artist for songs, the 
     * author for text-based works, etc.
     */
    String getCreator();
    
    /**
     * Sets the creator.
     * 
     * @param creator The creator to set.
     */
    void setCreator(String creator);
    
    /**
     * Accessor for the URN identifying this resource.
     * 
     * @return The URN identifying this resource.
     */
    String getSha1Urn();
    
    /**
     * Sets the URN identifying this resource.
     * 
     * @param urn The URN identifying this resource.
     */
    void setSha1Urn(String urn);
    
    /**
     * Accessor for the Mime type of the resource.
     * 
     * NOTE: It would be more space efficient not to store this and to just 
     * access it using the file name and the ServletContext, but that could be 
     * a pain in some situations.
     * 
     * @return The Mime type string for the resource.
     */
    String getMimeType();

    /**
     * Sets the mime type of the resource.
     * 
     * @param mimeType The mime type for the resource.
     */
    void setMimeType(String mimeType);
    
    /**
     * Accessor for the last modified time.
     * 
     * @return The number of milliseconds since Jan. 1 1970 marking the last 
     * time file was modified
     */
    Date getLastModified();
    
    /**
     * Sets the last modified field for the resource.
     * @param date The date for to set.
     */
    void setLastModified(Date date);

    }
