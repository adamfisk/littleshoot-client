package org.lastbamboo.client.services;

/**
 * Keys for session attributes.
 */
public class SessionAttributeKeys
    {

    private SessionAttributeKeys()
        {
        // Don't construct.
        }
    
    /**
     * The key for the secret key for a given session.
     */
    public static final String KEY = "key";
    
    //public static final String LOGGED_IN = "loggedIn";

    public static final String USER_ID = "userId";

    public static final String SESSION_ID = "sessionid";

    //public static final String SITE_KEY = "siteKey";

    public static final String CLIENT_VERSION = "appVersion";

    public static final String INSTANCE_ID = "instanceId";

    public static final String CLIENT_PRESENT = "appPresent";

    public static final String PENDING_DOWNLOAD = "pendingDownload";
    }
