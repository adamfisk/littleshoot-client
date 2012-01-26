package org.lastbamboo.client.services.command;

import java.net.URI;

/**
 * Class containing data for download stream requests..
 */
public class DownloadStreamCommand 
    {

    private URI m_uri;

    private long m_size;

    private String m_name;
    
    private boolean m_cancelOnStreamClose;
    
    public void setUri(URI uri)
        {
        m_uri = uri;
        }

    public URI getUri()
        {
        return m_uri;
        }

    public void setCancelOnStreamClose(boolean cancelOnStreamClose)
        {
        m_cancelOnStreamClose = cancelOnStreamClose;
        }

    public boolean isCancelOnStreamClose()
        {
        return m_cancelOnStreamClose;
        }

    public void setName(String name)
        {
        m_name = name;
        }

    public String getName()
        {
        return m_name;
        }

    public void setSize(long size)
        {
        m_size = size;
        }

    public long getSize()
        {
        return m_size;
        }

    }
