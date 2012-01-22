package org.lastbamboo.client.services.command;

import java.net.URI;

public class RemoveFileCommand
    {
    
    private URI m_sha1;
    
    private String m_name;

    public void setSha1(final URI sha1)
        {
        m_sha1 = sha1;
        }

    public URI getSha1()
        {
        return m_sha1;
        }

    public void setName(String name)
        {
        m_name = name;
        }

    public String getName()
        {
        return m_name;
        }
    }


