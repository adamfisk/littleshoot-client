package org.lastbamboo.server.services.command;

/**
 * Command class for users.
 */
public class UserCommand
    {
    
    private String m_email;
    private String m_password;

    public String getEmail()
        {
        return this.m_email;
        }

    public void setEmail(final String email)
        {
        this.m_email = email;
        }

    public String getPassword()
        {
        return this.m_password;
        }

    public void setPassword(final String password)
        {
        this.m_password = password;
        }
    }
