package org.lastbamboo.common.http.client;

import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

/**
 * The interface to the Apache Commons implementation of our HTTP client
 * interface.
 */
public interface CommonsHttpClient
    {
    
    /**
     * Executes a given HTTP method (from Commons HTTP).
     * 
     * @param method The method to execute.
     * @return The response code.
     */
    int executeMethod (HttpMethod method);
    
    /**
     * Returns this client's parameters.
     * 
     * @return This client's parameters.
     */
    HttpClientParams getParams ();
    
    /**
     * Returns this client's HTTP connection manager.
     * 
     * @return This client's HTTP connection manager.
     */
    HttpConnectionManager getHttpConnectionManager ();
    }
