package org.lastbamboo.client.util.settings;

import org.lastbamboo.client.util.settings.types.*;

/**
 * Specialized settings class for the user's profile.
 */
public class ProfileSettings extends ShootSettings {
	
	private ProfileSettings() {
		// Make sure this class cannot be instantiated.
	}
	
    /**
     * Setting for the user's user name.
     */
    public static final StringSetting USER_NAME =
        FACTORY.createStringSetting("USER_NAME", "");
    
    /**
     * Setting for the user's password.
     */
    public static final StringSetting PASSWORD =
        FACTORY.createStringSetting("PASSWORD", "");
    
	/**
	 * Setting for the user's first name.
	 */
	public static final StringSetting FIRST_NAME =
		FACTORY.createStringSetting("FIRST_NAME", "");
	
	/**
	 * Setting for the user's last name.
	 */
	public static final StringSetting LAST_NAME =
		FACTORY.createStringSetting("LAST_NAME", "");
	
	/**
	 * Setting for the user's last name.
	 */
	public static final StringSetting E_MAIL =
		FACTORY.createStringSetting("E_MAIL", "");
	
	/**
	 * Setting for the user's gender.
	 */
	public static final StringSetting GENDER = 
		FACTORY.createStringSetting("GENDER", "");
	
	/**
	 * Setting for the user's phone number.
	 */
	public static final StringSetting PHONE_NUMBER = 
		FACTORY.createStringSetting("PHONE_NUMBER", "");
	
	
	/**
	 * Utility method for checking whether or not the user has established a profile.
	 * 
	 * @return <tt>true</tt> if the user has successfully entered a profile, 
	 *  otherwise <tt>false</tt>
	 */
	public static final boolean hasProfile() {
		return !FIRST_NAME.getValue().equals("") &&
			!LAST_NAME.getValue().equals("") &&
			!E_MAIL.getValue().equals("");
	}
}
