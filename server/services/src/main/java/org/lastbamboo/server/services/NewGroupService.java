package org.lastbamboo.server.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.services.JsonCommandProvider;
import org.lastbamboo.server.resource.GroupExistsException;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.services.command.GroupCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for creating a new group.
 */
public class NewGroupService implements JsonCommandProvider
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final ResourceRepository m_resourceRepository;

    private EmailService m_emailService;
    
    /**
     * Creates a service for creating new groups.
     * 
     * @param rr The class for accessing resources.
     */
    public NewGroupService(final ResourceRepository rr)
        {
        this.m_resourceRepository = rr;
        }

    public String getJson(final Object obj)
        {
        m_log.debug("Creating new group...");
        final GroupCommand command = (GroupCommand) obj;
        
        final JSONObject json = new JSONObject();
        try
            {
            final String groupId =
                this.m_resourceRepository.newGroup(command.getUserId(), 
                    command.getName(), command.getDescription(), 
                    command.getPermission());
            
            m_log.debug("Created group with ID: "+groupId);
            JsonUtils.put(json, "success", true);
            JsonUtils.put(json, "exists", false);
            }
        catch (final GroupExistsException e)
            {
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "exists", true);
            }
        catch (final IOException e)
            {
            m_log.warn("Could not access simple DB: ", e);
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "exists", false);
            }
        catch (final RuntimeException e)
            {
            m_log.warn("Exception accessing database: ", e);
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "exists", false);
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
