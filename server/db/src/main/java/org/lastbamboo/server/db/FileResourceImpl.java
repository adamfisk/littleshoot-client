package org.lastbamboo.server.db;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.util.ShootConstants;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.ResourceVisitor;

/**
 * Bean for a simple file resource.
 */
public class FileResourceImpl implements FileResource
    {

    /**
     * The Hibernate ID of this file resource.
     */
    private Long m_id;
    
    private String m_uri;
    private String m_tags;
    private long m_size;
    private String m_sha1;
    private long m_instanceId;
    private String m_name;
    private String m_mimeType;
    private Date m_lastModified;
    private String m_creator;
    private String m_title;
    private String m_remoteHost;
    private String m_mediaType;
    private String m_language;
    private String m_country;
    private String m_timeZone;
    private long m_userId;
    
    private int m_permission;
    
    /**
     * Whether or not this file is being shared as the result of being in the 
     * default download directory.
     */
    private boolean m_downloaded;

    private Date m_publishTime;

    private String m_groupName;

    /**
     * Required no-argument constructor Hibernate uses to generate new 
     * instances.
     */
    public FileResourceImpl() 
        {
        // No argument constructor for hibernate to use.
        }
    
    /**
     * Creates a new file resource. This constructor uses the SHA-1 for the URI.
     * 
     * @param name The name of the resource.
     * @param mimeType The MIME type for the resource.
     * @param sha1 The hash for the resource.
     * @param bytes The size of the resource.
     * @param instanceId The ID of the LittleShoot instance submitting the 
     * resource.
     * @param remoteHost The remote host.
     * @param tags The tags for the resource.
     * @param mediaType The type of media for the file.
     * @param timeZone The time zone of the user who published the file.
     * @param country The country of the user who published the file.
     * @param language The language of the user who published the file.
     * @param downloaded Whether or not the file is being published as the 
     * result of a download. 
     * @param userId The ID of the user.
     */
    public FileResourceImpl(final String name, final String mimeType, 
        final String sha1, final long bytes, final long instanceId, 
        final String remoteHost, final String tags, final String mediaType, 
        final String language, final String country, final String timeZone, 
        final boolean downloaded, final long userId)
        {
        this (sha1, name, mimeType, sha1, bytes, instanceId, remoteHost, tags, 
            mediaType, language, country, timeZone, downloaded, userId);
        }
    
    /**
     * Creates a new file resource. This constructor uses the SHA-1 for the URI.
     * 
     * @param uri The URI for the file.
     * @param name The name of the resource.
     * @param mimeType The MIME type for the resource.
     * @param sha1 The hash for the resource.
     * @param bytes The size of the resource.
     * @param instanceId The ID of the LittleShoot instance submitting the 
     * resource.
     * @param remoteHost The remote host.
     * @param tags The tags for the resource.
     * @param mediaType The type of media for the file.
     * @param timeZone The time zone of the user who published the file.
     * @param country The country of the user who published the file.
     * @param language The language of the user who published the file.
     * @param downloaded Whether or not the file is being published as the 
     * result of a download. 
     * @param userId The ID of the user.
     */
    public FileResourceImpl(final String uri, final String name, 
        final String mimeType, final String sha1, final long bytes, 
        final long instanceId, final String remoteHost, final String tags, 
        final String mediaType, final String language, final String country, 
        final String timeZone, final boolean downloaded, final long userId)
        {
        this (uri, name, mimeType, sha1, bytes, instanceId, remoteHost, tags, 
            mediaType, language, country, timeZone, downloaded, userId,
            Permission.PUBLIC, ShootConstants.WORLD_GROUP);
        }
    
    /**
     * Creates a new file resource. This constructor uses the SHA-1 for the URI.
     * 
     * @param name The name of the resource.
     * @param mimeType The MIME type for the resource.
     * @param sha1 The hash for the resource.
     * @param bytes The size of the resource.
     * @param instanceId The ID of the LittleShoot instance submitting the 
     * resource.
     * @param remoteHost The remote host.
     * @param tags The tags for the resource.
     * @param mediaType The type of media for the file.
     * @param timeZone The time zone of the user who published the file.
     * @param country The country of the user who published the file.
     * @param language The language of the user who published the file.
     * @param downloaded Whether or not the file is being published as the 
     * result of a download. 
     * @param userId The ID of the user.
     * @param group The group the resource belongs to.
     * @param groupName The name of the group the resource belongs to.
     */
    public FileResourceImpl(final String name, final String mimeType, 
        final String sha1, final long bytes, final long instanceId, 
        final String remoteHost, final String tags, final String mediaType, 
        final String language, final String country, final String timeZone, 
        final boolean downloaded, final long userId, final int group,
        final String groupName)
        {
        this (sha1, name, mimeType, sha1, bytes, instanceId, remoteHost, tags, 
            mediaType, language, country, timeZone, downloaded, userId,
            group, groupName);
        }
    
    /**
     * Creates a new file resource.
     * 
     * @param uri The URI for the file.
     * @param name The name of the resource.
     * @param mimeType The MIME type for the resource.
     * @param sha1 The hash for the resource.
     * @param bytes The size of the resource.
     * @param instanceId The ID of the LittleShoot instance submitting the 
     * resource.
     * @param remoteHost The remote host.
     * @param tags The tags for the resource.
     * @param mediaType The type of media for the file.
     * @param timeZone The time zone of the user who published the file.
     * @param country The country of the user who published the file.
     * @param language The language of the user who published the file.
     * @param downloaded Whether or not the file is being published as the result
     * of a download. 
     * @param userId The ID of the user.
     * @param permission The permission level.
     * @param groupName The name of the group to publish to. 
     */
    public FileResourceImpl(final String uri, final String name, 
        final String mimeType, final String sha1, final long bytes, 
        final long instanceId, 
        final String remoteHost, final String tags, final String mediaType, 
        final String language, final String country, final String timeZone, 
        final boolean downloaded, final long userId, final int permission, 
        final String groupName)
        {
        if (StringUtils.isBlank(groupName))
            {
            throw new IllegalArgumentException("Need to specify a group name");
            }
        m_name = name;
        m_mimeType = mimeType;
        m_title = name;
        m_sha1 = sha1;
        m_size = bytes;
        m_instanceId = instanceId;
        m_remoteHost = remoteHost;
        m_tags = tags;
        m_language = language;
        m_country = country;
        m_timeZone = timeZone;
        m_permission = permission;
        this.m_groupName = groupName;
        m_lastModified = new Date();
        m_uri = uri;
        m_mediaType = mediaType;
        m_downloaded = downloaded; 
        m_userId = userId;
        m_publishTime = new Date();
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
        return this.m_instanceId;
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

    public String getUri()
        {
        return this.m_uri;
        }

    public void setCreator(final String creator)
        {
        this.m_creator = creator;
        }

    public void setLastModified(final Date date)
        {
        this.m_lastModified = date;
        }

    public void setMimeType(final String mimeType)
        {
        this.m_mimeType = mimeType;
        }

    public void setName(final String name)
        {
        this.m_name = name;
        }

    public void setInstanceId(final long instanceId)
        {
        this.m_instanceId = instanceId;
        }

    public void setSha1Urn(final String urn)
        {
        this.m_sha1 = urn;
        }

    public void setSize(final long size)
        {
        this.m_size = size;
        }

    public void setTags(final String tags)
        {
        this.m_tags = tags;
        }

    public void setUri(final String uri)
        {
        this.m_uri = uri;
        }
    
    public String getTitle()
        {
        return this.m_title;
        }

    public void setTitle(final String title)
        {
        this.m_title = title;
        }
    
    public String getRemoteHost()
        {
        return m_remoteHost;
        }

    public void setRemoteHost(final String remoteHost)
        {
        m_remoteHost = remoteHost;
        }

    public String getMediaType()
        {
        return m_mediaType;
        }

    public void setMediaType(final String mediaType)
        {
        m_mediaType = mediaType;
        }
    
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
    
    public void setDownloaded(final boolean downloaded)
        {
        this.m_downloaded = downloaded;
        }
    
    public boolean getDownloaded()
        {
        return this.m_downloaded;
        }

    public long getUserId()
        {
        return this.m_userId;
        }

    public void setUserId(long userId)
        {
        this.m_userId = userId;
        }

    public void setPublishTime(Date publishTime)
        {
        m_publishTime = publishTime;
        }

    public Date getPublishTime()
        {
        return m_publishTime;
        }

    public int getPermission()
        {
        return m_permission;
        }

    public void setPermission(final int permission)
        {
        this.m_permission = permission;
        }

    public String getGroupName()
        {
        return this.m_groupName;
        }

    public void setGroupName(final String group)
        {
        this.m_groupName = group;
        }
    
    public <T> T accept(final ResourceVisitor<T> visitor)
        {
        return visitor.visitFileResource(this);
        }

    @Override
    public String toString()
        {
        return getClass().getSimpleName() + " " + getTitle();
        }

    }
