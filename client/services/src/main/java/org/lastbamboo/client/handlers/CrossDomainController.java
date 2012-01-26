package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for responding to crossdomain.xml requests.  This isn't just a 
 * straight static handler because Flash caches 404s for crossdomain.xml, and
 * we need to be able to reload it.  We add random naming to get around the
 * caching, so it has to be dynamic.
 */
public class CrossDomainController extends HttpServlet {

    private static final long serialVersionUID = -8199205390867458200L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        if (m_log.isDebugEnabled()) {
            m_log.debug("Request URL: {}", request.getRequestURL());
            m_log.debug("Handling crossdomain-xxx.xml request: {}",
                request.getQueryString());
            ControllerUtils.printRequestHeaders(request);
        }
        ControllerUtils.preventCaching(response);

        final StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>\n");
        sb.append("<!DOCTYPE cross-domain-policy\n");
        sb.append("SYSTEM \"http://www.adobe.com/xml/dtds/cross-domain-policy.dtd\">\n");
        sb.append("<cross-domain-policy>\n");
        sb.append("<allow-access-from domain=\"*\" to-ports=\"8107\"/>\n");
        sb.append("<allow-http-request-headers-from domain=\"*\" headers=\"*\"/>");
        
        if (StringUtils.isBlank(request.getQueryString())) {
            m_log.debug("Request for master policy file...");
            sb.append("<site-control permitted-cross-domain-policies=\"all\"/>");
            //sb.append("<site-control permitted-cross-domain-policies=\"by-content-type\"/>\n");
        }
            
        sb.append("</cross-domain-policy>");
        
        try {
            final byte[] content = sb.toString().getBytes("UTF-8");
            response.setContentType("text/x-cross-domain-policy");
            response.setContentLength(content.length);
            response.addHeader("X-Permitted-Cross-Domain-Policies", "all");
            final OutputStream os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        }
        catch (final IOException e) {
            m_log.warn("Could not write domain file", e);
        }
    }

}
