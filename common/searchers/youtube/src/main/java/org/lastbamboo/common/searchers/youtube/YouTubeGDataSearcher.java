package org.lastbamboo.common.searchers.youtube;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.id.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.AbstractRestSearcher;
import org.lastbamboo.common.rest.JsonAtomRestResultBodyProcessor;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResultProcessor;
import org.lastbamboo.common.rest.RestResultSources;
import org.littleshoot.util.Pair;
import org.littleshoot.util.UriUtils;

/**
 * Class for searching the YouTube REST API for videos.
 */
public class YouTubeGDataSearcher extends AbstractRestSearcher<YouTubeJsonVideo>
    {

    private static final Logger LOG = LoggerFactory.getLogger(YouTubeGDataSearcher.class);
    
    private final int PAGE_SIZE = 50;

    private int m_pageSize;
    
    /**
     * Creates a new searcher with the given developer ID.
     * 
     * @param resultProcessor Class for processing the generated search results.
     * @param messageId The unique ID of the message. 
     * @param searchString The string to search for.
     */
    public YouTubeGDataSearcher(
        final RestResultProcessor<YouTubeJsonVideo> resultProcessor,
        final UUID messageId, final String searchString)
        {
        super(resultProcessor, messageId, searchString, 1,
            RestResultSources.YOU_TUBE);
        final JsonRestResultFactory<YouTubeJsonVideo> resultFactory =
            new YouTubeJsonResultFactory(this);
        this.m_resultBodyProcessor = 
            new JsonAtomRestResultBodyProcessor<YouTubeJsonVideo>(resultFactory,
                "openSearch$totalResults");
        }

    public String createUrlString(final String searchTerms)
        {
        LOG.debug("Creating string for search terms: "+searchTerms);
        final String baseUrl = "http://gdata.youtube.com/feeds/api/videos";
        final Collection<Pair<String,String>> params =
            new LinkedList<Pair<String,String>> ();

        // The YouTube API does not allow us to request more than 1000 results.
        if (this.m_pageIndex + PAGE_SIZE > 999)
            {
            this.m_pageSize = 1000 - this.m_pageIndex;
            }
        else
            {
            this.m_pageSize = PAGE_SIZE;
            }
        //this.m_pageSize = PAGE_SIZE;
        params.add (UriUtils.pair ("alt", "json"));
        params.add (UriUtils.pair ("start-index", this.m_pageIndex));
        params.add (UriUtils.pair ("max-results", m_pageSize));
        params.add (UriUtils.pair ("racy", "include"));
        params.add (UriUtils.pair ("orderby", "relevance"));
        params.add (UriUtils.pair ("vq", searchTerms));
        
        
        final String url = UriUtils.newUrl(baseUrl, params);
        LOG.debug("Sending search to URL: "+url);
        return url;
        }

    @Override
    protected void incrementPage(final int numResultsLastPass)
        {
        LOG.debug("Results last pass: "+numResultsLastPass);
        this.m_pageIndex += (numResultsLastPass - this.m_pageIndex);
        LOG.debug("Page index now: "+this.m_pageIndex);
        }
    
    /**
     * YouTube doesn't return very many results, so we always search twice.
     */
    protected boolean doubleSearch()
        {
        return true;
        }
    
    @Override
    public String toString()
        {
        return getClass().getSimpleName();
        }
    }
