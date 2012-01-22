
package org.lastbamboo.client.util.settings.types;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * Class for an Array of Strings setting.
 */
 
public class StringArraySetting extends Setting {
    
    private String[] value;

	/**
	 * Creates a new <tt>StringArraySetting</tt> instance with the specified
	 * key and defualt value.
	 *
	 * @param key the constant key to use for the setting
	 * @param defaultInt the default value to use for the setting
	 */
	public StringArraySetting(Properties defaultProps, Properties props, String key, 
		String[] defaultValue) {
		super(defaultProps, props, key, decode(defaultValue));
	}
        
	/**
	 * Accessor for the value of this setting.
	 * 
	 * @return the value of this setting
	 */
	public String[] getValue() {
        return value;
	}

	/**
	 * Mutator for this setting.
	 *
	 * @param value the value to store
	 */
	public void setValue(String[] value) {
		super.setValue(decode(value));
	}
    
    /** Load value from property string value
     * @param sValue property string value
     *
     */
    protected void loadValue(String sValue) {
		value = encode(sValue);
    }
    
    /**
     * Splits the string into an Array
     */
    private static final String[] encode(String src) {
        
        if (src == null || src.length()==0) {
            return (new String[0]);
        }
        
        return StringUtils.split(src, ";");
    }
    
    /**
     * Separates each field of the array by a semicolon
     */
    private static final String decode(String[] src) {
        
        if (src == null || src.length==0) {
            return "";
        }
        
        StringBuffer buffer = new StringBuffer();
        
        for(int i = 0; i < src.length; i++) {
            buffer.append(src[i]);
            if (i < src.length-1) { buffer.append(';'); }
        }
            
        return buffer.toString();
    }
}
