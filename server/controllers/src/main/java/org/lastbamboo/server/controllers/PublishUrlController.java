package org.lastbamboo.server.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.services.command.PublishUrlCommand;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

/**
 * Publishes a URL resource.
 */
public class PublishUrlController extends AbstractCommandController 
    {

    /**
     * Logger for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger(PublishUrlController.class);
    
    /**
     * Creates a new controller for publishing URL resources.
     */
    public PublishUrlController()
        {
        
        // This is due to Tomcat containing older versions of xalan that are
        // incompatible with Java 5, as far as I understand it.
        
        // TODO: Is this really still necessary??
        System.setProperty("javax.xml.transform.TransformerFactory", 
            "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        }
    
    @Override
    protected void initBinder(final HttpServletRequest req,
        final ServletRequestDataBinder binder) throws Exception
        {
        LOG.trace("Initializing custom editors...");
        binder.registerCustomEditor(URI.class, new URIEditor());
        }

    @Override
    protected ModelAndView handle(final HttpServletRequest request,
        final HttpServletResponse response, final Object command, 
        final BindException errors) throws Exception
        {        
        LOG.debug("Received publish request...");
        
        
        final PublishUrlCommand bean = (PublishUrlCommand) command;
        
        if (errors.hasErrors())
            {
            LOG.warn("Request has errors!!"+errors);
            return null;
            }
        
        final URI url = bean.getUrl();
        LOG.debug("Publishing url: "+url);
        final String title = bean.getTitle();
        LOG.debug("Publishing title: "+title);
        /*
        final UrlFileResource resource;
        try
            {
            resource = new UrlFileResource(url, title);
            }
        catch (final IOException e)
            {
            writeResponse(request, response,
                "Could not access resource at "+url);
            return null;
            }
        
        LOG.debug("Inserting resource: "+resource);
        this.m_repository.publishResource(resource);
        */
        
        //writeResponse(request, response, "Published URL: "+url);
        return null;
        }

    private void writeResponse(final HttpServletRequest request, 
        HttpServletResponse response, final String msg) throws IOException
        {
        final String functionName = request.getParameter("callback");
        LOG.trace("Callback function: "+functionName);
        final String javaScript = functionName+"(\""+msg+"\");";
        
        LOG.debug("Writing JavaScript: "+javaScript);
        final OutputStream os = response.getOutputStream();
        os.write(javaScript.getBytes("UTF-8"));
        os.flush();
        }
    }
