package org.lastbamboo.common.searchers.littleshoot;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.rest.ElementProcessingException;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResultSources;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestResultsMetadataImpl;
import org.littleshoot.util.ResourceTypeTranslator;
import org.littleshoot.util.ResourceTypeTranslatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for creating LittleShoot results
 */
public class JsonLittleShootResultFactory 
    implements JsonRestResultFactory<JsonLittleShootResult>
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final LittleShootSearcher m_searcher;
    private final ResourceTypeTranslator m_resourceTypeTranslator;

    /**
     * Creates a new result factory.
     * 
     * @param searcher The searcher that initiated the search.  This is needed
     * in case we need to make future calls to the server for more results.
     */
    public JsonLittleShootResultFactory(final LittleShootSearcher searcher)
        {
        this.m_searcher = searcher;
        this.m_resourceTypeTranslator = new ResourceTypeTranslatorImpl();
        }
    
    public JsonLittleShootResult createResult(final JSONObject fullJson,
        final JSONObject jsonResult, 
        final int index) throws ElementProcessingException 
        {
        try
            {
            final URI thumbUri = getThumbnailUrl(jsonResult);
            return new JsonLittleShootResult(jsonResult, index, thumbUri);
            }
        catch (final URISyntaxException e)
            {
            m_log.error("Could not read uris", e);
            throw new ElementProcessingException("Could not create photo", e);
            }

        }

    public RestResultsMetadata<JsonLittleShootResult> createResultsMetadata(
        final JSONObject fullJson)
        {
        final int totalResults = JsonUtils.extractInt(fullJson, "totalResults");
        
        return new RestResultsMetadataImpl<JsonLittleShootResult>(totalResults, 
            RestResultSources.LITTLE_SHOOT, this.m_searcher);
        }

    private URI getThumbnailUrl(final JSONObject jsonResult) 
        throws URISyntaxException
        {
        // Note the UI actually ignores this.
        final String uriPrefix =
            "http://www.littleshoot.org/images/icons/";
        final String mediaType = getMediaType (jsonResult);
        
        final Map<String,String> typesToUris = new HashMap<String,String> ();
        
        typesToUris.put ("document", "document.png");
        typesToUris.put ("audio", "audio.png");
        typesToUris.put ("video", "video.png");
        typesToUris.put ("image", "image.png");
        typesToUris.put ("application/mac", "application.png");
        typesToUris.put ("application/linux", "application.png");
        typesToUris.put ("application/win", "application.png");
        
        if (typesToUris.containsKey (mediaType))
            {
            final String suffix = typesToUris.get (mediaType);
            return new URI (uriPrefix + suffix);
            }
        else
            {
            return new URI (uriPrefix + "document.png");
            }
        }
    
    private String getMediaType(final JSONObject jsonResult)
        {
        return this.m_resourceTypeTranslator.getType (
            getString(jsonResult, "title"));
        }

    private String getString(final JSONObject jsonResult, final String key)
        {
        try
            {
            return jsonResult.getString(key);
            }
        catch (final JSONException e)
            {
            m_log.warn("Could not extract "+key+" from: {}", jsonResult);
            return "";
            }
        }
    }
