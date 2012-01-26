package org.lastbamboo.common.searchers.yahoo;

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
 * Factory class for creating Yahoo image results.
 */
public final class YahooJsonImageFactory 
    implements JsonRestResultFactory<YahooJsonImage>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final RestSearcher<YahooJsonImage> m_searcher;
    
    /**
     * Creates a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public YahooJsonImageFactory(final RestSearcher<YahooJsonImage> searcher)
        {
        this.m_searcher = searcher;
        }
    

    public YahooJsonImage createResult(final JSONObject fullJson,
        final JSONObject jsonResult, final int index) 
        throws ElementProcessingException 
        {
        try
            {
            return new YahooJsonImage(jsonResult);
            }
        catch (final URISyntaxException e)
            {
            m_log.error("Could not read uris", e);
            throw new ElementProcessingException("Could not create photo", e);
            }
        }

    public RestResultsMetadata<YahooJsonImage> createResultsMetadata(
        final JSONObject fullJson)
        {
        try
            {
            final JSONObject results = fullJson.getJSONObject("ysearchresponse");
            final int totalResults = JsonUtils.extractInt(results, "totalhits");
            final int resultLimit;
            if (CommonUtils.isPro())
                {
                resultLimit = totalResults;
                }
            else
                {
                resultLimit = CommonUtils.FREE_RESULT_LIMIT;
                }
            
            return new RestResultsMetadataImpl<YahooJsonImage>(resultLimit, 
                RestResultSources.YAHOO_IMAGES, this.m_searcher);
            }
        catch (final JSONException e)
            {
            m_log.warn("Bad JSON??", e);
            return new RestResultsMetadataImpl<YahooJsonImage>(0, 
                RestResultSources.YAHOO_IMAGES, this.m_searcher);
            }
        }
    }
