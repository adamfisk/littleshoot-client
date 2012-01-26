package org.lastbamboo.common.http.client;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import org.littleshoot.util.Pair;

/**
 * Issues a post request using HTTP client. 
 */
public class HttpClientGetRequester implements HttpClientRequester
    {

    private static final Collection<Pair<String, String>> EMPTY_PARAMS =
        Collections.unmodifiableList(new LinkedList<Pair<String,String>>());
    
    private final BaseHttpClientRequester m_baseRequester = 
        new BaseHttpClientRequester();
    
    public String request(final String address,
        final Collection<Pair<String, String>> parameters) throws IOException, 
        ServiceUnavailableException
        {
        return m_baseRequester.get(address, parameters);
        }

    public String request(final String address,
        final Map<String, String> parameters) throws IOException, 
        ServiceUnavailableException
        {
        return m_baseRequester.get(address, parameters);
        }

    public String request(final String url) throws IOException, 
        ServiceUnavailableException
        {
        return request(url, EMPTY_PARAMS);
        }
    
    public String request(final URL url) throws IOException, 
        ServiceUnavailableException
        {
        return request(url.toExternalForm());
        }
    }
