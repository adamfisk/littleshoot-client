package org.lastbamboo.server.db.stubs;

import java.util.Date;

import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.ResourceVisitor;

/**
 * Testing stub.
 */
public class FileResourceStub implements FileResource
    {

    private String m_creator = "Example Creator";
    private Date m_lastModified = new Date();
    private String m_mimeType = "application/octet-stream";
    private String m_name = "Example Name";
    private long m_ownerId = 4214297L;
    private String m_remoteHost = "http://example.com";
    private String m_sha1 = "urn:sha1:EXAMPLE";
    private long m_size = 748392L;
    private String m_tags = "sample tags";
    private String m_title = "title";
    private String m_uri = "sip://4294230//uri-res/N2R?"+m_sha1;
    private String m_mediaType;
    
    private String m_country;
    private String m_timeZone;
    private String m_language;
    private MetaFileResource m_metaFileResource;
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

    public String getCreator()
        {
        return this.m_creator;
        }

    public Date getLastModified()
        {
        return this.m_lastModified;
        }

    public String getMimeType()
        {
        return this.m_mimeType;
        }

    public String getName()
        {
        return this.m_name;
        }

    public long getInstanceId()
        {
        return this.m_ownerId;
        }

    public String getRemoteHost()
        {
        return this.m_remoteHost;
        }

    public String getSha1Urn()
        {
        return this.m_sha1;
        }

    public long getSize()
        {
        return this.m_size;
        }

    public String getTags()
        {
        return this.m_tags;
        }

    public String getTitle()
        {
        return this.m_title;
        }

    public String getUri()
        {
        return this.m_uri;
        }

    public void setSha1Urn(String sha1)
        {
        m_sha1 = sha1;
        }

    public void setCreator(String creator)
        {
        m_creator = creator;
        }

    public void setLastModified(Date lastModified)
        {
        m_lastModified = lastModified;
        }

    public void setMimeType(String mimeType)
        {
        m_mimeType = mimeType;
        }

    public void setName(String name)
        {
        m_name = name;
        }

    public void setInstanceId(long ownerId)
        {
        m_ownerId = ownerId;
        }

    public void setRemoteHost(String remoteHost)
        {
        m_remoteHost = remoteHost;
        }

    public void setSize(long size)
        {
        m_size = size;
        }

    public void setTags(String tags)
        {
        m_tags = tags;
        }

    public void setTitle(String title)
        {
        m_title = title;
        }

    public void setUri(String uri)
        {
        m_uri = uri;
        }

    public <T> T accept(final ResourceVisitor<T> visitor)
        {
        return visitor.visitFileResource(this);
        }

    public String getMediaType()
        {
        return this.m_mediaType;
        }

    public void setMediaType(final String mediaType)
        {
        this.m_mediaType = mediaType;
        }

    public MetaFileResource getMetaFileResource()
        {
        return this.m_metaFileResource;
        }

    public void setMetaFileResource(MetaFileResource mfr)
        {
        this.m_metaFileResource = mfr;
        }

    public boolean getDownloaded()
        {
        return this.m_downloaded;
        }

    public void setDownloaded(boolean downloaded)
        {
        this.m_downloaded = downloaded;
        }

    public Date getPublishTime()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public long getUserId()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public void setPublishTime(Date publishTime)
        {
        // TODO Auto-generated method stub
        
        }

    public void setUserId(long userId)
        {
        // TODO Auto-generated method stub
        
        }

    public int getPermission()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public void setPermission(int permission)
        {
        // TODO Auto-generated method stub
        
        }

    public String getGroup()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void setGroup(String group)
        {
        // TODO Auto-generated method stub
        
        }

    public String getGroupName()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void setGroupName(String group)
        {
        // TODO Auto-generated method stub
        
        }
    }
