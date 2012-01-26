package org.lastbamboo.server.db;

import java.util.Collection;

import org.lastbamboo.server.resource.User;

public class GroupImpl implements Group
    {

    /**
     * The hibernate ID of this user resource.
     */
    private Long m_id;
    
    private String m_name;
    
    private String m_description;

    private Collection<User> m_administrators;
    
    private Collection<User> m_users;
    
    private int m_defaultPermission;
    
    private int m_membershipPolicy;
    }
