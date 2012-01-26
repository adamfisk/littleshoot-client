package org.lastbamboo.common.searchers.yahoo;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.id.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.AbstractRestSearcher;
import org.lastbamboo.common.rest.JsonRestResultBodyProcessor;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResultProcessor;
import org.lastbamboo.common.rest.RestResultSources;
import org.littleshoot.util.Pair;
import org.littleshoot.util.UriUtils;

/**
 * Implementation of Yahoo photo searching.
 */
public final class YahooImageSearcher extends AbstractRestSearcher<YahooJsonImage>
    {

    private static final Logger LOG = LoggerFactory.getLogger(YahooImageSearcher.class);
    
    private final String m_developerId;
    
    /**
     * Creates a new searcher with the given developer ID.
     * 
     * @param resultProcessor Class for processing the generated search results.
     * @param messageId The unique ID of the message. 
     * @param developerId The ID to use to access the API.
     * @param searchString The string to search for.
     */
    public YahooImageSearcher(
        final RestResultProcessor<YahooJsonImage> resultProcessor,
        final UUID messageId, final String developerId, 
        final String searchString)
        {
        super(resultProcessor, messageId, searchString, 1, 
            RestResultSources.YAHOO_IMAGES);
        
        this.m_developerId = developerId;
        final JsonRestResultFactory<YahooJsonImage> resultFactory =
            new YahooJsonImageFactory(this);
        this.m_resultBodyProcessor = 
            new JsonRestResultBodyProcessor<YahooJsonImage>("ysearchresponse", 
                "resultset_images", "count", resultFactory);
        }
    
    private int m_start = 0;
    
    /*
    protected void incrementPage(final int numResultsLastPass)
        {
        this.m_start += numResultsLastPass;
        }
        */

    public String createUrlString(final String searchTerms)
        {
        final int numResults = 50;
        
        // BOSS starts at index 0.
        final int start = (this.m_pageIndex - 1) * numResults;
        if (start > 1000)
            {
            // Should not happen.
            LOG.warn("Exceeding 1000 for start index!!");
            }
        
        
        final String terms = UriUtils.urlNonFormEncode("{"+searchTerms+"}");
        final String baseUrl = 
            "http://boss.yahooapis.com/ysearch/images/v1/"+terms;
        final Collection<Pair<String,String>> params =
            new LinkedList<Pair<String,String>> ();
    
        params.add (UriUtils.pair ("appid", this.m_developerId));
        params.add (UriUtils.pair ("start", start));
        params.add (UriUtils.pair ("count", numResults));
        params.add (UriUtils.pair ("filter", "no"));
        
        final String url = UriUtils.newUrl(baseUrl, params);
        LOG.debug("Sending search to URL: "+url);
        return url;
        }
    
    /**
     * Yahoo doesn't return very many results, so we always search twice.
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
