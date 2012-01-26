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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating Yahoo video results.
 */
public final class YahooJsonVideoFactory 
    implements JsonRestResultFactory<YahooJsonVideo>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final RestSearcher<YahooJsonVideo> m_searcher;
    
    /**
     * Creates a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public YahooJsonVideoFactory(final RestSearcher<YahooJsonVideo> searcher)
        {
        this.m_searcher = searcher;
        }
    

    public YahooJsonVideo createResult(final JSONObject fullJson,
        final JSONObject jsonResult, 
        final int index) throws ElementProcessingException 
        {
        try
            {
            return new YahooJsonVideo(jsonResult);
            }
        catch (final URISyntaxException e)
            {
            m_log.error("Could not read uris", e);
            throw new ElementProcessingException("Could not create photo", e);
            }
        }

    public RestResultsMetadata<YahooJsonVideo> createResultsMetadata(
        final JSONObject fullJson)
        {
        try
            {
            final JSONObject results = fullJson.getJSONObject("ResultSet");
            final int parsedResults = 
                JsonUtils.extractInt(results, "totalResultsAvailable");
            
            final int totalResults;
            // The Yahoo API does not allow access to more than 1000 results.
            if (parsedResults > 1000)
                {
                totalResults = 1000;
                }
            else
                {
                totalResults = parsedResults;
                }
            return new RestResultsMetadataImpl<YahooJsonVideo>(totalResults, 
                RestResultSources.YAHOO_VIDEOS, this.m_searcher);
            }
        catch (final JSONException e)
            {
            m_log.warn("Bad JSON??", e);
            return new RestResultsMetadataImpl<YahooJsonVideo>(0, 
                RestResultSources.YAHOO_VIDEOS, this.m_searcher);
            }
        }
    }
