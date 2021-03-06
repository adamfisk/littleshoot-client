package org.lastbamboo.client.util.settings;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.client.util.settings.types.BooleanSetting;
import org.lastbamboo.client.util.settings.types.ByteSetting;
import org.lastbamboo.client.util.settings.types.CharArraySetting;
import org.lastbamboo.client.util.settings.types.ColorSetting;
import org.lastbamboo.client.util.settings.types.FileArraySetting;
import org.lastbamboo.client.util.settings.types.FileSetting;
import org.lastbamboo.client.util.settings.types.FloatSetting;
import org.lastbamboo.client.util.settings.types.IntSetting;
import org.lastbamboo.client.util.settings.types.LongSetting;
import org.lastbamboo.client.util.settings.types.Setting;
import org.lastbamboo.client.util.settings.types.StringArraySetting;
import org.lastbamboo.client.util.settings.types.StringSetting;


/**
 * Class for handling all LimeWire settings that are stored to disk.  To
 * add a new setting, simply add a new public static member to the list
 * of settings.  Each setting constructor takes the name of the key and 
 * the default value, and all settings are typed.  Choose the correct 
 * <tt>Setting</tt> subclass for your setting type.  It is also important
 * to choose a unique string key for your setting name -- otherwise there
 * will be conflicts.
 */
