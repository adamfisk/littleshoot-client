package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.common.offer.answer.NoAnswerException;
import org.lastbamboo.common.p2p.RawUdpServerDepot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for creating a raw UDP connection to a remote host. This will
 * simply copy raw data in the body of the HTTP request to the remote peer
 * in addition to copying incoming data to the HTTP response body.
 */
public class StartCallController extends HttpServlet {

    /**
     * Generated ID.
     */
    private static final long serialVersionUID = -3038620954415040293L;

    /**
     * Logger for this class.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        final String uriStr = request.getParameter("uri");
        if (StringUtils.isBlank(uriStr)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                "URI required.");
            return;
        }
        final UUID id = UUID.randomUUID();
        final URI uri = URI.create(uriStr);
        final String url = id.toString();
        final Runnable runner = new Runnable() {
            
            public void run() {
                final RawUdpServerDepot depot = 
                    LittleShootModule.getRawUdpServerDepot();
                
                // TODO: Change all calls to use the state pattern!!
                try {
                    // TODO: We currently use a reliable socket because we
                    // still want to use TCP connections if that's all we
                    // can get, and everything's not quite in place for purely
                    // unreliable UDP-based connections.
                    final Socket sock = 
                        LittleShootModule.getP2PSipClient().newUnreliableSocket(uri);
                    depot.addSocket(url, sock);
                } catch (final NoAnswerException e) {
                    log.info("Issue starting call", e);
                    depot.addError(url, e.getMessage());
                } catch (final IOException e) {
                    log.info("Issue starting call", e);
                    depot.addError(url, e.getMessage());
                }
            }
        };
        final Thread t = new Thread(runner, "Start-Call-Thread");
        t.setDaemon(true);
        t.start();
        final JSONObject json = new JSONObject();
        json.put("id", url);
        JsonControllerUtils.writeResponse(request, response, json);
    }
}
