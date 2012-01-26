package org.lastbamboo.server.controllers;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.lastbamboo.common.controllers.ControllerUtils;
import org.lastbamboo.common.controllers.JsonControllerUtils;
import org.lastbamboo.common.util.ResourceTypeTranslator;
import org.lastbamboo.common.util.ShootConstants;
import org.lastbamboo.server.db.FileResourceImpl;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.services.command.PublishFileCommand;
import org.lastbamboo.server.services.command.validators.PublishFileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

/**
 * Publishes a resource.
 */
public class PublishFileController extends AbstractCommandController 
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass()); 

    private final ResourceRepository m_resourceRepository;

    private final ResourceTypeTranslator m_resourceTypeTranslator;

    /**
     * Creates a new controller for publishing resources.
     * 
     * @param resourceRepository The class for managing resources.
     * @param resourceTypeTranslator Class for finding the correct type of the
     * resource.
     */
    public PublishFileController(final ResourceRepository resourceRepository,
        final ResourceTypeTranslator resourceTypeTranslator)
        {
        this.m_resourceRepository = resourceRepository;
        this.m_resourceTypeTranslator = resourceTypeTranslator;
        setSupportedMethods(new String[] {"POST"});
        setValidator(new PublishFileValidator());
        }
    
    @Override
    protected void initBinder(final HttpServletRequest req,
        final ServletRequestDataBinder binder) throws Exception
        {
        m_log.trace("Initializing custom editors...");
        binder.registerCustomEditor(File.class, new FileEditor());
        }

    @Override
    protected ModelAndView handle(final HttpServletRequest request,
        final HttpServletResponse response, final Object command, 
        final BindException errors) throws Exception
        {        
        m_log.debug("Received publish request: "+request.getQueryString());
        
        super.preventCaching(response);
        if (errors.hasErrors ())
            {
            m_log.warn ("Errors in request: {}", errors.getAllErrors());
            response.sendError(400, "Client Error");
            return null;
            }
        
        ControllerUtils.printCookies(request);
        
        final PublishFileCommand bean = (PublishFileCommand) command;
        
        final String signature = bean.getSignature();
        final long keyId = bean.getKeyId();
        m_log.debug("Signature is: {}", signature);

        if (!signatureMatches(request, keyId))
            {
            m_log.warn("Did not verify signature!");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Signature Mismatch");
            return null;
            }

        final String uri = bean.getUri();
        final long userId = bean.getUserId();
        final int permission = bean.getPermission();
        final String name = bean.getName();
        final String sha1 = bean.getSha1();
        final long bytes = bean.getBytes();
        final long instanceId = bean.getInstanceId();
        
        // Flag for whether or not this file was published as the result of 
        // being downloaded.
        final boolean downloaded = bean.isDownloaded();
        final String remoteHost = request.getRemoteHost();
        final String language = normalize(bean.getLanguage());
        final String country = normalize(bean.getCountry());
        final String timeZone = normalize(bean.getTimeZone());
        String groupName = normalize(bean.getGroupName());
        if (StringUtils.isBlank(groupName))
            {
            groupName = ShootConstants.WORLD_GROUP;
            }
        else if (!groupName.equals(ShootConstants.WORLD_GROUP) &&
            (userId != -1L))
            {
            if (!this.m_resourceRepository.hasGroupPermission(userId, groupName))
                {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "You don't have permission to publish to this group.");
                return null;
                }
            }

        final String tags = normalize(bean.getTags());
        
        m_log.debug("Publishing to group: {}", groupName);
        String mediaType = this.m_resourceTypeTranslator.getType(name);
        if (StringUtils.isBlank(mediaType))
            {
            m_log.error("No media type for file: "+name);
            mediaType = "document";
            }
        String mimeType = getServletContext().getMimeType(name);
        if (StringUtils.isBlank(mimeType))
            {
            m_log.debug("Could not find MIME type for file: {}", name);
            mimeType = "application/octet-stream";
            }
        
        m_log.debug("Inserting resource");
        final String uriToUse;
        if (StringUtils.isBlank(uri) || 
            !StringUtils.startsWithIgnoreCase(uri, "http"))
            {
            uriToUse = sha1;
            }
        else
            {
            uriToUse = uri;
            }
        final FileResource fr = 
            new FileResourceImpl(uriToUse, name, mimeType, sha1, bytes, 
                instanceId, remoteHost, tags, mediaType, language, country, 
                timeZone, downloaded, userId, permission, groupName);
        
        final JSONObject json = new JSONObject();
        json.put("fileName", fr.getName());
        // Now add it to the database.
        try
            {
            this.m_resourceRepository.insertResource(fr);
            json.put("published", Boolean.TRUE);
            json.put("message", "Successfully published \""+fr.getName()+"\"");
            }
        catch (final Exception e)
            {
            response.sendError(500, "Could Not Publish Resource");
            json.put("published", Boolean.FALSE);
            json.put("message", "Error publishing resource \""+fr.getName()+"\".  " +
                    "Please try again later.");
            }
        JsonControllerUtils.writeResponse(request, response, json);
        return null;
        }

    private boolean signatureMatches(final HttpServletRequest request, 
        final long publishId)
        {
        final HttpSession session = request.getSession();
        final String key = 
            (String) session.getAttribute(String.valueOf(publishId));
        return ControllerUtils.signatureMatches(request, key);
        }

    private String normalize(final String str)
        {
        if (StringUtils.isBlank(str)) return StringUtils.EMPTY;
        return str.toLowerCase();
        }
    }
