package org.lastbamboo.server.services.command;

/**
 * Command class for groups.
 */
public class GroupCommand
    {
    
    private String m_name = "";
    
    private String m_description = "";
    
    private String m_permission = "";
    
    private long m_userId;

    public void setName(String name)
        {
        m_name = name;
        }

    public String getName()
        {
        return m_name;
        }

    public void setDescription(String description)
        {
        m_description = description;
        }

    public String getDescription()
        {
        return m_description;
        }

    public void setPermission(String permission)
        {
        m_permission = permission;
        }

    public String getPermission()
        {
        return m_permission;
        }

    public void setUserId(long userId)
        {
        m_userId = userId;
        }

    public long getUserId()
        {
        return m_userId;
        }
    }
