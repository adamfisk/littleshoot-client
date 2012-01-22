package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.common.p2p.RawUdpServerDepot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for checking for incoming calls. The response is  
 * JSON-formatted and includes the IDs of incoming callers, if any.
 */
public class AllCallsController extends HttpServlet {

    private static final long serialVersionUID = 4446156163396990870L;
    
    /**
     * Logger for this class.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {

        final RawUdpServerDepot depot = 
            LittleShootModule.getRawUdpServerDepot();
        final JSONObject json = depot.toJson();
        
        JsonControllerUtils.writeResponse(request, response, json);
    }
}
