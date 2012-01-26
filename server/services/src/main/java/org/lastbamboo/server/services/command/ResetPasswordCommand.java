package org.lastbamboo.server.services.command;

/**
 * Command class for resetting a user's password.
 */
public class ResetPasswordCommand
    {

    private long m_resetId;
    private String m_email;
    private String m_password;

    public void setResetId(final long resetId)
        {
        m_resetId = resetId;
        }

    public long getResetId()
        {
        return m_resetId;
        }

    public void setEmail(final String email)
        {
        m_email = email;
        }

    public String getEmail()
        {
        return m_email;
        }

    public void setPassword(String password)
        {
        m_password = password;
        }

    public String getPassword()
        {
        return m_password;
        }
    }
