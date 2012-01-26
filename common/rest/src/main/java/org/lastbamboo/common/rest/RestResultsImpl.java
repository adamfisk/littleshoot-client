package org.lastbamboo.common.rest;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Set of REST search results from an individual source.
 * 
 * @param <T> The type of results.
 */
public class RestResultsImpl<T extends RestResult> implements RestResults<T>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final RestResultsMetadata<T> m_metadata;
    private volatile Collection<T> m_results;

    /**
     * Creates a new set of results.
     * 
     * @param metadata The metadata for the results.
     * @param results The results.
     */
    public RestResultsImpl(final RestResultsMetadata<T> metadata, 
        final Collection<T> results)
        {
        this.m_metadata = metadata;
        this.m_results = results;
        }

    public RestResultsMetadata<T> getMetadata()
        {
        return this.m_metadata;
        }

    public void addResults(final Collection<T> results)
        {
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Adding "+results.size()+" results");
            }
        // This is a little weird due to how this is called.  We
        // add the incoming results to the beginning.  The results passed in
        // are actually the existing results, whereas the results in this
        // class are the new results.  See SearchResultsImpl.
        synchronized (results)
            {
            synchronized (this.m_results)
                {
                results.addAll(this.m_results);
                this.m_results = results;
                }
            }
        }

    public Collection<T> getCurrentResults()
        {
        return this.m_results;
        }

    public boolean hasMoreResults()
        {
        final int current = this.m_results.size();
        final int total = this.m_metadata.getTotalResults();
        final boolean more = current < total;
        if (more)
            {
            m_log.debug(this.m_metadata.getSource() +
                " has more results.  current of "+current+" < "+total);
            }
        else
            {
            m_log.debug(this.m_metadata.getSource() +
                " has no more results.  current of "+current+" not < "+total);
            }
        return more;
        }   
    

    public boolean isComplete()
        {
        // For the default results, the mere existence of the results 
        // indicates completeness.  For typical REST cases, this doesn't mean
        // we've fetched all the results, but rather that we've accessed the
        // first group.
        return true;
        }
    
    @Override 
    public String toString()
        {
        return getClass().getSimpleName() + " with results: " + this.m_results;
        }
    }
