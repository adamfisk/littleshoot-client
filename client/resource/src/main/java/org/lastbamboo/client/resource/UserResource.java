package org.lastbamboo.client.resource;

import java.util.Collection;
import java.util.Date;

/**
 * Interface providing access to all available data for a given user.
 */
public interface UserResource
    {
    /**
     * Determines whether or not the user is currently on the network.
     *  
     * @return <code>true</code> if the user is currently online, otherwise
     * <code>false</code>.
     */
    boolean isOnline();
    
    /**
     * Accessor for the unique ID of this user.
     * @return The unique ID of this user.
     */
    long getUserId();
    
    /**
     * Sets the user's online status.
     * 
     * @param online The user's online status.
     */
    void setOnline(final boolean online);
    
    /**
     * Sets the unique user ID of this user.
     * 
     * @param id The unique ID of this user.
     */
    void setUserId(final long id);
    
    /**
     * Sets the Hibernate ID for this persistent instance.
     * @param id The unique Hibernate ID to use.
     */
    void setId(final long id);
    
    /**
     * Accessor for the unique Hibernate ID.
     * @return The unique Hibernate ID.
     */
    long getId();

    /**
     * Accessor for the last time in milliseconds we made a resource listing
     * request to this user.
     * @return The last time in milliseconds we made a resource listing 
     * request to this user, since 1970.
     */
    long getLastRequestTime();
    
    /**
     * Sets the last time we made a resource listing request to this user, in
     * milliseconds.
     * @param time The last time we made a resource listing request to this
     * user in milliseconds.
     */
    void setLastRequestTime(final long time);

    /**
     * Returns the last date that online status was updated for this user
     * resource.
     * 
     * @return
     *      The last date that online status was updated for this user resource.
     *      This can return null, if no online status has been set yet for this
     *      resource.
     */
    Date getLastOnlineStatusDate
            ();
    
    /**
     * Sets the last date that online status was updated for this user resource.
     * 
     * @param date
     *      The last date that online status was updated for this user resource. 
     */
    void setLastOnlineStatusDate
            (Date date);
    
    /**
     * Sets the <code>Collection</code> of <code>UserResource</code> instances
     * this user has a relationship with.
     * @param relationships The users this user has a relationship with.
     */
    void setRelationships(final Collection relationships);

    /**
     * Accessor for the relationships for this user.
     * @return The relationships for this user.
     */
    Collection getRelationships();
    
    }
