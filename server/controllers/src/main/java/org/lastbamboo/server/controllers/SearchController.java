package org.lastbamboo.server.controllers;

import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.util.ShootConstants;
import org.lastbamboo.server.db.OpenSearchResourceVisitor;
import org.lastbamboo.server.db.OpenSearchResourceVisitorFactory;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.MetaFileResourceResult;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.services.command.SearchCommand;
import org.lastbamboo.server.services.command.validators.SearchValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

/**
 * Searches the database for published resources.
 */
public class SearchController extends AbstractCommandController 
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass()); 

    private final ResourceRepository m_resourceRepository;

    private final OpenSearchResourceVisitorFactory m_openSearchFactory;

    /**
     * Creates a new controller for publishing resources.
     * 
     * @param resourceRepository The class for managing resources.
     * @param openSearchFactory The factory for creating Amazon Open Search
     * responses.
     */
    public SearchController(final ResourceRepository resourceRepository,
        final OpenSearchResourceVisitorFactory openSearchFactory)
        {
        this.m_resourceRepository = resourceRepository;
        this.m_openSearchFactory = openSearchFactory;
        
        setValidator(new SearchValidator());
        
        // This has to do with classpath issues using Xalan, Tomcat, and Java 5.
        // See: http://forum.java.sun.com/thread.jspa?tstart=30&forumID=34&threadID=542044&trange=15
        //System.setProperty("javax.xml.transform.TransformerFactory", 
          //  "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        }

    @Override
    protected ModelAndView handle(final HttpServletRequest request,
        final HttpServletResponse response, final Object command, 
        final BindException errors) throws Exception
        {        
        m_log.debug("Received search query: "+request.getQueryString());
        super.preventCaching(response);
        if (errors.hasErrors ())
            {
            m_log.warn ("Errors in request!");
            response.sendError(400, errors.getMessage());
            return null;
            }
        final SearchCommand bean = (SearchCommand) command;
            
        final String keywords = bean.getKeywords().toLowerCase();
        final int startIndex = bean.getStartPage();
        final int itemsPerPage = bean.getItemsPerPage();
        final String operatingSystem = bean.getOs();
        final long userId = bean.getUserId();
        final boolean applications = bean.isApplications();
        final boolean audio = bean.isAudio();
        final boolean docs = bean.isDocuments();
        final boolean images = bean.isImages();
        final boolean videos = bean.isVideo();
        String groupName = bean.getGroupName(); 
        if (StringUtils.isBlank(groupName))
            {
            groupName = ShootConstants.WORLD_GROUP;
            }
        else if (!groupName.equals(ShootConstants.WORLD_GROUP))
            {
            if (!this.m_resourceRepository.hasGroupPermission(userId, groupName))
                {
                response.sendError(403, 
                    "You don't have permission to search to this group.");
                return null;
                }
            }
        
        m_log.debug("Using group name: {}", groupName);
        
        // TODO: Add support for non-gzipped results?? Easier from things like
        // javascript.  Also add support for JSON results!!!
        
        final MetaFileResourceResult result = 
            this.m_resourceRepository.search(keywords, startIndex, 
                itemsPerPage, operatingSystem, userId, groupName, applications, 
                audio, docs, images, videos);
        
        final Collection<MetaFileResource> resources = result.getResults();
        final int totalResults = result.getTotalResults();
        
        final OpenSearchResourceVisitor openSearchVisitor = 
            this.m_openSearchFactory.createVisitor(keywords, startIndex, 
                itemsPerPage, resources, totalResults);
        
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
        
        return null;
        }
    }
