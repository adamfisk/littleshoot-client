package org.lastbamboo.integration.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.lastbamboo.common.bencode.BDecoderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BDecodeTest
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    @Test public void testNetworkDecode() throws Exception
        {
        final HttpClient client = new HttpClient();
        runTest(client, "http://stealthisfilm.com/torrent/StealThisFilmII.iPod.torrent", "Steal This Film II.iPod.m4v", 1, 544860157L);
        runTest(client, "http://torrent.ibiblio.org/torrents/download/7ad5ce94db6258b34643ee37b1f5ce5740120830.torrent", "engarde-community-3.0.22.i686.iso", 1, 666742784L);
        runTest(client, "http://torrent.ibiblio.org/torrents/download/67aadaf8cdd273757b1c7369bd6c110588939c40.torrent", "eclipse-jee-ganymede-win32.zip", 1, 171132790L);
        }

    private void runTest(final HttpClient client, final String url, 
        final String expectedTitle, final int expectedNumFiles, 
        final long expectedLength) throws Exception
        {
        final GetMethod method =  new GetMethod(url);
        
        client.executeMethod(method);
        
        final InputStream is = method.getResponseBodyAsStream();
        final File testFile = new File(getClass().getSimpleName()+"-Test");
        testFile.deleteOnExit();
        final FileOutputStream os = new FileOutputStream(testFile);
        IOUtils.copy(is, os);
        is.close();
        os.close();
        
        //BDecoderUtils.print(testFile);
        final Map<String, Object> torrentMap = BDecoderUtils.map(testFile);
        
        final String name = BDecoderUtils.name(torrentMap);
        final int numFiles = BDecoderUtils.numFiles(torrentMap);
        final long length = BDecoderUtils.getLength(torrentMap);
        
        assertEquals(expectedTitle, name);
        assertEquals(expectedNumFiles, numFiles);
        assertEquals(expectedLength, length);
        }
        
    }
