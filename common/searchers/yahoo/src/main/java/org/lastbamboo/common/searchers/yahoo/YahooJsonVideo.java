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
 * Video class for Yahoo video REST results.
 */
public class YahooJsonVideo extends AbstractJsonRestResult
    {

    private static final Logger LOG = LoggerFactory.getLogger(YahooJsonVideo.class);
    
    /**
     * Creates a Yahoo video bean from JSON data.
     * 
     * @param jsonResult The JSON result data.
     * @throws URISyntaxException Thrown if there's an error parsing a URI. 
     * @throws ElementProcessingException If there's an error processing any
     * element.
     */
    public YahooJsonVideo(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        super(jsonResult, newUri(jsonResult), 
            newThumbnailUrl(jsonResult), "yahoo_video");
        }
    
    private static URI newThumbnailUrl(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        //System.out.println(jsonResult);
        try
            {
            final JSONObject tn = jsonResult.getJSONObject("Thumbnail");
            return new URI(tn.getString("Url"));
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
            return new URI(jsonResult.getString("ClickUrl"));
            }
        catch (final JSONException e)
            {
            throw new ElementProcessingException("No URL in "+jsonResult, e);
            }
        }
    }
