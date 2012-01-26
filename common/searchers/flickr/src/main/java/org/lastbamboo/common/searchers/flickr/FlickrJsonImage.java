package org.lastbamboo.common.searchers.flickr;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.rest.AbstractJsonRestResult;
import org.lastbamboo.common.rest.ElementProcessingException;

/**
 * Class containing data for a single Flickr photo.
 */
public final class FlickrJsonImage extends AbstractJsonRestResult
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FlickrJsonImage.class);
    
    /**
     * Creates a Flickr image bean from JSON data.
     * 
     * @param jsonResult The JSON result data.
     * @throws URISyntaxException Thrown if there's an error parsing a URI. 
     * @throws ElementProcessingException If there's an error processing any
     * element.
     */
    public FlickrJsonImage(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        super(jsonResult, newUri(jsonResult), 
            newThumbnailUrl(jsonResult), "flickr");
        }

    private static URI newThumbnailUrl(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        //System.out.println(jsonResult);
        return newFlickrUri(jsonResult);
        }
    
    private static URI newUri(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        return newFlickrUri(jsonResult);
        }

    private static URI newFlickrUri(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        try
            {
            final int farmId = jsonResult.getInt("farm");
            final String id = jsonResult.getString("id");
            final String serverId = jsonResult.getString("server");
            final String secret = jsonResult.getString("secret");
            final StringBuilder sb = new StringBuilder();
            sb.append("http://farm");
            sb.append(farmId);
            sb.append(".static.flickr.com/");
            sb.append(serverId);
            sb.append("/");
            sb.append(id);
            sb.append("_");
            sb.append(secret);
            final String photoUrl = sb.toString();
            
            //LOG.debug("Returning URI value: "+photoUrl);
            return new URI(photoUrl);
            }
        catch (final JSONException e)
            {
            LOG.warn("Exception reading JSON", e);
            throw new ElementProcessingException("Error reading "+jsonResult,e);
            }
        }
    }
