package org.lastbamboo.server.db;

import java.util.Collection;

import org.lastbamboo.server.resource.OnlineInstance;

/**
 * Interface for DAOs accessing LittleShoot instance and user data.  An 
 * instance is a single running instance of LittleShoot.
 */
public interface InstanceDao
    {

    /**
     * Sets whether the specified instance is online or offline.
     * 
     * @param instanceId The ID of the instance.
     * @param baseUri The base URI to use for accessing this user.
     * @param online Whether they're online or not.
     * @param serverAddress The address of the server reporting the online
     * status
     */
    void setInstanceOnline(long instanceId, String baseUri, boolean online, 
        String serverAddress);

    /**
     * Accesses all online users.
     * @return The {@link Collection} of online users. 
     */
    Collection<OnlineInstance> getOnlineInstances();

    /**
     * Sets the online status of a server.  This is included with user 
     * resources because it affects the online status of users.
     * 
     * @param online Whether or not the server is online.
     * @param serverAddress The address of the server.
     */
    void setServerOnline(boolean online, String serverAddress);
    

    }
