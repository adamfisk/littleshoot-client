package org.lastbamboo.server.resource;


/**
 *  Interface for users logged in to the web site.
 */
public interface User
    {

    String getEmail();

    void setEmail(String email);

    String getPassword();

    void setPassword(String password);
    
    void setVerified(boolean verified);
    
    boolean getVerified();
    
    void setUserId(long id);
    
    long getUserId();
    
    /**
     * Sets the ID used to reset the user's password.
     * 
     * @param resetId The current ID for reseting the user's password.
     */
    void setPasswordResetId(long resetId);
    
    /**
     * Accesses the ID for reseting the user's password.
     * @return The ID for reseting the user's password.
     */
    long getPasswordResetId();
    
    
    }
