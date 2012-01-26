package org.lastbamboo.client.services.download;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lastbamboo.client.services.command.DownloadStreamCommand;

/**
 * Handler for streaming downloads.
 */
public interface DownloadStreamHandler
    {

    public void streamDownload(HttpServletRequest request,
        HttpServletResponse response,
        DownloadStreamCommand downloadRequest, 
        boolean responseSet, ServletContext servletContext);

    /**
     * Attempts to stream the file from our repository.  This only works if
     * we've previously downloaded the file.
     * 
     * @param downloadRequest The data for the local resource.
     * @param request The servlet request.
     * @param response The HTTP response object.
     * @param servletContext The context for the servlet.
     * @return <code>true</code> if we found the file locally and attempted
     * to stream it, otherwise <code>false</code>.
     */
    public boolean streamFromLocalFile(DownloadStreamCommand downloadRequest, 
        HttpServletRequest request, HttpServletResponse response, 
        ServletContext servletContext);

    }
