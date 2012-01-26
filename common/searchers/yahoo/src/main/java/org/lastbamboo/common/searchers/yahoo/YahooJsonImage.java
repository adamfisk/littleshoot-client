package org.lastbamboo.common.searchers.yahoo;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.rest.AbstractJsonRestResult;
import org.lastbamboo.common.rest.ElementProcessingException;

/**
 * Video class for Yahoo image REST results.
 */
public class YahooJsonImage extends AbstractJsonRestResult
    {

    private static final Logger LOG = LoggerFactory.getLogger(YahooJsonImage.class);
    
    /**
     * Creates a Yahoo image bean from JSON data.
     */
    public YahooJsonImage(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        super(jsonResult, newUri(jsonResult), 
            newThumbnailUrl(jsonResult), "yahoo_boss_image");
        }
    
    private static URI newThumbnailUrl(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        try
            {
            return new URI(jsonResult.getString("thumbnail_url"));
            }
        catch (final JSONException e)
            {
            throw new ElementProcessingException("No URL in "+jsonResult, e);
            }
        }

    private static URI newUri(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        try
            {
            return new URI(jsonResult.getString("clickurl"));
            }
        catch (final JSONException e)
            {
            throw new ElementProcessingException("No URL in "+jsonResult, e);
            }
        }
    }
