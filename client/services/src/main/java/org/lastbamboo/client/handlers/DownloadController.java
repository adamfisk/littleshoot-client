package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.services.command.DownloadCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Starts a download and streams it back to the caller.
 */
public final class DownloadController extends HttpServlet {

    private static final long serialVersionUID = 3497817998363172826L;

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.debug("Downloading with query: {}", request.getQueryString());
        if (m_log.isDebugEnabled()) {
            ControllerUtils.printRequestHeaders(request);
        }
        final Map<String, String> params = ControllerUtils.toParamMap(request);
        final DownloadCommand dc = new DownloadCommand();
        ControllerUtils.populate(dc, params, Sets.newHashSet("uri"));
        try {
            LittleShootModule.getDownloadRequestHandler().handleRequest(dc, 
                request, response, getServletContext());
        } catch (final Throwable t) {
            m_log.error("Exception during download", t);
            // We should respond with a 503 here.
        }
    }
}
