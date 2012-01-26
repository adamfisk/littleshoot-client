package org.lastbamboo.server.controllers;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.services.command.DownloadViewCommand;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;


/**
 * Returns the src URL for the last initiated download.
 */
public final class DownloadViewController extends AbstractCommandController
    {
    /**
     * Logger for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger (DownloadViewController.class);

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
        LOG.trace ("Initiating download view!!!");

        if (errors.hasErrors ())
            {
            LOG.warn ("Errors in request!");
            return null;
            }

        final DownloadViewCommand requestBean = (DownloadViewCommand) command;
        
        response.setHeader("Content-Disposition", 
            "Attachment; filename="+requestBean.getName());
        response.setContentType("text/html");
        
        //dl.writeFile(response);
        
        LOG.debug("Finished writing response...");
        
        // We have no model and view to show in this case.        
        return new ModelAndView("download");
        }
    }
