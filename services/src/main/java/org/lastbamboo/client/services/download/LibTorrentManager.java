package org.lastbamboo.client.services.download;

import org.lastbamboo.jni.JLibTorrent;


/**
 * Manager interface for LibTorrent.
 */
public interface LibTorrentManager
    {

    /**
     * Accessor for the LibTorrent instance.
     * 
     * @return The LibTorrent instance.
     */
    JLibTorrent getLibTorrent();

    /**
     * Turns seeding on or off.
     * 
     * @param seeding <code>true</code> to turn seeding on, otherwise
     * <code>false</code>.
     */
    void setSeeding(boolean seeding);

    /**
     * Sets the maximum upload speed in bytes per second
     * 
     * @param bytesPerSecond The maximum upload speed.
     */
    void setMaxUploadSpeed(int bytesPerSecond);

    }
