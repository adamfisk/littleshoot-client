package org.lastbamboo.server.db;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.OnlineInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the class containing metadata for all file resources.
 */
public class MetaFileResourceDaoImpl implements MetaFileResourceDao
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass()); 
    
    private final SessionFactory m_sessionFactory;

    /**
     * Creates a new DAO instance.
     * 
     * @param sessionFactory The Hibernate session factory.
     */
    public MetaFileResourceDaoImpl(final SessionFactory sessionFactory)
        {
        this.m_sessionFactory = sessionFactory;
        }
    
    public MetaFileResource insertResource(final FileResource fr) 
        throws IOException
        {
        // Don't share dot files.
        if (fr.getName().startsWith("."))
            {
            throw new IOException("Can't share dot file");
            }
        MetaFileResource mfr = getResource(fr.getSha1Urn());
        if (mfr == null)
            {
            m_log.debug("Creating new resource...");
            mfr = new MetaFileResourceImpl(fr);
            this.m_sessionFactory.getCurrentSession().persist(mfr);
            }
        else
            {
            mfr.addTags(fr.getTags());
            mfr.addTitle(fr.getTitle());
            }
        
        m_log.debug("Added MFR: {}", mfr);

        // Add the instance for the given resource to the set of instances who
        // have the file if that user is online.
        final long instanceId = fr.getInstanceId();
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                OnlineInstanceImpl.class);
        criteria.add(Restrictions.eq("m_instanceId", Long.valueOf(instanceId)));

        final OnlineInstance oi = (OnlineInstance) criteria.uniqueResult();

        if (oi == null)
            {
            m_log.debug("We don't know about the user");
            }
        else 
            {
            mfr.addInstance(oi);  
            }
        return mfr;
        }
    
    public void editResource(final String sha1, final String tags, 
        final String url) throws FileNotFoundException
        {
        final MetaFileResource mfr = getResource(sha1);
        if (mfr == null)
            {
            m_log.warn("No matching resource for: "+sha1);
            
            final Criteria criteria = 
                this.m_sessionFactory.getCurrentSession().createCriteria(
                    MetaFileResourceImpl.class);
    
            m_log.warn("Found: "+criteria.list());
            throw new FileNotFoundException("Could not find file");
            }
        else
            {
            mfr.addTags(tags);
            }
        }
    
    public void deleteResource(final long instanceId, final String sha1)
        {
        m_log.trace("Stopping sharing of file...");
        
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                OnlineInstanceImpl.class);
        criteria.add(Restrictions.eq("m_instanceId", Long.valueOf(instanceId)));
    
        final OnlineInstance ur = (OnlineInstance) criteria.uniqueResult();
    
        // We search for the user because we don't want to delete the meta
        // resource.  We still want to know about it, even if there are no
        // users online with that file.
        if (ur != null)
            {
            final MetaFileResource mfr = getResource(sha1);
            if (mfr != null)
                {
                m_log.debug("Removing resource.");
                mfr.removeInstance(ur);
                }
            else
                {
                m_log.warn("Could not find resource with SHA-1: {}", sha1);
                }
            }
        else 
            {
            // It's possible the file was deleted before the user came online.
            m_log.debug("Could not find online instance with ID: {}",instanceId);
            }
        }
    
    public void takeDown(final String sha1Urn, boolean takeDown)
        {
        final MetaFileResource mfr = getResource(sha1Urn);
        mfr.setTakenDown(takeDown);
        }
    
    private MetaFileResource getOrCreateResource(final FileResource fr)
        {
        
        MetaFileResource resource = getResource(fr.getSha1Urn());
        if (resource == null)
            {
            m_log.debug("Creating new resource...");
            
            // We always create the meta file resource before the file resource.
            resource = new MetaFileResourceImpl(fr);
            this.m_sessionFactory.getCurrentSession().persist(resource);
            }
        return resource;
        }

    private MetaFileResource getResource(final String sha1)
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                MetaFileResourceImpl.class);
        criteria.add(Restrictions.eq("m_sha1", sha1));

        return (MetaFileResource) criteria.uniqueResult();
        }

    }
