package org.lastbamboo.server.resource;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;


/**
 * Visitor for representing resources in the JSON data format.
 */
public class JsonResourceVisitor
    {

    private static final Logger LOG = LoggerFactory.getLogger(JsonResourceVisitor.class);
    private final String m_escapedJson;
    
    private final Collection<JSONObject> m_resources = 
        new LinkedList<JSONObject>();
   
    
    /**
     * Creates a new visitor for converting resources to the JSON data format.
     * 
     * @param resources The collection of resources to visit.
     * @param totalResults The total number of results available.
     */
    public JsonResourceVisitor(final Collection resources, 
        final int totalResults)
        {
        final JSONObject json = new JSONObject();
        
        final NumberFormat format = NumberFormat.getInstance();
        final String formatted = format.format(totalResults);
        JsonUtils.put(json, "totalResults", totalResults);
        JsonUtils.put(json, "totalResultsFormatted", formatted);
        
        final JsonFileResourceVisitor visitor = new JsonFileResourceVisitor();
        // Leave this for now.
        for (final Iterator iter = resources.iterator(); iter.hasNext();)
            {
            final VisitableResource vr = (VisitableResource) iter.next();
            final JSONObject jsonObject = vr.accept(visitor);
            m_resources.add(jsonObject);
            }
        try
            {
            json.put("results", m_resources);
            }
        catch (final JSONException e)
            {
            LOG.warn("Exception inserting resources", e);
            }
        
        final String jsonString = json.toString();
        
        // We need to escape single quotes from the titles because they're
        // interpreted in JavaScript as close quotes.  We do it here because
        // the JSON code will do its own escaping if we do it when inserting
        // into the map.
        this.m_escapedJson = jsonString.replaceAll("'", "\\\\'");
        } 

    /**
     * Accessor for the JSON string.
     * 
     * @return The string for the visited resources in the JSON data format.
     */
    public String getJson()
        {
        return this.m_escapedJson;
        }

    }
