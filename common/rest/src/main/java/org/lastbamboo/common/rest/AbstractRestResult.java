package org.lastbamboo.common.rest;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.littleshoot.util.ResourceTypeTranslatorImpl;

/**
 * Generalized REST result with minimal fields.
 */
public abstract class AbstractRestResult implements RestResult
    {

    private final String m_title;
    private final URI m_url;
    private final long m_fileSize;
    private final String m_source;
    private final URI m_sha1;
    private final URI m_thumbnailUrl;
    private final String m_mimeType;
    private final String m_mediaType;
    private final int m_thumbnailWidth;
    private final int m_thumbnailHeight;
    
    private final String m_description;
    private final String m_author;
    private final int m_lengthSeconds;
    private float m_rating;

    /**
     * Creates a new REST result with the specified title, url, and file size.
     * 
     * @param title The title of the result.
     * @param url The URL for the result.
     * @param fileSize The size in bytes of the file, if any.
     * @param source The source of the result.
     * @param metadataUrl The URL for the metadata for the resource.
     * @param sha1 The SHA-1 URN for this resource.  Can be <code>null</code>.
     */
    protected AbstractRestResult(final String title, final URI url, 
        final URI thumbnailUrl, final int thumbWidth, final int thumbHeight,  
        final long fileSize, final String source, 
        final URI sha1, final String mimeType)
        {
        this (title, url, thumbnailUrl, thumbWidth, thumbHeight, fileSize,
            source, sha1, mimeType, StringUtils.EMPTY, StringUtils.EMPTY, -1, 
            -1);
        }

    protected AbstractRestResult(final String title, final URI url, 
        final URI thumbnailUrl, final int thumbnailWidth, 
        final int thumbnailHeight, final long fileSize, final String source, 
        final URI sha1, final String mimeType, 
        final String description, final String author, final int lengthSeconds,
        final float rating)
        {
        this.m_title = title;
        this.m_url = url;
        this.m_thumbnailUrl = thumbnailUrl;
        this.m_thumbnailWidth = thumbnailWidth;
        this.m_thumbnailHeight = thumbnailHeight;
        this.m_fileSize = fileSize;
        this.m_source = source;
        this.m_sha1 = sha1;
        this.m_mimeType = mimeType;
        this.m_rating = rating;
        this.m_mediaType = new ResourceTypeTranslatorImpl ().getType (title);
        this.m_description = description;
        this.m_author = author;
        this.m_lengthSeconds = lengthSeconds;
        }

    public long getFileSize()
        {
        return m_fileSize;
        }

    public String getTitle()
        {
        return m_title;
        }

    public URI getUrl()
        {
        return m_url;
        }
    
    public URI getSha1Urn()
        {
        return this.m_sha1;
        }
    
    public String getSource()
        {
        return m_source;
        }
    
    public URI getThumbnailUrl()
        {
        return m_thumbnailUrl;
        }
    
    public String getMimeType()
        {
        return m_mimeType;
        }
    
    public long getUserId()
        {
        return -1L;
        }
    
    public int getNumSources()
        {
        return -1;
        }
    
    public String getMediaType()
        {
        return this.m_mediaType;
        }

    public int getThumbnailWidth()
        {
        return m_thumbnailWidth;
        }

    public int getThumbnailHeight()
        {
        return m_thumbnailHeight;
        }

    public String getDescription()
        {
        return m_description;
        }

    public String getAuthor()
        {
        return m_author;
        }

    public int getLengthSeconds()
        {
        return m_lengthSeconds;
        }

    public float getRating()
        {
        return this.m_rating;
        }
    
    public byte[] getJson()
        {
        return null;
        }
    
    @Override
    public String toString() 
        {
        return getClass().getSimpleName()+ " with title: " + 
            getTitle() + 
            " URL: " + getUrl() +
            " size: " + getFileSize() +
            " sha1: " + getSha1Urn(); 
        }
    }
