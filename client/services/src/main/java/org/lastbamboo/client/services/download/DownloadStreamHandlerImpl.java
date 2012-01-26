package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpHeaders;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.services.command.DownloadStreamCommand;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.littleshoot.util.IoUtils;
import org.littleshoot.util.MimeType;
import org.littleshoot.util.RuntimeIoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class for streaming downloads.
 */
public class DownloadStreamHandlerImpl implements DownloadStreamHandler
    {
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public void streamDownload(final HttpServletRequest request,
        final HttpServletResponse response,
        final DownloadStreamCommand downloadRequest,
        final boolean responseSet, final ServletContext servletContext) {
        final URI uri = downloadRequest.getUri();
        if (LittleShootModule.getDownloadTracker().hasActiveDownloader(uri)) {

            final Downloader<MoverDState<Sha1DState<MsDState>>> dl = 
                LittleShootModule.getDownloadTracker().getActiveDownloader(uri);

            if (!responseSet) {
                m_log.debug("Setting response data...");
                // This should be an OK response and not partial content 
                // because we're trying to serve the whole file.
                response.setStatus(HttpStatus.SC_OK);
                final MimeType mt = new MimeType() {
                    public String getMimeType(final String fileName) {
                        // If we need to define more mime types, add them
                        // to web.xml.
                        return servletContext.getMimeType(fileName);
                    }
                };
                final String contentType = mt.getMimeType(dl.getFinalName());

                if (StringUtils.isNotBlank(contentType)) {
                    response.setContentType(contentType);
                    if (contentType.equalsIgnoreCase(
                        "application/x-bittorrent")) {
                        m_log.warn("setting content type to torrent??");
                    }
                    m_log.debug("Set content type to: {}", contentType);
                } else {
                    m_log.warn("No content type for file: {}",
                            dl.getFinalName());
                }

                final String contentLength = String.valueOf(dl.getSize());
                m_log.debug("Set content length to: {}", contentLength);
                response.setHeader("Content-Length", contentLength);
                final long date = System.currentTimeMillis();
                response.setDateHeader(HttpHeaders.LAST_MODIFIED, 
                    dl.getStartTime());
                response.setDateHeader(HttpHeaders.DATE, date);
                response.setHeader(HttpHeaders.CONNECTION, "close");
            }
            
            // We do the following to make sure the caller gets at least
            // some data ASAP.
            try {
                response.flushBuffer();
            } catch (final IOException e) {
                m_log.warn("Exception flushing response buffer", e);
                return;
            }

            if (request.getMethod().equalsIgnoreCase("HEAD")) {
                m_log.info("Not sending body with HEAD request");
                return;
            }
            
            final OutputStream os;
            try {
                os = response.getOutputStream();
            } catch (final IOException e) {
                m_log.warn("Could not get outputstream", e);
                return;
            }
            
            final Enumeration<String> reqRanges = request.getHeaders(HttpHeaders.RANGE);
            if (reqRanges != null && reqRanges.hasMoreElements()) {
                // Parse the satisfiable ranges
                /*
                List ranges =InclusiveByteRange.satisfiableRanges(reqRanges,content_length);

                //  if there are no satisfiable ranges, send 416 response
                if (ranges==null || ranges.size() == 0) {
                    writeHeaders(response, content, content_length);
                    response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    response.setHeader(HttpHeaders.CONTENT_RANGE,
                        InclusiveByteRange.to416HeaderRangeString(content_length));
                    resource.writeTo(os,0,content_length);
                    return;
                }

                //  if there is only a single valid range (must be satisfiable
                //  since were here now), send that range with a 216 response
                if (ranges.size()== 1) {
                    InclusiveByteRange singleSatisfiableRange =
                        (InclusiveByteRange)ranges.get(0);
                    long singleLength = singleSatisfiableRange.getSize(content_length);
                    writeHeaders(response,content,singleLength                     );
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    response.setHeader(HttpHeaders.CONTENT_RANGE,
                        singleSatisfiableRange.toHeaderRangeString(content_length));
                    resource.writeTo(os,singleSatisfiableRange.getFirst(content_length),singleLength);
                    return;
                }
                */
            }
            
            try {
                dl.write(os, downloadRequest.isCancelOnStreamClose());
                m_log.debug("Finished writing response");
                return;
            } catch (final RuntimeIoException e) {
                m_log.debug("RuntimeIoException streaming download.  "
                        + "Download canceled?", e);
                return;
            } catch (final RuntimeException e) {
                m_log.warn("Runtime exception streaming download", e);
                return;
            } finally {
                IOUtils.closeQuietly(os);
            }
        } else {
            m_log.debug("Could not find downloader for URI: " + uri + ", "
                    + "attempting to stream from local file...");
            if (!streamFromLocalFile(downloadRequest, request, response,
                    servletContext)) {
                m_log.warn("Could not handle download in any form!!");
            }
            return;
        }
    }
    
    public boolean streamFromLocalFile(
        final DownloadStreamCommand downloadRequest,
        final HttpServletRequest request,
        final HttpServletResponse response,
        final ServletContext servletContext) {
        final URI uri = downloadRequest.getUri();
        final File mappedFile = LittleShootModule.getFileMapper().getFile(uri);

        if (mappedFile == null || !mappedFile.isFile()) {
            m_log.debug("File not mapped...");
            final String name = downloadRequest.getName();
            if (StringUtils.isBlank(name)) {
                return false;
            }
            final File defaultFileOrFolder = DownloadUtils.defaultFile(name);
            if (defaultFileOrFolder.isDirectory()) {
                m_log.debug("Can't 'stream' folder");
                return false;
            } else if (defaultFileOrFolder.isFile()) {
                m_log.debug("Found file: {}", defaultFileOrFolder);
                final long length = downloadRequest.getSize();
                if (length == defaultFileOrFolder.length()) {
                    m_log.debug("Streaming local file anyway - same length...");
                    return writeFile(defaultFileOrFolder, response,
                            servletContext);
                } else {
                    return false;
                }
            } else {
                m_log.debug("Could not find unmapped file or folder either.");
                return false;
            }
        } else {
            m_log.debug("Found file in mapper at: {}", mappedFile);
            // I think we do this for canonical path reasons...
            final File file = new File(mappedFile.getPath());
            if (!file.isFile())
                return false;
            return writeFile(file, response, servletContext);
        }
    }

    private boolean writeFile(final File defaultFile,
        final HttpServletResponse response,
        final ServletContext servletContext) {
        final String mimeType = 
            servletContext.getMimeType(defaultFile.getName());
        m_log.debug("Setting Content-Type to: " + mimeType);
        if (StringUtils.isNotBlank(mimeType)) {
            response.setContentType(mimeType);
        }

        response.setStatus(HttpStatus.SC_OK);
        response.setHeader("Content-Length",
                String.valueOf(defaultFile.length()));
        response.setDateHeader("Last-Modified", defaultFile.lastModified());

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(defaultFile);
            os = response.getOutputStream();
            IoUtils.copy(is, os, defaultFile.length());
            // IOUtils.copy(is, os);
            return true;
        } catch (final IOException e) {
            m_log.debug("Exception streaming file", e);
            // We found the file, but an error occurred. Still return true.
            return true;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }
}
