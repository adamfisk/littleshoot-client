package org.lastbamboo.client.services.download;

import java.io.File;

/**
 * Interface for publishing files.
 */
public interface FilePublisher
    {

    /**
     * Publishes the file.
     * 
     * @param file The file to publish.
     * @return The JSON response.
     */
    String publish(File file);

    }
