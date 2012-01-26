package org.lastbamboo.common.rest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface for classes that create REST results from XML.
 * 
 * @param <R> A type extending a REST result.
 */
public interface JsonRestResultFactory <R extends RestResult>
    {


    /**
     * Creates metadata about the set of results.
     * 
     * @param document The XML results.
     * @return The metadata about the results.
     * @throws JSONException 
     */
    RestResultsMetadata<R> createResultsMetadata(JSONObject fullJson) throws JSONException;
        

    R createResult(JSONObject fullJson, JSONObject jsonResult, int index) 
        throws ElementProcessingException;

    }
