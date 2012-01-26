package org.lastbamboo.common.searchers.isohunt;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.rest.AbstractJsonRestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing data for a single IsoHunt search result.
 */
public final class JsonIsoHuntResult extends AbstractJsonRestResult
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = 
        LoggerFactory.getLogger(JsonIsoHuntResult.class);

    public JsonIsoHuntResult(final JSONObject fullJson, 
        final JSONObject jsonResult) throws URISyntaxException
        {
        super(jsonResult, getUri(jsonResult, "enclosure_url"), 
            getThumbnailUrl(fullJson), "isohunt");
        LOG.debug("Created new result...");
        }

    private static URI getUri(final JSONObject jsonResult, final String key) 
        throws URISyntaxException
        {
        try
            {
            final String val = jsonResult.getString(key);
            return new URI(val);
            }
        catch (final JSONException e)
            {
            LOG.warn("Could not extract "+key+" from: {}", jsonResult);
            final String msg = key + " not found in "+jsonResult;
            throw new URISyntaxException("Bad JSON?", msg);
            }
        }

    private static URI getThumbnailUrl(final JSONObject json) 
        throws URISyntaxException
        {
        try
            {
            final JSONObject image = json.getJSONObject("image");
            return new URI(image.getString("url"));
            }
        catch (final JSONException e)
            {
            final String msg = "Image or url not found in "+json;
            LOG.warn(msg, e);
            throw new URISyntaxException("Bad JSON?", msg);
            }
        
        }
    }
