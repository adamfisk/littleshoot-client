package org.lastbamboo.server.db;

import java.util.Collection;

import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.FileResourceResult;

/**
 * Encapsulates data for {@link FileResource} queries, including things 
 * like the total number of results.
 */
public class FileResourceResultImpl implements FileResourceResult
    {

    private final int m_totalResults;
    private final Collection<FileResource> m_resources;

    /**
     * Creates a new results bean.
     * 
     * @param resources The resource {@link Collection}.
     * @param totalResults The total number of results available.
     */
    public FileResourceResultImpl(
        final Collection<FileResource> resources, final int totalResults)
        {
        m_resources = resources;
        m_totalResults = totalResults;  
        }

    public Collection<FileResource> getResources()
        {
        return m_resources;
        }

    public int getTotalResults()
        {
        return m_totalResults;
        }

    }
