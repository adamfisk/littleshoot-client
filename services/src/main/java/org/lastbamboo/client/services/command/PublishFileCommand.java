package org.lastbamboo.client.services.command;

import java.io.File;

/**
 * Bean for data for publishing a resource.
 */
public class PublishFileCommand
    {

    private File m_file;
    
    private String m_tags;

    private String m_signature;
    
    private String m_groupName;
    
    private String m_twitterUserName;
    
    private String m_twitterPassword;
    
    private String m_twitterMessage;
    
    private boolean m_storeTwitterCredentials;
    
    private boolean m_useStoredTwitterCredentials;
    
    public String getTags()
        {
        return m_tags;
        }

    public void setTags(String tags)
        {
        m_tags = tags;
        }

    public void setFile(final File file)
        {
        this.m_file = file;
        }
    
    public File getFile()
        {
        return this.m_file;
        }

    public void setSignature(String signature)
        {
        m_signature = signature;
        }

    public String getSignature()
        {
        return m_signature;
        }

    public void setGroupName(final String groupName)
        {
        m_groupName = groupName;
        }

    public String getGroupName()
        {
        return m_groupName;
        }

    public void setTwitterUserName(String twitterUserName)
        {
        m_twitterUserName = twitterUserName;
        }

    public String getTwitterUserName()
        {
        return m_twitterUserName;
        }

    public void setTwitterPassword(String twitterPassword)
        {
        m_twitterPassword = twitterPassword;
        }

    public String getTwitterPassword()
        {
        return m_twitterPassword;
        }

    public void setTwitterMessage(String twitterMessage)
        {
        m_twitterMessage = twitterMessage;
        }

    public String getTwitterMessage()
        {
        return m_twitterMessage;
        }

    public void setStoreTwitterCredentials(boolean storeTwitterCredentials)
        {
        m_storeTwitterCredentials = storeTwitterCredentials;
        }

    public boolean isStoreTwitterCredentials()
        {
        return m_storeTwitterCredentials;
        }

    public void setUseStoredTwitterCredentials(boolean useStoredTwitterCredentials)
        {
        m_useStoredTwitterCredentials = useStoredTwitterCredentials;
        }

    public boolean isUseStoredTwitterCredentials()
        {
        return m_useStoredTwitterCredentials;
        }
    }
