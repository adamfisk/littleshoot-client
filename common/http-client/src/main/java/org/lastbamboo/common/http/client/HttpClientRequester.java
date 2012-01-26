package org.lastbamboo.common.http.client;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.littleshoot.util.Pair;

/**
 * Interface for utility classes for issuing HTTP client requests.
 */
public interface HttpClientRequester
    {

    /**
     * Writes a request to the specified address.
     * 
     * @param baseUrl The base URL to send the request to.
     * @param parameters The request parameters.
     * @return The response body.
     * @throws IOException If an IO error occurs.
     * @throws ServiceUnavailableException If the service is unavailable. 
     */
    String request(String baseUrl, 
        Collection<Pair<String,String>> parameters) throws IOException, 
        ServiceUnavailableException;
    
    /**
     * Writes a request to the specified address.
     * 
     * @param baseUrl The base URL to send the request to.
     * @param parameters The request parameters.
     * @return The response body.
     * @throws IOException If an IO error occurs.
     * @throws ServiceUnavailableException If the service is unavailable. 
     */
    String request(String baseUrl, Map<String, String> parameters) 
        throws IOException, ServiceUnavailableException;

    /**
     * Issue a request with no parameters.
     * 
     * @param url The URL to issue the request to.
     * @return The response body.
     * @throws IOException If an IO error occurs.
     * @throws ServiceUnavailableException If the service is unavailable.
     */
    String request(String url) throws IOException, ServiceUnavailableException;

    /**
     * Issue a request with no parameters.
     * 
     * @param url The URL to issue the request to.
     * @return The response body.
     * @throws IOException If any IO error occurs.
     * @throws ServiceUnavailableException If the service is unavailable.
     */
    String request(URL url) throws IOException, ServiceUnavailableException;
    }
