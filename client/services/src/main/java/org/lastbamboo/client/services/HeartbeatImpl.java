package org.lastbamboo.client.services;

import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a system heartbeat class.
 */
public class HeartbeatImpl implements Heartbeat, Runnable
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final Collection<HeartbeatListener> m_listeners = 
        new HashSet<HeartbeatListener>();
    
    /**
     * Creates a new heartbeat class.
     */
    public HeartbeatImpl()
        {
        final Thread heartbeatThread = 
            new Thread(this, "Heartbeat-Thread-"+hashCode());
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
        }
    
    /**
     * Starts the thread for updating resources.
     */
    public void run()
        {
        m_log.debug("Starting update process...");

        long lastRunTime = System.currentTimeMillis();
        while (true) 
            {
            if (System.currentTimeMillis() - lastRunTime > 20000)
                {
                notifySleep();
                }
            notifyListeners();
            try
                {
                Thread.sleep(4 * 1000);
                }
            catch (final InterruptedException e)
                {
                m_log.debug("Interrupted while sleeping", e);
                break;
                }
            lastRunTime = System.currentTimeMillis();
            }
        }

    private void notifySleep()
        {
        synchronized (m_listeners)
            {
            for (final HeartbeatListener hl : this.m_listeners)
                {
                hl.onSleep();
                }
            }
        }

    private void notifyListeners()
        {
        synchronized (m_listeners)
            {
            for (final HeartbeatListener hl : this.m_listeners)
                {
                hl.onHeartbeat();
                }
            }
        }

    public void addListener(final HeartbeatListener heartbeatListener)
        {
        this.m_listeners.add(heartbeatListener);
        }
    }
