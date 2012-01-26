package org.lastbamboo.server.services.command;

/**
 * Command class for when users have forgotten their passwords.
 */
public class ForgotPasswordCommand
    {
    
    private String m_email;

    public String getEmail()
        {
        return this.m_email;
        }

    public void setEmail(final String email)
        {
        this.m_email = email;
        }

    }
