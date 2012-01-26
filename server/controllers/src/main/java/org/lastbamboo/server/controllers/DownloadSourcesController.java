package org.lastbamboo.server.controllers;

import java.io.OutputStream;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.db.OpenSearchResourceVisitor;
import org.lastbamboo.server.db.OpenSearchResourceVisitorFactory;
import org.lastbamboo.server.resource.FileAndInstances;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.services.command.DownloadSourcesCommand;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

/**
 * Searches the database for all sources for a given file, specified as a 
 * SHA-1 URN.
 */
public class DownloadSourcesController extends AbstractCommandController 
    {

    /**
     * Logger for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger(DownloadSourcesController.class);

    private final ResourceRepository m_resourceRepository;

    private final OpenSearchResourceVisitorFactory m_openSearchFactory;

    /**
     * Creates a new controller for publishing resources.
     * 
     * @param resourceRepository The class for managing resources.
     * @param openSearchFactory The factory for creating Amazon Open Search
     * responses.
     */
    public DownloadSourcesController(final ResourceRepository resourceRepository,
        final OpenSearchResourceVisitorFactory openSearchFactory)
        {
        this.m_resourceRepository = resourceRepository;
        this.m_openSearchFactory = openSearchFactory;
        
        // This has to do with classpath issues using Xalan, Tomcat, and Java 5.
        // See: http://forum.java.sun.com/thread.jspa?tstart=30&forumID=34&threadID=542044&trange=15
        //System.setProperty("javax.xml.transform.TransformerFactory", 
          //  "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
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
        LOG.debug("Received search request: "+request.getQueryString());
        super.preventCaching(response);
        if (errors.hasErrors ())
            {
            LOG.warn ("Errors in request!");
            return null;
            }
        final DownloadSourcesCommand bean = (DownloadSourcesCommand) command;
        
        // These are duplicated because LittleShoot 0.40 and earlier used
        // sha1 as the lookup parameter.
        final URI sha1 = bean.getSha1();
        final URI uri = bean.getUri();
        
        final URI uriToUse;
        if (sha1 == null)
            {
            uriToUse = uri;
            }
        else
            {
            uriToUse = sha1;
            }
        
        final FileAndInstances fileAndInstances = 
            this.m_resourceRepository.getFileAndInstances(uriToUse);
        
        if (fileAndInstances == null)
            {
            LOG.debug("No match for URI: " + uriToUse);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        else
            {
            final OpenSearchResourceVisitor openSearchVisitor = 
                this.m_openSearchFactory.createSourceOnlyVisitor(fileAndInstances);
            final String encodings = request.getHeader("Accept-Encoding");
            
            // If the client has not specified the encodings they accept, we assume
            // they accept anything and send them gzip. This is optional
            // behavior as specified in section 14.3 of RFC 2616.
            //
            // Also, if they don't accept gzip, send an error.
            if (encodings != null && !StringUtils.contains(encodings, "gzip")
                && !StringUtils.contains(encodings, "*"))
                {
                throw new HttpRequestClientErrorException(406, "Not Acceptable",
                    encodings);
                }
            
            // Apache will automatically gzip this with the proper config.
            response.setContentType("text/xml");
    
            final OutputStream os = response.getOutputStream();
            openSearchVisitor.write(os);
            os.flush();
            }
        
        return null;
        }
    }
