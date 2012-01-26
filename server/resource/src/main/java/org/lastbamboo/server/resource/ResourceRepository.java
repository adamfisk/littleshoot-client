package org.lastbamboo.server.resource;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;


/**
 * Interface for classes that are repositories for resources.  These 
 * could be different remote server implementations, a local database, or 
 * anything else that might be interested in storing resource data.
 */
public interface ResourceRepository
    {

    /**
     * Inserts a new resource into the repository.
     * 
     * @param fr The file resource to insert.
     * @throws IOException If there's an error adding the resource.
     */
    void insertResource(FileResource fr) throws IOException;
    
    /**
     * Deletes a resource from the repository.
     * 
     * @param instanceId The ID of the instance.
     * @param sha1 The SHA-1 URN of the resource.
     * 
     * @throws IOException If there's an error adding the resource.
     */
    void deleteResource(long instanceId, String sha1) throws IOException;

    /**
     * Searches the repository with the given criteria.
     * 
     * @param keywords The keywords to search for.
     * @param startIndex The start index of results to return.
     * @param itemsPerPage The number of items to return.
     * @param os The operating system.
     * @param userId The ID of the user initiating the search.
     * @param applications Whether or not to search for applications.
     * @param audio Whether or not to search for audio.
     * @param docs Whether or not to search for documents.
     * @param images Whether or not to search for images.
     * @param videos Whether or not to search for videos.
     * @return The matching resources.
     */
    MetaFileResourceResult search(String keywords, int startIndex, 
        int itemsPerPage, String os, long userId, boolean applications, 
        boolean audio, boolean docs, boolean images, boolean videos);
    
    /**
     * Searches the repository with the given criteria.
     * 
     * @param keywords The keywords to search for.
     * @param startIndex The start index of results to return.
     * @param itemsPerPage The number of items to return.
     * @param os The operating system.
     * @param userId The ID of the user initiating the search.
     * @param groupName The name of the group to search in.
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
     * Sets whether or not the instance with the specified ID is online or 
     * offline.
     * 
     * @param instanceId The ID of the instance in question.
     * @param baseUri The base URI to use for accessing this user.
     * @param online Whether or not the user is online.
     * @param serverAddress The address of the server reporting the online
     * status. 
     */
    void setInstanceOnline(long instanceId, String baseUri, boolean online, 
        String serverAddress);

    /**
     * Accessor for a single file resource and the {@link Collection} of online
     * users with that resource.
     * 
     * @param uri The URI for the resource.
     * @return The file resource and the {@link Collection} of instances with 
     * that resource.
     */
    FileAndInstances getFileAndInstances(URI uri);

    /**
     * Marks the resource with the specified URN as "taken down", presumably
     * with a DMCA takedown notice.
     * 
     * @param sha1Urn The URN of the file to take down.
     * @param takeDown Whether to take the file down or to bring it up.
     */
    void takeDown(String sha1Urn, boolean takeDown);
    
    /**
     * Accesses all online instances.
     * @return The {@link Collection} of online instances. 
     */
    Collection<OnlineInstance> getOnlineInstances();

    /**
     * Sets the online status of a server.
     * 
     * @param online Whether or not the server is online.
     * @param serverAddress The address of the server.
     */
    void setServerOnline(boolean online, String serverAddress);

    /**
     * Retrieves paged {@link FileResource}s for a specific user.
     * 
     * @param pageIndex The index of the page to retrieve.
     * @param resultsPerPage The results per page to retrieve.
     * @param instanceId The ID of the LittleShoot instance.
     * @return The results.
     */
    FileResourceResult getFileResources(int pageIndex, int resultsPerPage, 
        long instanceId);

    /**
     * Retrieves paged {@link FileResource}s for a specific user.
     * 
     * @param pageIndex The index of the page to retrieve.
     * @param resultsPerPage The results per page to retrieve.
     * @param instanceId The ID of the LittleShoot instance.
     * @param groupName The name of the group.
     * @return The results.
     */
    FileResourceResult getFileResources(int pageIndex, int resultsPerPage, 
        long instanceId, String groupName);
    
    /**
     * Accessor for a single file resource.
     * 
     * @param userId The ID of the user.
     * @param sha1 The SHA-1 of the resource.
     * @return The {@link FileResource} or <code>null</code> if it doesn't 
     * exist.
     */
    FileResource getFileResource(long userId, String sha1);

    /**
     * Creates a new user.
     * 
     * @param email The email address for the new user.
     * @param password The password for the new user.
     * @return The authentication token for this user, or -1 if new user 
     * creation failed.
     * @throws UserExistsException If the user already exists.
     */
    long newWebUser(String email, String password) throws UserExistsException;

    /**
     * Authenticates the user.
     * 
     * @param email The email address for the user.
     * @param password The password.
     * @return The user ID.
     * @throws UserNotVerifiedException If the user is not yet verified.
     * @throws UserNotFoundException If the user is not found.
     */
    long authenticateWebUser(String email, String password) 
        throws UserNotVerifiedException, UserNotFoundException;

    /**
     * Deletes the specified user.
     * 
     * @param email The email of the user.
     * @return <code>true</code> if the user was successfully deleted.  
     * Otherwise <code>false</code>.
     */
    boolean deleteWebUser(String email);

    /**
     * Verifies a user's e-mail address with the specified token.
     * 
     * @param userId The token verifying the user's e-mail address.
     * @return <code>true</code> if the user's e-mail address was successfully 
     * verified.  This will also return <code>true</code> if the user's address 
     * has recently been verified.
     */
    boolean confirmNewUser(long userId);

    /**
     * Generates an ID to use in a dynamically created URL allowing the user to 
     * reset his or her password.
     * 
     * @param email The e-mail ID of the user.
     * @return The generated password reset ID.
     * @throws UserNotFoundException If we cannot locate a user with the 
     * specified e-mail address.
     */
    long generatePasswordResetId(String email) throws UserNotFoundException;

    /**
     * Resets the specified user's password.
     * 
     * @param email The user's e-mail address.
     * @param password The user's password.
     * @param resetId The reset ID.
     * @throws UserNotFoundException If the user cannot be found. 
     * @throws BadPasswordResetIdException Thrown on an invalid reset ID.
     * @throws IOException If we cannot access the database. 
     */
    void resetPassword(String email, String password, long resetId) 
        throws UserNotFoundException, BadPasswordResetIdException, IOException;

    String newGroup(long userId, String name, String description,
        String permission) throws GroupExistsException, IOException;

    boolean hasGroupPermission(long userId, String groupName);

    /**
     * Edit the resource with the specified instance ID, tags, etc.
     * 
     * @param instanceId The ID of the instance.
     * @param sha1 The URN for the file to edit.
     * @param tags The new tags.
     * @param url The URL for the file.
     * @throws IOException If we cannot access the database.
     */
    void editResource(long instanceId, String sha1, String tags, String url) 
        throws IOException;
    }
