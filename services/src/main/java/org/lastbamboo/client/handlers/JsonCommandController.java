package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command controller that calls a service that provides JSON data.  This 
 * allows many new services to skip writing controllers and to simply write 
 * service classes that implement 
 * {@link JsonCommandProvider}.
 */
public class JsonCommandController extends HttpServlet {

    private static final long serialVersionUID = -4855130626053590380L;

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final JsonCommandProvider m_jsonProvider;
    
    /**
     * Creates a new controller for accessing SIP server addresses.
     * 
     * @param jsonProvider The class that provides data in JSON format.
     */
    public JsonCommandController(final JsonCommandProvider jsonProvider) {
        this.m_jsonProvider = jsonProvider;
    }

    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.trace("Handling JSON request: {}", request.getQueryString());
        ControllerUtils.preventCaching(response);
        final String data = this.m_jsonProvider.getJson(request);
        JsonControllerUtils.writeResponse(request, response, data);
    }
}
