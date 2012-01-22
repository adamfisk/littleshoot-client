package org.lastbamboo.client.services;

/**
 * Interface for purging files that have been deleted on disk.
 */
public interface FilesPurger
    {

    /**
     * Purges deleted files.
     */
    void purgeFiles();

    }
