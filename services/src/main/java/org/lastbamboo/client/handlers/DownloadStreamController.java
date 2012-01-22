package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.services.command.DownloadStreamCommand;
import org.lastbamboo.client.services.download.DownloadStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts a download and streams it back to the caller.
 */
public final class DownloadStreamController extends HttpServlet {
    
    private static final long serialVersionUID = 8923754912000536669L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        if (m_log.isDebugEnabled()) {
            m_log.debug("Method: {}", request.getMethod());
            m_log.debug("Full URL: {}", request.getRequestURL());
            m_log.debug("Protocol: {}", request.getProtocol());
            m_log.debug ("Initiating download with query: {}",
                request.getQueryString());
            ControllerUtils.printRequestHeaders(request);
        }

        //ControllerUtils.preventCaching(response);
        
        final DownloadStreamCommand downloadRequest = 
            new DownloadStreamCommand();
        ControllerUtils.populate(downloadRequest, request, "uri");
        
        final DownloadStreamHandler streamHandler =
            LittleShootModule.getDownloadStreamHandler();
        
        try {
            streamHandler.streamDownload(request, response, 
                downloadRequest, false, getServletContext());
        }
        catch (final Throwable t) {
            m_log.error("Exception during download", t);
            // We should respond with a 503 here.
        }
    }

}
