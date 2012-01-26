package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.littleshoot.util.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for getting listings of shared files.
 */
public class PublishedFilesController extends HttpServlet {

    private static final long serialVersionUID = 1392978322302262448L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.trace("Handling JSON request: {}", request.getQueryString());
        
        ControllerUtils.preventCaching(response);
        
        final MimeType mt = new MimeType() {
            public String getMimeType(final String fileName) {
                return getServletContext().getMimeType(fileName);
            }
        };
        final JSONObject json = 
            LittleShootModule.getPublishedFilesTracker().getPublishedFiles(mt);
        
        JsonControllerUtils.writeResponse(request, response, json);
    }
}
