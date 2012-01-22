package org.lastbamboo.client.search;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResultSources;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestSearcher;
import org.lastbamboo.common.rest.SearchRequestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class keeping track of the search results for an individual search.
 * 
 * @param <T> Class extending {@link RestResult}.
 */
public final class SearchResultsImpl<T extends RestResult> 
    implements SearchResults<T>
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final Map<Integer, RestResults<T>> m_results =
        new ConcurrentHashMap<Integer, RestResults<T>>();
    
    private final List<Integer> m_resultsOrder;

    private int m_totalResults;

    private final SearchRequestBean m_requestBean;

    private final UUID m_guid;
    
    /**
     * Creates a new result set.  Each search gets its own result set.
     * 
     * @param request The data about the search request.
     * @param guid The search guid. 
     */
    public SearchResultsImpl(final SearchRequestBean request, final UUID guid)
        {
        final List<Integer> resultsOrder = new LinkedList<Integer>();
        if (request.isLittleShoot())
            {
            resultsOrder.add(new Integer(RestResultSources.LITTLE_SHOOT.toInt()));
            }
        
        if (request.isLimeWire())
            {
            resultsOrder.add(new Integer(RestResultSources.LIMEWIRE.toInt()));
            }
        
        if (request.isAudio() && request.isIMeem())
            {
            resultsOrder.add(new Integer(RestResultSources.IMEEM.toInt()));
            }
        if (request.isIsoHunt())
            {
            resultsOrder.add(new Integer(RestResultSources.ISO_HUNT.toInt()));
            }
        if (request.isVideo())
            {
            if (request.isYouTube())
                {
                resultsOrder.add(new Integer(RestResultSources.YOU_TUBE.toInt()));
                }
            /*
            if (request.isYahoo())
                {
                resultsOrder.add(new Integer(RestResultSources.YAHOO_VIDEOS.toInt()));
                }
                */
            }
        if (request.isImages())
            {
            if (request.isFlickr())
                {
                resultsOrder.add(new Integer(RestResultSources.FLICKR.toInt()));
                }
            if (request.isYahoo())
                {
                resultsOrder.add(new Integer(RestResultSources.YAHOO_IMAGES.toInt()));
                }
            }
        this.m_resultsOrder = Collections.unmodifiableList(resultsOrder);
        this.m_requestBean = request;
        this.m_guid = guid;
        }
    
    public void addResults(final RestResults<T> newResults)
        {
        final RestResultSources source = newResults.getMetadata().getSource();
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Adding "+newResults.getCurrentResults().size()+
                " results to: "+this+" of type: "+source);
            }
        final Integer key = new Integer(source.toInt());
        final RestResults<T> existingResults = this.m_results.get(key);
        if (m_log.isDebugEnabled())
            {
            final int numResults;
            if (existingResults == null)
                {
                numResults = 0;
                }
            else
                {
                numResults = existingResults.getCurrentResults().size();
                }
            m_log.debug(numResults + " current results for source...");
            }
        if (existingResults != null)
            {
            m_log.debug("Adding to existing results...");
            newResults.addResults(existingResults.getCurrentResults());
            
            if (m_log.isDebugEnabled())
                {
                m_log.debug("Now "+ newResults.getCurrentResults().size() + 
                    " results for source...");
                }
            }
        
        // Note the new results contains the updates values at this point,
        // and we swap the new results in for the old.
        this.m_results.put(key, newResults);       
        updateTotalResults();
        }

    private void updateTotalResults()
        {
        m_log.debug("Updating result total...");
        final Collection<RestResults<T>> allResults = this.m_results.values();
        int totalResults = 0;
        for (final RestResults<T> results : allResults)
            {
            totalResults += results.getMetadata().getTotalResults();
            }
        this.m_totalResults = totalResults;
        m_log.debug("Updated total: "+totalResults);
        }

    public Collection<T> getResults(final int numResults, 
        final int resultsIndex)
        {
        m_log.debug("Accessing "+numResults+" results starting at: "+
            resultsIndex);
        final Collection<T> resultsToReturn = new LinkedList<T>();
        
        // The working index increases as we add results.
        int workingIndex = resultsIndex;
        int resultsNeeded = numResults;
        
        // The index of the source we're searching.
        int sourceIndex = 0;
        while (resultsToReturn.size() < numResults &&
            sourceIndex < this.m_resultsOrder.size())
            {
            try
                {
                final Collection<T> activeSourceResults = 
                    getSourceResultsForIndex(resultsNeeded, workingIndex);
                if (activeSourceResults == null)
                    {
                    break;
                    }
                final int resultsAdded = activeSourceResults.size();
                resultsNeeded -= resultsAdded;
                workingIndex += resultsAdded;
                resultsToReturn.addAll(activeSourceResults);
                }
            catch (final IOException e)
                {
                // This means we could not connect to the source.  This can
                // happen if the Internet connection goes out or it the host
                // is overloaded, for example.
                m_log.debug("Could not connect to host", e);
                
                // TODO: The source should update it's result totals in this
                // case...
                }
            
            sourceIndex++;
            }
        
        return resultsToReturn;
        }
 
    /**
     * This method accesses the {@link Collection} of results for the 
     * specified index we're looking for.
     * 
     * @param resultsNeeded The number of results needed.
     * @param resultsIndex The index to get the appropriate results collection
     * for.
     * @return The {@link Collection} of search results, or null if there
     * are no results available.
     * @throws IOException If we needed to request more results from a source,
     * and there was a network error accessing those results.
     */
    private Collection<T> getSourceResultsForIndex(final int resultsNeeded, 
        final int resultsIndex) throws IOException
        {
        int globalIndex = 0;
        for (final Integer source : this.m_resultsOrder)
            {
            m_log.debug("Accessing results for source: "+
                RestResultSources.toType(source));
            final RestResults<T> results = this.m_results.get(source);
            if (results == null)
                {
                // We haven't received any results from that source.
                m_log.debug("No results from source: "+
                    RestResultSources.toType(source));
                continue;
                }
            final RestResultsMetadata metadata = results.getMetadata();
            
            final int totalResults = metadata.getTotalResults();
            final int sourceOffset = globalIndex;
            m_log.debug("Adding "+totalResults+" to the global index...");
            globalIndex += totalResults;
            if (resultsIndex < globalIndex)
                {
                m_log.debug("Results index is "+resultsIndex);
                m_log.debug("Source offset is "+sourceOffset);
                
                // This is the offset in this result set.
                final int offset = resultsIndex - sourceOffset;
                final int endIndex = resultsNeeded + offset;
                m_log.debug("Getting results from "+offset+" to "+endIndex+
                    " for source: "+RestResultSources.toType(source));
                return getResultsForSource(results, offset, endIndex, 
                    resultsNeeded);
                }
            }
        
        // There are no available results for the specified index.
        m_log.debug("No results available for index: "+resultsIndex);
        return null;
        }

    private Collection<T> getResultsForSource(final RestResults<T> results, 
        final int offset, int endIndex, final int resultsNeeded) 
        throws IOException
        {
        
        // OK, we're working with the correct source.  Now add all the
        // results we can for that source.
        final Collection<T> currentResults = results.getCurrentResults();
        
        final boolean requestMoreResults = 
            ((currentResults.size()-offset) < resultsNeeded) && 
            results.hasMoreResults();
        
        // It we need more results than it has, request the remaining
        // results for the source.
        final int originalSize = currentResults.size();
        final RestSearcher<T> searcher = results.getMetadata().getSearcher();
        if (requestMoreResults)
            {
            // This should add any remaining results to our total.
            // This will also ultimately add results to this class.
            m_log.debug("Searching for more results from: {}", searcher);
            searcher.search();
            }
        final Collection<T> updatedResults = results.getCurrentResults();
        if (requestMoreResults && 
            (updatedResults.size() <= originalSize))
            {
            m_log.warn("Attempt to get more results failed from: {}", searcher);
            m_log.warn("Old size: {}", originalSize);
            m_log.warn("New size: {}", updatedResults.size());
            }
        
        return getResults(updatedResults, offset, endIndex);
        }

    /**
     * Returns the results from the specified {@link Collection} starting at
     * the given offset and ending at the specified ending index.  If the
     * result source does not have results up to the index, this will return
     * whatever it does have.
     * 
     * @param results The results to filter.
     * @param offset The starting offset for results to return within the set.
     * @param endIndex The index of the last result to return.
     * @return The filtered {@link Collection} of results.
     */
    private Collection<T> getResults(final Collection<T> results, 
        final int offset, final int endIndex)
        {
        final Collection<T> resultsToReturn = new LinkedList<T>();
        final Iterator<T> iter = results.iterator();
        int index = 0;
        while (iter.hasNext() && index < endIndex)
            {
            final T result = iter.next();
            if (index >= offset)
                {
                resultsToReturn.add(result);
                }
            index++;
            }
        m_log.debug("Contributing new results totalling: {}", 
            resultsToReturn.size());
        return resultsToReturn;
        }

    public int getTotalResults()
        {
        return this.m_totalResults;
        }

    public boolean isComplete()
        {
        for (final Integer sourceKey : this.m_resultsOrder)
            {
            if (!this.m_results.containsKey(sourceKey))
                {
                m_log.debug("No completion token for: {}", 
                    RestResultSources.toType(sourceKey));
                return false;
                }
            }
        
        if (this.m_results.containsKey(RestResultSources.LIMEWIRE.toInt()))
            {
            m_log.debug("Returning LimeWire complete status");
            final RestResults<T> results = 
                this.m_results.get(RestResultSources.LIMEWIRE.toInt());
            return results.isComplete();
            }
        return true;
        }

    public SearchRequestBean getRequestBean()
        {
        return m_requestBean;
        }

    public UUID getGuid()
        {
        return m_guid;
        }

    }


