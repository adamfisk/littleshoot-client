package org.lastbamboo.client.services.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

/**
 * Test for JSON download sources processing.
 */
public class JsonLittleShootSourceInputStreamHandlerTest
    {

    @Test public void testJsonStream() throws Exception
        {
        final String uri1 = "sip://24024/uri-res/N2R?urn:sha1:1289JOEUO2982";
        final String uri2 = "sip://24024324/uri-res/N2R?urn:sha1:1289JOEUO2982";
        final String json = "{\"downloads\": 3, \"urls\": [\"sip:\\/\\/24024\\/uri-res\\/N2R?urn:sha1:1289JOEUO2982\", \"sip:\\/\\/24024324\\/uri-res\\/N2R?urn:sha1:1289JOEUO2982\"]}";
        
        final InputStream is = new ByteArrayInputStream(json.getBytes("UTF-8"));
        
        final JsonLittleShootSourcesInputStreamHandler handler = 
            new JsonLittleShootSourcesInputStreamHandler();
        final Collection<URI> urls = handler.handleInputStream(is);
        assertEquals(2, urls.size());
        final Iterator<URI> iter = urls.iterator();
        final String parsedUri1 = iter.next().toASCIIString();
        final String parsedUri2 = iter.next().toASCIIString();
        
        assertNotNull(parsedUri1);
        assertNotNull(parsedUri2);
        assertEquals(uri1, parsedUri1);
        assertEquals(uri2, parsedUri2);
        
        }
    }
