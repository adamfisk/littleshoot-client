package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for responding to requests for directory listings.
 */
public class DirectoryRedirectController extends HttpServlet {

    private static final long serialVersionUID = -3197152177400424192L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = 
        LoggerFactory.getLogger(DirectoryRedirectController.class); 

    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.debug("Handling directory request query string: {}",
            request.getQueryString());
        m_log.debug("Handling directory request uri: {}",
            request.getRequestURI());   
        response.sendRedirect("http://www.littleshoot.org");
    }
}
