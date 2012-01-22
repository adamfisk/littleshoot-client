package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for clearing inactive downloads.
 */
public class ClearInactiveDownloadsController extends HttpServlet
    {

    private static final long serialVersionUID = 213662822586820309L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.debug("Handling clear inactive downloads query string: {}",
            request.getQueryString());
        
        LittleShootModule.getDownloadTracker().clearInactive();
        final JSONObject json = new JSONObject();
        JsonControllerUtils.writeResponse(request, response, json);
    }
}
