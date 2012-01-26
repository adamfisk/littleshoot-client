package org.lastbamboo.client.services;

/**
 * Class that runs a separate thread can calls services with intervals.  The
 * services should be short-lived.  The heartbeat also detects things like
 * system sleeps and notifies listeners when they occur.
 */
public interface Heartbeat
    {

    /**
     * Adds a hearbeat listener.
     * 
     * @param heartbeatListener The listener.
     */
    void addListener(HeartbeatListener heartbeatListener);

    }
