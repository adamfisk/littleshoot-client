package org.lastbamboo.server.db;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.lastbamboo.server.resource.Instance;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.OnlineInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the DAO for web users and LittleShoot instances.
 */
public class InstanceDaoImpl implements InstanceDao
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final SessionFactory m_sessionFactory;

    /**
     * Creates a new DAO instance.
     * 
     * @param sessionFactory The Hibernate session factory.
     */
    public InstanceDaoImpl(final SessionFactory sessionFactory)
        {
        this.m_sessionFactory = sessionFactory;
        }
    
    public Collection<OnlineInstance> getOnlineInstances()
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                OnlineInstanceImpl.class);
        
        final Collection<OnlineInstance> users = 
            (Collection<OnlineInstance>)criteria.list();
        return users;
        }
    
    public void setInstanceOnline(final long instanceId, final String baseUri, 
        final boolean online, final String serverAddress)
        {
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Setting instances online: "+online);
            }
        
        Instance instance = getInstance(instanceId);
        if (instance == null)
            {
            instance = insertResource(instanceId, baseUri, online);
            }
        else
            {
            if (m_log.isDebugEnabled())
                {
                m_log.debug("Setting existing instance online status to: " + 
                    online);
                }
            instance.setOnline(online);
            instance.setBaseUri(baseUri);
            }
        
        updateOnlineInstance(instanceId, baseUri, online, serverAddress);
        }
    

    public void setServerOnline(final boolean online, 
        final String serverAddress)
        {
        // If the server is online, all the users associate with that server
        // are still online.
        if (online) return;
        
        final String selectHql = 
            "from OnlineInstanceImpl where " +
            "m_serverAddress = :serverAddress";

        final Query query = 
            this.m_sessionFactory.getCurrentSession().createQuery(selectHql);
        query.setString("serverAddress", serverAddress);
        
        // We need to manually set each individual user to offline because 
        // otherwise users' associate files will not correctly reflect their
        // online status.
        final Collection<OnlineInstance> users = query.list();
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Modifying "+users.size()+" resources for user...");
            }
        for (final OnlineInstance our : users)
            {
            deleteOnlineInstance(our);
            }
        }

    private void deleteOnlineInstance(final OnlineInstance instance)
        {
        updateFiles(instance, false);
        this.m_sessionFactory.getCurrentSession().delete(instance);
        }

    private void updateOnlineInstance(final long instanceId, 
        final String baseUri, final boolean online, final String serverAddress)
        {
        OnlineInstance instance = getOnlineInstance(instanceId);
        if (instance == null)
            {
            if (online)
                {
                instance = insertOnlineInstance(instanceId, baseUri, 
                    serverAddress);
                updateFiles(instance, online);
                }
            else
                {
                m_log.warn("Setting user we don't know about to offline -- " +
                    "ignoring...");
                return;
                }
            }
        else if (!online)
            {
            // This also updates the files associated with the user!!
            deleteOnlineInstance(instance);
            }
        }

    private void updateFiles(final OnlineInstance ur, final boolean online) 
        {
        final String hql = 
            "select mfr " +
            "from MetaFileResourceImpl mfr, FileResourceImpl fr " +
            "where " +
            "mfr.m_sha1 = fr.m_sha1 and " +
            "fr.m_instanceId = :instanceId";
        final Query query = 
            this.m_sessionFactory.getCurrentSession().createQuery(hql);
        query.setLong("instanceId", ur.getInstanceId());
        
        final Collection<MetaFileResource> resources = query.list();
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Modifying "+resources.size()+" resources for user...");
            }
        for (final MetaFileResource mfr : resources)
            {
            if (online)
                {
                mfr.addInstance(ur);
                }
            else
                {
                m_log.debug("Removing user: "+ur);
                mfr.removeInstance(ur);
                }
            }
        }

    private Instance insertResource(final long instanceId, 
        final String baseUri, final boolean online)
        {
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Setting new user online status to: " + online);
            }
        final Instance instance = new InstanceImpl(instanceId, baseUri, online);
        this.m_sessionFactory.getCurrentSession().persist(instance);
        return instance;
        }
    
    private OnlineInstance insertOnlineInstance(final long instanceId, 
        final String baseUri, final String serverAddress)
        {
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Inserting new online user");
            }
        final OnlineInstance ur = 
            new OnlineInstanceImpl(instanceId, baseUri, serverAddress);
        this.m_sessionFactory.getCurrentSession().persist(ur);
        return ur;
        }

    private Instance getInstance(final long instanceId)
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                InstanceImpl.class);
        criteria.add(Restrictions.eq("m_instanceId", new Long(instanceId)));
        final Instance ur = (Instance)criteria.uniqueResult();
        return ur;
        }
    
    private OnlineInstance getOnlineInstance(final long instanceId)
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                OnlineInstanceImpl.class);
        criteria.add(Restrictions.eq("m_instanceId", new Long(instanceId)));
        final OnlineInstance ur = (OnlineInstance)criteria.uniqueResult();
        return ur;
        }

    }
