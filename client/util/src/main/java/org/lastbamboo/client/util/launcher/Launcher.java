package org.lastbamboo.client.util.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Interface for underlying file launching implementations.  These methods 
 * typically vary by operating system, so each operating system has its own
 * implementation.
 */
public interface Launcher
    {
    
    /**
     * Launches the file with it's associated application on Windows. 
     *
     * @param file the path of the file to launch
     * @return an int for the exit code of the native method
     * @throws IOException If an error occurs making the native call.
     * @throws NullPointerException if the <code>file</code> argument
     *  is <code>null</code>
     */
    int launchFile(final File file) throws IOException;

    /**
     * Opens the specified url in the default web browser on the user's 
     * system.
     *
     * @param url the url to open
     * @return the return code of the native call
     * @throws IOException If an error occurs in the native call.
     * @throws NullPointerException if the <tt>url</tt> argument is 
     * <code>null</code>
     */
    int openUrl(final URI url) throws IOException;
    }