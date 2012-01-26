package org.lastbamboo.server.db;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.lastbamboo.common.util.SecurityUtils;
import org.lastbamboo.server.resource.BadPasswordResetIdException;
import org.lastbamboo.server.resource.User;
import org.lastbamboo.server.resource.UserExistsException;
import org.lastbamboo.server.resource.UserNotFoundException;
import org.lastbamboo.server.resource.UserNotVerifiedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User resource DAO implementation.
 */
public class UserDaoImpl implements UserDao
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final SessionFactory m_sessionFactory;
    private final SecureRandom m_idCreator;

    /**
     * Creates a new DAO instance.
     * 
     * @param sessionFactory The Hibernate session factory.
     */
    public UserDaoImpl(final SessionFactory sessionFactory)
        {
        this.m_sessionFactory = sessionFactory;
        this.m_idCreator = new SecureRandom();
        
        // The first request can take awhile, so do it now.
        m_idCreator.nextLong();
        }
    
    public long newWebUser(final String email, final String password) 
        throws UserExistsException
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                UserImpl.class);
        criteria.add(Restrictions.eq("m_email", email));
        final User user = (User)criteria.uniqueResult();
        if (user != null)
            {
            throw new UserExistsException("User exists");
            }
        else
            {
            final long userId = this.m_idCreator.nextLong();
            final String hashed = SecurityUtils.hash(password);
            final User usr = new UserImpl(email, hashed, userId);
            this.m_sessionFactory.getCurrentSession().persist(usr);
            return userId;
            }
        }

    public boolean confirmNewUser(final long token)
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                UserImpl.class);
        criteria.add(Restrictions.eq("m_userId", new Long(token)));
        final User user = (User)criteria.uniqueResult();
        if (user != null)
            {
            user.setVerified(true);
            return true;
            }
        else
            {
            m_log.warn("Received verification token for user we don't see");
            return false;
            }
        }
    
    public boolean deleteWebUser(final String email)
        {
        final String deleteString = 
            "delete UserImpl user where " +
            "user.m_email=:email";
        final Query query = 
            this.m_sessionFactory.getCurrentSession().createQuery(deleteString);
        query.setString("email", email);
        final int numDeleted = query.executeUpdate();
        if (numDeleted == 1)
            {
            return true;
            }
        if (numDeleted > 1)
            {
            m_log.warn("More than one user deleted with user name: "+email);
            return true;
            }
        return false;
        }
    

    public long authenticateWebUser(final String email, final String password) 
        throws UserNotVerifiedException, UserNotFoundException
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                UserImpl.class);
        criteria.add(Restrictions.eq("m_email", email));
        final String hashed = SecurityUtils.hash(password);
        criteria.add(Restrictions.eq("m_password", hashed));
        final User user = (User)criteria.uniqueResult();
        
        if (user == null)
            {
            throw new UserNotFoundException("No user with e-mail: "+email);
            }
        
        else if (!user.getVerified())
            {
            throw new UserNotVerifiedException();
            }
        return user.getUserId();
        }

    public long generatePasswordResetId(final String email)
        throws UserNotFoundException
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(UserImpl.class);
        criteria.add(Restrictions.eq("m_email", email));
        final User user = (User)criteria.uniqueResult();

        if (user == null)
            {
            m_log.debug("Could not find user with email: {}", email);
            throw new UserNotFoundException("Could not find user with email.");
            }
        
        final long resetId = this.m_idCreator.nextLong();
        user.setPasswordResetId(resetId);
        return resetId;
        }

    public void resetPassword(final String email, final String password, 
        final long resetId) throws UserNotFoundException, 
        BadPasswordResetIdException
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                UserImpl.class);
        criteria.add(Restrictions.eq("m_email", email));
        final User user = (User)criteria.uniqueResult();
    
        if (user == null)
            {
            m_log.debug("Could not find user with email: {}", email);
            throw new UserNotFoundException("Could not find user with email.");
            }
        
        if (user.getPasswordResetId() == resetId)
            {
            // We recognize this as a valid verification.
            user.setVerified(true);
            final String hashed = SecurityUtils.hash(password);
            user.setPassword(hashed);
            // The reset's all set, so we change the ID.
            user.setPasswordResetId(this.m_idCreator.nextLong());
            }
        else
            {
            // We reset the ID in any case.  This makes attacks against the ID
            // virtually impossible, as you only get one guess.  Valid users can
            // always request another reset if someone somehow gets here first.
            user.setPasswordResetId(this.m_idCreator.nextLong());
            throw new BadPasswordResetIdException();
            }
        }

    public void newAminGroup(long userId, String groupId) throws IOException
        {
        // TODO Auto-generated method stub
        
        }

    public Map<String, Collection<String>> getAttributes(long userId) throws IOException
        {
        // TODO Auto-generated method stub
        return null;
        }

    public boolean hasGroupPermission(long userId, String groupName)
        {
        // TODO Auto-generated method stub
        return false;
        }
    }
