package org.lastbamboo.client.prefs;

import java.io.File;
import java.security.SecureRandom;
import java.util.Random;
import java.util.prefs.Preferences;

import org.apache.commons.lang.StringUtils;
import org.littleshoot.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General preferences handler.
 */
public class Prefs {

    private static final Logger LOG = LoggerFactory.getLogger(Prefs.class);
    private static boolean s_newVersion = false;
    private static boolean s_newVersionSet = false;
    private static boolean s_installed;

    /**
     * Sets the user's ID for other classes to use.
     * 
     * @return Whether or not setting the ID succeeded.
     */
    private static long setId() {
        LOG.debug("Setting ID...");
        final Random random = new SecureRandom();
        final long newId = Math.abs(random.nextInt());
        LOG.debug("Adding ID to prefs: " + newId);
        final Preferences prefs = Preferences.userRoot();
        prefs.putLong(PrefKeys.ID, newId);

        // Set the BASE URI to a SIP URI until the NAT discovery process tells
        // us differently (that the host is a public server).
        prefs.put(PrefKeys.BASE_URI, "sip://" + newId);
        return newId;
    }

    /**
     * Accessor for the user ID.
     * 
     * @return The user ID.
     */
    public static long getId() {
        final Preferences prefs = Preferences.userRoot();
        final long id = prefs.getLong(PrefKeys.ID, -1);
        if (id == -1) {
            return setId();
        }
        return id;
    }

    /**
     * Accessor for the version of the client we're running. If we're running on
     * the main line, this just returns 0.00.
     * 
     * @return The version we're running.
     */
    public static double getVersion() {
        // Note this is set from the command line with a -D argument!
        final String versionString = System.getProperty(
                "org.lastbamboo.client.version", "0.00");
        return Double.parseDouble(versionString);
    }

    /**
     * Returns whether or not this is a new version.
     * 
     * @return <code>true</code> if this is a new version, otherwise
     *         <code>false</code>.
     */
    public static boolean newVersion() {
        // It's actually very important that we cache this setting. The reason
        // is that this method also sets the old version to the current version,
        // so simply running the method again will not report a new version
        // the second time. So this is cached for correctness rather than
        // performance!!
        if (s_newVersionSet) {
            LOG.debug("New version already set...");
            return s_newVersion;
        }

        final double newVersion = getVersion();
        LOG.debug("New version is: " + newVersion);
        if (newVersion == 0.00) {
            // We're running on the main line, so we don't consider it a
            // "new version"
            LOG.debug("New version is 0.00");
            return false;
        }

        final Preferences prefs = Preferences.userRoot();

        final double oldVersion = prefs.getDouble(PrefKeys.LAST_VERSION, -1);
        LOG.debug("Old version is: " + oldVersion);
        if (oldVersion == -1) {
            LOG.debug("No last version");
            s_newVersion = true;
        }

        // Make sure we record the current version for the next run.
        prefs.putDouble(PrefKeys.LAST_VERSION, newVersion);
        if (newVersion > oldVersion) {
            LOG.debug("This version is newer");
            s_newVersion = true;
        }

        s_newVersionSet = true;
        LOG.debug("Returning: " + s_newVersion);
        return s_newVersion;
    }

    /**
     * Access the base URI for this user.
     * 
     * @return The base URI for addressing this user.
     */
    public static String getBaseUri() {
        final Preferences prefs = Preferences.userRoot();
        final String baseUri = prefs.get(PrefKeys.BASE_URI, "");
        if (StringUtils.isBlank(baseUri)) {
            LOG.error("Base URI not set!!");
        }
        return baseUri;
    }

    /**
     * Returns whether or not there's an ID for a LittleShoot user. This is a
     * good indicator of whether or not LittleShoot has ever been installed on
     * this machine.
     * 
     * @return <code>true</code> If there is an existing ID, otherwise
     *         <code>false</code>.
     */
    public static boolean hasId() {
        final Preferences prefs = Preferences.userRoot();
        final long id = prefs.getLong(PrefKeys.ID, -1);
        if (id == -1) {
            return false;
        }
        return true;
    }

    public static void setInstalled(final boolean installed) {
        s_installed = installed;
    }

    public static boolean isInstalled() {
        return s_installed;
    }

    public static boolean running() {
        final Preferences prefs = Preferences.userRoot();
        return prefs.getBoolean(PrefKeys.RUNNING, true);
    }

    /**
     * Returns the last version recorded as having run.
     * 
     * @return The last version recorded as having run.
     */
    public static double getLastVersion() {
        final Preferences prefs = Preferences.userRoot();
        return prefs.getDouble(PrefKeys.LAST_VERSION, 0.0);
    }

    /**
     * Sets the last version recorded as having run to the currently running
     * version.
     */
    public static void setLastVersion() {
        final Preferences prefs = Preferences.userRoot();
        prefs.putDouble(PrefKeys.LAST_VERSION, getVersion());
    }

    public static File getTorrentDir() {
        final File lsDir = CommonUtils.getDataDir();
        if (!lsDir.isDirectory()) {
            lsDir.mkdirs();
        }
        final File torrentDir = new File(lsDir, "torrents");
        if (!torrentDir.isDirectory()) {
            if (!torrentDir.mkdirs()) {
                LOG.error("Could not create torrent dir");
            }
        }
        return torrentDir;
    }

}
