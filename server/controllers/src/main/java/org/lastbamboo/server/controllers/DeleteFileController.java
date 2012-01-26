package org.lastbamboo.server.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.services.command.DeleteFileCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

/**
 * Deletes a resource.
 */
public class DeleteFileController extends AbstractCommandController 
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final ResourceRepository m_resourceRepository;

    /**
     * Creates a new controller for deleting resources.
     * 
     * @param resourceRepository The class for managing resources.
     */
    public DeleteFileController(final ResourceRepository resourceRepository)
        {
        this.m_resourceRepository = resourceRepository;
        setSupportedMethods(new String[] {"POST"});
        }
    
    @Override
    protected void initBinder(final HttpServletRequest req,
        final ServletRequestDataBinder binder) throws Exception
        {
        m_log.trace("Initializing custom editors...");
        binder.registerCustomEditor(File.class, new FileEditor());
        binder.registerCustomEditor(URI.class, new URIEditor());
        }

    @Override
    protected ModelAndView handle(final HttpServletRequest request,
        final HttpServletResponse response, final Object command, 
        final BindException errors) throws Exception
        {        
        m_log.debug("Received delete file request: "+request.getQueryString());
        
        super.preventCaching(response);
        final DeleteFileCommand bean = (DeleteFileCommand) command;

        if (!process(bean))
            {
            response.sendError(HttpStatus.SC_BAD_REQUEST, "Bad Request");
            }
        
        return null;
        }

    private boolean process(final DeleteFileCommand bean)
        {
        final long instanceId = bean.getInstanceId();
        final String sha1 = bean.getSha1();
        try
            {
            this.m_resourceRepository.deleteResource(instanceId, sha1);
            return true;
            }
        catch (final IOException e)
            {
            m_log.warn("Could not remove file", e);
            return false;
            }
        }
    }
