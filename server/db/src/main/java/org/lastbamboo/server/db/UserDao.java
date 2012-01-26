package org.lastbamboo.server.db;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.lastbamboo.server.resource.BadPasswordResetIdException;
import org.lastbamboo.server.resource.UserExistsException;
import org.lastbamboo.server.resource.UserNotFoundException;
import org.lastbamboo.server.resource.UserNotVerifiedException;

/**
 * DAO for users.
 */
public interface UserDao
    {

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
     * @param email The user's email address.
     * @param password The password.
     * @return The user ID.
     * @throws UserNotVerifiedException If the user is not yet verified.
     * @throws UserNotFoundException If the user is not found.
     */
    long authenticateWebUser(String email, String password) throws 
        UserNotVerifiedException, UserNotFoundException;
    
    /**
     * Deletes the specified user.
     * 
     * @param email The email address of the user.
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
     * @throws BadPasswordResetIdException Exception for an invalid reset ID.
     * @throws IOException If we cannot access the database. 
     */
    void resetPassword(String email, String password, long resetId) 
        throws UserNotFoundException, BadPasswordResetIdException, IOException;

    void newAminGroup(long userId, String groupId) throws IOException;

    Map<String, Collection<String>> getAttributes(long userId) throws IOException;

    boolean hasGroupPermission(long userId, String groupName);
    }
