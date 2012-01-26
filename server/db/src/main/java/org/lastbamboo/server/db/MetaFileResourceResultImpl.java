package org.lastbamboo.server.db;

import java.util.Collection;

import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.MetaFileResourceResult;

/**
 * Class containing data for a file resource search.
 */
public class MetaFileResourceResultImpl implements MetaFileResourceResult
    {

    private final Collection<MetaFileResource> m_results;
    private final int m_totalResults;

    /**
     * Creates a new result data bean.
     * 
     * @param results The file resources matching the query.
     * @param totalResults
     */
    public MetaFileResourceResultImpl(Collection<MetaFileResource> results, 
        final int totalResults)
        {
        m_results = results;
        m_totalResults = totalResults;
        }

    public Collection<MetaFileResource> getResults()
        {
        return m_results;
        }

    public int getTotalResults()
        {
        return m_totalResults;
        }

    }
