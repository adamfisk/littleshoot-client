package org.lastbamboo.server.services.command;

/**
 * Command class for requests to set users as online or offline.
 */
public class UserOnlineCommand
    {

    private boolean m_online;
    
    private long m_userId;
    
    private String m_baseUri;
    
    private String m_serverAddress;

    public String getServerAddress()
        {
        return m_serverAddress;
        }

    public void setServerAddress(String serverAddress)
        {
        m_serverAddress = serverAddress;
        }

    public boolean isOnline()
        {
        return m_online;
        }

    public void setOnline(boolean online)
        {
        m_online = online;
        }

    public long getUserId()
        {
        return m_userId;
        }

    public void setUserId(long userId)
        {
        m_userId = userId;
        }

    public String getBaseUri()
        {
        return m_baseUri;
        }

    public void setBaseUri(final String baseUri)
        {
        m_baseUri = baseUri;
        }
    
    }
