package org.lastbamboo.server.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.services.command.UserOnlineCommand;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

/**
 * Sets whether or not a user is online.
 */
public class UserOnlineController extends AbstractCommandController 
    {

    /**
     * Logger for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger(UserOnlineController.class);

    private final ResourceRepository m_resourceRepository;

    /**
     * Creates a new controller for setting a user's online and offline status.
     * 
     * @param resourceRepository The class for accessing resources.
     */
    public UserOnlineController(final ResourceRepository resourceRepository)
        {
        this.m_resourceRepository = resourceRepository;
        }

    @Override
    protected ModelAndView handle(final HttpServletRequest request,
        final HttpServletResponse response, final Object command, 
        final BindException errors) throws Exception
        {        
        LOG.debug("Received user online request: "+request.getQueryString());
        if (errors.hasErrors ())
            {
            LOG.warn ("Errors in request!");
            return null;
            }
        final UserOnlineCommand bean = (UserOnlineCommand) command;
        
        final long userId = bean.getUserId();
        final boolean online = bean.isOnline();
        final String baseUri = bean.getBaseUri();
        final String serverAddress = bean.getServerAddress();
        this.m_resourceRepository.setInstanceOnline(userId, baseUri, online, 
            serverAddress);
        
        return null;
        }
    }
