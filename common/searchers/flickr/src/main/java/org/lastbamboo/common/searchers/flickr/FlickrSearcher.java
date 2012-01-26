package org.lastbamboo.common.searchers.flickr;

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
 * Class for searching Flickr's REST api.
 */
public class FlickrSearcher extends AbstractRestSearcher<FlickrJsonImage>
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(FlickrSearcher.class);

    private final String m_developerId;

    /**
     * Creates a new searcher with the given developer ID.
     * 
     * @param resultProcessor Class for processing the generated search results.
     * @param messageId The unique ID of the message. 
     * @param developerId The ID to use to access the API.
     * @param searchString The string to search for.
     */
    public FlickrSearcher(
        final RestResultProcessor<FlickrJsonImage> resultProcessor,
        final UUID messageId, final String developerId, 
        final String searchString)
        {
        super(resultProcessor, messageId, searchString, 1, 
            RestResultSources.FLICKR);
        //this.m_resultBodyProcessor = 
          //  new XmlRestResultBodyProcessor<FlickrJsonImage>("photo", 
            //    new FlickrJsonImageFactory(this));
        this.m_developerId = developerId;
        
        final JsonRestResultFactory<FlickrJsonImage> resultFactory =
            new FlickrJsonImageFactory(this);
        this.m_resultBodyProcessor = 
            new JsonRestResultBodyProcessor<FlickrJsonImage>("photos", 
                "photo", "total", resultFactory);
        }

    public String createUrlString(final String searchTerms)
        {
        final String baseUrl = "http://api.flickr.com/services/rest/";
        final Collection<Pair<String,String>> params =
            new LinkedList<Pair<String,String>> ();
    
        params.add (UriUtils.pair ("api_key", this.m_developerId));
        params.add (UriUtils.pair ("method", "flickr.photos.search"));
        params.add (UriUtils.pair ("sort", "relevance"));
        params.add (UriUtils.pair ("per_page", "100"));
        params.add (UriUtils.pair ("safe_search", "3"));
        params.add (UriUtils.pair ("page", this.m_pageIndex));
        params.add (UriUtils.pair ("text", searchTerms));
        params.add (UriUtils.pair ("format", "json"));
        params.add (UriUtils.pair ("nojsoncallback", 1));
        
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
