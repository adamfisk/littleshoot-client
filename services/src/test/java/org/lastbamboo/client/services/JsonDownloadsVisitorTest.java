package org.lastbamboo.client.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.lastbamboo.client.services.download.DownloadTracker;
import org.lastbamboo.client.services.download.DownloadTrackerImpl;
import org.lastbamboo.client.services.download.LibTorrentManager;
import org.lastbamboo.client.services.download.LibTorrentManagerImpl;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.download.VisitableDownloader;
import org.littleshoot.util.Pair;
import org.littleshoot.util.Sha1Hasher;


public class JsonDownloadsVisitorTest
    {

    @Test public void testDownloadsJson() throws Exception 
        {
        final FileMapper mapper = new FileMapperAdaptor()
            {

            @Override
            public URI getUri(final File file)
                {
                try
                    {
                    return Sha1Hasher.createSha1Urn(file);
                    }
                catch (IOException e)
                    {
                    return null;
                    }
                }
            };
            
        final DownloadTracker<MoverDState<Sha1DState<MsDState>>, Downloader<MoverDState<Sha1DState<MsDState>>>> tracker =
            new DownloadTrackerImpl<MoverDState<Sha1DState<MsDState>>, Downloader<MoverDState<Sha1DState<MsDState>>>>();
        
        final URI id1 = new URI("urn:sha1:qquoru8918");
        final URI id2 = new URI("urn:sha1:are874y329r0");
        final Downloader<MoverDState<Sha1DState<MsDState>>> downloader1 =
            new DownloaderTestAdaptor<MoverDState<Sha1DState<MsDState>>>()
            {
            
            @Override
            public MoverDState<Sha1DState<MsDState>> getState()
                {
                return new MoverDStateAdaptor();
                }
            };
        final Downloader<MoverDState<Sha1DState<MsDState>>> downloader2 =
            new DownloaderTestAdaptor<MoverDState<Sha1DState<MsDState>>>()
            {
            
            @Override
            public MoverDState<Sha1DState<MsDState>> getState()
                {
                return new MoverDStateAdaptor();
                }
            };
            
        VisitableDownloader<MsDState> visitableDownloader1 = new DownloaderTestAdaptor<MsDState>();
        tracker.trackDownloader(id1, downloader1, visitableDownloader1);
        tracker.trackDownloader(id2, downloader2, visitableDownloader1);
        
        final Collection<Entry<URI, Pair<Downloader<MoverDState<Sha1DState<MsDState>>>, VisitableDownloader<MsDState>>>> active = 
            tracker.getActive();
        final Iterator<Entry<URI, Pair<Downloader<MoverDState<Sha1DState<MsDState>>>, VisitableDownloader<MsDState>>>> iter = active.iterator();
        final Entry<URI, Pair<Downloader<MoverDState<Sha1DState<MsDState>>>, VisitableDownloader<MsDState>>> first = iter.next();
        final Entry<URI, Pair<Downloader<MoverDState<Sha1DState<MsDState>>>, VisitableDownloader<MsDState>>> second = iter.next();
        assertEquals("Unexpected first key", id1, first.getKey());
        assertEquals("Unexpected second key", id2, second.getKey());
        
        
        final File dir = new File(getClass().getSimpleName()+"-TestDir");
        dir.deleteOnExit();
        dir.mkdir();
        FileUtils.cleanDirectory(dir);
        assertEquals(0, dir.listFiles().length);
        
        final File file = File.createTempFile("jsonDownloadsTest1", ".tmp", dir);
        if (file.exists()) 
            {
            assertTrue(file.delete());
            }
        final Writer fw = new FileWriter(file);
        fw.write("testing 1,2,3");
        fw.close();
        file.deleteOnExit();
        final URI uri1 = Sha1Hasher.createSha1Urn(file);
        //System.out.println("lm1: "+file.lastModified());
        //System.out.println("now: "+System.currentTimeMillis());
        
        // For some reason last modified only seems accurate to the second 
        // level.  Bizarre, I know.
        Thread.sleep(1000);
        final File file2 = new File(dir, "jsonDownloadsTest"+RandomUtils.nextInt());
        if (file2.exists()) 
            {
            assertTrue(file2.delete());
            }
        //System.out.println("lm2: "+file2.lastModified());
        final Writer fw2 = new FileWriter(file2);
        fw2.write("testing 1,2,3,4");
        fw2.close();
        file2.deleteOnExit();
        final URI uri2 = Sha1Hasher.createSha1Urn(file2);
        
        //System.out.println("lm1: "+file.lastModified());
        //System.out.println("lm2: "+file2.lastModified());
        
        final LibTorrentManager torrentManager = new LibTorrentManagerImpl();
        
        final Collection all = tracker.getAll();
        
        JsonDownloadsVisitor visitor = 
            new JsonDownloadsVisitor(20, 0, mapper, dir, torrentManager, all);
        
        String jsonString = visitor.getJson();
        JSONObject json = new JSONObject(jsonString);
        JSONArray downloads = json.getJSONArray("downloads");
        assertEquals(4, downloads.length());
        
        visitor = new JsonDownloadsVisitor(1, 0, mapper, dir, torrentManager, all);
        jsonString = visitor.getJson();
        json = new JSONObject(jsonString);
        downloads = json.getJSONArray("downloads");
        assertEquals(1, downloads.length());
        JSONObject returnedDownload = downloads.getJSONObject(0);
        String returnedUri = returnedDownload.getString("uri");
        assertEquals("Unexpected URI: "+returnedUri, id1.toASCIIString(), returnedUri);
        
        visitor = new JsonDownloadsVisitor(1, 1, mapper, dir, torrentManager, all);
        jsonString = visitor.getJson();
        json = new JSONObject(jsonString);
        downloads = json.getJSONArray("downloads");
        assertEquals(1, downloads.length());
        returnedDownload = downloads.getJSONObject(0);
        returnedUri = returnedDownload.getString("uri");
        assertEquals("Unexpected URI: "+returnedUri, id2.toASCIIString(), returnedUri);
        
        visitor = new JsonDownloadsVisitor(2, 1, mapper, dir, torrentManager, all);
        jsonString = visitor.getJson();
        json = new JSONObject(jsonString);
        downloads = json.getJSONArray("downloads");
        assertEquals(2, downloads.length());
        returnedDownload = downloads.getJSONObject(0);
        returnedUri = returnedDownload.getString("uri");
        assertEquals("Unexpected URI: "+returnedUri, uri1.toASCIIString(), returnedUri);
        JSONObject returnedDownload2 = downloads.getJSONObject(1);
        String returnedUri2 = returnedDownload2.getString("uri");
        assertEquals("Unexpected URI: "+returnedUri2, uri2.toASCIIString(), returnedUri2);
        
        
        visitor = new JsonDownloadsVisitor(3, 1, mapper, dir, torrentManager, all);
        jsonString = visitor.getJson();
        json = new JSONObject(jsonString);
        downloads = json.getJSONArray("downloads");
        assertEquals(1, downloads.length());
        returnedDownload = downloads.getJSONObject(0);
        returnedUri = returnedDownload.getString("uri");
        assertEquals("Unexpected URI: "+returnedUri, uri2.toASCIIString(), returnedUri);
        }
    }
