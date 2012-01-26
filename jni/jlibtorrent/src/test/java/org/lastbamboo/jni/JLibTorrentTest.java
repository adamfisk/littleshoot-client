package org.lastbamboo.jni;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.littleshoot.util.DefaultHttpClient;
import org.littleshoot.util.DefaultHttpClientImpl;
import org.littleshoot.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JLibTorrentTest
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
     @Test public void testTorrent() throws Exception
        {
        final File pwd = new File("../../lib");
        
        final File fullFile = 
            new File (pwd.getCanonicalFile(), System.mapLibraryName("jnltorrent"));
        
        final String fullPath = fullFile.getCanonicalPath();
        final JLibTorrent torrent = 
            new JLibTorrent(fullPath, CommonUtils.isPro());
        Thread.sleep(4000);
        torrent.updateSessionStatus();
        
        Thread.sleep(10000);
        /*
        final String uri = "http://stealthisfilm.com/torrent/StealThisFilmII.iPod.torrent";
        final File savePath = new File(".").getCanonicalFile();
        final File torrentFile = downloadTorrentFile(uri);
        torrent.download(savePath, torrentFile, false, -1);
        
        
        for (int i = 0; i< 6; i++)
            {
            System.out.println("Calling process events");
            final int state = 
                torrent.getStateForTorrent(torrentFile);
            m_log.debug("In state: " + state);
            Thread.sleep(100);
            }

    */
        System.out.println("Stopping torrent...");
        torrent.stopLibTorrent();
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
            /*
            final byte[] body = IOUtils.toByteArray(is);
            if (body.length == 0)
                {
                // Could easily be a post request, which would not have a body.
                m_log.debug("No response body.  Post request?");
                throw new IOException("Empty response");
                }
                */
            
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
                /*
                final Object metaInfo = Token.parse(body);
                
                if(!(metaInfo instanceof Map))
                    throw new ValueException("metaInfo not a Map!");
                
                m_log.debug("Got torrent map:\n {} ", metaInfo);
                //final BTData data = new BTDataImpl((Map<?, ?>) metaInfo);
                return data;
                */
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
