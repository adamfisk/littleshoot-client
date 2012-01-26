package org.lastbamboo.server.services.command;



/**
 * Bean for download requests.
 */
public class DownloadCommand
    {

    private String m_uri;

    public void setUri(final String uri)
        {
        m_uri = uri;
        }

    public String getUri()
        {
        return m_uri;
        }
    
    }
