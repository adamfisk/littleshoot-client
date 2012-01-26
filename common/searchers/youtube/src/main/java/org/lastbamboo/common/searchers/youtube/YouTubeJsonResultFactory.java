package org.lastbamboo.common.searchers.youtube;

import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.rest.ElementProcessingException;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResultSources;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestResultsMetadataImpl;
import org.littleshoot.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for creating LittleShoot results
 */
public class YouTubeJsonResultFactory 
    implements JsonRestResultFactory<YouTubeJsonVideo>
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final YouTubeGDataSearcher m_searcher;

    
    /**
     * Creates a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public YouTubeJsonResultFactory(final YouTubeGDataSearcher searcher)
        {
        this.m_searcher = searcher;
        }
    
    public YouTubeJsonVideo createResult(final JSONObject fullJson,
        final JSONObject jsonResult, 
        final int index) throws ElementProcessingException 
        {
        try
            {
            return new YouTubeJsonVideo(jsonResult);
            }
        catch (final URISyntaxException e)
            {
            m_log.error("Could not read uris", e);
            throw new ElementProcessingException("Could not create photo", e);
            }
        }

    public RestResultsMetadata<YouTubeJsonVideo> createResultsMetadata(
        final JSONObject fullJson)
        {
        final int totalResults;
        try
            {
            final JSONObject total = 
                fullJson.getJSONObject("openSearch$totalResults");
            final int parsedResults = JsonUtils.extractInt(total, "$t");
            
            // The YouTube API does not allow access to more than 1000 results.
            final int resultLimit;
            if (CommonUtils.isPro())
                {
                resultLimit = 1000;
                }
            else
                {
                resultLimit = CommonUtils.FREE_RESULT_LIMIT;
                }
            if (parsedResults > resultLimit)
                {
                totalResults = resultLimit;
                }
            else
                {
                totalResults = parsedResults;
                }
            }
        catch (final JSONException e)
            {
            m_log.warn("Bad JSON??", e);
            return new RestResultsMetadataImpl<YouTubeJsonVideo>(0, 
                RestResultSources.YOU_TUBE, this.m_searcher);
            }
        return new RestResultsMetadataImpl<YouTubeJsonVideo>(totalResults, 
            RestResultSources.YOU_TUBE, this.m_searcher);
        }

    }
