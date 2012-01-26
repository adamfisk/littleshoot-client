package org.lastbamboo.common.jlibtorrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.littleshoot.util.DefaultHttpClient;
import org.littleshoot.util.DefaultHttpClientImpl;
import org.littleshoot.util.Sha1Hasher;
import org.littleshoot.util.CommonUtils;
import org.lastbamboo.jni.JLibTorrent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LibTorrentStreamerImplTest
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    @Test public void testTorrent() throws Exception
        {
        if (true)
            {
            return;
            }
        //final File pwd = new File("../../lib");
        final String libName = System.mapLibraryName("jnltorrent");
        //final File fullFile = 
        //    new File (pwd.getCanonicalFile(), System.mapLibraryName("jnltorrent"));
        
        final Collection<File> libCandidates = new LinkedList<File>();
        libCandidates.add(new File (new File("../../lib"), libName));
        libCandidates.add(new File (libName));
        libCandidates.add(new File (
            new File(SystemUtils.USER_HOME, ".littleshoot"), libName));
        //assertTrue(fullFile.isFile());
        //final String fullPath = fullFile.getCanonicalPath();
        final JLibTorrent torrent = new JLibTorrent(libCandidates, 
            CommonUtils.isPro());
        //torrent.startSession();
        final Thread hook = new Thread(new Runnable()
            {
            public void run()
                {
                torrent.stopLibTorrent();
                }
            });
        Runtime.getRuntime().addShutdownHook(hook);
        
        
        final File incompleteBaseDir = new File(getClass().getSimpleName()+"tmp");
        final File incompleteDir = newUniqueIncompleteDir(incompleteBaseDir);
        incompleteBaseDir.deleteOnExit();
        incompleteDir.deleteOnExit();
        
        //final File savePath = new File(".").getCanonicalFile();
        final File download = 
            new File(incompleteDir, "Patent It Yourself ~@nthr@x~.pdf");
        //download.deleteOnExit();
        download.delete();
        
        final String uri = 
            "http://torrents.thepiratebay.org/4573249/patent_it_yourself.4573249.TPB.torrent";
        final File torrentFile = downloadTorrentFile(uri);
        torrentFile.deleteOnExit();
        
        torrent.download(incompleteDir, torrentFile, false, -1);
        
        //System.out.println("Size: "+torrent.getSizeForTorrent(torrentFile));
        //Thread.sleep(60000);
        final long completeSize = torrent.getSizeForTorrent(torrentFile);
        
        final File testFile = 
            new File(getClass().getSimpleName()+".testDownload");
        testFile.deleteOnExit();
        testFile.delete();
        final OutputStream os = new FileOutputStream(testFile);
        final LibTorrentDownloadListener listener = 
            new LibTorrentDownloadListenerAdaptor();
        
        
        final LibTorrentStreamer streamer = 
            new LibTorrentStreamerImpl(torrent, torrentFile, completeSize, 
                incompleteDir, listener);
        
        streamer.write(os, false);
        

        assertTrue(download.isFile());
        
        assertEquals(22106771L, testFile.length());
        assertEquals(22106771L, download.length());
        
        final URI hash1 = Sha1Hasher.createSha1Urn(download);
        final URI hash2 = Sha1Hasher.createSha1Urn(testFile);
        
        assertEquals(hash1, hash2);
        }
    

    private File newUniqueIncompleteDir(final File incompleteBaseDir) 
        throws IOException
        {
        for (int i = 0; i < 20; i++)
            {
            final File dir = 
                new File(incompleteBaseDir, "torrent-"+RandomUtils.nextInt());
            if (!dir.isDirectory())
                {
                if (!dir.mkdirs())
                    {
                    m_log.error("Could not make directory: {}", dir);
                    }
                else
                    {
                    return dir;
                    }
                }
            }
        m_log.error("Could not make unique directory");
        throw new IOException("Could not create unique directory!!");
        }
    
    private File downloadTorrentFile(final String uri) throws IOException
        {
        final GetMethod method = new GetMethod(uri);
        method.addRequestHeader("connection", "close");
        
        InputStream is = null;
        final DefaultHttpClient client = new DefaultHttpClientImpl();
        try
            {
            client.executeMethod(method);
            
            final int statusCode = method.getStatusCode();
            final StatusLine statusLine = method.getStatusLine();
            final Header encoding = 
                method.getResponseHeader("Content-Encoding");
            if (encoding != null && encoding.getValue().equals("gzip"))
                {
                m_log.debug("Unzipping body...");
                is = new GZIPInputStream(method.getResponseBodyAsStream());
                }
            else
                {
                is = method.getResponseBodyAsStream();
                }
            
            if (statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE)
                {
                final String msg = "Got 503 Service Unavailable " + 
                    method.getURI() + "\n" +
                    statusLine + "\n" + IOUtils.toString(is);
                m_log.warn(msg);
                throw new IOException(msg);
                }
            if (statusCode != HttpStatus.SC_OK)
                {
                final String msg = "NO 200 OK: " + method.getURI() + "\n" +
                    statusLine + "\n" + IOUtils.toString(is);
                m_log.warn(msg);
                throw new IOException(msg);
                }
            else
                {
                m_log.debug("Got 200 response...");
                
                final File tmpDir = 
                    new File(System.getProperty("java.io.tmpdir"));
                final File file = 
                    new File(tmpDir, RandomUtils.nextInt() +".torrent");
                final OutputStream fos = new FileOutputStream(file);
                IOUtils.copy(is, fos);
                fos.close();
                
                return file;
                }
            }
        catch (final HttpException e)
            {
            m_log.warn("HTTP Error", e);
            throw new IOExceptionWithCause("HTTP Error", e);
            }
        finally 
            {
            IOUtils.closeQuietly(is);
            method.releaseConnection();
            }
        }
    }
