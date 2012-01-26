package org.lastbamboo.common.searchers.youtube;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.lastbamboo.common.rest.JsonAtomRestResultBodyProcessor;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResultBodyProcessor;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.XmlRestResultBodyProcessor;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.RestSearcher;
import org.lastbamboo.common.searchers.youtube.stubs.RestSearcherStub;

/**
 * Test for querying YouTube REST API.
 */
public class YouTubeTest 
    {

    private static final Logger LOG = LoggerFactory.getLogger(YouTubeTest.class);
    

    @Test public void testJsonFromFile() throws Exception
        {
        final InputStream is =
            getClass().getClassLoader().getResourceAsStream("rest.json");
        assertNotNull(is);
        final RestSearcher searcher = new RestSearcherStub();
        
        final JsonRestResultFactory<YouTubeJsonVideo> resultFactory =
            new YouTubeJsonResultFactory(null);
        //this.m_resultBodyProcessor = //new YouTubeRestResultBodyProcessor(this);
          //  new JsonAtomRestResultBodyProcessor<YouTubeJsonVideo>("entry", resultFactory);
        final RestResultBodyProcessor<YouTubeJsonVideo> handler =
            new JsonAtomRestResultBodyProcessor<YouTubeJsonVideo>(resultFactory, "openSearch$totalResults");
            //new JsonAtomRestResultBodyProcessor<YouTubeJsonVideo>("entry", resultFactory);
        final RestResults<YouTubeJsonVideo> restResults = 
            handler.processResults(is);
        final Collection<YouTubeJsonVideo> results = 
            restResults.getCurrentResults();
        
        final RestResultsMetadata<YouTubeJsonVideo> meta = restResults.getMetadata();
        
        assertEquals(1000, meta.getTotalResults());
        verifyJsonResults(results);
        //assertEquals(2, restResults.getMetadata().getTotalResults());
        }
    
    /**
     * This tests everything that the test for the static file above tests,
     * but it also does the full download.
     *
     * @throws Exception If any unexpected error occurs.
     */
    @Test public void testFullDownloadJson() throws Exception
        {
        final RestSearcher searcher = 
            new YouTubeGDataSearcher(null, UUID.randomUUID(), "hurricane katrina");
        /*
        final RestSearcher searcher = new YouTubeSearcher(null,
            UUID.randomUUID(), "-gD_rV9L6ps", "hurricane katrina");
            */
        LOG.debug("About to perform download...");
        final RestResults results = searcher.search();
        verifyJsonResults(results.getCurrentResults());
        }
    
    private void verifyJsonResults(
        final Collection<YouTubeJsonVideo> results) throws Exception
        {
        assertFalse("Did not read any results", results.isEmpty());
        for (final RestResult result : results)
            {
            //final String title = result.getTitle();
            //assertFalse("No title", StringUtils.isBlank(title));
            //assertFalse("No extension on the title!!",
            //    FilenameUtils.getExtension(title).equals(StringUtils.EMPTY));
            final String url = result.getUrl().toASCIIString();
            LOG.debug("Using URL: "+url);
            assertFalse("No url", StringUtils.isBlank(url));
            assertTrue("Unexpected URL: "+url,
                url.startsWith("http://www.youtube.com/"));
            }
        }

    public void testNewApiFromFile() throws Exception
        {
        final InputStream is =
            getClass().getClassLoader().getResourceAsStream("gdata.xml");
        assertNotNull(is);
        final RestSearcher searcher = new RestSearcherStub();
        final RestResultBodyProcessor<YouTubeGDataVideo> handler =
            new YouTubeRestResultBodyProcessor(searcher);
        final RestResults<YouTubeGDataVideo> restResults = 
            handler.processResults(is);
        final Collection<YouTubeGDataVideo> results = 
            restResults.getCurrentResults();
        
        verifyResults(results, 130, 97);
        assertEquals(2, restResults.getMetadata().getTotalResults());
        
        final Iterator<YouTubeGDataVideo> iter = results.iterator();
        final YouTubeGDataVideo result = iter.next();
        
        assertEquals("Andy Samplo", result.getAuthor());
        assertEquals(79, result.getLengthSeconds());
        assertEquals("My walk with Mr. Darcy.flv", result.getTitle());
        assertEquals(new URI("http://www.youtube.com/v/ZTUVgYoeN_b&fmt=18"), 
            result.getUrl());
        assertEquals(new URI("http://img.youtube.com/vi/ZTUVgYoeN_b/2.jpg"), 
            result.getThumbnailUrl());
        assertEquals(130, result.getThumbnailWidth());
        assertEquals(97, result.getThumbnailHeight());
        assertEquals((float)4.94, result.getRating());
        
        final YouTubeGDataVideo result2 = iter.next();
        assertEquals("Andy Samplo", result2.getAuthor());
        assertEquals(79, result2.getLengthSeconds());
        assertEquals("Another walk with Mrs. Darcy.flv", result2.getTitle());
        assertEquals(new URI("http://www.youtube.com/v/ZTUVgYoeN_b&fmt=18"), 
                result2.getUrl());
        assertEquals(new URI("http://img.youtube.com/vi/ZTUVgYoeN_b/2.jpg"), 
                result2.getThumbnailUrl());
        assertEquals(130, result2.getThumbnailWidth());
        assertEquals(97, result2.getThumbnailHeight());
        }
    
    private void verifyResults(
        final Collection<YouTubeGDataVideo> results, 
        final int width, final int height) throws Exception
        {
        assertFalse("Did not read any results", results.isEmpty());
        for (final RestResult result : results)
            {
            final String title = result.getTitle();
            assertFalse("No title", StringUtils.isBlank(title));
            assertFalse("No extension on the title!!",
                FilenameUtils.getExtension(title).equals(StringUtils.EMPTY));
            final String url = result.getUrl().toASCIIString();
            LOG.debug("Using URL: "+url);
            assertFalse("No url", StringUtils.isBlank(url));
            assertTrue("Unexpected URL: "+url,
                url.startsWith("http://www.youtube.com/"));
    
            assertEquals(width, result.getThumbnailWidth());
            assertEquals(height, result.getThumbnailHeight());
            }
        }
        
    
    /**
     * Runs a test for a static file we've downloaded from the REST service.
     *
     * @throws Exception If any unexpected error occurs.
     */
    public void testHandleInputStream() throws Exception
        {
        final InputStream is =
            getClass().getClassLoader().getResourceAsStream("katrina.xml");
        assertNotNull(is);
        final XmlRestResultBodyProcessor handler =
            new XmlRestResultBodyProcessor<YouTubeVideoImpl>("video",
                new YouTubeVideoFactory(null));
        final RestResults results = handler.processResults(is);
        verifyResults(results.getCurrentResults());

        assertEquals(120, results.getMetadata().getTotalResults());
        }


    /**
     * This tests everything that the test for the static file above tests,
     * but it also does the full download.
     *
     * @throws Exception If any unexpected error occurs.
     */
    public void testFullDownload() throws Exception
        {
        final RestSearcher searcher = 
            new YouTubeGDataSearcher(null, UUID.randomUUID(), "hurricane katrina");
        /*
        final RestSearcher searcher = new YouTubeSearcher(null,
            UUID.randomUUID(), "-gD_rV9L6ps", "hurricane katrina");
            */
        LOG.debug("About to perform download...");
        final RestResults results = searcher.search();
        verifyResults(results.getCurrentResults());
        }

    private void verifyResults(final Collection results) throws Exception
        {
        assertFalse("Did not read any results", results.isEmpty());
        for (final Iterator iter = results.iterator(); iter.hasNext();)
            {
            final RestResult result = (RestResult) iter.next();
            final String title = result.getTitle();
            assertFalse("No title", StringUtils.isBlank(title));
            assertFalse("No extension on the title!!",
                FilenameUtils.getExtension(title).equals(StringUtils.EMPTY));
            final String url = result.getUrl().toASCIIString();
            LOG.debug("Using URL: "+url);
            assertFalse("No url", StringUtils.isBlank(url));
            assertTrue("Unexpected URL: "+url,
                url.startsWith("http://www.youtube.com/"));

            assertTrue(result.getThumbnailWidth() > 0);
            assertTrue(result.getThumbnailHeight() > 0);
            //final HttpClient client = new HttpClient();
            //final HeadMethod method = new HeadMethod(url);
            //client.executeMethod(method);
            //assertEquals(200, method.getStatusCode());
            }
        }

    }
