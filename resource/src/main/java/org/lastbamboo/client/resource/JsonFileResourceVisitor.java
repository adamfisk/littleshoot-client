package org.lastbamboo.client.resource;

import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.littleshoot.util.ResourceTypeTranslator;
import org.littleshoot.util.ResourceTypeTranslatorImpl;
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
    
    public JSONObject visitLocalFileResource(final LocalFileResource fr)
        {
        final JSONObject json = new JSONObject();
        JsonUtils.put(json, "path", fr.getPath());
        return visitFileResource(fr, json);
        }

    public JSONObject visitAudioFileResource(final AudioFileResource afr)
        {
        return visitLocalFileResource(afr);
        }

    public JSONObject visitFileResource(final FileResource fr)
        {
        final JSONObject json = new JSONObject();
        return visitFileResource(fr, json);
        }
    
    private JSONObject visitFileResource(final FileResource fr, 
        final JSONObject json)
        {
        JsonUtils.put(json, "title", fr.getTitle());
        JsonUtils.put(json, "uri", fr.getUri());
        JsonUtils.put(json, "size", new Long(fr.getSize()));
        JsonUtils.put(json, "mimeType", fr.getMimeType());
        JsonUtils.put(json, "tags", fr.getTags());
        JsonUtils.put(json, "mediaType", 
            this.m_typeTranslator.getType(fr.getTitle()));
        JsonUtils.put(json, "urn", fr.getSha1Urn());
        return json;
        }
    
    public JSONObject visitDirectoryResource(final DirectoryResource dr)
        {
        final JSONObject json = new JSONObject();
        JsonUtils.put(json, "title", dr.getTitle());
        JsonUtils.put(json, "path", dr.getDir());
        JsonUtils.put(json, "tags", dr.getTags());
        JsonUtils.put(json, "numFiles", dr.getNumFiles());
        return json;
        }   
    }
