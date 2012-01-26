package org.lastbamboo.server.services.command;

import java.net.URI;

/**
 * Bean for data for publishing a URL resource.
 */
public class PublishUrlCommand
    {

    private URI m_url;
    private String m_title;

    public void setUrl(final URI uri)
        {
        this.m_url = uri;
        }
    
    public URI getUrl()
        {
        return this.m_url;
        }
    
    public void setTitle(final String title)
        {
        this.m_title = title;
        }

    public String getTitle()
        {
        return m_title;
        }
    }
