package org.lastbamboo.client.prefs;

/**
 * Keys for user preferences.
 */
public class PrefKeys
    {

    /**
     * The key for the unique ID for each user.
     */
    public static final String ID = "LITTLESHOOT_ID";
    
    /**
     * Key for the shared directory.
     */
    public static final String SHARED_DIR = "SHARED_DIR";

    /**
     * Key for the incomplete directory.
     */
    public static final String INCOMPLETE_DIR = "INCOMPLETE_DIR";

    /**
     * Key for the public URL for this node, typically discovered using STUN.
     */
    public static final String PUBLIC_URL = "PUBLIC_URL";

    /**
     * Directory for completed downloads.
     */
    public static final String DOWNLOAD_DIR = "DOWNLOAD_DIR";

    /**
     * The key for the base URI to use for this host.  This could be an HTTP
     * URL if the server is not behind a firewall, for example, or it could
     * be a SIP URI.
     */
    public static final String BASE_URI = "BASE_URI";

    /**
     * Key for the version of the application.
     */
    public static final String APP_VERSION = "APP_VERSION";
    
    /**
     * Key for the version of the application the last time it ran.
     */
    public static final String LAST_VERSION = "LAST_VERSION";

    /**
     * Key for whether or not we're using the command-line interface.
     */
    public static final String CLI = "CLI";

    public static final String TWITTER_PASSWORD = "TWITTER_PASSWORD";
    public static final String TWITTER_USER_NAME = "TWITTER_USER_NAME";

    public static final String RUNNING = "RUNNING";

    public static final String TOTAL_BITTORRENT_DOWNLOAD_BYTES = "BITTORRENT_DOWNLOAD_BYTES";
    public static final String TOTAL_BITTORRENT_UPLOAD_BYTES = "BITTORRENT_UPLOAD_BYTES";

    }
