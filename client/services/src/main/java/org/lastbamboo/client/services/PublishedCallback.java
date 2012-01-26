package org.lastbamboo.client.services;

import java.io.File;
import java.net.URI;

/**
 * Callback for when files are published.
 */
public interface PublishedCallback
    {

    /**
     * Called when the file has been posted to the LittleShoot servers.
     * 
     * @param file The published file.
     * @param sha1 The SHA-1.
     */
    void onFilePublished(File file, URI sha1);

    }
