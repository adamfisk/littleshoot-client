package org.lastbamboo.client.util;

import java.io.File;

import org.apache.commons.lang.SystemUtils;

/**
 * Utility class for dealing with user-specific settings.
 */
public class UserUtils 
    {
	
	/**
	 * Constant for the name of the bamboo user preferences directory.
	 */
	private static final String BAMBOO_PREFS_DIR_NAME = ".shoot";

	/**
     * Variable for the settings directory.
     */
    private static File m_userSettingsDir;
    
    /**
     * The System property key for the user home directory.
     */
    private static final String USER_HOME_KEY = "user.home";
    
    /**
     * Returns the directory where all user settings should be stored.  This
     * is where all application data should be stored.  If the directory does
     * does not already exist, this attempts to create the directory, although
     * this is not guaranteed to succeed.
     *
     * @return the <tt>File</tt> instance denoting the user's home 
     *  directory for the application, or <tt>null</tt> if that directory 
	 *  does not exist
     */
    public static File getUserSettingsDir() 
        {
        if ( m_userSettingsDir != null ) return m_userSettingsDir;
        
        File settingsDir = 
        	new File(getUserHome(), BAMBOO_PREFS_DIR_NAME);
        if(SystemUtils.IS_OS_MAC_OSX) 
            {            
            File tempSettingsDir = 
            	new File(getUserHome(), "Library/Preferences");
            settingsDir = new File(tempSettingsDir, "LittleShoot");
            } 

        if(!settingsDir.isDirectory()) 
            {
            settingsDir.delete(); // delete whatever it may have been
            if(!settingsDir.mkdirs()) 
                {
                String msg = "could not create preferences directory: "+
                    settingsDir;
                throw new RuntimeException(msg);
                }
            }

        if(!settingsDir.canWrite()) 
            {
            throw new RuntimeException("settings dir not writable");
            }

        if(!settingsDir.canRead()) 
            {
            throw new RuntimeException("settings dir not readable");
            }   

        // cache the directory.
        m_userSettingsDir = settingsDir;
        return settingsDir;
        }
    
    /**
     * <p>Gets the user home directory as a <code>File</code>.</p>
     * 
     * @return a directory
     * @throws  SecurityException  if a security manager exists and its  
     *             <code>checkPropertyAccess</code> method doesn't allow
     *              access to the specified system property.
     * @see System#getProperty(String)
     */
    public static File getUserHome() 
        {
        return new File(System.getProperty(USER_HOME_KEY));
        }
    }
