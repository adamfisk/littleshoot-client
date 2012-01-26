package org.lastbamboo.common.searchers.flickr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.lastbamboo.common.rest.JsonRestResultBodyProcessor;
import org.lastbamboo.common.rest.JsonRestResultFactory;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResults;
import org.lastbamboo.common.rest.RestResultsMetadata;
import org.lastbamboo.common.rest.RestSearcher;
import org.lastbamboo.common.rest.XmlRestResultBodyProcessor;

/**
 * Tests accessing the Flickr REST API.
 */
public class FlickrTest
    {
    private static final Logger LOG = LoggerFactory.getLogger(FlickrTest.class);

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
        final XmlRestResultBodyProcessor<FlickrImage> handler =
            new XmlRestResultBodyProcessor<FlickrImage>("photo",
                new FlickrImageFactory(null));
        
        final RestResults results = handler.processResults(is);

        verifyResults(results.getCurrentResults());

        final RestResultsMetadata metadata = results.getMetadata();
        assertEquals(33031, metadata.getTotalResults());
        }

    /**
     * Runs a test for a static file we've downloaded from the REST service.
     *
     * @throws Exception If any unexpected error occurs.
     */
    @Test public void testJsonHandleInputStream() throws Exception
        {
        final InputStream is =
            getClass().getClassLoader().getResourceAsStream("flickr.json");
        assertNotNull(is);
        
        final JsonRestResultFactory resultFactory = new FlickrJsonImageFactory(null);
        final JsonRestResultBodyProcessor handler =
            new JsonRestResultBodyProcessor("photos", "photo", "total", resultFactory);
        final RestResults results = handler.processResults(is);

        verifyResults(results.getCurrentResults());

        final RestResultsMetadata metadata = results.getMetadata();
        assertEquals(90231, metadata.getTotalResults());
        
        
        }
    
    /**
     * This tests everything that the test for the static file above tests,
     * but it also does the full download.
     *
     * @throws Exception If any unexpected error occurs.
     */
    @Test public void testFullDownload() throws Exception
        {
        final RestSearcher searcher =
            new FlickrSearcher(null, UUID.randomUUID(),
                "d67bc572b8b129a7264d1780fd9ed084", "hurricane katrina");

        LOG.debug("About to perform download...");
        final RestResults results = searcher.search();
        assertNotNull("Error searching Flickr!!!", results);
        verifyResults(results.getCurrentResults());
        }

    private void verifyResults(final Collection results) throws Exception
        {
        assertFalse("Did not read any results from file", results.isEmpty());
        for (final Iterator iter = results.iterator(); iter.hasNext();)
            {
            final RestResult result = (RestResult) iter.next();
            //final String title = result.getTitle();
            //assertFalse("No title", StringUtils.isBlank(title));
            //assertFalse("No extension on the title!!",
              //  FilenameUtils.getExtension(title).equals(StringUtils.EMPTY));
            final String url = result.getUrl().toASCIIString();
            assertFalse("No url", StringUtils.isBlank(url));
            }
        }
    }
