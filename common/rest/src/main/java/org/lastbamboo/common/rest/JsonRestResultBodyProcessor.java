package org.lastbamboo.common.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.littleshoot.util.IoExceptionWithCause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for processing REST results in JSON format.
 * 
 * @param <T> An instance of a REST result.
 */
public class JsonRestResultBodyProcessor<T extends RestResult> 
    implements RestResultBodyProcessor<T>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass()); 

    private final JsonRestResultFactory<T> m_resultFactory;
    
    /**
     * Used for debugging to write JSON feeds to a file.
     */
    private final static boolean WRITE_TO_FILE = false;

    private final String m_arrayName;
    
    private final String m_baseNode;

    private final String m_countKey;

    private final JsonRestResultBodyExtraProcessor m_extraProcessor;

    /**
     * Creates a new processor that looks for JSON nodes with the specified 
     * name for an individual result.
     * @param baseNode An optional base node that will contain the array of
     * result data.
     * @param arrayName The name of the array of results.
     * @param countKey The key for checking the count.  Some source don't 
     * return an array if there are no hits, so we need to check the count
     * before attempting to parse the array.
     * @param resultFactory The factory for creating our Java results from
     * an individual JSON node.
     */
    public JsonRestResultBodyProcessor(final String baseNode,
        final String arrayName, final String countKey,
        final JsonRestResultFactory<T> resultFactory)
        {
        this(baseNode, arrayName, countKey, resultFactory, 
            new JsonRestResultBodyExtraProcessorAdaptor());
        }
    
    /**
     * Creates a new processor that looks for JSON nodes with the specified 
     * name for an individual result.
     * @param baseNode An optional base node that will contain the array of
     * result data.
     * @param arrayName The name of the array of results.
     * @param countKey The key for checking the count.  Some source don't 
     * return an array if there are no hits, so we need to check the count
     * before attempting to parse the array.
     * @param resultFactory The factory for creating our Java results from
     * an individual JSON node.
     * @param extraProcessor Extra processor for odd cases that may arise.
     */
    public JsonRestResultBodyProcessor(final String baseNode,
        final String arrayName, final String countKey,
        final JsonRestResultFactory<T> resultFactory,
        final JsonRestResultBodyExtraProcessor extraProcessor)
        {
        this.m_baseNode = baseNode;
        this.m_arrayName = arrayName;
        this.m_countKey = countKey;
        this.m_resultFactory = resultFactory;
        this.m_extraProcessor = extraProcessor;
        }

    /**
     * Creates a new processor that looks for JSON nodes with the specified 
     * name for an individual result.
     * 
     * @param arrayName The name of the array of results.
     * @param resultFactory The factory for creating our Java results from
     * an individual JSON node.
     */
    public JsonRestResultBodyProcessor(final String arrayName,
        final JsonRestResultFactory<T> resultFactory)
        {
        this(null, arrayName, null, resultFactory);
        }

    public RestResults<T> processResults(final InputStream is) 
        throws IOException
        {
        m_log.debug("Processing input stream...");
        writeToFile(is);
        
        final String jsonString = IOUtils.toString(is);
        
        m_log.debug("Got JSON: {}", jsonString);
        final JSONObject json;
        final JSONObject base;
        try
            {
            json = new JSONObject(jsonString);
            if (StringUtils.isNotBlank(this.m_baseNode))
                {
                if (!json.has(this.m_baseNode))
                    {
                    this.m_extraProcessor.noBaseNode(json);
                    return null;
                    }
                base = json.getJSONObject(this.m_baseNode);
                }
            else
                {
                base = json;
                }
            }
        catch (final JSONException e)
            {
            m_log.warn("Could not read JSON from: "+jsonString, e);
            throw new IoExceptionWithCause("Could not read JSON", e);
            }

        if (StringUtils.isNotBlank(this.m_countKey))
            {
            try
                {
                //final int count = json.getInt(this.m_countKey);
                final String countStr = base.getString(this.m_countKey);
                if (!NumberUtils.isNumber(countStr))
                    {
                    m_log.warn("No number at "+this.m_countKey+" in: "+
                        jsonString);
                    return null;
                    }
                final int count = Integer.parseInt(countStr);
                if (count == 0)
                    {
                    return null;
                    }
                }
            catch (final JSONException e)
                {
                m_log.warn("Could not access count", e);
                return null;
                }
            }
        
        final Collection<T> results = new LinkedList<T>();
        final JSONArray jsonResults;
        try
            {
            jsonResults = base.getJSONArray(this.m_arrayName);
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
                    this.m_resultFactory.createResult(json, jsonResult, i);
                results.add(result);
                }
            catch (final ElementProcessingException e)
                {
                m_log.error("Could not process element", e);
                }
            }
        
        final RestResultsMetadata<T> metadata;
        try
            {
            metadata = this.m_resultFactory.createResultsMetadata(json);
            }
        catch (final JSONException e)
            {
            throw new IoExceptionWithCause("Could not read JSON result", e);
            }
        return new RestResultsImpl<T>(metadata, results);
        }

    /**
     * Just for debugging JSON feeds.
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
