package org.lastbamboo.client.util.settings;

import org.lastbamboo.client.util.settings.types.IntSetting;

/**
 * Settings class for HTTP settings.
 */
public final class HttpSettings extends ShootSettings
    {

    private HttpSettings()
        {
        // Nothing to construct.
        }
    
    /**
     * The port the HTTP server should run on.
     */
    public static final IntSetting HTTP_PORT = 
        FACTORY.createIntSetting("HTTP_PORT", 8107);
    }
