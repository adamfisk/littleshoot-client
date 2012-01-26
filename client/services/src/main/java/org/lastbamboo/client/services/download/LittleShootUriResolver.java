package org.lastbamboo.client.services.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.lastbamboo.client.prefs.Prefs;
import org.lastbamboo.common.download.UriResolver;
import org.littleshoot.util.DefaultHttpClient;
import org.littleshoot.util.DefaultHttpClientImpl;
import org.littleshoot.util.HttpParamKeys;
import org.littleshoot.util.Pair;
import org.littleshoot.util.ShootConstants;
import org.littleshoot.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that resolves a {@link URI} for downloading into the available sources
 * for that {@link URI}.  This class sends a request to the LittleShoot 
 * servers to access all available online sources for the file.
 */
public class LittleShootUriResolver implements UriResolver
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private URI m_sha1;
    
    public Collection<URI> resolve(final URI uri) throws IOException 
        {
        final String uriString = createUrlString(uri);
        final DefaultHttpClient client = new DefaultHttpClientImpl();
        final GetMethod method = new GetMethod(uriString);
        method.setRequestHeader("Accept-Encoding", "gzip");
        
        // Do not delete!!!  Here for easier testing.
        final JsonLittleShootSourcesInputStreamHandler handler = 
            new JsonLittleShootSourcesInputStreamHandler();
        InputStream is = null;
        try
            {
            client.executeMethod(method);
            final int responseCode = method.getStatusCode();
            final Header encoding = 
                method.getResponseHeader("Content-Encoding");
            
            if (responseCode != HttpStatus.SC_OK)
                {
                // The server will return 404 if there are no other sources
                // for the content, so this is often expected.
                m_log.debug("Got non-200 response code: "+method.getStatusCode());
                // We need to always make sure we read the full message body.
                final String body;
                if (encoding != null && encoding.getValue().equals("gzip"))
                    {
                    is = new GZIPInputStream(method.getResponseBodyAsStream()); 
                    body = IOUtils.toString(is);
                    }
                else
                    {
                    body = method.getResponseBodyAsString();
                    }
                m_log.debug("Received unexpected response from server:\n" + 
                   method.getStatusLine() + "\n" +
                   Arrays.asList(method.getResponseHeaders())+"\n"+
                   body+"\n\noriginal URL:\n"+uriString);
                if (uri.getScheme().equalsIgnoreCase("http"))
                    {
                    final Collection<URI> uris = new LinkedList<URI>();
                    uris.add(uri);
                    return uris;
                    }
                else
                    {
                    return Collections.emptySet();
                    }
                }
            else
                {
                m_log.debug("Got 200 response");
                Collection<URI> uris;
                if (encoding != null && encoding.getValue().equals("gzip"))
                    {
                    is = new GZIPInputStream(method.getResponseBodyAsStream());  
                    uris = handler.handleInputStream(is);
                    }
                else
                    {
                    // All bodies returned should be zipped.
                    is = method.getResponseBodyAsStream();
                    final String body = IOUtils.toString(is);
                    try
                        {
                        uris = handler.handleString(body);
                        }
                    catch (final IOException e)
                        {
                        // Make sure bad returned data can't kill the whole
                        // download
                        uris = new LinkedList<URI>();
                        }
                        
                    m_log.debug("Received body that's not gzipped.:\n" + 
                       method.getStatusLine() + "\n" + 
                       body+"\n\noriginal URL:\n"+uriString);
                    }
                
                this.m_sha1 = handler.getSha1();
                
                // If we're looking up an HTTP URI, make sure we use the URI itself.
                if (uri.getScheme().equalsIgnoreCase("http"))
                    {
                    uris.add(uri);
                    }
                return uris;
                }
            }
        finally
            {
            IOUtils.closeQuietly(is);
            method.releaseConnection();
            }
        }
    

    private String createUrlString(final URI uri)
        {
        final String baseUrl = 
            ShootConstants.SERVER_URL + "/api/downloadSources";
        final Collection<Pair<String,String>> params =
            new LinkedList<Pair<String,String>> ();

        // We switched this to be just a URI with LittleShoot 0.50.
        if (!uri.getScheme().equalsIgnoreCase("http"))
            {
            params.add (UriUtils.pair ("sha1", uri));
            }
        params.add (UriUtils.pair ("uri", uri));
        params.add (UriUtils.pair ("os", SystemUtils.OS_NAME));
        params.add (UriUtils.pair ("timeZone", SystemUtils.USER_TIMEZONE));
        params.add (UriUtils.pair (HttpParamKeys.INSTANCE_ID, Prefs.getId()));
        
        final String url = UriUtils.newUrl(baseUrl, params);
        m_log.debug("Created download sources URL: {}", url);
        return url;
        }

    public URI getSha1()
        {
        return this.m_sha1;
        }
    }
