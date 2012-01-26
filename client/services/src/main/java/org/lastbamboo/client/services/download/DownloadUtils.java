package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.prefs.Preferences;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.client.services.command.DownloadCommand;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.download.MoverDState.Downloading;
import org.littleshoot.util.DefaultHttpClient;
import org.littleshoot.util.DefaultHttpClientImpl;
import org.littleshoot.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for downloads.
 */
public class DownloadUtils
    {


    private static final Logger LOG = 
        LoggerFactory.getLogger(DownloadUtils.class);
    
    public static File determineFile(final String name,
        final DownloadTracker<MoverDState<Sha1DState<MsDState>>, 
            Downloader<MoverDState<Sha1DState<MsDState>>>> tracker)
        {
        final File file = defaultFile(name);
        final File toUse;
        if (file.exists() || tracker.saveLocationTaken(file))
            {
            LOG.debug("File exists: " + file.exists());
            LOG.debug("Location taken: "+tracker.saveLocationTaken(file));
            final String newName =
                FilenameUtils.getBaseName(file.getName()) + "-"+
                (RandomUtils.nextInt() % 10000) + "." +
                FilenameUtils.getExtension(file.getName());
            toUse = new File(file.getParentFile(), newName);
            }
        else
            {
            toUse = file;
            }
        return toUse;
        }
    
    public static File determineFile(final DownloadCommand downloadRequest,
        final DownloadTracker<MoverDState<Sha1DState<MsDState>>,
            Downloader<MoverDState<Sha1DState<MsDState>>>> tracker)
        {
        final String name;
        final String requestName = downloadRequest.getName();
        if (StringUtils.isEmpty(requestName))
            {
            final URI uri = downloadRequest.getUri();
            name = uri.getRawPath();
            }
        else
            {
            name = requestName;
            }
        return determineFile(name, tracker);
        }

    public static File defaultFile(final String name)
        {
        final String baseName;
        if (name.contains("/")) 
            {
            baseName = StringUtils.substringAfterLast(name, "/");
            }
        else
            {
            baseName = name;
            }
        final String fileName = 
            FileUtils.removeIllegalCharsFromFileName(baseName);
        final Preferences prefs = Preferences.userRoot ();
        final File downloadsDir = 
            new File(prefs.get (PrefKeys.DOWNLOAD_DIR, ""));
        if (!downloadsDir.isDirectory())
            {
            LOG.error("Downloads dir does not exist at: {}", downloadsDir);
            if (!downloadsDir.mkdirs())
                {
                LOG.error("Still could not make downloads dir: {}", downloadsDir);
                }
            }
        return new File (downloadsDir, fileName);
        }
    
    /**
     * Returns whether a given state is the moved state.
     * 
     * @param <DsT> The type of the underlying delegate downloading state.
     * @param state The state.
     * @return True if the state is the moved state, false otherwise.
     */
    public static <DsT> boolean isMoved (final MoverDState<DsT> state)
        {
        final MoverDState.VisitorAdapter<Boolean,DsT> visitor =
            new MoverDState.VisitorAdapter<Boolean,DsT> (Boolean.FALSE)
            {
            @Override
            public Boolean visitMoved (
                final MoverDState.Moved<DsT> downloadingState)
                {
                return Boolean.TRUE;
                }
            };
            
        return state.accept (visitor).booleanValue();
        }
    
    
    /**
     * Returns whether a given state is the downloading state.
     * 
     * @param <DsT> The type of the underlying delegate downloading state.
     * @param state The state.
     * @return True if the state is the downloading state, false otherwise.
     */
    public static <DsT> boolean isDownloading (final MoverDState<DsT> state)
        {
        final MoverDState.VisitorAdapter<Boolean,DsT> visitor =
            new MoverDState.VisitorAdapter<Boolean,DsT> (Boolean.FALSE)
            {
            @Override
            public Boolean visitDownloading (
                final Downloading<DsT> downloadingState)
                {
                return Boolean.TRUE;
                }
            };
            
        return state.accept (visitor).booleanValue();
        }

    /**
     * Utility method to download the torrent file.
     * 
     * @param uri The URI for the file.
     * @return The downloaded {@link File}.
     * @throws IOException If there's an IO error on the network or writing to
     * disk.
     */
    public static File downloadTorrentFile(final String uri) throws IOException
        {
        final GetMethod method = new GetMethod(uri);
        method.addRequestHeader("connection", "close");
        
//        final HttpParams params = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(params, 40 * 1000);
//        HttpConnectionParams.setSoTimeout(params, 30 * 1000);
//        HttpClientParams.setRedirecting(params, true);
        
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
                LOG.debug("Unzipping body...");
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
                LOG.warn(msg);
                throw new IOException(msg);
                }
            if (statusCode != HttpStatus.SC_OK)
                {
                final String msg = "NO 200 OK: " + method.getURI() + "\n" +
                    statusLine + "\n" + IOUtils.toString(is);
                LOG.warn(msg);
                throw new IOException(msg);
                }
            else
                {
                LOG.debug("Got 200 response...");
                
                final File tmpDir = 
                    new File(System.getProperty("java.io.tmpdir"));
                final File file = 
                    new File(tmpDir, RandomUtils.nextInt() +".torrent");
                final OutputStream fos = new FileOutputStream(file);
                IOUtils.copy(is, fos);
                IOUtils.closeQuietly(fos);
                
                return file;
                }
            }
        catch (final HttpException e)
            {
            LOG.warn("HTTP Error", e);
            throw new IOExceptionWithCause("HTTP Error", e);
            }
        finally 
            {
            IOUtils.closeQuietly(is);
            method.releaseConnection();
            }
        }

    public static HeadMethod issueHeadRequest(final URI uri, 
        final HttpServletResponse response)
        {
        // With HTTP URIs, we issue a HEAD request to get the response
        // headers to send.
        final DefaultHttpClient client = new DefaultHttpClientImpl();
        final HeadMethod method = new HeadMethod(uri.toASCIIString());
        try
            {
            final int responseCode = client.executeMethod(method);
            response.setStatus(method.getStatusCode());
            final Header[] headers = method.getResponseHeaders();
            for (final Header header : headers)
                {
                response.setHeader(header.getName(), header.getValue());
                }
            
            if (responseCode > 299 || responseCode < 200)
                {
                // The caller will just get the data from the HEAD request
                // in this case.
                LOG.warn("Unexpected response code from HTTP download: " +
                    responseCode);
                return null;
                }
            else
                {
                return method;
                }
            }
        catch (final HttpException e)
            {
            return null;
            }
        catch (final IOException e)
            {
            return null;
            }
        finally
            {
            method.releaseConnection();
            }
        }
    }