public final class SettingsFactory 
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(SettingsFactory.class);
    
    /**
     * Bytes used to ensure that we can write to the settings file.
     */
    private final byte[] PRE_HEADER = 
        "#LittleShoot Properties IO Test\n".getBytes();
    
    /**
     * Time interval, after which the accumulated information expires
     */
    private static final long EXPIRY_INTERVAL = 14 * 24 * 60 * 60 * 1000; //14 days
    
    /**
     * An internal Setting to store the last expire time
     */
    private LongSetting LAST_EXPIRE_TIME = null;
    
    /** 
	 * <tt>File</tt> object from which settings are loaded and saved 
	 */    
    private File SETTINGS_FILE;
    
    private final String HEADING;

    /**
     * <tt>Properties</tt> instance for the defualt values.
     */
    protected final Properties DEFAULT_PROPS = new Properties();

    /**
     * The <tt>Properties</tt> instance containing all settings.
     */
	protected final Properties PROPS = new Properties(DEFAULT_PROPS);
    
    /* List of all settings associated with this factory 
     * LOCKING: must hold this monitor
     */
    private ArrayList /* of Settings */ settings = new ArrayList(10);
    
    private boolean expired = false;
    
	/**
	 * Creates a new <tt>SettingsFactory</tt> instance with the specified file
	 * to read from and write to.
	 *
	 * @param settingsFile the file to read from and to write to
	 */
	SettingsFactory(File settingsFile) 
        {
        this(settingsFile, "");
        }
    
	/**
	 * Creates a new <tt>SettingsFactory</tt> instance with the specified file
	 * to read from and write to.
	 *
	 * @param settingsFile the file to read from and to write to
     * @param heading heading to use when writing property file
	 */
	SettingsFactory(File settingsFile, String heading) 
        {
        SETTINGS_FILE = settingsFile;
        if(SETTINGS_FILE.isDirectory()) SETTINGS_FILE.delete();
        HEADING = heading;
		reload();
        }
	
	/**
	 * Returns the iterator over the settings stored in this factory.
	 *
	 * LOCKING: The caller must ensure that this factory's monitor
	 *   is held while iterating over the iterator.
	 */
	public synchronized Iterator iterator() 
        {
	    return settings.iterator();
        }

	/**
	 * Reloads the settings with the predefined settings file from
     * disk.
	 */
	public synchronized void reload() {
		// If the props file doesn't exist, the init sequence will prompt
		// the user for the required values, so return.  If this is not 
		// loading limewire.props, but rather something like themes.txt,
		// we also return, as attempting to load an invalid file will
		// not do any good.
		if(!SETTINGS_FILE.isFile()) 
            {
		    setExpireValue();
		    return;
            }
		FileInputStream fis = null;
        try
            {
            fis = new FileInputStream(SETTINGS_FILE);
            // Loading properties can cause problems if the
            // file is invalid. Ignore these invalid values,
            // as the default properties will be used and that's
            // a-OK.
            try
                {
                PROPS.load(fis);
                }
            catch (IOException iox)
                {
                String msg = iox.getMessage();
                if (msg.indexOf("orrupted") < 0)
                    {
                    throw iox;
                    }
                //it was the "file or directory corrupted" exception
                SETTINGS_FILE.delete();//revert to defaults
                }
            }
        catch (IOException e)
            {
            LOG.error("IO error handling settings", e);
            // the default properties will be used -- this is fine and expected
            }
        finally
            {
            if (fis != null)
                {
                try
                    {
                    fis.close();
                    }
                catch (IOException e)
                    {
                    }
                }
            }

        // Reload all setting values
        Iterator ii = settings.iterator();
        while (ii.hasNext())
            {
            Setting set = (Setting) ii.next();
            set.reload();
            }

        setExpireValue();
        }
	
	/**
	 * Sets the last expire time if not already set.
	 */
	private synchronized void setExpireValue() {
        // Note: this has only an impact on launch time when this
        // method is called by the constructor of this class!
        if (LAST_EXPIRE_TIME == null) {
            LAST_EXPIRE_TIME = createLongSetting("LAST_EXPIRE_TIME", 0);
            
            // Set flag to true if Settings are expiried. See
            // createExpirable<whatever>Setting at the bottom
            expired =
                (LAST_EXPIRE_TIME.getValue() + EXPIRY_INTERVAL <
                        System.currentTimeMillis());
            
            if (expired)
                LAST_EXPIRE_TIME.setValue(System.currentTimeMillis());
        }
    }	    
	
	/**
	 * Changes the backing file to use for this factory.
	 */
    public synchronized void changeFile(File toUse) {
        SETTINGS_FILE = toUse;
        if(SETTINGS_FILE.isDirectory()) SETTINGS_FILE.delete();
        revertToDefault();
        reload();
    }
	
	/**
	 * Reverts all settings to their factory defaults.
	 */
	public synchronized void revertToDefault() {
	    Iterator ii = settings.iterator();
	    while( ii.hasNext() ) {
	        Setting set = (Setting)ii.next();
	        set.revertToDefault();
	    }
	}
    
    /**
     * Save setting information to property file
     * We want to NOT save any properties which are the default value,
     * as well as any older properties that are no longer in use.
     * To avoid having to manually encode the file, we clone
     * the existing properties and manually remove the ones
     * which are default and aren't required to be saved.
     * It is important to do it this way (as opposed to creating a new
     * properties object and adding only those that should be saved
     * or aren't default) because 'adding' properties may fail if
     * certain settings classes haven't been statically loaded yet.
     * (Note that we cannot use 'store' since it's only available in 1.2)
     */
    public synchronized void save() 
        {
        Properties toSave = (Properties)PROPS.clone();

        //Add any settings which require saving or aren't default
        Iterator ii = settings.iterator();
        while( ii.hasNext() ) {
            Setting set = (Setting)ii.next();
            if( !set.shouldAlwaysSave() && set.isDefault() )
                toSave.remove( set.getKey() );
        }
        
        FileOutputStream out = null;
        try {
            // some bugs were reported where the settings file was a directory.
            if(SETTINGS_FILE.isDirectory()) SETTINGS_FILE.delete();

            // some bugs were reported where the settings file's parent
            // directory was deleted.
            File parent = SETTINGS_FILE.getParentFile();
            if(parent != null) 
                {
                parent.mkdirs();
                }
            out = new FileOutputStream(SETTINGS_FILE);

            // save the properties to disk.
            toSave.store( out, HEADING);          
        } catch (final FileNotFoundException e) {
            LOG.error("IO error handling settings", e);
        } catch (final IOException e) {
			LOG.error("IO error handling settings", e);
        } finally {
            if ( out != null ) {
                try {
                    out.close();
                } catch (final IOException ignored) {}
            }
        }
    }
    
    /**
     * Return settings properties
     */
    Properties getProperties() {
        return PROPS;
    }
    
	/**
	 * Creates a new <tt>StringSetting</tt> instance with the specified
	 * key and default value.
	 *
	 * @param key the key for the setting
	 * @param defaultValue the default value for the setting
	 */
	public synchronized StringSetting createStringSetting(String key, String defaultValue) {
		StringSetting result = 
			new StringSetting(DEFAULT_PROPS, PROPS, key, defaultValue);
        settings.add(result);
        result.reload();
        return result;
	}

	/**
	 * Creates a new <tt>BooleanSetting</tt> instance with the specified
	 * key and default value.
	 *
	 * @param key the key for the setting
	 * @param defaultValue the default value for the setting
	 */
	public synchronized BooleanSetting createBooleanSetting(String key, boolean defaultValue) {
		BooleanSetting result = 
			new BooleanSetting(DEFAULT_PROPS, PROPS, key, defaultValue);
        settings.add(result);
        result.reload();
        return result;
	}

	/**
	 * Creates a new <tt>IntSetting</tt> instance with the specified
	 * key and default value.
	 *
	 * @param key the key for the setting
	 * @param defaultValue the default value for the setting
	 */
	public synchronized IntSetting createIntSetting(String key, int defaultValue) {
		IntSetting result = 
            new IntSetting(DEFAULT_PROPS, PROPS, key, defaultValue);
        settings.add(result);
        result.reload();
        return result;
	}


	/**
	 * Creates a new <tt>ByteSetting</tt> instance with the specified
	 * key and default value.
	 *
	 * @param key the key for the setting
	 * @param defaultValue the default value for the setting
	 */
	public synchronized ByteSetting createByteSetting(String key, byte defaultValue) {
		ByteSetting result = 
                new ByteSetting(DEFAULT_PROPS, PROPS, key, defaultValue);
        settings.add(result);
        result.reload();
        return result;
	}


	/**
	 * Creates a new <tt>LongSetting</tt> instance with the specified
	 * key and default value.
	 *
	 * @param key the key for the setting
	 * @param defaultValue the default value for the setting
	 */
	public synchronized LongSetting createLongSetting(final String key, 
        final long defaultValue) 
        {
		final LongSetting result = 
		    new LongSetting(DEFAULT_PROPS, PROPS, key, defaultValue);
		settings.add(result);
		result.reload();
		return result;
        }


	/**
	 * Creates a new <tt>FileSetting</tt> instance with the specified
	 * key and default value.
	 *
	 * @param key the key for the setting
	 * @param defaultValue the default value for the setting
	 */
	public synchronized FileSetting createFileSetting(
        final String key, final File defaultValue) 
        {
	    final String parentString = defaultValue.getParent();
	    if( parentString != null ) 
            {
		    final File parent = new File(parentString);
		    if(!parent.isDirectory())
		        parent.mkdirs();
            }

		final FileSetting result = 
            new FileSetting(DEFAULT_PROPS, PROPS, key, defaultValue);
        settings.add(result);
        result.reload();
        return result;
        }

	/**
	 * Creates a new <tt>ColorSetting</tt> instance with the specified
	 * key and default value.
	 *
	 * @param key the key for the setting
	 * @param defaultValue the default value for the setting
	 */
	public synchronized ColorSetting createColorSetting(
        final String key, final Color defaultValue) 
        {
		final ColorSetting result = 
            ColorSetting.createColorSetting(DEFAULT_PROPS, PROPS, key, 
                defaultValue);
        settings.add(result);
        result.reload();
        return result;
	    }

    /**
     * Creates a new <tt>CharArraySetting</tt> instance for a character array 
     * setting with the specified key and default value.
     *
     * @param key the key for the setting
     * @param defaultValue the default value for the setting
     */
    public synchronized CharArraySetting createCharArraySetting(
        final String key, final char[] defaultValue) 
        {
        final CharArraySetting result =
            CharArraySetting.createCharArraySetting(DEFAULT_PROPS, PROPS, 
                                                    key, defaultValue);
        settings.add(result);
        result.reload();
        return result;
        }
    
    /**
	 * Creates a new <tt>FloatSetting</tt> instance with the specified
	 * key and default value.
	 *
	 * @param key the key for the setting
	 * @param defaultValue the default value for the setting
	 */
	public synchronized FloatSetting createFloatSetting(final String key, 
		final float defaultValue) 
        {
		final FloatSetting result = 
            new FloatSetting(DEFAULT_PROPS, PROPS, key, defaultValue);
        settings.add(result);
        result.reload();
        return result;
        }
    
    /**
     * Creates a new <tt>StringArraySetting</tt> instance for a String array 
     * setting with the specified key and default value.
     *
     * @param key the key for the setting
     * @param defaultValue the default value for the setting
     */
    public synchronized StringArraySetting 
        createStringArraySetting(final String key, final String[] defaultValue) 
        {
        final StringArraySetting result = 
        	new StringArraySetting(DEFAULT_PROPS, PROPS, key, defaultValue);
                
        settings.add(result);
        result.reload();
        return result;
        }
    
    /**
     * Creates a new <tt>FileArraySetting</tt> instance for a File array 
     * setting with the specified key and default value.
     *
     * @param key the key for the setting
     * @param defaultValue the default value for the setting
     */
    public synchronized FileArraySetting 
        createFileArraySetting(final String key, final File[] defaultValue) 
        {
        final FileArraySetting result = 
            new FileArraySetting(DEFAULT_PROPS, PROPS, key, defaultValue);
                
        settings.add(result);
        result.reload();
        return result;
        }
    
    /**
	 * Creates a new expiring <tt>BooleanSetting</tt> instance with the 
	 * specified key and default value.
	 *
	 * @param key the key for the setting
	 * @param defaultValue the default value for the setting
	 */
	public synchronized BooleanSetting createExpirableBooleanSetting(String key, boolean defaultValue) {
        BooleanSetting result = createBooleanSetting(key, defaultValue);
        
        if (expired) {
            result.revertToDefault();
        }
        
        return result;
	}
    
    /**
	 * Creates a new expiring <tt>IntSetting</tt> instance with the specified
	 * key and default value.
	 *
	 * @param key the key for the setting
	 * @param defaultValue the default value for the setting
	 */
	public synchronized IntSetting createExpirableIntSetting(final String key, 
        final int defaultValue) 
        {
		final IntSetting result = createIntSetting(key, defaultValue);
        
        if (expired) 
            {
            result.revertToDefault();
            }
        
        return result;
        }

    }
