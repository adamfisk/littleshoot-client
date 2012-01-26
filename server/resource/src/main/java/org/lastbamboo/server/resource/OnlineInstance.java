package org.lastbamboo.server.resource;

import java.util.Set;

/**
 * Resource for an individual online instance of LittleShoot.
 */
public interface OnlineInstance 
    {
    
    void setInstanceId(long instanceId);
    
    long getInstanceId();

    void addMetaFileResource(MetaFileResource mfr);

    void removeMetaFileResource(MetaFileResource mfr);
    
    Set<MetaFileResource> getFiles();
    
    void setFiles(Set<MetaFileResource> files);
    
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
    
    /**
     * Gets the address of the server this user is online with -- typically
     * a SIP server.
     * 
     * @return The address of the server that recorded the user as online.
     */
    String getServerAddress();

    /**
     * Sets the address of the server recording the user's online status.
     * 
     * @param serverAddress The address of the server recording the user
     * as online.
     */
    public void setServerAddress(final String serverAddress);

    }
