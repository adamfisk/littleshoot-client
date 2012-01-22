package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a filter to make sure requests are coming from localhost.  
 * If requests are not from localhost, this returns an error message in JSON.
 * <p>
 * 
 * This check combined with the same-origin policy of the browser offers 
 * security against many forms of attacks such as a web page attempting to
 * publish a sensitive file on a user's machine without the user's knowledge.
 */
public class LocalHostFilter implements Filter
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public void doFilter(final ServletRequest servletRequest, 
        final ServletResponse servletResponse,
        final FilterChain filterChain) throws IOException, ServletException
        {
        final HttpServletRequest request =
            (HttpServletRequest) servletRequest;
    
        final HttpServletResponse response =
            (HttpServletResponse) servletResponse;
        m_log.debug("Handling request: {}", request.getQueryString());
        final String requestOrigin = request.getRemoteAddr();
        m_log.debug("Request origin is: "+requestOrigin);
        if (!requestOrigin.startsWith("127"))
            {
            writeErrorCallback(request, response);
            }
        else
            {
            filterChain.doFilter (request, response);
            }
        }

    private void writeErrorCallback(final HttpServletRequest request, 
        final HttpServletResponse response) 
        throws UnsupportedEncodingException, IOException
        {
        final String functionName = request.getParameter("callback");
        m_log.trace("Callback function: "+functionName);
        final JSONObject json = new JSONObject();
        
        try
            {
            json.put("error", "Unexpected request origin "+
                request.getRemoteAddr());
            final String javaScript = functionName+"("+json.toString()+");";
            m_log.debug("Writing JavaScript: "+javaScript);
            final OutputStream os = response.getOutputStream();
            os.write(javaScript.getBytes("UTF-8"));
            os.flush();
            }
        catch (final JSONException e)
            {
            m_log.error("Error encoding JSON", e);
            }
        }
    
    public void destroy()
        {
        // Do nothing.
        }

    public void init(final FilterConfig config) throws ServletException
        {
        // Do nothing.
        }
    }
