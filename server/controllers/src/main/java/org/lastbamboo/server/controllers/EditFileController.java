package org.lastbamboo.server.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.lastbamboo.common.controllers.JsonControllerUtils;
import org.lastbamboo.common.controllers.SignedController;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.services.command.EditFileCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Edits the details of a file.
 */
public class EditFileController extends SignedController 
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final ResourceRepository m_resourceRepository;

    /**
     * Creates a new controller for editing resources.
     * 
     * @param resourceRepository The class for managing resources.
     */
    public EditFileController(final ResourceRepository resourceRepository)
        {
        this.m_resourceRepository = resourceRepository;
        }
    
    @Override
    protected ModelAndView handleSigned(final HttpServletRequest request,
        final HttpServletResponse response, final Object command, 
        final BindException errors) throws Exception
        {
        m_log.debug("Received edit file request: "+request.getQueryString());
        
        final EditFileCommand bean = (EditFileCommand) command;

        final JSONObject json = new JSONObject();
        if (!process(bean))
            {
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "message", "Could not modify file.");
            //response.sendError(HttpStatus.SC_BAD_REQUEST, "Bad Request");
            }
        else
            {
            JsonUtils.put(json, "success", true);
            JsonUtils.put(json, "message", "Successfully modified file.");
            }
        
        JsonControllerUtils.writeResponse(request, response, json);
        return null;
        }


    private boolean process(final EditFileCommand bean)
        {
        final long instanceId = bean.getInstanceId();
        final String sha1 = bean.getSha1();
        final String tags = bean.getTags();
        final String url = bean.getUrl();
        try
            {
            this.m_resourceRepository.editResource(instanceId, sha1, tags, url);
            return true;
            }
        catch (final IOException e)
            {
            m_log.warn("Could not edit file", e);
            return false;
            }
        }

    }
