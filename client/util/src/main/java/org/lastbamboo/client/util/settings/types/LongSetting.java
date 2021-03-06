package org.lastbamboo.client.util.settings.types;

import java.util.Properties;

/**
 * Class for a long setting.
 */
public final class LongSetting extends Setting {
    
    private long value;

	/**
	 * Creates a new <tt>LongSetting</tt> instance with the specified
	 * key and defualt value.
	 *
	 * @param key the constant key to use for the setting
	 * @param defaultLong the default value to use for the setting
	 */
	public LongSetting(Properties defaultProps, Properties props, String key, long defaultLong) {
		super(defaultProps, props, key, String.valueOf(defaultLong));
	}
        
	/**
	 * Accessor for the value of this setting.
	 * 
	 * @return the value of this setting
	 */
	public long getValue() {
        return value;
	}

	/**
	 * Mutator for this setting.
	 *
	 * @param value the value to store
	 */
	public void setValue(long value) {
		super.setValue(String.valueOf(value));
	}
    
    /** Load value from property string value
     * @param sValue property string value
     *
     */
    protected void loadValue(String sValue) {
        try {
            value = Long.parseLong(sValue.trim());
        } catch(NumberFormatException nfe) {
            revertToDefault();
        }
    }
}
