package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.client.LittleShootModule;
import org.littleshoot.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for writing call data for a specific call.
 */
public class WriteCallDataController extends HttpServlet {

    private static final long serialVersionUID = 8962523578772931518L;
    
    /**
     * Logger for this class.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    
    @Override
    protected void doPost(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        final String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                "URI required.");
            return;
        }

        ControllerUtils.printRequestHeaders(request);

        final int cl = request.getContentLength();
        if (cl == -1) {
            response.sendError(HttpServletResponse.SC_LENGTH_REQUIRED, 
                "The Content-Length header is required.");
            return;
        }
        final InputStream requestInputStream = request.getInputStream();
        
        final Socket sock = 
            LittleShootModule.getRawUdpServerDepot().getSocket(id);
        if (sock == null) {
            log.warn("Call not found!!");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                "Call still connecting?");
            return;
        }
        //IOUtils.toByteArray(requestInputStream);
        
        final byte[] data = IoUtils.toByteArray(requestInputStream, cl);
        
        log.info("Writing raw data: {}", new String(data));
        
        LittleShootModule.getRawUdpServerDepot().write(id, data);
        
    }
}
