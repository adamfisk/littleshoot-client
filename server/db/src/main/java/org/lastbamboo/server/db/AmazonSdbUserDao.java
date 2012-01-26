package org.lastbamboo.server.db;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.amazon.sdb.AmazonSdb;
import org.lastbamboo.common.amazon.sdb.AmazonSdbUtils;
import org.lastbamboo.common.util.SecurityUtils;
import org.lastbamboo.server.resource.BadPasswordResetIdException;
import org.lastbamboo.server.resource.UserExistsException;
import org.lastbamboo.server.resource.UserNotFoundException;
import org.lastbamboo.server.resource.UserNotVerifiedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User resource DAO implementation.
 */
public class AmazonSdbUserDao implements UserDao
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final SecureRandom m_idCreator;
    private final AmazonSdb m_simpleDb;

    /**
     * Creates a new user DAO.
     * 
     * @param simpleDb The Amazon Simple DB database.
     */
    public AmazonSdbUserDao(final AmazonSdb simpleDb)
        {
        m_log.debug("Creating new Amazon Simple DB DAO");
        this.m_simpleDb = simpleDb;
        this.m_idCreator = new SecureRandom();
        
        // The first request can take awhile, so do it now.
        m_idCreator.nextLong();
        try
            {
            this.m_simpleDb.createDomain();
            }
        catch (final IOException e)
            {
            m_log.warn("Could not access Simple DB", e);
            throw new IllegalArgumentException("Could not create domain!!");
            }
        }

    public long newWebUser(final String email, final String password) 
        throws UserExistsException
        {
        m_log.debug("Creating new user...");
        try
            {
            final String queryExpression = 
                AmazonSdbUtils.createQuery(UserAttributes.EMAIL, email);
            final Collection<String> items = 
                this.m_simpleDb.query(queryExpression);
            if (!items.isEmpty())
                {
                m_log.debug("User already exists");
                throw new UserExistsException("User exists");
                }
            final Map<String, Collection<String>> map = 
                new HashMap<String, Collection<String>>();
            
            final long id = this.m_idCreator.nextLong();
            AmazonSdbUtils.addAttribute(map, UserAttributes.ID, id);
            final String hashed = SecurityUtils.hash(password);
            AmazonSdbUtils.addAttribute(map, UserAttributes.EMAIL, email);
            AmazonSdbUtils.addAttribute(map, UserAttributes.PWD, hashed);
            AmazonSdbUtils.addAttribute(map, UserAttributes.VERIFIED, "false");
            AmazonSdbUtils.addAttribute(map, UserAttributes.PWD_RESET_ID, newId());
            AmazonSdbUtils.addAttribute(map, UserAttributes.CREATION_DATE, 
                new Date());
            this.m_simpleDb.putAttributes(newItemName(id), map);
            return id;
            }
        catch (final IOException e)
            {
            m_log.warn("Could not access Simple DB!!", e);
            return -1L;
            }
        }

    private String newId()
        {
        final long rawId = this.m_idCreator.nextLong();
        return String.valueOf(rawId); 
        }

    public boolean confirmNewUser(final long userId)
        {
        m_log.debug("Confirming new user...");
        
        final String itemName = newItemName(userId);
        final Map<String, String> attributes;
        try
            {
            attributes = this.m_simpleDb.getAttributes(itemName);
            }
        catch (final IOException e)
            {
            return false;
            }
        
        if (attributes.isEmpty())
            {
            m_log.warn("Received verification token for user we don't see");
            return false;
            }
        
        else
            {
            final Map<String, Collection<String>> map =
                new HashMap<String, Collection<String>>();
            AmazonSdbUtils.addAttribute(map, UserAttributes.VERIFIED, "true");
            try
                {
                this.m_simpleDb.putAttributes(itemName, map, true);
                return true;
                }
            catch (final IOException e)
                {
                m_log.warn("Could not access Simple DB", e);
                return false;
                }
            }
        }
    
    public boolean deleteWebUser(final String email)
        {
        try
            {
            final String itemName = getItemName(email);
            this.m_simpleDb.deleteAttributes(itemName);
            return true;
            }
        catch (final IOException e)
            {
            m_log.warn("Could not access Simple DB!!", e);
            return false;
            }
        catch (final UserNotFoundException e)
            {
            m_log.warn("Could not find matching user for email: "+email);
            return false;
            }
        }
    

    public long authenticateWebUser(final String email, final String password) 
        throws UserNotVerifiedException, UserNotFoundException
        {
        m_log.debug("Authenticating user...");
        final String hashed = SecurityUtils.hash(password);
        try
            {
            final Map<String, String> attributes = getAttributes(email);
            
            final String pwd = attributes.get(UserAttributes.PWD);
            if (StringUtils.isBlank(pwd) || !pwd.equals(hashed))
                {
                throw new UserNotFoundException("No user with e-mail: "+email);
                }
            else
                {
                final String userIdLong = attributes.get(UserAttributes.ID);
                if (StringUtils.isBlank(userIdLong))
                    {
                    throw new UserNotFoundException("No user with e-mail: "+email);
                    }
                final String verifiedString = 
                    attributes.get(UserAttributes.VERIFIED);
                if (verifiedString.equals("true"))
                    {
                    return Long.parseLong(userIdLong);
                    }
                else
                    {
                    throw new UserNotVerifiedException();
                    }
                }
            }
        catch (final IOException e)
            {
            m_log.warn("Could not access Simple DB!!", e);
            return -1L;
            }
        }

    public long generatePasswordResetId(final String email) 
        throws UserNotFoundException
        {
        try
            {
            final String itemName = getItemName(email);
            final Map<String, Collection<String>> map = 
                new HashMap<String, Collection<String>>();
            final long resetId = this.m_idCreator.nextLong();
            AmazonSdbUtils.addAttribute(map, UserAttributes.PWD_RESET_ID, 
                String.valueOf(resetId));
            this.m_simpleDb.putAttributes(itemName, map, true);
            return resetId;
            
            }
        catch (final IOException e)
            {
            m_log.warn("Could not access Simple DB!!", e);
            return -1L;
            }
        }

    public void resetPassword(final String email, final String password, 
        final long resetId) throws UserNotFoundException, 
        BadPasswordResetIdException, IOException
        {
        final String itemName = getItemName(email);
        final Map<String, String> attributes = 
            this.m_simpleDb.getAttributes(itemName);
        final String resetIdString = 
            attributes.get(UserAttributes.PWD_RESET_ID);
        
        if (StringUtils.isBlank(resetIdString))
            {
            m_log.debug("Could not find user with email: {}", email);
            throw new UserNotFoundException("Could not find user with email.");
            }
        
        if (resetIdString.equals(String.valueOf(resetId)))
            {
            final String hashed = SecurityUtils.hash(password);
            
            final Map<String, Collection<String>> map = 
                new HashMap<String, Collection<String>>();
            AmazonSdbUtils.addAttribute(map, UserAttributes.VERIFIED, true);
            AmazonSdbUtils.addAttribute(map, UserAttributes.PWD, hashed);
            AmazonSdbUtils.addAttribute(map, UserAttributes.PWD_RESET_ID, 
                this.m_idCreator.nextLong());
            this.m_simpleDb.putAttributes(itemName, map, true);
            }
        else
            {
            m_log.warn(resetId + " does not match DB value of "+resetIdString);
            
            // We reset the ID in any case.  This makes attacks against the ID
            // virtually impossible, as you only get one guess.  Valid users can
            // always request another reset if someone somehow gets here first.
            final Map<String, Collection<String>> map = 
                new HashMap<String, Collection<String>>();
            final long newId = this.m_idCreator.nextLong();
            AmazonSdbUtils.addAttribute(map, UserAttributes.PWD_RESET_ID, newId);
            this.m_simpleDb.putAttributes(itemName, map, true);
            throw new BadPasswordResetIdException();
            }
        }
    
    private Map<String, String> getAttributes(final String email) 
        throws IOException, UserNotFoundException
        {
        final String itemName = getItemName(email);
        return this.m_simpleDb.getAttributes(itemName);
        }

    private String getItemName(final String email) throws IOException, 
        UserNotFoundException
        {
        final String queryExpression = 
            AmazonSdbUtils.createQuery(UserAttributes.EMAIL, email);
        final Collection<String> items = this.m_simpleDb.query(queryExpression);
        
        if (items.isEmpty())
            {
            m_log.debug("No user found with e-mail: "+email);
            throw new UserNotFoundException(
                "No user found with e-mail: "+email);
            }
    
        if (items.size() > 1)
            {
            m_log.error("There are "+items.size()+" users with the same " +
                "email address of: "+email);
            }
        final String itemName = items.iterator().next(); 
        return itemName;
        }

    private String newItemName(final long id)
        {
        return UserAttributes.ID+"."+id;
        }
    
    public void newAminGroup(final long userId, final String groupId) 
        throws IOException
        {
        final String itemName = newItemName(userId);
        this.m_simpleDb.putAttribute(itemName, UserAttributes.ADMIN_GROUPS, 
            groupId);
        }
    
    public Map<String, Collection<String>> getAttributes(final long userId) 
        throws IOException
        {
        final String itemName = newItemName(userId);
        return this.m_simpleDb.getMultiValueAttributes(itemName);
        }
    
    public boolean hasGroupPermission(final long userId, 
        final String groupName)
        {
        final String itemName = newItemName(userId);
        final Map<String, String> attributes;
        try
            {
            attributes = this.m_simpleDb.getAttributes(itemName);
            }
        catch (final IOException e)
            {
            m_log.warn("Could not access Simple DB", e);
            return false;
            }
        
        if (attributes.isEmpty())
            {
            m_log.warn("Could not locate user.");
            return false;
            }
        
        final String email = attributes.get(UserAttributes.EMAIL);
        String domain = StringUtils.substringAfter(email, "@");
        domain = StringUtils.substringBeforeLast(domain, ".");
        if (groupName.equalsIgnoreCase(domain))
            {
            m_log.debug("E-mail and group names match for: {}", domain);
            return true;
            }
        
        m_log.debug("Domains don't match.  " +
            "Expected "+groupName+" but e-mail was "+email);
        return false;
        
        }
    }
