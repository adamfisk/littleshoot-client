package org.lastbamboo.client.util.settings.types;

import java.util.Properties;
import java.io.*;

/**
 * This class handles settings for <tt>File</tt>s.
 */
public final class FileSetting extends Setting 
    {
    
    private File value;
    private String absolutePath;

	/**
	 * Creates a new <tt>SettingBool</tt> instance with the specified
	 * key and defualt value.
	 *
	 * @param key the constant key to use for the setting
	 * @param defaultFile the default value to use for the setting
	 */
	public FileSetting(final Properties defaultProps, final Properties props, 
        final String key, final File defaultFile) 
        {
		super(defaultProps, props, key, defaultFile.getAbsolutePath());
        }
        
	/**
	 * Accessor for the value of this setting.
	 * Duplicates the setting so it cannot be changed outside of this package.
	 * 
	 * @return the value of this setting
	 */
	public File getValue() 
        {
        return new File(absolutePath);
        }

	/**
	 * Mutator for this setting.
	 *
	 * @param value the value to store
	 */
	public void setValue(final File value) 
        {
		super.setValue(value.getAbsolutePath());
        }
     
    /**
     * Load value from property string value
     * @param sValue property string value
     */
    protected void loadValue(final String sValue) 
        {
        value = new File(sValue);
        absolutePath = value.getAbsolutePath();
        }
    }
