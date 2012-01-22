package org.lastbamboo.client.services.download;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


public class TorrentDecoderImplTest
    {

    @Test public void testErrorFile() throws Exception
        {
        
        final String name1 = "Steal This Film II.Xvid.avi";
        testTorrent("errorFile1.torrent", name1, 1);
        
        // Test2
        final String name2 =
            "Blinkenlichten Produktionen - Elektrischer Reporter 007 - " +
            "Kollektives Kino - Und alle Filmen mit";
        testTorrent("errorFile2.torrent", name2, 4);
        
        // Test 3
        final String name3 =
            "Blinkenlichten Produktionen - Elektrischer Reporter 006 - " +
            "Neues vom Sport - Daddeln als Karriere";
        testTorrent("errorFile3.torrent", name3, 4);
        
        // Test 4 - one of the actual torrent files Janko was having problems
        // with.
        //final String name4 = "test";
        //testTorrent("822739415.torrent", name4, 1);
        //final File testFile = new File ("src/test/resources", "doesNotExist");
        //final TorrentDecoder decoder = new TorrentDecoderImpl(testFile);
        
        }

    private void testTorrent(final String torrentName, 
        final String expectedName, final int expectedNumFiles) 
        throws IOException
        {
        final File testFile = new File ("src/test/resources", torrentName);
        
        final TorrentDecoder decoder = new TorrentDecoderImpl(testFile);
        
        final String name = decoder.getName();
        //System.out.println(name);
      
        assertEquals(expectedName, name);
        
        final int numFiles = decoder.getNumFiles();
        assertEquals(expectedNumFiles, numFiles);
        }
    }
