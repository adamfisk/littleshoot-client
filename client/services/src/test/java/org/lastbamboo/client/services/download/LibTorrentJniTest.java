package org.lastbamboo.client.services.download;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Test;
import org.lastbamboo.jni.JLibTorrent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test for the BitTorrent downloader.
 */
public class LibTorrentJniTest
    {


    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    @Test public void testRawTorrent() throws Exception 
        {
        final String libName = System.mapLibraryName("jnltorrent");
        
        final Collection<File> libCandidates = new LinkedList<File>();
        libCandidates.add(new File (new File("../../lib"), libName));
        libCandidates.add(new File (libName));
        libCandidates.add(new File (
            new File(SystemUtils.USER_HOME, ".littleshoot"), libName));

        final JLibTorrent torrent = new JLibTorrent(libCandidates, true);
        final Thread hook = new Thread(new Runnable()
            {
            public void run()
                {
                torrent.stopLibTorrent();
                }
            });
        Runtime.getRuntime().addShutdownHook(hook);
        
        
        final File incompleteDir = new File ("temp_incomplete_dir").getCanonicalFile();
        incompleteDir.mkdirs();
        incompleteDir.deleteOnExit();
        
        final File torrentFile = new File("src/test/resources/the-future-of-ideas.torrent");
        
        assertTrue(torrentFile.exists());
        torrent.download(incompleteDir, torrentFile, false, -1);
        final long completeSize = torrent.getSizeForTorrent(torrentFile);
        
        for (int i = 0; i < 60; i++)
            {
            final long read = torrent.getBytesRead(torrentFile);
            if (read == completeSize)
                {
                System.out.println("Got complete!");
                break;
                }
            Thread.sleep(1000);
            }
        
        final File testCompleteDir = new File ("test_complete_dir");
        testCompleteDir.mkdirs();
        FileUtils.cleanDirectory(testCompleteDir);
        assertTrue(testCompleteDir.listFiles().length == 0);
        testCompleteDir.deleteOnExit();
        
        final File incompleteFile = new File(incompleteDir, torrent.getName(torrentFile));
        assertTrue("Incomplete dir does not exist", incompleteFile.isDirectory());
        
        torrent.moveToDownloadsDir(torrentFile, testCompleteDir);
        final File completeFile = new File(testCompleteDir, torrent.getName(torrentFile));
        for (int i = 0; i < 10; i++)
            {
            if (completeFile.isDirectory())
                {
                System.out.println("Got dir!!");
                break;
                }
            Thread.sleep(200);
            }
        
        assertTrue("Complete dir does not exist", completeFile.isDirectory());
        
        FileUtils.cleanDirectory(testCompleteDir);
        }
    }
