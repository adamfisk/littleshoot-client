package org.lastbamboo.server.services.command;

import org.lastbamboo.server.db.Permission;


/**
 * Bean for data for publishing a file resource.
 */
public class PublishFileCommand
    {

    private long m_userId = -1L;
    private long m_instanceId = -1L;
    private long m_keyId = -1L;
    private long m_bytes = -1L;
    private String m_sha1 = "";
    private String m_tags = "";
    private String m_name = "";
    private String m_country = "";
    private String m_timeZone = "";
    private String m_language = "";
    private boolean m_downloaded;
    private String m_signature = "";
    private int m_permission = Permission.PUBLIC; 
    private String m_groupName = "";
    
    private String m_uri = "";

    public long getInstanceId()
        {
        return this.m_instanceId;
        }

    public void setInstanceId(long instanceId)
        {
        this.m_instanceId = instanceId;
        }

    public String getCountry()
        {
        return m_country;
        }

    public void setCountry(final String country)
        {
        m_country = country;
        }

    public String getLanguage()
        {
        return m_language;
        }

    public void setLanguage(final String language)
        {
        m_language = language;
        }

    public String getTimeZone()
        {
        return m_timeZone;
        }

    public void setTimeZone(final String timeZone)
        {
        m_timeZone = timeZone;
        }

    public String getTags()
        {
        return m_tags;
        }

    public void setTags(String tags)
        {
        m_tags = tags;
        }

    public long getBytes()
        {
        return m_bytes;
        }

    public void setBytes(long bytes)
        {
        m_bytes = bytes;
        }

    public String getSha1()
        {
        return m_sha1;
        }

    public void setSha1(String sha1)
        {
        m_sha1 = sha1;
        }

    public long getUserId()
        {
        return m_userId;
        }

    public void setUserId(long userId)
        {
        m_userId = userId;
        }

    public String getName()
        {
        return this.m_name;
        }

    public void setName(final String name)
        {
        this.m_name = name;
        }

    public boolean isDownloaded()
        {
        return this.m_downloaded;
        }

    public void setDownloaded(boolean downloaded)
        {
        this.m_downloaded = downloaded;
        }

    public void setSignature(String signature)
        {
        m_signature = signature;
        }

    public String getSignature()
        {
        return m_signature;
        }

    public void setPermission(int permission)
        {
        m_permission = permission;
        }

    public int getPermission()
        {
        return m_permission;
        }

    public void setGroupName(String groupName)
        {
        m_groupName = groupName;
        }

    public String getGroupName()
        {
        return m_groupName;
        }

    public void setKeyId(long keyId)
        {
        m_keyId = keyId;
        }

    public long getKeyId()
        {
        return m_keyId;
        }

    public void setUri(String uri)
        {
        m_uri = uri;
        }

    public String getUri()
        {
        return m_uri;
        }
    }
