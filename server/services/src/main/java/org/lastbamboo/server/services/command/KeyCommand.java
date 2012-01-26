package org.lastbamboo.server.services.command;



/**
 * Bean for data for requesting a key.
 */
public class KeyCommand
    {

    private long m_keyId = -1L;

    public void setKeyId(long keyId)
        {
        m_keyId = keyId;
        }

    public long getKeyId()
        {
        return m_keyId;
        }

    }
