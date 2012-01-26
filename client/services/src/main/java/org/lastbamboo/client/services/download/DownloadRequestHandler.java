package org.lastbamboo.client.services.download;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lastbamboo.client.services.command.DownloadCommand;

/**
 * Class for handling download requests.
 */
public interface DownloadRequestHandler
    {

    void handleRequest(DownloadCommand downloadRequest,
        HttpServletRequest request, HttpServletResponse response, 
        ServletContext servletContext);

    void handleTorrentRequest(DownloadCommand downloadRequest,
            HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext);

    void startBitTorrentDownload(HttpServletRequest request, 
            DownloadCommand downloadRequest);

    }
