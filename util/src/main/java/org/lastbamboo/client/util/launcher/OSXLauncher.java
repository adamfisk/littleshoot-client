package org.lastbamboo.client.util.launcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File and URL launching implementation for OSX.
 */
public final class OSXLauncher implements Launcher
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(OSXLauncher.class);

    /**
     * Method for opening a URL in a browser.
     */
    private Method m_openURLMethod;
    
    /**
     * Flag for whether or not the method for opening urls was loaded 
     * successfully.
     */
    private boolean m_openUrlMethodLoadedSuccessfully;

    /**
     * Creates a new launcher for URLs and files in OSX, loading any necessary
     * OSX-specific classes.
     */
    public OSXLauncher() 
        {
        try
            {
            loadUrlMethod();
            this.m_openUrlMethodLoadedSuccessfully = true;
            } 
        catch (IOException e)
            {
            this.m_openUrlMethodLoadedSuccessfully = false;
            }
        }
    
    /** 
     * Loads specialized classes for the OSX needed to launch files.
     *
     * @throws IOException If an exception occurs loading the necessary 
     * classes.
     */
    private void loadUrlMethod() throws IOException 
        {
        try 
            {
            final Class mrjFileUtilsClass = 
                Class.forName("com.apple.mrj.MRJFileUtils");

            final String openURLName = "openURL";
            final Class[] openURLParams = {String.class};
            this.m_openURLMethod = 
                mrjFileUtilsClass.getDeclaredMethod(openURLName, openURLParams);
            } 
        catch (final ClassNotFoundException e) 
            {
            LOG.error("Unexpected exception", e);
            throw new IOException(e.getMessage());
            } 
        catch (final NoSuchMethodException e) 
            {
            LOG.error("Unexpected exception", e);
            throw new IOException(e.getMessage());
            } 
        catch (final SecurityException e) 
            {
            LOG.error("Unexpected exception", e);
            throw new IOException(e.getMessage());
            } 
        }
    
    /* (non-Javadoc)
     * @see com.bamboo.util.launcher.Launcher#launchFile(java.io.File)
     */
    public int launchFile(final File file) throws IOException
        {
        Runtime.getRuntime().exec(
            new String[]{"open", file.getCanonicalPath()});
        
        // Return a random "code", since there's no code to return on OSX.
        return 100;
        }

    /* (non-Javadoc)
     * @see com.bamboo.util.launcher.Launcher#openUrl(java.net.URI)
     */
    public int openUrl(final URI url) throws IOException
        {
        if(!m_openUrlMethodLoadedSuccessfully) 
            {
            throw new IOException("Necessary method not loaded.");
            }
        try 
            {
            final Object[] params = new Object[] {url.toASCIIString()};
            this.m_openURLMethod.invoke(null, params);
            }    
        catch (final NoSuchMethodError e) 
            {
            // this can occur when earlier versions of MRJ are used which
            // do not support the openURL method.
            LOG.error("Unexpected exception", e);
            throw new IOException(e.getMessage());
            } 
        catch (final NoClassDefFoundError e) 
            {
            // this can occur under runtime environments other than MRJ.
            LOG.error("Unexpected exception", e);
            throw new IOException(e.getMessage());
            } 
        catch (final IllegalAccessException e) 
            {
            LOG.error("Unexpected exception", e);
            throw new IOException(e.getMessage());
            } 
        catch (final InvocationTargetException e) 
            {
            LOG.error("Unexpected exception", e);
            throw new IOException(e.getMessage());
            }
        // Return a random "code", since there's no code to return on OSX.
        return 100;
        }

    }
