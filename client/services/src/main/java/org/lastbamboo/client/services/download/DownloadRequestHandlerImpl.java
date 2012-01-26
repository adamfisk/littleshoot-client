package org.lastbamboo.client.services.download;

import java.io.File;
import java.net.URI;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.lang.StringUtils;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.handlers.ControllerUtils;
import org.lastbamboo.client.services.command.DownloadCommand;
import org.littleshoot.util.ShootConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for handling requests for downloads.
 */
public class DownloadRequestHandlerImpl implements DownloadRequestHandler {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    public void handleTorrentRequest(final DownloadCommand downloadRequest,
        final HttpServletRequest request,
        final HttpServletResponse response,
        final ServletContext servletContext) {

        if (LittleShootModule.getDownloadStreamHandler().streamFromLocalFile(
                downloadRequest, request, response, servletContext)) {
            m_log.debug("Streamed from local file...");
            return;
        }

        else {
            downloadTorrent(downloadRequest, request, response, servletContext);
        }
    }

    private void downloadTorrent(final DownloadCommand downloadRequest,
        final HttpServletRequest request,
        final HttpServletResponse response,
        final ServletContext servletContext) {
        startBitTorrentDownload(request, downloadRequest);
        LittleShootModule.getDownloadStreamHandler().streamDownload(request,
                response, downloadRequest, false, servletContext);
    }

    public void handleRequest(final DownloadCommand downloadRequest,
        final HttpServletRequest request,
        final HttpServletResponse response,
        final ServletContext servletContext) {
        if (LittleShootModule.getDownloadStreamHandler().streamFromLocalFile(
                downloadRequest, request, response, servletContext)) {
            m_log.debug("Streamed from local file...");
            return;
        }

        else {
            download(downloadRequest, request, response, servletContext);
        }
    }

    private void download(final DownloadCommand downloadRequest,
            final HttpServletRequest request,
            final HttpServletResponse response,
            final ServletContext servletContext) {
        final String source = downloadRequest.getSource();
        final File toUse = DownloadUtils.determineFile(downloadRequest,
            LittleShootModule.getDownloadTracker());
        final URI uri = downloadRequest.getUri();
        if (uri.getScheme().equalsIgnoreCase("http")) {
            final HeadMethod method = DownloadUtils.issueHeadRequest(uri,
                    response);
            if (method != null) {
                final Map<String, String> paramMap = createParamMap(request);
                final long contentLength = method.getResponseContentLength();
                LittleShootModule.getStartDownloadService().download(uri,
                        (URI) null, toUse.getName(), contentLength, paramMap,
                        ControllerUtils.createCookieMap(request));
                LittleShootModule.getDownloadStreamHandler().streamDownload(
                        request, response, downloadRequest, true,
                        servletContext);
                return;
            } else {
                return;
            }
        } else if (StringUtils.isEmpty(source)
                || source.equalsIgnoreCase("littleshoot")) {
            m_log.debug("Performing LittleShoot download...");
            startLittleShootDownload(toUse, request, downloadRequest);
            LittleShootModule.getDownloadStreamHandler().streamDownload(
                    request, response, downloadRequest, false, servletContext);
            return;
        } else if (source.equalsIgnoreCase("limewire")) {
            m_log.debug("Performing LimeWire download...");
            startLimeWireDownload(toUse, request, downloadRequest);
            LittleShootModule.getDownloadStreamHandler().streamDownload(
                    request, response, downloadRequest, false, servletContext);
            return;
        } else {
            m_log.debug("Could not handle source: {}", source);
            return;
        }
    }

    public void startBitTorrentDownload(final HttpServletRequest request,
        final DownloadCommand downloadRequest) {
        final String uri = downloadRequest.getUri().toASCIIString();

        LittleShootModule.getStartDownloadService().bitTorrentDownload(uri,
                ControllerUtils.toParamMap(request),
                ControllerUtils.createCookieMap(request));
    }

    private void startLimeWireDownload(final File completeFile,
        final HttpServletRequest request,
        final DownloadCommand downloadRequest) {
        final URI sha1 = downloadRequest.getUrn();
        final long size = downloadRequest.getSize();

        LittleShootModule.getStartDownloadService().limeWireDownload(
            completeFile, sha1, size, ControllerUtils.toParamMap(request),
            ControllerUtils.createCookieMap(request));
    }

    private void startLittleShootDownload(final File file,
        final HttpServletRequest request,
        final DownloadCommand downloadRequest) {
        String groupName = downloadRequest.getGroupName();
        if (StringUtils.isBlank(groupName)) {
            groupName = ShootConstants.WORLD_GROUP;
        }
        final Map<String, String> paramMap = createParamMap(request);
        final URI uri = downloadRequest.getUri();
        LittleShootModule.getStartDownloadService().download(uri,
            downloadRequest.getUrn(), file.getName(),
            downloadRequest.getSize(), paramMap,
            ControllerUtils.createCookieMap(request));
    }

    private Map<String, String> createParamMap(final HttpServletRequest request) {
        final Map<String, String> paramMap = 
            ControllerUtils.toParamMap(request);
        paramMap.remove("signature");
        paramMap.remove("callback");
        return paramMap;
    }
}
