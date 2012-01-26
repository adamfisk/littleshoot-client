package org.lastbamboo.server.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.lastbamboo.common.util.ShootConstants;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.FileResourceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A repository for resources that is stored locally in a database.
 */
public class FileResourceDaoImpl implements FileResourceDao
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final SessionFactory m_sessionFactory;

    /**
     * Creates a new DAO instance.
     * 
     * @param sessionFactory The Hibernate session factory.
     */
    public FileResourceDaoImpl(final SessionFactory sessionFactory)
        {
        this.m_sessionFactory = sessionFactory;
        }
    
    public void deleteResource(final long instanceId, final String sha1) 
        throws IOException
        {
        m_log.trace("Stopping sharing of file...");
        
        final String deleteString = 
            "delete FileResourceImpl fr where " +
            "fr.m_sha1=:sha1 and fr.m_instanceId=:instanceId";
        final Query query = 
            this.m_sessionFactory.getCurrentSession().createQuery(deleteString);
        query.setString("sha1", sha1);
        query.setLong("instanceId", instanceId);
        final int updated = query.executeUpdate();  
        if (updated == 0)
            {
            m_log.warn("Nothing deleted -- no matching file.");
            }
        else if (updated > 1)
            {
            m_log.warn("Updated more than one file: {}", updated);
            }
        else
            {
            m_log.debug("Successfully deleted file...");
            }
        }

    public void insertResource(final FileResource fr) 
        throws IOException
        {
        m_log.trace("Starting to share file: "+fr);
        
        // Don't share dot files.
        if (fr.getName().startsWith("."))
            {
            return;
            }
        
        final FileResource existingResource = getExistingResource(fr);
        if (existingResource == null)
            {
            m_log.trace("Adding shared file: {}", fr);
            this.m_sessionFactory.getCurrentSession().persist(fr);
            m_log.trace("Added shared file resource: {}", fr);
            }
        else
            {
            m_log.debug("The user has already published the resource.");
            final String oldTags = existingResource.getTags();
            final String newTags = fr.getTags();
            if (!oldTags.contains(newTags))
                {
                final StringBuilder sb = new StringBuilder();
                sb.append(oldTags);
                sb.append(" ");
                sb.append(newTags.trim());
                existingResource.setTags(sb.toString());
                }
            } 
        }

    public FileResourceResult getFileResources(final int pageIndex,
        final int resultsPerPage, final long instanceId)
        {
        return getFileResources(pageIndex, resultsPerPage, instanceId, 
            ShootConstants.WORLD_GROUP);
        }
    
    public FileResourceResult getFileResources(final int pageIndex,
        final int resultsPerPage, final long instanceId, final String groupName)
        {
        m_log.debug("Accessing local resources...");
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                FileResourceImpl.class);
        criteria.add(Restrictions.eq("m_instanceId", Long.valueOf(instanceId)));
        criteria.add(Restrictions.eq("m_downloaded", new Boolean(false)));
        criteria.add(Restrictions.eq("m_groupName", groupName));
        
        // Put the results in alphabetical order.
        criteria.addOrder(Order.asc("m_title"));
        
        final int rowIndex = pageIndex * resultsPerPage;

        final Collection<FileResource> resources = 
            new LinkedList<FileResource>();
        int totalResults = 0;
        final ScrollableResults results = criteria.scroll();
        try
            {
            results.first();
            if (results.scroll(rowIndex))
                {
                m_log.debug("Found results at index..");
                
                for (int i = 0; i < resultsPerPage; i++)
                    {
                    final FileResource lfr = (FileResource) results.get(0);
                    resources.add(lfr);
                    m_log.debug("Found: "+lfr);
                    if (!results.next())
                        {
                        break;
                        }
                    }
                }
            else 
                {
                m_log.debug("No results at: "+rowIndex);
                }
            
            
            if (results.last())
                {
                totalResults = results.getRowNumber() + 1;
                }
            else
                {
                totalResults = 0;
                }
            }
        finally
            {
            results.close();
            }
        final FileResourceResult result = 
            new FileResourceResultImpl(resources, totalResults);
        return result;
        }

    public FileResource getFileResource(final long instanceId, final String sha1)
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                FileResourceImpl.class);
        criteria.add(Restrictions.eq("m_sha1", sha1));
        criteria.add(Restrictions.eq("m_instanceId", new Long(instanceId)));
    
        return (FileResource) criteria.uniqueResult();
        }
    
    public void editResource(final long instanceId, final String sha1, 
        final String tags, final String url) throws FileNotFoundException
        {
        final FileResource fr = getFileResource(instanceId, sha1);
        if (fr == null)
            {
            m_log.warn("No matching resource!!");
            throw new FileNotFoundException("Could not find resource");
            }
        else
            {
            fr.setTags(tags);
            }
        }

    private FileResource getExistingResource(final FileResource fr)
        {
        final String sha1 = fr.getSha1Urn();
        final long instanceId = fr.getInstanceId();
        return getFileResource(instanceId, sha1);
        }
    }
