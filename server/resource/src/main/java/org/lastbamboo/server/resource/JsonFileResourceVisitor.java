package org.lastbamboo.server.resource;

import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.util.ResourceTypeTranslator;
import org.lastbamboo.common.util.ResourceTypeTranslatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor for converting a single file resource into JSON.
 */
public class JsonFileResourceVisitor implements ResourceVisitor<JSONObject>
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final ResourceTypeTranslator m_typeTranslator = 
        new ResourceTypeTranslatorImpl();

    public JSONObject visitAudioFileResource(final AudioFileResource afr)
        {
        return visitFileResource(afr);
        }

    public JSONObject visitFileResource(final FileResource fr)
        {
        final JSONObject json = new JSONObject();
        return visitFileResource(fr, json);
        }
    

    public JSONObject visitMetaFileResource(final MetaFileResource resource)
        {
        throw new UnsupportedOperationException("Visiting MFR for user");
        }
    
    private JSONObject visitFileResource(final FileResource fr, 
        final JSONObject json)
        {
        JsonUtils.put(json, "title", fr.getTitle());
        JsonUtils.put(json, "uri", fr.getUri());
        JsonUtils.put(json, "size", fr.getSize());
        JsonUtils.put(json, "mimeType", fr.getMimeType());
        JsonUtils.put(json, "tags", fr.getTags());
        JsonUtils.put(json, "mediaType", 
            this.m_typeTranslator.getType(fr.getTitle()));
        JsonUtils.put(json, "urn", fr.getSha1Urn());
        return json;
        }
    }
