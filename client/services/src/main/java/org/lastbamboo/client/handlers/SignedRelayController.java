package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.lastbamboo.client.prefs.Prefs;
import org.lastbamboo.client.services.SessionAttributeKeys;
import org.lastbamboo.client.services.command.SignedRelayCommand;
import org.littleshoot.util.DefaultHttpClient;
import org.littleshoot.util.DefaultHttpClientImpl;
import org.littleshoot.util.HttpParamKeys;
import org.littleshoot.util.ShootConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller that takes a url argument and forwards requests to that
 * URL.  It takes care of signing the request along the way.  The added 
 * signature is the reason this controller exists, allowing, for example, calls
 * to the LittleShoot web service over HTTP connections as opposed to HTTPS.
 */
public class SignedRelayController extends SignedController {

    private static final long serialVersionUID = -2283578229689188123L;

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final DefaultHttpClient m_clientManager = 
        new DefaultHttpClientImpl();

    @Override
    protected void handleSigned(final HttpServletRequest request,
        final HttpServletResponse response) {
        if (!ControllerUtils.verifyMethod(request, response, "GET", "POST")) {
            return;
        }

        final SignedRelayCommand bean = new SignedRelayCommand();
        ControllerUtils.populate(bean, request);
        final String path = bean.getPath();
        final String baseUrl = ShootConstants.SERVER_URL + "/api/" + path;
        final Map<String, String> params = ControllerUtils.toParamMap(request);
        params.remove(HttpParamKeys.SIGNATURE);
        params.remove("path");
        params.put(HttpParamKeys.INSTANCE_ID, String.valueOf(Prefs.getId()));
        final String url;
        try {
            url = ControllerUtils.sign(request, baseUrl, params);
        } catch (final IOException e) {
            try {
                m_log.debug("Sending forbidden error");
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Signing error");
            } catch (final IOException e1) {
                m_log.warn("Could not send error", e1);
            }
            return;
        }

        final HttpMethod method = new PostMethod(url);

        final Map<String, String> cookieMap = 
            ControllerUtils.createCookieMap(request);
        final String sessionId = cookieMap.get(SessionAttributeKeys.SESSION_ID);
        // method.setRequestHeader("Cookie", "JSESSIONID="+sessionId);
        method.setRequestHeader("Cookie", "sessionid=" + sessionId);
        m_log.debug("Writing request with query: {}", method.getQueryString());

        try {
            m_clientManager.executeMethod(method);
            final int statusCode = method.getStatusCode();
            final InputStream is = method.getResponseBodyAsStream();
            final byte[] body = IOUtils.toByteArray(is);
            m_log.debug("Read whole body: {}", new String(body));
            if (statusCode < 200 || statusCode > 299) {
                m_log.warn("Did not get 200.  Received "
                        + method.getStatusLine() + "\n"
                        + new String(body, "UTF-8"));
            }

            response.setStatus(method.getStatusCode());
            final Header[] headers = method.getResponseHeaders();
            for (final Header header : headers) {
                response.setHeader(header.getName(), header.getValue());
            }
            final OutputStream os = response.getOutputStream();

            m_log.debug("Writing success data.");
            os.write(body);
            os.flush();
            m_log.debug("Wrote all success data.");
        } catch (final HttpException e) {
            try {
                response.sendError(method.getStatusCode());
            } catch (final IOException e1) {
                m_log.warn("Could not send error", e1);
            }
        } catch (final IOException e) {
            m_log.warn("Could not send request", e);
        } finally {
            method.releaseConnection();
        }

        // Note: We don't need to use JsonControllerUtils here to return the
        // JSON response because the server is actually responsible for writing
        // the JSON, and this class just relays it!!
        m_log.debug("Returning!!");
    }
}
