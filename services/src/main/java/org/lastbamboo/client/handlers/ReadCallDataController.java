package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.client.LittleShootModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for reading call data.
 */
public class ReadCallDataController extends HttpServlet {

    
    private static final long serialVersionUID = -4309618597270600103L;
    
    /**
     * Logger for this class.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) {
        log.info("Reading call data with request arguments...");
        final String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "id argument required.");
            } catch (final IOException e) {
                log.error("Could not send error?", e);
            }
            return;
        }
        
        final String length = request.getParameter("length");
        if (StringUtils.isBlank(length)) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Desired length is required in the length argument.");
            } catch (final IOException e) {
                log.error("Could not send error?", e);
            }
            return;
        }

        final int len = Integer.parseInt(length);
        final long read;
        try {
            read = LittleShootModule.getRawUdpServerDepot().read(id.trim(), 
                response.getOutputStream(), len);
        } catch (final IOException e) {
            log.warn("Exception on read", e);
            try {
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
                    "Error reading from the stream: "+e.getMessage());
            } catch (final IOException ioe) {
                log.error("Could not send error?", ioe);
            }
            return;
        }
        log.info("Bytes read: {}", read);
        if (read > -1) {
            response.setContentLength((int) read);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        log.info("Finished read call");
    }
}
