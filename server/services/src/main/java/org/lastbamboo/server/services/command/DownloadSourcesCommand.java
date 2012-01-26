package org.lastbamboo.server.services.command;

import java.net.URI;


/**
 * Bean for queries for sources for a specified SHA-1 URN.
 */
public class DownloadSourcesCommand
    {
    private URI m_sha1;
    
    private URI m_uri;
    
    public URI getSha1()
        {
        return m_sha1;
        }

    public void setSha1(final URI sha1)
        {
        m_sha1 = sha1;
        }

    public void setUri(URI uri)
        {
        m_uri = uri;
        }

    public URI getUri()
        {
        return m_uri;
        }

    }
