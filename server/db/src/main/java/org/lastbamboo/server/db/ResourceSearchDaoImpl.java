package org.lastbamboo.server.db;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.lastbamboo.server.resource.FileAndInstances;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.MetaFileResourceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO implementation for performing searches across resource tables.
 */
public class ResourceSearchDaoImpl implements ResourceSearchDao
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass()); 
    private final SessionFactory m_sessionFactory;

    /**
     * Creates a new DAO instance.
     * 
     * @param sessionFactory The Hibernate session factory.
     */
    public ResourceSearchDaoImpl(final SessionFactory sessionFactory)
        {
        this.m_sessionFactory = sessionFactory;
        }

    public FileAndInstances getFileAndInstances(final URI uri)
        {
        m_log.trace("Accessing resource by URI: {}", uri);
        
        final String hql = 
            "select mfr " +
            "from MetaFileResourceImpl mfr " +
            "where " +
            "(mfr.m_sha1 = :sha1 or mfr.m_uri = :uri) and " +
            "mfr.m_takenDown = false";
        
        final Query query = 
            this.m_sessionFactory.getCurrentSession().createQuery(hql);
        
        query.setString("sha1", uri.toASCIIString());
        query.setString("uri", uri.toASCIIString());
        query.setReadOnly(true);
        query.setMaxResults(200);
        final MetaFileResource mfr = (MetaFileResource) query.uniqueResult();
        
        if (mfr == null)
            {
            m_log.warn("Could not find instance for URI: {}", uri);
            return null;
            }
        return new FileAndInstancesImpl(mfr);
        }

    public MetaFileResourceResult search(final String keywords, 
        final int pageIndex, final int resultsPerPage, final String os,
        final long instanceId, final String groupName, 
        final boolean applications, final boolean audio, 
        final boolean docs, final boolean images, final boolean videos)
        {
        //m_log.trace("Handling query: "+keywords);

        final String hql = 
            "select distinct mfr " +
            "from MetaFileResourceImpl mfr " +
            "where "+
            "mfr.m_groupName = :groupName and "+
            "mfr.m_numOnlineInstances > 0 and " +
            createResourceTypeWhere(os, applications, audio, docs, images, videos, "mfr") +
            "(lower(mfr.m_titles) like :keywords or lower(mfr.m_tags) like :keywords or lower(mfr.m_groupName) like :keywords) and " +
            "mfr.m_takenDown = false " +
            "order by mfr.m_numOnlineInstances desc";

        final Query query = 
            this.m_sessionFactory.getCurrentSession().createQuery(hql);
        query.setReadOnly(true);

        final int rowIndex = pageIndex * resultsPerPage;
        query.setString("groupName", groupName);
        query.setString("keywords", "%"+keywords.toLowerCase()+"%");
        
        final Collection<MetaFileResource> resources = 
            new LinkedList<MetaFileResource>();
        int totalResults = 0;
        final ScrollableResults results = query.scroll();
        try
            {
            if (!results.first())
                {
                //m_log.debug("No results");
                }
            else if (results.scroll(rowIndex))
                {
                //m_log.debug("Found results at index..");
                
                for (int i = 0; i < resultsPerPage; i++)
                    {
                    final MetaFileResource mfr = (MetaFileResource) results.get(0);
                    resources.add(mfr);
                    //m_log.debug("Found: {}", mfr);
                    if (!results.next())
                        {
                        break;
                        }
                    }
                }
            else 
                {
                //m_log.debug("No results at: "+rowIndex);
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
        
        if (m_log.isDebugEnabled())
            {
            //m_log.debug("Sent query: {}", query.getQueryString());
            //m_log.debug("Returning "+resources.size()+" results...");
            }
        return new MetaFileResourceResultImpl(resources, totalResults);
        }
    
    private String createResourceTypeWhere(final String os, 
        final boolean applications, final boolean audio, final boolean docs, 
        final boolean images, final boolean videos, final String alias)
        {
        //m_log.debug("Searching using os: {}", os);
        if (applications && audio && docs && images && videos)
            {
            // Query for everything, no need to specify types.
            //m_log.debug("Querying for all types");
            return "";
            }
        if (!applications && !audio && !docs && !images && !videos)
            {
            // Ignoring types.
            //m_log.debug("No type selected -- do everything.");
            return "";
            }
        boolean appendedOne = false;
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (applications)
            {
            sb.append(alias);
            sb.append(".m_mediaType = 'application'");
            sb.append(" or ");
            sb.append(alias);
            sb.append(".m_mediaType = 'archive'");
            
            // Expect anything from rogue clients.
            if (StringUtils.isNotBlank(os))
                {
                sb.append(" or ");
                sb.append(alias);
                if (os.contains("mac"))
                    {
                    sb.append(".m_mediaType = 'application/mac'");
                    }
                else if (os.contains("win"))
                    {
                    sb.append(".m_mediaType = 'application/win'");
                    }
                else
                    {
                    sb.append(".m_mediaType = 'application/linux'");
                    }
                }
            appendedOne = true;
            }
        if (audio)
            {
            if (appendedOne)
                {
                sb.append(" or ");
                }
            sb.append(alias);
            sb.append(".m_mediaType = 'audio'");
            appendedOne = true;
            }
        if (docs)
            {
            if (appendedOne)
                {
                sb.append(" or ");
                }
            sb.append(alias);
            sb.append(".m_mediaType = 'document'");
            sb.append(" or ");
            sb.append(alias);
            sb.append(".m_mediaType = 'archive'");
            appendedOne = true;
            }
        if (images)
            {
            if (appendedOne)
                {
                sb.append(" or ");
                }
            sb.append(alias);
            sb.append(".m_mediaType = 'image'");
            appendedOne = true;
            }
        if (videos)
            {
            if (appendedOne)
                {
                sb.append(" or ");
                }
            sb.append(alias);
            sb.append(".m_mediaType = 'video'");
            appendedOne = true;
            }
        sb.append(") and ");
        m_log.debug("Using type query: {}", sb);
        return sb.toString();
        }
    }
