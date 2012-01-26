package org.lastbamboo.common.searchers.yahoo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.lastbamboo.common.rest.JsonRestResultBodyProcessor;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResultFactory;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestSearcher;
import org.lastbamboo.common.rest.XmlRestResultBodyProcessor;

/**
 * Tests processing of the image streams from Yahoo.
 */
public class YahooSearchTest 
    {

    private static final Logger LOG = LoggerFactory.getLogger(YahooSearchTest.class);

    /**
     * Tests the method for handling an input stream from Yahoo.
     * @throws Exception If any unexpected error occurs.
     */
    public void testHandleInputStream() throws Exception
        {
        runTest("katrina_image.xml", new YahooImageFactory(null), 763);
        //runTest("katrina_video.xml", new YahooVideoFactory(null), 4499);
        }
    
    /**
     * Tests the method for handling an input stream from Yahoo.
     * @throws Exception If any unexpected error occurs.
     */
    @Test public void testJsonInputStream() throws Exception
        {
        runJsonTest("images.json", new YahooJsonImageFactory(null), 616462, 
            "ysearchresponse", "resultset_images", "count");
        //runJsonTest("videos.json", new YahooJsonVideoFactory(null), 1000, 
        //    "ResultSet", "Result", "totalResultsReturned");
        }

    private void runJsonTest(final String testFilePath,
        final JsonRestResultFactory resultFactory, 
        final int numResults, final String baseNode, final String arrayName, 
        final String countKey) throws Exception
        {
        final InputStream is =
            getClass().getClassLoader().getResourceAsStream(testFilePath);
        assertNotNull(is);
        final JsonRestResultBodyProcessor processor =
            new JsonRestResultBodyProcessor(baseNode, arrayName, countKey, resultFactory);
        final RestResults results = processor.processResults(is);
        verifyResults(results.getCurrentResults());

        final RestResultsMetadata metadata = results.getMetadata();
        assertEquals(numResults, metadata.getTotalResults());
        }

    private void runTest(final String testFilePath,
        final RestResultFactory factory, final int numResults) throws Exception
        {
        final InputStream is =
            getClass().getClassLoader().getResourceAsStream(testFilePath);
        assertNotNull(is);
        final XmlRestResultBodyProcessor processor =
            new XmlRestResultBodyProcessor("Result", factory);
        final RestResults results = processor.processResults(is);
        verifyResults(results.getCurrentResults());

        final RestResultsMetadata metadata = results.getMetadata();
        assertEquals(numResults, metadata.getTotalResults());
        }

    /**
     * This tests everything that the test for the static file above tests,
     * but it also does the full download.
     *
     * @throws Exception If any unexpected error occurs.
     */
    @Test public void testFullDownload() throws Exception
        {
        LOG.debug("Running full download tests...");
        runFullTest(new YahooImageSearcher(null, UUID.randomUUID(), "ajzgmvnV34GrMcdruY9h3vKb4GD5AZoqfiLXhWfazKWcIkuaWZYlyoCqxrEV", "hurricane katrina"));
        //runFullTest(new YahooVideoSearcher(null, UUID.randomUUID(), "littleshoot", "hurricane katrina"));
        }

    private void runFullTest(final RestSearcher searcher) throws Exception
        {
        final RestResults results = searcher.search();

        verifyResults(results.getCurrentResults());
        }

    private void verifyResults(final Collection results) throws Exception
        {
        assertFalse("Did not read any results from file", results.isEmpty());
        for (final Iterator iter = results.iterator(); iter.hasNext();)
            {
            final RestResult result = (RestResult) iter.next();
            final String url = result.getUrl().toASCIIString();
            assertFalse("No url", StringUtils.isBlank(url));
            }
        }
    }
