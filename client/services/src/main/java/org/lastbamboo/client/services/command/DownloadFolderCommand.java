package org.lastbamboo.client.services.command;

import java.net.URI;

/**
 * Class containing data for opening download folders.
 */
public class DownloadFolderCommand 
    {

    private URI m_uri;
    
    private String m_name;
    
    public void setUri(URI uri)
        {
        m_uri = uri;
        }

    public URI getUri()
        {
        return m_uri;
        }

    public void setName(final String name)
        {
        this.m_name = name;
        }

    public String getName()
        {
        return m_name;
        }
    }
