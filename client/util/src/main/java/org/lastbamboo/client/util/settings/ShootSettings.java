package org.lastbamboo.client.util.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic superclass for specialized settings subclasses for different types of 
 * LittleShoot settings.
 */
public class ShootSettings extends AbstractSettings 
    {
	
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ShootSettings.class);
    
	/**
	 * Constant for the version of the app we're running.
	 */
    private static final String VERSION = "0.0.1";
    
	/**
	 * Single <code>ShootSettings</code> instance, following singleton.
	 */
	private static final ShootSettings INSTANCE = new ShootSettings();
	
	/**
	 * The FACTORY is used for subclasses of SimmerSettings, so they know which 
	 * factory to add classes to.
	 */
	protected static final SettingsFactory FACTORY = INSTANCE.getFactory();

	
	/**
	 * This is protected so that subclasses can extend from it, but subclasses 
	 * should NEVER instantiate a copy themselves.
	 */
	protected ShootSettings() 
        {
		super("littleshoot.props", "LittleShoot properties file");
        LOG.trace("Created props file...");
        }
    
    /**
     * Accessor for the version of LastBamboo the user is running.
     * 
     * @return the LastBamboo version
     */
    public static String getVersion() 
        {
        return VERSION;
        }

    /**
     * Instance accessor for this class.
     * @return The instance of this class.
     */
    public static ShootSettings instance()
        {
        return INSTANCE;
        }
		
    }
