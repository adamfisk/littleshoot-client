package org.lastbamboo.client.resource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Type-safe enumeration for the different permissions.
 */
public final class Permission implements Serializable
    {
    
    /**
     * Serial version uid for identifying this type accross revisions.
     */
    private static final long serialVersionUID = -7697805723630402867L;

    /**
     * The numerical ID for this permission.
     */
    private final int m_id;
    
    /**
     * Map of IDs to immutable <code>Permission</code> instances.
     */
    private static final Map PERMISSIONS = new HashMap();

    /**
     * The string key for this permission, for representation in things like 
     * xml documents.
     */
    private final String m_key;
    
    /**
     * Index of the given permission, allowing it to be accessed in the 
     * database.  This is auto-incremented at the class level as new 
     * permissions are added.
     */
    private static int m_permissionIndex = 0;

    /**
     * Private constructor to ensure that only this class can construct new 
     * permissions.
     * 
     * @param key the key identifier for this permission.
     */
    private Permission(final String key)
        {
        this.m_id = Permission.m_permissionIndex;
        this.m_key = key;
        
        // Immediately map the id to the permission level.
        PERMISSIONS.put(new Integer(this.m_id), this);
        Permission.m_permissionIndex++;
        }
    
    /**
     * Accessor for the integer identifier for this permission.
     * 
     * @return the integer id for this permission.
     */
    public int getID()
        {
        return this.m_id;
        }
    
    /**
     * Accessor the string key identifying this permission type.
     * 
     * @return a string identifier for this permission.
     */
    public String getKey()
        {
        return this.m_key;
        }
    
    /**
     * Permission for files that are not shared with the outside world at all.
     */
    public static final Permission NOT_SHARED = new Permission("private");
    
    /**
     * Permission for files that are only shared with friends.
     */
    public static final Permission FRIENDS = new Permission("friends");
    
    /**
     * Permission for files that are shared publicly with anyone on the network.
     */
    public static final Permission PUBLIC = new Permission("public");
    
    /**
     * Permission for files friends you're connected to are sharing with
     * their friends.
     */
    public static final Permission FRIEND_FRIEND = 
        new Permission("friend-friend");
    
    /**
     * Permission for files your friends are sharing publicly.
     */
    public static final Permission FRIEND_PUBLIC = 
        new Permission("friend-public");

    /**
     * Permission for all files in the database.
     */
    public static final Permission ALL = new Permission("all");
        
    /**
     * Utility method for accessing <code>Permission</code> instances by their 
     * permission id.  This is used internally by Hibernate to map database 
     * values to instances of this class.  Normal application code should 
     * never call this method.
     * 
     * @param id the id of the <code>Permission</code> instance -- only used in 
     * Hibernate internally.
     * @return the <code>Permission</code> associated with the given id.
     */
    public static Permission getInstance(final int id)
        {
        return (Permission) PERMISSIONS.get(new Integer(id));
        }
    
    /**
     * Overrides object toString to give more data for debugging.
     * 
     * @return a string for debugging.
     */
    public String toString()
        {
        return "Permission: "+this.m_key;
        }
    
    }
