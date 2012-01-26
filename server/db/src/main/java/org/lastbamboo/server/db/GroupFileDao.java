package org.lastbamboo.server.db;

import java.io.IOException;

import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.UnexpectedValueException;

/**
 * Interface for files belonging to groups.
 */
public interface GroupFileDao
    {

    void insertFile(FileResource fr) throws UnexpectedValueException, IOException;

    }
