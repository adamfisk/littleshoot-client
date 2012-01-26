package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.services.command.DownloadCommand;
import org.lastbamboo.client.services.download.DownloadRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts a download and streams it back to the caller.
 */
public final class DownloadTorrentController extends HttpServlet {
    
    private static final long serialVersionUID = 4051917802341055715L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        
        if (m_log.isDebugEnabled()) {
            m_log.debug ("Initiating download with query: {}", 
                request.getQueryString());
            ControllerUtils.printRequestHeaders(request);
        }

        final DownloadCommand downloadRequest = new DownloadCommand();
        ControllerUtils.populate(downloadRequest, request, "uri");
        
        final DownloadRequestHandler requestHandler =
            LittleShootModule.getDownloadRequestHandler();
        try {
            requestHandler.handleTorrentRequest(downloadRequest, 
                request, response, getServletContext());
        }
        catch (final Throwable t) {
            m_log.error("Exception during download", t);
            // We should respond with a 503 here.
        }
    }

}
