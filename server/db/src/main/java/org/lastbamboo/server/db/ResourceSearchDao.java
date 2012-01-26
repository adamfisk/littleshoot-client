package org.lastbamboo.server.db;

import java.net.URI;
import java.util.Collection;

import org.lastbamboo.server.resource.FileAndInstances;
import org.lastbamboo.server.resource.MetaFileResourceResult;

/**
 * DAO for resource searching across the various resource tables.
 */
public interface ResourceSearchDao
    {
    
    /**
     * Searches the repository with the given criteria.
     * 
     * @param keywords The keywords to search for.
     * @param startIndex The start index of results to return.
     * @param itemsPerPage The number of items to return.
     * @param os The operating system.
     * @param userId The ID of the user initiating the search.
     * @param groupName The name of the group.
     * @param applications Whether or not to search for applications.
     * @param audio Whether or not to search for audio.
     * @param docs Whether or not to search for documents.
     * @param images Whether or not to search for images.
     * @param videos Whether or not to search for videos.
     * @return The matching resources.
     */
    MetaFileResourceResult search(String keywords, int startIndex, 
        int itemsPerPage, String os, long userId, String groupName, 
        boolean applications, boolean audio, boolean docs, boolean images, 
        boolean videos);

    /**
     * Accessor for a single file resource and the {@link Collection} of online
     * users with that resource.
     * 
     * @param sha1 The SHA-1 URN for the resource.
     * @return The file resource and the {@link Collection} of users with that
     * resource.
     */
    FileAndInstances getFileAndInstances(URI sha1);

    }
