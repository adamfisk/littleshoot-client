package org.lastbamboo.common.paypal;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.lastbamboo.common.http.client.HttpClientGetRequester;
import org.lastbamboo.common.http.client.HttpClientPostRequester;

public class PayPalTest
    {

    @Test public void testPayPal() throws Exception
        {
    
        final Collection<Pair<String, String>> params = createParams();
        final HttpClientPostRequester post = new HttpClientPostRequester();
        
        
        final String url = "https://api-3t.sandbox.paypal.com/nvp";
        //final String response = post.request(url, params);
        
        //System.out.println("\n"+response);
        }
    
    /**
     * Returns a URL used to publish a resource in the repository.
     * 
     * @param resource
     *      The resource to publish.
     *      
     * @return
     *      A URL used to publish the given resource in the repository.
     */
    private static Collection<Pair<String, String>> createParams ()
        {
        final Collection<Pair<String,String>> params =
                new LinkedList<Pair<String,String>> ();
        
        params.add(UriUtils.pair("USER", "a_1199846682_biz_api1.lastbamboo.org"));
        params.add(UriUtils.pair("PWD", "1199846697"));
        params.add(UriUtils.pair("SIGNATURE", "An5ns1Kso7MWUdW4ErQKJJJ4qi4-AAXXcZQWUpGl7fm4M0zauwPYsUHJ"));
        params.add(UriUtils.pair("VERSION", "3.2"));
        
        params.add(UriUtils.pair("METHOD", "SetExpressCheckout"));
        params.add(UriUtils.pair("AMT", "10.00"));
        params.add(UriUtils.pair("RETURNURL", "https://www.littleshoot.org"));
        params.add(UriUtils.pair("CANCELURL", "https://www.littleshoot.org"));

        return params;
        }
    }
