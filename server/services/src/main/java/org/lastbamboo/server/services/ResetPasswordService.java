package org.lastbamboo.server.services;

import java.io.IOException;

import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.services.JsonCommandProvider;
import org.lastbamboo.server.resource.BadPasswordResetIdException;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.resource.UserNotFoundException;
import org.lastbamboo.server.services.command.ResetPasswordCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for resetting a user's password.
 */
public class ResetPasswordService implements JsonCommandProvider
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final ResourceRepository m_resourceRepository;

    /**
     * Creates a service for resetting a user's password.
     * 
     * @param rr The class for accessing resources.
     */
    public ResetPasswordService(final ResourceRepository rr)
        {
        this.m_resourceRepository = rr;
        }

    public String getJson(final Object obj)
        {
        m_log.debug("Processing reset password request..");
        final ResetPasswordCommand command = (ResetPasswordCommand) obj;
        
        final JSONObject json = new JSONObject();
        
        try
            {
            this.m_resourceRepository.resetPassword(
                command.getEmail(), command.getPassword(), command.getResetId());
            JsonUtils.put(json, "success", true);
            JsonUtils.put(json, "exists", true);
            }
        catch (final UserNotFoundException e)
            {
            m_log.debug("We could not locate the user!!");
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "exists", false);
            }
        catch (final BadPasswordResetIdException e)
            {
            m_log.warn("Bad reset ID: {}", new Long(command.getResetId()));
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "exists", true);
            }
        catch (final IOException e)
            {
            m_log.debug("We could not access the database!!");
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "exists", false);
            }

        return json.toString();
        }
    }
