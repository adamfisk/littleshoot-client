package org.lastbamboo.server.services.command;

/**
 * Command class for file removal requests.
 */
public class DeleteFileCommand
    {

    private String m_sha1;
    
    private long m_instanceId;
    private long m_userId;

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

    }
