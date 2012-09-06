package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.security.auth.login.CredentialException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.common.sip.stack.SipUriFactory;
import org.littleshoot.commom.xmpp.XmppP2PClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for user logins.
 */
public class LoginController extends HttpServlet {
    
    private static final long serialVersionUID = 414801969218563944L;

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Override
    protected void doGet(final HttpServletRequest request, 
            final HttpServletResponse response) throws ServletException, 
            IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        log.info("Processing login request...");
        final String username = request.getParameter("username");
        final String password = request.getParameter("password");
        log.info("Got password: {}", password);
        final String server = request.getParameter("server");
        final String port = request.getParameter("port");
        if (StringUtils.isBlank(username)) {
            error(request, response, 
                "'username' and 'password' arguments required");
            return;
        }
        if (StringUtils.isBlank(password)) {
            error(request, response, 
                "'username' and 'password' arguments required");
            return;
        }
        if (StringUtils.isBlank(server)) {
            error(request, response, "'server' argument required");
            return;
        }
        final XmppP2PClient p2pClient = LittleShootModule.getP2PXmppClient();
        
        if (!p2pClient.isLoggedOut()) {
            p2pClient.logout();
        }
        
        final JSONObject json = new JSONObject();
        // Note the following call *will not* login again if it's already
        // logged in.
        try {
            final String userName;
            if (StringUtils.isBlank(port)) {
                userName = p2pClient.login(username, password, server);
            } else {
                userName = p2pClient.login(username, password, server, 
                    Integer.parseInt(port), "");
            }
            //p2pClient.login(username, password);
            json.put("success", true);
            json.put("uri", userName);
                //SipUriFactory.createSipUri(username).toASCIIString());
            log.info("Logged in");
            JsonControllerUtils.writeResponse(request, response, json);
        } catch (final IOException e) {
            log.warn("Could not log in!", e);
            error(request, response, e.getMessage());
        } catch (final CredentialException e) {
            log.warn("Could not log in!", e);
            error(request, response, e.getMessage());
        }
        
    }

    private void error(final HttpServletRequest request, 
        final HttpServletResponse response, final String msg) 
        throws IOException {
        final JSONObject json = new JSONObject();
        json.put("success", false);
        json.put("reason", msg);
        JsonControllerUtils.writeResponse(request, response, json);
    }
}
