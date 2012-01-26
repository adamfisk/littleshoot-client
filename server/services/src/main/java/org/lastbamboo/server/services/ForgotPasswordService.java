package org.lastbamboo.server.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.services.JsonCommandProvider;
import org.lastbamboo.common.util.Pair;
import org.lastbamboo.common.util.UriUtils;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.resource.UserNotFoundException;
import org.lastbamboo.server.services.command.ForgotPasswordCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service that sends the user a reminder e-mail when they've forgotten their password.
 */
public class ForgotPasswordService implements JsonCommandProvider
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final ResourceRepository m_resourceRepository;

    private final EmailService m_emailService;

    /**
     * Creates a service for sending users password reminder e-mails.
     * 
     * @param rr The class for accessing resources.
     * @param emailService The service for sending e-mails.
     */
    public ForgotPasswordService(final ResourceRepository rr, 
        final EmailService emailService)
        {
        this.m_resourceRepository = rr;
        this.m_emailService = emailService;
        }

    public String getJson(final Object obj)
        {
        m_log.debug("Processing password reminder request..");
        final ForgotPasswordCommand command = (ForgotPasswordCommand) obj;
        
        final JSONObject json = new JSONObject();
        try
            {
            final long resetId =
                this.m_resourceRepository.generatePasswordResetId(
                    command.getEmail());
            
            final boolean sent = sendBody(command.getEmail(), resetId);
            JsonUtils.put(json, "success", sent);
            JsonUtils.put(json, "exists", true);
            }
        catch (final UserNotFoundException e)
            {
            m_log.debug("We could not locate the user!!");
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "exists", false);
            }
        catch (final RuntimeException e)
            {
            // This could be from either the database access or sending the e-mail.
            m_log.warn("Exception accessing database or sending e-mail: ", e);
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "exists", false);
            }

        return json.toString();
        }
    
    private boolean sendBody(final String address, final long resetId)
        {
        m_log.debug("Sending e-mail body");
        final Collection<Pair<String,String>> params =
            new LinkedList<Pair<String,String>> ();
        
        params.add (UriUtils.pair ("ol", "resetPassword"));
        params.add (UriUtils.pair ("param1", resetId));
        params.add (UriUtils.pair ("param2", address));
        final String url = 
            UriUtils.newUrl("http://www.littleshoot.org/index.html", params);
        
        final Map<String, String> map = new HashMap<String, String>();
        map.put("resetUrl", url);

        return m_emailService.sendEmail(address, "LittleShoot Password Reminder", map, 
            "forgotPasswordEmail.html");
        }
    }
