package org.lastbamboo.common.online;

import java.net.InetAddress;


/**
 * Interface for classes that update the online status of users.
 */
public interface OnlineStatusUpdater
    {

    /**
     * Updates the online status of users.
     * 
     * @param userId The ID of the user. 
     * @param baseUri The base URI of the host.  This can be an HTTP URL or a
     * SIP URI, for example.
     * @param online Whether or not the user is online.
     * @param ia The address of the server updating the status.
     */
    void updateStatus(String userId, String baseUri, boolean online, 
        InetAddress ia);

    /**
     * Sets whether or not updates are active.  This can be used during testing,
     * for example.
     * 
     * @param updateActive Whether or not updates should be active.
     */
    void setUpdateActive(boolean updateActive);

    /**
     * Specifies that all hosts that are registered with the server at the
     * specified address are now offline.
     * 
     * @param serverAddress The address of the server.
     */
    void allOffline(InetAddress serverAddress);

    }
