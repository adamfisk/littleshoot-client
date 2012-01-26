package org.lastbamboo.server.db;

import java.io.IOException;

import org.lastbamboo.server.resource.GroupExistsException;

/**
 * DAO for groups.
 */
public interface GroupDao
    {

    String newGroup(long userId, String name, String description,
        String permission) throws GroupExistsException, IOException;

    }
