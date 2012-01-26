package org.lastbamboo.client.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.lastbamboo.common.searchers.limewire.LimeWire;
import org.lastbamboo.common.sip.stack.IdleSipSessionListener;
import org.littleshoot.util.DaemonThreadFactory;
import org.limewire.core.api.connection.ConnectionLifecycleEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.limegroup.gnutella.ConnectionManager;
import com.limegroup.gnutella.connection.ConnectionLifecycleEvent;
import com.limegroup.gnutella.connection.ConnectionLifecycleListener;

/**
 * Class that monitors the LimeWire module to make sure it stays connected.
 */
public class LimeWireMonitor implements IdleSipSessionListener
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final LimeWire m_limeWire;
    private final Timer m_timer = new Timer("LimeWire-Monitor-Timer", true);
    private volatile int m_disconnectedCount = 0;
    
    private final Executor m_threadPool = 
        Executors.newCachedThreadPool(
            new DaemonThreadFactory("LimeWire-Connecting-Thread"));

    /**
     * Creates a new class for monitoring LimeWire.
     * 
     * @param limeWire The interface to LimeWire. 
     */
    public LimeWireMonitor(final LimeWire limeWire)
        {
        this.m_limeWire = limeWire;
        }
    
    public void start() 
        {
        this.m_limeWire.start();
        final ConnectionLifecycleListener listener =
            new ConnectionLifecycleListener()
            {

            public void handleConnectionLifecycleEvent(
                final ConnectionLifecycleEvent cle)
                {
                final ConnectionLifecycleEventType eventType = cle.getType();
                switch (eventType)
                    {
                    case CONNECTING:
                        m_log.debug("Got connecting event...");
                        break;
                    case CONNECTED:
                        m_log.debug("Got connected event...");
                        break;
                    case DISCONNECTED:
                        m_log.debug("Got disconnected event...");
                        break;
                    case NO_INTERNET:
                        m_log.debug("Got no internet");
                        break;
                    case CONNECTION_INITIALIZING:
                        m_log.debug("Got connection initializing");
                        break;
                    case CONNECTION_INITIALIZED:
                        m_log.debug("Got connection initialized");
                        break;
                    case CONNECTION_CLOSED:
                        m_log.debug("Got connection closed");
                        break;
                    case CONNECTION_CAPABILITIES:
                        m_log.debug("Got connection capabilities.");
                        break;
                    }
                }
            };
        final ConnectionManager manager = 
            this.m_limeWire.getConnectionManager();
        manager.addEventListener(listener);
        final TimerTask task = new TimerTask()
            {
            @Override
            public void run()
                {
                if (!m_limeWire.isEnabled())
                    {
                    m_log.debug("Not monitoring when it's not enabled.");
                    return;
                    }
                if (!manager.isConnected())
                    {
                    m_disconnectedCount++;
                    m_log.warn("LimeWire no longer connected.  " +
                        "Now "+m_disconnectedCount+" in a row.");
                    
                    // Note we don't want to disconnect here because it can
                    // confuse LimeWire's automatic re-connector when we lose
                    // connectivity, particularly the _disconnectTime in
                    // ConnectionManagerImpl.noInternetConnection.
                    
                    // Note the gnutella.net file on OSX is located at
                    // ~/Library/Preferences/LimeWire/gnutella.net
                    
                    // We run this in a new thread in case blocking the event
                    // notification thread has any adverse consequences.
                    m_threadPool.execute(new Runnable()
                        {
                        public void run()
                            {
                            m_log.debug("Running connect.");
                            manager.connect();
                            }
                        });
                    
                    }
                else
                    {
                    // Reset the count.
                    m_log.debug("Got connected");
                    m_disconnectedCount = 0;
                    }
                }
            };
        final long firstRun = 1000 * 60;
        final long interval = 1000 * 60 * 4;
        this.m_timer.schedule(task, firstRun, interval);
        }

    public void onIdleSession()
        {
        m_log.debug("Received idle SIP session event...");
        if (!this.m_limeWire.isEnabled())
            {
            m_log.debug("Ignoring since we're not enabled.");
            return;
            }
        
        // This often indicates, for example, the user has put their 
        // computer to sleep and awoken it again it.  When it awakes, the SIP 
        // session will be considered idle. It's also very likely LimeWire
        // has lost its connectivity in that time, so we reconnect.
        
        // Note we don't want to disconnect here because it can
        // confuse LimeWire's automatic re-connector when we lose
        // connectivity, particularly the _disconnectTime in
        // ConnectionManagerImpl.noInternetConnection.
        m_threadPool.execute(new Runnable()
            {
            public void run()
                {
                m_log.debug("Running connect.");
                final ConnectionManager cm = m_limeWire.getConnectionManager();
                if (cm != null)
                    {
                	cm.connect();
                    }
                else
                    {
                    m_log.warn("Core is still null on idel session!!");
                    }
                }
            });
        }
    
    }
