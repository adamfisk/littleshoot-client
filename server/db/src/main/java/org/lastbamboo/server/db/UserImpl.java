package org.lastbamboo.server.db;

import org.apache.commons.lang.math.RandomUtils;
import org.lastbamboo.server.resource.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource class for users stored in the database.
 */
public class UserImpl implements User
    {

    private static final Logger m_log = 
        LoggerFactory.getLogger(UserImpl.class);
    
    /**
     * The hibernate ID of this user resource.
     */
    private Long m_id;

    private String m_email;

    private String m_password;
    
    private boolean m_verified;
    
    private Long m_userId;
    
    private Long m_passwordResetId;
    
    
    
    /**
     * Required no-argument constructor Hibernate uses to generate new 
     * instances.
     */
    public UserImpl() 
        {
        // No argument constructor for hibernate to use.
        }
    
    /**
     * Creates a new user resource.
     * 
     * @param email The user's email.
     * @param password The user's password.
     * @param userId The id of the user.
     */
    public UserImpl(final String email, final String password, 
        final long userId)
        {
        this.m_email = email;
        this.m_password = password;
        this.m_userId = new Long(userId);
        this.m_passwordResetId = new Long(RandomUtils.nextLong());
        
        // Users aren't verified when they're first created.
        this.m_verified = false;
        }

    public String getEmail()
        {
        return this.m_email;
        }

    public void setEmail(final String eMail)
        {
        this.m_email = eMail;
        }

    public String getPassword()
        {
        return this.m_password;
        }

    public void setPassword(final String password)
        {
        this.m_password = password;
        }

    public boolean getVerified()
        {
        return this.m_verified;
        }

    public void setVerified(boolean verified)
        {
        this.m_verified = verified;
        }

    public long getUserId()
        {
        return this.m_userId.longValue();
        }

    public void setUserId(final long userId)
        {
        this.m_userId = new Long(userId);
        }

    public long getPasswordResetId()
        {
        return this.m_passwordResetId.longValue();
        }

    public void setPasswordResetId(final long passwordResetId)
        {
        this.m_passwordResetId = new Long(passwordResetId);
        }
    }
