package org.lastbamboo.server.resource;


/**
 * Resource for an individual LittleShoot instance.
 */
public interface Instance
    {

    void setOnline(boolean online);
    
    boolean isOnline();
    
    void setInstanceId(long instanceId);
    
    long getInstanceId();

    String getBaseUri();
    
    void setBaseUri(String baseUri);
    
    /**
     * Returns whether or not this user is a repeat infringer.
     * 
     * @return Whether or not this user is a repeat infringer.
     */
    boolean isRepeatInfringer();
    
    /**
     * Sets whether or not this user is marked as a repeat infringer.
     * 
     * @param repeatInfringer Whether or not this user is a repeat infringer.
     */
    void setRepeatInfringer(boolean repeatInfringer);

    }
