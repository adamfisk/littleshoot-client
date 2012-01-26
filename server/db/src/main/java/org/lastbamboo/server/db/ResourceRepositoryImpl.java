package org.lastbamboo.server.db;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Collection;

import org.hibernate.SessionFactory;
import org.hibernate.context.ThreadLocalSessionContext;
import org.lastbamboo.common.jmx.client.JmxMonitor;
import org.lastbamboo.common.jmx.client.ServerStatusListener;
import org.lastbamboo.common.util.ShootConstants;
import org.lastbamboo.server.resource.BadPasswordResetIdException;
import org.lastbamboo.server.resource.FileAndInstances;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.FileResourceResult;
import org.lastbamboo.server.resource.GroupExistsException;
import org.lastbamboo.server.resource.MetaFileResourceResult;
import org.lastbamboo.server.resource.OnlineInstance;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.resource.UserExistsException;
import org.lastbamboo.server.resource.UserNotFoundException;
import org.lastbamboo.server.resource.UserNotVerifiedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A repository for resources that are stored in the database.
 */
public class ResourceRepositoryImpl implements ResourceRepository,
    ServerStatusListener
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final FileResourceDao m_fileResourceDao;
    private final InstanceDao m_instanceDao;
    private final ResourceSearchDao m_resourceSearchDao;
    private final SessionFactory m_sessionFactory;
    private final MetaFileResourceDao m_metaFileResourceDao;
    private final UserDao m_userDao;
    
    private final GroupDao m_groupDao;

    /**
     * Creates a new repository.
     * 
     * @param metaFileResourceDao The DAO for metadata about a single 
     * {@link FileResource}.
     * @param fileResourceDao The DAO for file resources.
     * @param userResourceDao The DAO for instance resources.
     * @param userDao The DAO for users.
     * @param resourceSearchDao The DAO for searching across the various 
     * resource tables.
     * @param sessionFactory The Hibernate session factory.
     * @param serverMonitor The class that monitors our servers.
     */
    public ResourceRepositoryImpl(
        final MetaFileResourceDao metaFileResourceDao,
        final FileResourceDao fileResourceDao,
        final InstanceDao userResourceDao, 
        final UserDao userDao, final GroupDao groupDao,
        final ResourceSearchDao resourceSearchDao,
        final SessionFactory sessionFactory,
        final JmxMonitor serverMonitor)
        {
        this.m_metaFileResourceDao = metaFileResourceDao;
        this.m_fileResourceDao = fileResourceDao;
        this.m_instanceDao = userResourceDao;
        this.m_userDao = userDao;
        this.m_groupDao = groupDao;
        this.m_resourceSearchDao = resourceSearchDao;
        this.m_sessionFactory = sessionFactory;
        serverMonitor.addListener(this);
        }
    
    public void deleteResource(final long instanceId, final String sha1) 
        throws IOException 
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            this.m_metaFileResourceDao.deleteResource(instanceId, sha1);
            this.m_fileResourceDao.deleteResource(instanceId, sha1);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            }
        
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        catch (final IOException e)
            {
            rollback(e);
            throw e;
            }
        }

    public void insertResource(final FileResource fr) throws IOException 
        {
        /*
        if (!StringUtils.isBlank(fr.getGroup()))
            {
            this.m_groupFileDao.insertFile(fr);
            return;
            }
            */
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            this.m_metaFileResourceDao.insertResource(fr);
            this.m_fileResourceDao.insertResource(fr);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        catch (final IOException e)
            {
            rollback(e);
            throw e;
            }
        }
    
    public FileResourceResult getFileResources(final int pageIndex,
        final int resultsPerPage, final long instanceId)
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final FileResourceResult result = 
                this.m_fileResourceDao.getFileResources(pageIndex, 
                    resultsPerPage, instanceId);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return result;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }
    

    public FileResourceResult getFileResources(final int pageIndex,
        final int resultsPerPage, final long instanceId, final String groupName)
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final FileResourceResult result = 
                this.m_fileResourceDao.getFileResources(pageIndex, 
                    resultsPerPage, instanceId, groupName);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return result;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }
    
    public FileResource getFileResource(final long userId, final String sha1)
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final FileResource result = 
                this.m_fileResourceDao.getFileResource(userId, sha1);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return result;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }

    public void setInstanceOnline(final long instanceId, final String baseUri, 
        final boolean online, final String serverAddress)
        {    
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            this.m_instanceDao.setInstanceOnline(instanceId, baseUri, online, 
                serverAddress);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }
    

    public long newWebUser(final String eMail, final String password) 
        throws UserExistsException 
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final long created = this.m_userDao.newWebUser(eMail, password);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return created;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        catch (final UserExistsException e)
            {
            rollback(e);
            throw e;
            }
        }
    
    public boolean hasGroupPermission(final long userId, final String groupName)
        {
        return this.m_userDao.hasGroupPermission(userId, groupName);
        }
    
    public String newGroup(final long userId, final String name, 
        final String description, final String permission) 
        throws GroupExistsException, IOException
        {
        final String groupId;
        try
            {
            groupId = this.m_groupDao.newGroup(userId, name, description, 
                 permission);
            }
        catch (final GroupExistsException e)
            {
            m_log.debug("Group exists!");
            throw e;
            }
        catch (final IOException e)
            {
            m_log.error("Could not access Simple DB.", e);
            throw e;
            }
        
        try
            {
            this.m_userDao.newAminGroup(userId, groupId);
            }
        catch (final IOException e)
            {
            // TODO: We need to theoretically roll back the entire transaction  
            // in this case.
            m_log.error("Could not access Simple DB.", e);
            throw e;
            }
        return groupId;
        }
    
    public boolean deleteWebUser(final String userName)
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final boolean deleted = this.m_userDao.deleteWebUser(userName);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return deleted;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }

    public long authenticateWebUser(final String userName, 
        final String password) throws UserNotFoundException, 
        UserNotVerifiedException 
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final long userId =
                this.m_userDao.authenticateWebUser(userName, password);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return userId;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        catch (final UserNotVerifiedException e)
            {
            rollback(e);
            throw e;
            }
        catch (final UserNotFoundException e)
            {
            rollback(e);
            throw e;
            }
        }
    
    public boolean confirmNewUser(final long token)
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final boolean result =
                this.m_userDao.confirmNewUser(token);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return result;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }

    public long generatePasswordResetId(final String email) 
        throws UserNotFoundException
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final long result =
                this.m_userDao.generatePasswordResetId(email);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return result;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        catch (final UserNotFoundException e)
            {
            rollback(e);
            throw e;
            }
        }

    public void resetPassword(final String email, final String password, 
        final long resetId) throws UserNotFoundException, 
        BadPasswordResetIdException, IOException 
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            this.m_userDao.resetPassword(email, password, resetId);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            }
        catch (final UserNotFoundException e)
            {
            rollback(e);
            throw e;
            }
        catch (final BadPasswordResetIdException e)
            {
            rollback(e);
            throw e;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        catch (final IOException e)
            {
            rollback(e);
            throw e;
            }
        }
    
    public void setServerOnline(boolean online, String serverAddress)
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            this.m_instanceDao.setServerOnline(online, serverAddress);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }


    public void editResource(final long instanceId, final String sha1, 
        final String tags, final String url) throws IOException
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            this.m_metaFileResourceDao.editResource(sha1, tags, url);
            this.m_fileResourceDao.editResource(instanceId, sha1, tags, url);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }        
        }
    
    public FileAndInstances getFileAndInstances(final URI uri)
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final FileAndInstances resource = 
                this.m_resourceSearchDao.getFileAndInstances(uri);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return resource;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }

    public MetaFileResourceResult search(final String keywords, 
        final int startIndex, final int itemsPerPage, final String os,
        final long userId, final boolean applications, final boolean audio, 
        final boolean docs, final boolean images, final boolean videos)
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final MetaFileResourceResult resources = 
                this.m_resourceSearchDao.search(keywords, startIndex, 
                    itemsPerPage, os, userId, ShootConstants.WORLD_GROUP,
                    applications, audio, docs, images, 
                    videos);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return resources;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }
    

    public MetaFileResourceResult search(final String keywords, 
        final int startIndex, final int itemsPerPage, final String os,
        final long userId, final String groupName, final boolean applications, 
        final boolean audio, final boolean docs, final boolean images, 
        final boolean videos)
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final MetaFileResourceResult resources = 
                this.m_resourceSearchDao.search(keywords, startIndex, 
                    itemsPerPage, os, userId, groupName, applications, audio, 
                    docs, images, videos);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return resources;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }

    public void takeDown(final String sha1Urn, final boolean takeDown)
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            this.m_metaFileResourceDao.takeDown(sha1Urn, takeDown);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }

    public Collection<OnlineInstance> getOnlineInstances()
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final Collection<OnlineInstance> users = 
                this.m_instanceDao.getOnlineInstances();
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return users;
            }
        catch (final RuntimeException e)
            {
            rollback(e);
            throw e;
            }
        }
    
    public void onOnline(final InetAddress server, final boolean online)
        {
        if (!online)
            {
            setServerOnline(online, server.getHostAddress());
            }
        }
    
    private void rollback(final Exception e)
        {
        m_log.warn("Got exception.  Rolling back transaction.", e);
        try 
            {
            this.m_sessionFactory.getCurrentSession().getTransaction().rollback();
            }
        catch (final RuntimeException re)
            {
            m_log.error ("Could now rollback transaction!", re);
            }
        }
    }
