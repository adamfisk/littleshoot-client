package org.lastbamboo.common.rest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.id.uuid.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.littleshoot.util.DefaultHttpClient;
import org.littleshoot.util.DefaultHttpClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for searching generic REST APIs.
 * 
 * @param <T> A class extending {@link RestResult}
 */
public abstract class AbstractRestSearcher<T extends RestResult> 
    implements RestSearcher<T>
    {

    private final Logger m_log = 
        LoggerFactory.getLogger(AbstractRestSearcher.class); 

    protected RestResultBodyProcessor<T> m_resultBodyProcessor;

    private final RestResultProcessor<T> m_uiProcessor;

    private final UUID m_messageId;

    protected final String m_searchString;

    protected int m_pageIndex;

    private final RestResultSources m_source;
    
    private final DefaultHttpClient m_httpClient = new DefaultHttpClientImpl();

    private boolean m_searchActive;
    
    /**
     * Creates a new REST searcher.
     * 
     * @param resultProcessor The class for processing result instances.
     * @param messageId The ID of the search message.
     * @param searchString The search string we're looking for.
     * @param pageIndex The index of the results on the particular search
     * service.
     * @param source The source for this searcher. 
     */
    public AbstractRestSearcher(final RestResultProcessor<T> resultProcessor, 
        final UUID messageId, final String searchString, final int pageIndex, 
        final RestResultSources source)
        {
        if (StringUtils.isBlank(searchString))
            {
            throw new IllegalArgumentException("No search string");
            }
        this.m_uiProcessor = resultProcessor;
        this.m_messageId = messageId;
        this.m_searchString = searchString;
        this.m_pageIndex = pageIndex;
        this.m_source = source;
        }
    
    public final String getSearchString()
        {
        return this.m_searchString;
        }
    
    protected boolean doubleSearch()
        {
        return false;
        }
    
    protected void incrementPage(final int numResultsLastPass)
        {
        // We just increment the page by default.
        this.m_pageIndex++;
        }

    public RestResults<T> search()
        {
        m_log.debug("Searching for: "+this.m_searchString);
        
        if (this.m_searchActive)
            {
            m_log.warn("Search is active");
            return null;
            }
        this.m_searchActive = true;
        RestResults<T> results = runSearch();
        
        if (results != null && doubleSearch() && results.hasMoreResults())
            {
            // If we got no results the first time, don't try again.
            if (!results.getCurrentResults().isEmpty())
                {
                m_log.debug("Running second search...");
                results = runSearch();
                }
            }
        
        this.m_searchActive = false;
        
        if (results == null)
            {
            addEmpty();
            }
        // Note the return value is not used except in tests.
        return results;
        }

    private RestResults<T> runSearch() 
        {
        final String urlString = createUrlString(this.m_searchString);
        
        if (StringUtils.isBlank(urlString))
            {
            m_log.warn("Could not create URL string!!");
            return null;
            }
        
        m_log.debug("Sending search to: {}", urlString);
        final GetMethod method = new GetMethod(urlString);
        
        m_log.debug("All headers:\n{}",
            Arrays.asList(method.getRequestHeaders()));
        
        // We need to do this to enable gzip compression with GData.  Really
        // odd, but that's how Google chose to do it.  
        // TODO: One problem: it doesn't work.  See the apparently incorrect:
        // http://code.google.com/support/bin/answer.py?answer=76755&topic=10955
        method.setRequestHeader("User-Agent", 
            "Jakarta Commons-HttpClient/3.1 (gzip)");
        method.setRequestHeader("Accept-Encoding", "gzip");
        
        // We don't care about cookies for REST requests.
        method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        
        InputStream is = null;
        try
            {
            m_httpClient.executeMethod(method);

            m_log.debug("All request headers:\n{}",
                Arrays.asList(method.getRequestHeaders()));
            
            m_log.debug("All response headers:\n{}",
                Arrays.asList(method.getResponseHeaders()));
            final Header encoding = 
                method.getResponseHeader("Content-Encoding");
            
            // Always read the response body.
            if (encoding != null)
                {
                final String value = encoding.getValue();
                if (StringUtils.isNotBlank(value) && value.contains("gzip"))
                    {
                    m_log.debug("Unzipping body...");
                    is = new GZIPInputStream(method.getResponseBodyAsStream());
                    }
                else
                    {
                    is = method.getResponseBodyAsStream();
                    }
                }
            else
                {
                is = method.getResponseBodyAsStream();
                }
            
            final int statusCode = method.getStatusCode();
            
            final StatusLine statusLine = method.getStatusLine();
            if (statusCode != HttpStatus.SC_OK)
                {
                final String body = IOUtils.toString(is);
                m_log.warn("ERROR ISSUING REQUEST: " + urlString + "\n" +
                    statusLine+"\n"+body);
                
                IOUtils.write(body.getBytes(), 
                    new FileOutputStream(new File(getClass().getSimpleName()+"-Error-Response.html")));
                
                return null;
                }
            else
                {
                m_log.debug("Successfully wrote request...");
                
                // If there's a proxy or something odd between this node and
                // the network, we can often get a redirected HTML response 
                // back.  This just checks for that case.
                final InputStream buffered = new BufferedInputStream(is);
                buffered.mark(21);
                final byte[] buf = new byte[20];
                buffered.read(buf);
                
                final String start = new String(buf, "US-ASCII");
                if (start.startsWith("<body bgcolor") || start.startsWith("<html"))
                    {
                    m_log.error("Got weird HTML response: "+start);
                    writeFile(is);
                    return null;
                    }
                else
                    {
                    buffered.reset();
                    return processStream(buffered);
                    }
                }

            }
        catch (final HttpException e)
            {
            m_log.warn("HTTP error", e);
            return null;
            }
        catch (final IOException e)
            {
            m_log.warn("IO error", e);
            return null;
            }
        catch (final Exception e)
            {
            m_log.warn("IO error", e);
            return null;
            }
        finally
            {
            IOUtils.closeQuietly(is);
            method.releaseConnection();
            }
        }


    private RestResults<T> processStream(final InputStream is) 
        throws IOException 
        {
        final RestResults<T> results = 
            this.m_resultBodyProcessor.processResults(is);
        
        m_log.debug("Processed results.");
        if (results == null)
            {
            m_log.debug("Null results");
            return null;
            }
        
        else
            {
            // The result processor can be null during testing.
            if (this.m_uiProcessor != null)
                {
                m_log.debug("Sending results to frontend...");
                this.m_uiProcessor.processResults(this.m_messageId, 
                    results);
                }
            incrementPage(results.getCurrentResults().size());
            return results;
            }
        }
    
    private void addEmpty()
        {
        final RestResultsMetadata<T> metadata = 
            new RestResultsMetadataImpl<T>(0, this.m_source, this);
        final RestResults<T> results = 
            new RestResultsImpl<T>(metadata, new LinkedList<T>());
        
        // The result processor can be null during testing.
        if (this.m_uiProcessor != null)
            {
            this.m_uiProcessor.processResults(this.m_messageId,results);
            }
        }

    private void writeFile(final InputStream is)
        {
        
        try
            {
            final String body = IOUtils.toString(is);
            final String fileName = 
                getClass().getSimpleName()+"-Error-Response.html";
            IOUtils.write(body.getBytes(), 
                new FileOutputStream(new File(fileName)));
            }
        catch (FileNotFoundException e)
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
        catch (IOException e)
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
        }
    }
