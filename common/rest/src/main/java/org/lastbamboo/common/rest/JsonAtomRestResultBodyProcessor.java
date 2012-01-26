package org.lastbamboo.common.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.littleshoot.util.IoExceptionWithCause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for processing REST results in JSON format.
 * 
 * @param <T> An instance of a REST result.
 */
public class JsonAtomRestResultBodyProcessor<T extends RestResult> 
    implements RestResultBodyProcessor<T>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass()); 

    private final JsonRestResultFactory<T> m_resultFactory;
    
    /**
     * Used for debugging to write XML feeds to a file.
     */
    private final static boolean WRITE_TO_FILE = false;

    private final String m_countKey;
    
    /**
     * Creates a new processor that looks for XML nodes with the specified 
     * name for an individual result.
     * 
     * @param resultFactory The factory for creating our Java results from
     * an individual XML node.
     */
    public JsonAtomRestResultBodyProcessor(
        final JsonRestResultFactory<T> resultFactory, final String countKey)
        {
        this.m_resultFactory = resultFactory;
        this.m_countKey = countKey;
        }


    public RestResults<T> processResults(final InputStream is) 
        throws IOException
        {
        m_log.debug("Processing input stream...");
        writeToFile(is);
        
        final String jsonString = IOUtils.toString(is);
        
        //m_log.debug("Got JSON: {}", jsonString);
        final JSONObject feed;
        try
            {
            feed = new JSONObject(jsonString);
            }
        catch (final JSONException e)
            {
            m_log.warn("Could not read JSON from: "+jsonString, e);
            throw new IoExceptionWithCause("Could not read JSON", e);
            }
        final JSONObject json;
        try
            {
            json = feed.getJSONObject("feed");
            }
        catch (final JSONException e)
            {
            m_log.warn("Could not read JSON from: "+jsonString, e);
            throw new IoExceptionWithCause("Could not read JSON", e);
            }
        
        
        try
            {
            final JSONObject jsonCount = json.getJSONObject(this.m_countKey);
            final int count = JsonUtils.extractInt(jsonCount, "$t");
            if (count == 0)
                {
                return null;
                }
            }
        catch (final JSONException e)
            {
            m_log.warn("Could not read JSON from: "+jsonString, e);
            throw new IoExceptionWithCause("Could not read JSON", e);
            }
        
        final Collection<T> results = new LinkedList<T>();
        final JSONArray jsonResults;
        try
            {
            jsonResults = json.getJSONArray("entry");
            }
        catch (final JSONException e)
            {
            m_log.warn("Could not get results array from: "+jsonString, e);
            throw new IoExceptionWithCause("Could not get results array", e);
            }
        for (int i = 0; i < jsonResults.length(); i++)
            {
            final JSONObject jsonResult;
            try
                {
                jsonResult = jsonResults.getJSONObject(i);
                }
            catch (final JSONException e)
                {
                m_log.warn("Could not read JSON result from: "+jsonString, e);
                throw new IoExceptionWithCause("Could not read JSON result", e);
                }
            try
                {
                final T result = 
                    this.m_resultFactory.createResult(feed, jsonResult, i);
                results.add(result);
                }
            catch (final ElementProcessingException e)
                {
                m_log.error("Could not process element", e);
                }
            }
        
        RestResultsMetadata<T> metadata;
        try
            {
            metadata = this.m_resultFactory.createResultsMetadata(json);
            }
        catch (final JSONException e)
            {
            m_log.warn("Could not read JSON", e);
            throw new IoExceptionWithCause("Could not read JSON metadata", e);
            }
        return new RestResultsImpl<T>(metadata, results);
        }

    /**
     * Just for debugging XML feeds.
     * 
     * @param is The input stream.
     * @throws IOException If there's an error reading or writing.
     */
    private void writeToFile(final InputStream is) throws IOException
        {
        if (!WRITE_TO_FILE)
            {
            return;
            }
        final File outFile = new File("rest.json");
        outFile.delete();
        final OutputStream os = new FileOutputStream(outFile);
        IOUtils.copy(is, os);
        os.flush();
        os.close();
        is.close();
        }
    }
