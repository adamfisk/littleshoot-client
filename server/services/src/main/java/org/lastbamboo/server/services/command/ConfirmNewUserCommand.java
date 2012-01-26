package org.lastbamboo.server.services.command;

/**
 * Command class for verifying user e-mail addresses.
 */
public class ConfirmNewUserCommand
    {
    
    private long m_userId;

    public long getUserId()
        {
        return this.m_userId;
        }

    public void setUserId(long userId)
        {
        this.m_userId = userId;
        }
    }
