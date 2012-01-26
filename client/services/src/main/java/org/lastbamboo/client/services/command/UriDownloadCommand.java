package org.lastbamboo.client.services.command;

import java.net.URI;

/**
 * Bean containing data for a download identified by a URI.
 */
public class UriDownloadCommand
    {

    private URI m_uri;

    public void setUri(URI uri)
        {
        m_uri = uri;
        }

    public URI getUri()
        {
        return m_uri;
        }
    
    }
