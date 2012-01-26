package org.lastbamboo.client.services.http;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for resolving HTTP request exceptions and responding appropriately.
 */
public final class HttpHandlerExceptionResolver {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public void resolveException(final HttpServletRequest request,
        final HttpServletResponse response, final Object handler, 
        final Exception ex)
        {
        m_log.warn("Resolving exception", ex);
        ex.printStackTrace();
        if (ex instanceof HttpRequestClientErrorException)
            {
            final HttpRequestClientErrorException httpException =
                (HttpRequestClientErrorException) ex;
            m_log.error("Client error: "+httpException.getStatusCode(), 
                httpException);
            response.setStatus(httpException.getStatusCode());
            }
        
        else if (ex instanceof FileNotFoundException)
            {
            // This can happen if the user deletes the file while we're 
            // processing the request.
            m_log.trace("File Not Found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        
        else
            {
            m_log.error("Something scandalous on the server -- our bug.", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        
        
        // Return dummy model and view.  This does not show up correctly in
        // the browser, but we're still returning the correct response code.
        //return new ModelAndView("exception.jsp");
        
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream writer = new PrintStream(baos);
        ex.printStackTrace(writer);
        try
            {
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();
            }
        catch (final IOException e)
            {
            e.printStackTrace();
            }
        }
    }
