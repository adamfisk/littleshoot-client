package org.lastbamboo.server.services.command;

/**
 * Command class for file edit requests.
 */
public class EditFileCommand
    {

    private String m_sha1;
    private long m_instanceId;
    private long m_userId;
    
    private String m_tags;
    
    private String m_url;
    private String m_signature;

    public long getUserId()
        {
        return m_userId;
        }

    public String getSha1()
        {
        return m_sha1;
        }

    public void setSha1(String sha1)
        {
        m_sha1 = sha1;
        }

    public void setUserId(long userId)
        {
        m_userId = userId;
        }

    public void setInstanceId(long instanceId)
        {
        m_instanceId = instanceId;
        }

    public long getInstanceId()
        {
        return m_instanceId;
        }

    public void setTags(final String tags)
        {
        m_tags = tags;
        }

    public String getTags()
        {
        return m_tags;
        }

    public void setUrl(String url)
        {
        m_url = url;
        }

    public String getUrl()
        {
        return m_url;
        }

    public void setSignature(String signature)
        {
        m_signature = signature;
        }

    public String getSignature()
        {
        return m_signature;
        }

    }
