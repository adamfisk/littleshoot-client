package org.lastbamboo.common.searchers.youtube;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.id.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.AbstractRestSearcher;
import org.lastbamboo.common.rest.XmlRestResultBodyProcessor;
import org.lastbamboo.common.rest.RestResultProcessor;
import org.lastbamboo.common.rest.RestResultSources;
import org.littleshoot.util.Pair;
import org.littleshoot.util.UriUtils;

/**
 * Class for searching the YouTube REST API for videos.
 */
public class YouTubeSearcher extends AbstractRestSearcher<YouTubeVideoImpl>
    {

    private static final Logger LOG = LoggerFactory.getLogger(YouTubeSearcher.class);
    
    private final String m_developerId;
    
    /**
     * Creates a new searcher with the given developer ID.
     * 
     * @param resultProcessor Class for processing the generated search results.
     * @param messageId The unique ID of the message. 
     * @param developerId The ID to use to access the API.
     * @param searchString The string to search for.
     */
    public YouTubeSearcher(
        final RestResultProcessor<YouTubeVideoImpl> resultProcessor,
        final UUID messageId, final String developerId, 
        final String searchString)
        {
        super(resultProcessor, messageId, searchString, 1,
            RestResultSources.YOU_TUBE);
        this.m_resultBodyProcessor = 
            new XmlRestResultBodyProcessor<YouTubeVideoImpl>("video", 
                new YouTubeVideoFactory(this));
        this.m_developerId = developerId;
        }

    public String createUrlString(final String searchTerms)
        {
        LOG.debug("Creating string for search terms: "+searchTerms);
        final String baseUrl = "http://www.youtube.com/api2_rest";
        final Collection<Pair<String,String>> params =
            new LinkedList<Pair<String,String>> ();
    
        params.add (UriUtils.pair ("dev_id", this.m_developerId));
        params.add (UriUtils.pair ("method", "youtube.videos.list_by_tag"));
        params.add (UriUtils.pair ("page", this.m_pageIndex));
        params.add (UriUtils.pair ("per_page", "100"));
        params.add (UriUtils.pair ("tag", searchTerms));
        
        final String url = UriUtils.newUrl(baseUrl, params);
        LOG.debug("Sending search to URL: "+url);
        return url;
        }

    @Override
    public String toString()
        {
        return getClass().getSimpleName();
        }
    }
