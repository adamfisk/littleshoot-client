package org.lastbamboo.client.services.download;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

import junit.framework.TestCase;

/**
 * Tests the class for resolving URIs using the LittleShoot servers.
 */
public class LittleShootInputStreamHandlerTest extends TestCase
    {

    public void testResolveUri() throws Exception
        {
        final InputStream is =
                getClass().getClassLoader().getResourceAsStream
                    ("open-search-sources.xml");
        assertNotNull(is);

        final LittleShootSourcesInputStreamHandler ish =
               new LittleShootSourcesInputStreamHandler();
        ish.handleInputStream(is);
        final Collection<URI> uris = ish.getUris();

        assertEquals(1, uris.size());
        }
    }
