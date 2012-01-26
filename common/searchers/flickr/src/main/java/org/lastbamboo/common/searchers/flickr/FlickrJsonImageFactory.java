package org.lastbamboo.common.searchers.flickr;

import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.rest.ElementProcessingException;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResultSources;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestResultsMetadataImpl;
import org.lastbamboo.common.rest.RestSearcher;
import org.littleshoot.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for creating Flickr images.
 */
public class FlickrJsonImageFactory 
    implements JsonRestResultFactory<FlickrJsonImage>
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final RestSearcher<FlickrJsonImage> m_searcher;
    
    /**
     * Creates a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public FlickrJsonImageFactory(final RestSearcher<FlickrJsonImage> searcher)
        {
        this.m_searcher = searcher;
        }

    public FlickrJsonImage createResult(final JSONObject fullJson, 
        final JSONObject jsonResult, final int index) 
        throws ElementProcessingException 
        {
        try
            {
            return new FlickrJsonImage(jsonResult);
            }
        catch (final URISyntaxException e)
            {
            m_log.error("Could not read uris", e);
            throw new ElementProcessingException("Could not create photo", e);
            }
        }

    public RestResultsMetadata<FlickrJsonImage> createResultsMetadata(
        final JSONObject fullJson)
        {
        //System.out.println(fullJson);
        try
            {
            final JSONObject results = fullJson.getJSONObject("photos");
            final int totalResults = JsonUtils.extractInt(results, "total");

            final int resultLimit;
            if (CommonUtils.isPro())
                {
                resultLimit = totalResults;
                }
            else
                {
                resultLimit = CommonUtils.FREE_RESULT_LIMIT;
                }
            return new RestResultsMetadataImpl<FlickrJsonImage>(resultLimit, 
                RestResultSources.FLICKR, this.m_searcher);
            }
        catch (final JSONException e)
            {
            m_log.warn("Bad JSON??", e);
            return new RestResultsMetadataImpl<FlickrJsonImage>(0, 
                RestResultSources.FLICKR, this.m_searcher);
            }
        }
    }
