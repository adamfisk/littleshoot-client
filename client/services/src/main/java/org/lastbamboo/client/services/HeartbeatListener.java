package org.lastbamboo.client.services;

/**
 * Listener for heartbeat events.
 */
public interface HeartbeatListener
    {

    /**
     * Called when a heartbeat interval occurs.
     */
    void onHeartbeat();

    /**
     * Called when the computer goes to sleep.
     */
    void onSleep();

    }
