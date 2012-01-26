package org.lastbamboo.client.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller that calls a service that provides JSON data.  This always
 * returns compressed JSON.  This allows many new services to skip writing
 * controllers and to simply write service classes that implement 
 * {@link JsonProvider}.
 */
public class JsonController extends HttpServlet {

    private static final long serialVersionUID = 1530593557445681080L;

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final JsonProvider m_jsonProvider;
    
    /**
     * Creates a new controller for accessing SIP server addresses.
     * 
     * @param jsonProvider The class that provides data in JSON format.
     */
    public JsonController(final JsonProvider jsonProvider) {
        this.m_jsonProvider = jsonProvider;
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.trace("Handling JSON request: {}", request.getQueryString());

        ControllerUtils.preventCaching(response);
        final String data = this.m_jsonProvider.getJson();
        JsonControllerUtils.writeResponse(request, response, data);
    }
}
