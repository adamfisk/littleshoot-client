package org.lastbamboo.client.util.settings;

import org.lastbamboo.client.util.settings.types.BooleanSetting;


/**
 * Settings for searches.
 */
public final class SearchSettings extends ShootSettings {
    
    private SearchSettings() {
        // Nothing to construct.
    }
    
	/**
	 * Constant for whether or not Gnutella searching is currently enabled.
	 */
    public static final BooleanSetting GNUTELLA_ENABLED = 
        FACTORY.createBooleanSetting("GNUTELLA_ENABLED", true);
    
	/**
	 * Constant for whether or not Friends searching is currently enabled.
	 */
    public static final BooleanSetting FRIENDS_ENABLED = 
        FACTORY.createBooleanSetting("FRIENDS_ENABLED", true);
}
