package org.lastbamboo.client.services.download;

import java.io.File;

/**
 * Interface for moving downloaded files.
 */
public interface FileMover
    {

    boolean move(File incomplete, File complete);

    }
