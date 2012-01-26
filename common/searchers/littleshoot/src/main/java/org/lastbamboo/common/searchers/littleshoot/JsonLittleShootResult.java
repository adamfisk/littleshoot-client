package org.lastbamboo.common.searchers.littleshoot;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.rest.AbstractJsonRestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing data for a single LittleShoot search result.
 */
public final class JsonLittleShootResult extends AbstractJsonRestResult
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = 
        LoggerFactory.getLogger(JsonLittleShootResult.class);

    public JsonLittleShootResult(final JSONObject jsonResult, final int index,
        final URI thumbnailUri) throws URISyntaxException
        {
        super(jsonResult, getUri(jsonResult, "uri"), thumbnailUri, "littleshoot");
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
    }
