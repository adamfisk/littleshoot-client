package org.lastbamboo.client.services.opener;

import java.lang.reflect.Method;

import org.apache.commons.lang.SystemUtils;
import org.littleshoot.util.StringListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for handling OSX launch services.
 */
public class OsxLaunchServicesHandler 
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private volatile boolean m_receivedFileEvent = false;

    private final StringListener m_fileListener;
    
    /**
     * Creates a new OSX launch services handler with the specified listener
     * for open file events.
     * 
     * @param fileListener The class that will listen for file paths we
     * receive in response to open file events.
     */
    public OsxLaunchServicesHandler(final StringListener fileListener)
        {
        this.m_fileListener = fileListener;
        registerForMacOSXEvents();
        }

    private void registerForMacOSXEvents()
        {
        m_log.info("Registering for OSX events");
        if (!SystemUtils.IS_OS_MAC_OSX)
            {
            m_log.warn("Not running on OSX.");
            return;
            }
        
        try
            {
            // Generate and register the OSXAdapter, passing it a hash of
            // all the methods we wish to
            // use as delegates for various
            // com.apple.eawt.ApplicationListener methods
            OsxAdapter.setFileHandler(this, getClass().getDeclaredMethod(
                    "openFile", new Class[] { String.class }));
            
            }
        catch (final Exception e)
            {
            m_log.error("Error while loading the OSXAdapter:", e);
            }
        
        try
            {
            final Method open = 
                getClass().getDeclaredMethod("openApplication", new Class[] { String.class });
            OsxAdapter.setOpenApplicationHandler(this, open);
            }
        catch (final Exception e)
            {
            m_log.error("Error while loading the openApplication OSXAdapter:", e);
            }
        }
    
    /**
     * This is useful if we're expecting a file and want to wait for it for
     * some period of time.
     */
    public void waitForFile()
        {
        synchronized (this) 
            {
            if (!m_receivedFileEvent)
                {
                try
                    {
                    wait(2000);
                    }
                catch (final InterruptedException e)
                    {
                    e.printStackTrace();
                    }
                }
            }
        }

    /**
     * Called from the OSX {@link ApplicationListener} when we receive an
     * open file event from Launch Services.
     * 
     * @param path The path to the file to open.
     */
    public void openFile(final String path)
        {
        m_log.info("Got open file event!!! "+path);
        this.m_fileListener.onString(path);
        synchronized (this) 
            {
            m_receivedFileEvent = true;
            notifyAll();
            }
        }
    
    /**
     * Called from the OSX {@link ApplicationListener} when we receive an
     * open application event from Launch Services.
     * 
     * @param path Any file path associated with the event, if any.
     */
    public void openApplication(final String path)
        {
        m_log.info("Got open application call with path: "+path);
        }

    /**
     * Returns whether or not we've handled an open file event.
     * 
     * @return <code>true</code> if we've handled an open file event,
     * otherwise <code>false</code>.
     */
    public boolean handledFileEvent()
        {
        return m_receivedFileEvent;
        }
    }
