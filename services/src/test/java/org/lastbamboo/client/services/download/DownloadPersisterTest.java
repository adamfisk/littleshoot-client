package org.lastbamboo.client.services.download;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Test for download persistence.
 */
public class DownloadPersisterTest
    {

    @Test public void testPersistence() throws Exception
        {
        final DownloadPersister persister = new DownloadPersister();
        persister.clear();
        final String uri = "http://www.torrents.com/torrent.torrent";
        final File torrentFile = new File("testTorrentFile").getCanonicalFile();
        final File incompleteDir = new File("testIncompleteDir").getCanonicalFile();
        torrentFile.createNewFile();
        torrentFile.deleteOnExit();
        incompleteDir.mkdir();
        incompleteDir.deleteOnExit();
        
        persister.saveTorrent(uri, torrentFile, incompleteDir, 100);
        Thread.sleep(200);
        
        final TorrentDataCallback callback = new TorrentDataCallback()
            {
            public void onData(final String decodedUri, final File torrentFile,
                final File incompleteDir, final int torrentState)
                {
                assertEquals(uri, decodedUri);
                assertEquals(torrentFile, torrentFile);
                assertEquals(incompleteDir, incompleteDir);
                assertEquals(100, torrentState);
                }
            };
        
        persister.loadTorrents(callback);
        persister.clear();
        }
    }
