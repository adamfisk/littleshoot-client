package org.lastbamboo.common.searchers.isohunt;

import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.rest.ElementProcessingException;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResultSources;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestResultsMetadataImpl;
import org.littleshoot.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for creating IsoHunt results.
 */
public class JsonIsoHuntResultFactory 
    implements JsonRestResultFactory<JsonIsoHuntResult>
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final IsoHuntSearcher m_searcher;

    
    /**
     * Creates a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public JsonIsoHuntResultFactory(final IsoHuntSearcher searcher)
        {
        this.m_searcher = searcher;
        }
    
    public JsonIsoHuntResult createResult(final JSONObject fullJson,
        final JSONObject jsonResult, 
        final int index) throws ElementProcessingException 
        {
        try
            {
            return new JsonIsoHuntResult(fullJson, jsonResult);
            }
        catch (final URISyntaxException e)
            {
            m_log.error("Could not read uris", e);
            throw new ElementProcessingException("Could not create result", e);
            }
        }

    public RestResultsMetadata<JsonIsoHuntResult> createResultsMetadata(
        final JSONObject fullJson) throws JSONException
        {
        final int totalResults = fullJson.getInt("total_results");
        final int resultLimit;
        if (CommonUtils.isPro())
            {
            resultLimit = totalResults;
            }
        else if (totalResults > CommonUtils.FREE_RESULT_LIMIT)
            {
            resultLimit = CommonUtils.FREE_RESULT_LIMIT;
            }
        else
            {
            resultLimit = totalResults;
            }
        
        m_log.debug("Got total results: {}", resultLimit);
        return new RestResultsMetadataImpl<JsonIsoHuntResult>(resultLimit, 
            RestResultSources.ISO_HUNT, this.m_searcher);
        }

    }
