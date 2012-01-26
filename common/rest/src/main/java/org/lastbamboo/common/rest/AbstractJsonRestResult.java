package org.lastbamboo.common.rest;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.littleshoot.util.IoUtils;
import org.littleshoot.util.ResourceTypeTranslator;
import org.littleshoot.util.ResourceTypeTranslatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for making it easy to create search results from JSON data.
 */
public class AbstractJsonRestResult implements RestResult
    {

    private final Logger m_log = 
        LoggerFactory.getLogger(AbstractJsonRestResult.class);
    
    private static ResourceTypeTranslator s_translator = 
        new ResourceTypeTranslatorImpl ();
    private final byte[] m_json;
    private final URI m_url;
    private final String m_source;
    private final String m_mediaType;
    private final String m_title;

    
    public AbstractJsonRestResult(final JSONObject json, final URI uri,
        final URI thumbnailUrl, final String source)
        {
        this(json, null, uri, thumbnailUrl, source);
        }

    public AbstractJsonRestResult(final JSONObject json, final String title,
        final URI uri, final URI thumbnailUrl, final String source)
        {
        JsonUtils.put(json, "thumbnailUrl", thumbnailUrl);
        JsonUtils.put(json, "source", source);
        JsonUtils.put(json, "uri", uri);

        final String original = json.toString();
        this.m_json = IoUtils.deflate(original);
        
        final int completeSize = original.length();
        final int savings = 100 * this.m_json.length/completeSize;
        m_log.debug("Compressed to percent of original: {}", savings);
        //m_log.debug("IN/OUT"+in.length()+"/"+this.m_json.length());
        this.m_url = uri;
        this.m_source = source;
        String tempTitle;
        if (StringUtils.isBlank(title) && json.has("title"))
            {
            try
                {
                tempTitle = json.getString("title");
                }
            catch (final JSONException e)
                {
                tempTitle = null;
                }
            }
        else
            {
            tempTitle = title;
            }
        this.m_title = tempTitle;
        if (StringUtils.isNotBlank(title))
            {
            this.m_mediaType = s_translator.getType (title);
            }
        else
            {
            this.m_mediaType = "";
            }
        }

    public byte[] getJson()
        {
        return m_json;
        }

    public String getAuthor()
        {
        return null;
        }

    public String getDescription()
        {
        return null;
        }

    public long getFileSize()
        {
        return -1;
        }

    public int getLengthSeconds()
        {
        return -1;
        }

    public String getMediaType()
        {
        return this.m_mediaType;
        }

    public String getMimeType()
        {
        return null;
        }

    public int getNumSources()
        {
        return -1;
        }

    public float getRating()
        {
        return -1;
        }

    public URI getSha1Urn()
        {
        return null;
        }

    public String getSource()
        {
        return this.m_source;
        }

    public int getThumbnailHeight()
        {
        return -1;
        }

    public URI getThumbnailUrl()
        {
        return null;
        }

    public int getThumbnailWidth()
        {
        return -1;
        }

    public String getTitle()
        {
        return this.m_title;
        }

    public URI getUrl()
        {
        return this.m_url;
        }

    public long getUserId()
        {
        return -1;
        }

    }
