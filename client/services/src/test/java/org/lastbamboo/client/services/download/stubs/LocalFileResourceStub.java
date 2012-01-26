package org.lastbamboo.client.services.download.stubs;

import java.util.Date;

import org.lastbamboo.client.resource.LocalFileResource;
import org.lastbamboo.client.resource.ResourceVisitor;


/**
 * Stub class for testing.
 */
public class LocalFileResourceStub implements LocalFileResource
    {

    private String m_creator = "test";
    private String m_uri = "test";
    private String m_title = "test";
    private String m_path = "test";
    private String m_name = "test";
    private String m_mimeType = "test";
    private long m_size = 77;
    private Date m_lastModified = new Date(77);
    private long m_ownerId = 77;
    private String m_url = "test";
    private String m_tags = "test_tags";
    private boolean m_inSharedDirectory;
    private String m_timeZone;
    private String m_country;
    private String m_language;
    private boolean m_downloaded;
    
    public String getCountry()
        {
        return m_country;
        }

    public void setCountry(String country)
        {
        m_country = country;
        }

    public String getLanguage()
        {
        return m_language;
        }

    public void setLanguage(String language)
        {
        m_language = language;
        }

    public String getTimeZone()
        {
        return m_timeZone;
        }

    public void setTimeZone(String timeZone)
        {
        m_timeZone = timeZone;
        }

    /**
     * Creates a new stub for testing.  All string fields are set to the 
     * defualt "test".  Integer and long fields are set to the default "77".
     */
    public LocalFileResourceStub()
        {    
        // Nothing to construct.
        }
    
    /**
     * Creates a new stub for testing.  All string fields are set to the given
     * string argument.  Integer and long fields are set to the default "77".
     * 
     * @param string The value for string fields.
     */
    public LocalFileResourceStub(final String string) 
        {
        this.m_creator = string;
        this.m_uri = string;
        this.m_title = string;
        this.m_path = string;
        this.m_name = string;
        this.m_mimeType = string;     
        this.m_url = string;
        }

    public String getCreator()
        {
        return this.m_creator;
        }

    public long getSize()
        {
        return this.m_size;
        }

    public Date getLastModified()
        {
        return this.m_lastModified;
        }

    public String getTitle()
        {
        return this.m_title;
        }

    public String getPath()
        {
        return this.m_path;
        }

    public String getName()
        {
        return this.m_name;
        }

    public void setCreator(final String creator)
        {
        this.m_creator = creator;
        }

    public void setSize(final long size)
        {
        this.m_size = size;
        }

    public void setTitle(final String title)
        {
        this.m_title = title;
        }

    public void setName(final String name)
        {
        this.m_name = name;
        }

    public String getMimeType()
        {
        return this.m_mimeType;
        }

    public void setMimeType(final String mimeType)
        {
        this.m_mimeType = mimeType;
        }

    public void setPath(final String path)
        {
        this.m_path = path;
        }

    public void setLastModified(final Date lastModified)
        {
        this.m_lastModified = lastModified;
        }

    public long getUserId ()
        {
        return (m_ownerId);
        }

    public void setUserId (final long personId)
        {
        m_ownerId = personId;
        }

    public void setSha1Urn(final String url)
        {
        this.m_url = url;
        }

    public String getSha1Urn()
        {
        return this.m_url;
        }

    public String getUri()
        {
        return this.m_uri;
        }

    public void setUri(final String uri)
        {
        this.m_uri = uri;
        }

    public String getTags()
        {
        return this.m_tags;
        }

    public void setTags(String tags)
        {
        this.m_tags = tags;
        }

    public void setInSharedDirectory(boolean inSharedDirectory)
        {
        this.m_inSharedDirectory = inSharedDirectory;
        }
    
    public boolean isInSharedDirectory()
        {
        return this.m_inSharedDirectory;
        }
    
    public boolean getDownloaded()
        {
        return this.m_downloaded;
        }

    public void setDownloaded(final boolean downloaded)
        {
        this.m_downloaded = downloaded;
        }
    
    public <T> T accept(final ResourceVisitor<T> visitor)
        {
        return visitor.visitLocalFileResource(this);
        }
    }


