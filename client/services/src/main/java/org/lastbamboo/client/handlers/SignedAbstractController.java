package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract controller for any controllers that want to verify the signature
 * of a request.
 */
public abstract class SignedAbstractController extends HttpServlet {

    private static final long serialVersionUID = -2675521734850118598L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.debug("Received request: {}", request.getQueryString());
        ControllerUtils.preventCaching(response);
        ControllerUtils.printCookies(request);

        if (!verifySignature(request)) {
            m_log.warn("Did not verify signature!");
            response.sendError(403, "Signature Mismatch");
            return;
        }
        handleSigned(request, response);
    }

    private boolean verifySignature(final HttpServletRequest request) {
        if (!signatureMatches(request)) {
            return false;
        }
        return true;
    }

    private boolean signatureMatches(final HttpServletRequest request) {
        final Map<String, String> cookieMap = ControllerUtils
                .createCookieMap(request);
        final String key = cookieMap.get("siteKey");
        return ControllerUtils.signatureMatches(request, key);
    }

    protected abstract void handleSigned(final HttpServletRequest request,
        final HttpServletResponse response) throws IOException,
        ServletException;
}
