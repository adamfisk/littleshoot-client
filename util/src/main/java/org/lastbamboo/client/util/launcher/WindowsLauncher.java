package org.lastbamboo.client.util.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for calls to native Windows code that launches files in their 
 * associated applications.
 */
public final class WindowsLauncher implements Launcher 
    {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(WindowsLauncher.class);
	
    /**
     * Creates a new launcher, loading the appropriate library.
     */
    public WindowsLauncher()
        {
        System.loadLibrary("flauncher");
        }
    
	/**
	 * Launches the file with it's associated application on Windows. 
	 *
	 * @param file the path of the file to launch
	 * @return an int for the exit code of the native method
	 * @throws IOException If an error occurs making the native call.
	 * @throws NullPointerException if the <code>file</code> argument
	 *  is <code>null</code>
	 */
	public int launchFile(final File file) throws IOException 
        {
		LOG.trace("Launching file: "+file);
		// don't want to pass null values to the native code
		if(file == null) 
            {
			LOG.error("null file to launch");
			throw new NullPointerException("cannot accept null url values");
            }
		try 
            {
			return nativeLaunchFile(file.getCanonicalPath());
            } 
        catch (final UnsatisfiedLinkError e) 
            {
            LOG.error("Unsatisfied link", e);
            throw new IOException("Unexpected error: "+e.getMessage()+
                " opening file: "+file);
            }
        }

	/**
	 * Opens the specified url in the default web browser on the user's 
	 * system.
	 *
	 * @param url the url to open
	 * @return the return code of the native call
	 * @throws IOException If an error occurs in the native call.
	 * @throws NullPointerException if the <code>url</code> argument is 
     * <code>null</code>
	 */
	public int openUrl(final URI url) throws IOException 
        {
		LOG.trace("Opening url: "+url);
		// don't want to pass null values to the native code
		if(url == null) 
            {
			LOG.error("null uri to launch");
			throw new NullPointerException("cannot accept null url values");
            }
		try 
            {
			LOG.debug("Opening URL: "+url);
			return nativeOpenURL(url.toASCIIString());
            } 
        catch (final UnsatisfiedLinkError e) 
            {
			LOG.error("Unsatisfied link", e);
            throw new IOException("Unexpected error: "+e.getMessage()+
                " opening url: "+url);
            }
        }

	/** 
	 * Native method for launching the specified file.
	 *
	 * @param file the full path of the file to launch
	 * @return the return code of the native method
	 */
	private static native int nativeLaunchFile(String file);


	/**
	 * Native method for launching the specified url in the user's default
	 * web browser.
	 *
	 * @param url the url to open
	 * @return the return code of the native method
	 */
	private static native int nativeOpenURL(String url);
}
