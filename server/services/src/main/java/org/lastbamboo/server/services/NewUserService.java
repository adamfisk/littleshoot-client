package org.lastbamboo.server.services;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.services.JsonCommandProvider;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.resource.UserExistsException;
import org.lastbamboo.server.services.command.UserCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for creating a new user.
 */
public class NewUserService implements JsonCommandProvider
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final ResourceRepository m_resourceRepository;

    private EmailService m_emailService;
    
    /**
     * Creates a service for creating new users.
     * 
     * @param rr The class for accessing resources.
     * @param emailService The class that sends mail.
     */
    public NewUserService(final ResourceRepository rr, 
        final EmailService emailService)
        {
        this.m_resourceRepository = rr;
        this.m_emailService = emailService;
        }

    public String getJson(final Object obj)
        {
        m_log.debug("Creating new user...");
        final UserCommand command = (UserCommand) obj;
        
        final JSONObject json = new JSONObject();
        try
            {
            final long userId =
                this.m_resourceRepository.newWebUser(command.getEmail(), 
                    command.getPassword());
            sendBody(command.getEmail(), userId);
            JsonUtils.put(json, "success", true);
            JsonUtils.put(json, "exists", false);
            JsonUtils.put(json, "emailSent", true);
            }
        catch (final UserExistsException e)
            {
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "exists", true);
            JsonUtils.put(json, "emailSent", false);
            }
        catch (final RuntimeException e)
            {
            m_log.warn("Exception accessing database: ", e);
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "exists", false);
            JsonUtils.put(json, "emailSent", false);
            }
        
        return json.toString();
        }

    private boolean sendBody(final String address, final long userId)
        {
        m_log.debug("Sending e-mail body");
        final Map<String, String> map = new HashMap<String, String>();
        map.put("userId", String.valueOf(userId));
        return m_emailService.sendEmail(address, "Welcome to LittleShoot", map, 
            "littleShootConfirmationEmail.html");
        }
    }
