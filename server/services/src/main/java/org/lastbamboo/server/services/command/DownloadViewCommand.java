package org.lastbamboo.server.services.command;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing data for download requests.
 */
public class DownloadViewCommand 
    {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadViewCommand.class);

    private URI m_uri;
    
    private String m_name;

    public void setUri(final URI uri)
        {
        LOG.debug("Setting uri to: "+uri);
        this.m_uri = uri;
        }

    public URI getUri()
        {
        return m_uri;
        }
    
    public String getName()
        {
        return m_name;
        }

    public void setName(final String name)
        {
        m_name = name;
        }

    public String toString()
        {
        return "Download View for: " + this.m_uri;
        }
    }
