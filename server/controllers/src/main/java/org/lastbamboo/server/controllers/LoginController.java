package org.lastbamboo.server.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.lastbamboo.common.controllers.JsonControllerUtils;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.services.SessionAttributeKeys;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.resource.UserNotFoundException;
import org.lastbamboo.server.resource.UserNotVerifiedException;
import org.lastbamboo.server.services.command.UserCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import sun.misc.BASE64Encoder;

/**
 * Controller for a client "login".
 */
public class LoginController extends AbstractCommandController 
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final ResourceRepository m_resourceRepository;

    private final KeyGenerator m_generator;
    
    private final BASE64Encoder m_encoder = new BASE64Encoder();
    
    private final Random m_random = new SecureRandom();

    /**
     * Cookie expire time, in seconds.
     */
    private final int MAX_SITE_COOKIE_AGE = 60*60*8;
    
    /**
     * Creates a new controller for publishing resources.
     * 
     * @param resourceRepository The class for managing resources.
     */
    public LoginController(final ResourceRepository resourceRepository)
        {
        this.m_resourceRepository = resourceRepository;
        try
            {
            this.m_generator = KeyGenerator.getInstance("Blowfish");
            }
        catch (final NoSuchAlgorithmException e)
            {
            throw new RuntimeException(
                "Could not create Blowfish key generator");
            }
        m_generator.init(128);
        
        // This can take awhile, so generate the first one here.
        m_random.nextLong();
        //setSupportedMethods(new String[] {"POST"});
        }

    @Override
    protected ModelAndView handle(final HttpServletRequest request,
        final HttpServletResponse response, final Object obj, 
        final BindException errors) throws IOException 
        {        
        m_log.debug("Received login request: {}", request.getQueryString());
        super.preventCaching(response);
        final boolean secure = request.isSecure();
        m_log.debug("Secure: "+secure);
        if (!secure)
            {
            m_log.warn("Attempt to login insecurely.");
            response.sendError(403, "HTTPS Required");
            return null;
            }

        final String host = request.getHeader("Host");
        if (StringUtils.isBlank(host))
            {
            m_log.warn("No host header!!");
            response.sendError(403, "No Host Header");
            return null;
            }
        if (host.startsWith("openplans"))
            {
            m_log.debug("In topp group...");
            }
        if (host.startsWith("group"))
            {
            m_log.debug("In test group...");
            }
        
        m_log.debug("Host is: {}", host);
        final UserCommand command = (UserCommand) obj;
        final JSONObject json = new JSONObject();
        final HttpSession session = request.getSession();
        try
            {
            final long userId =
                this.m_resourceRepository.authenticateWebUser(command.getEmail(), 
                    command.getPassword());
            m_log.debug("Authentication succeeded for: {}", command.getEmail());
            final SecretKey key = this.m_generator.generateKey();
            final String base64Key = m_encoder.encode(key.getEncoded());
            
            m_log.debug("Setting key cookie.");
            session.setAttribute(SessionAttributeKeys.KEY, base64Key);
            
            setClientCookie(response, SessionAttributeKeys.KEY, base64Key);
            setClientCookie(response, SessionAttributeKeys.SESSION_ID, 
                session.getId());
            
            final SecretKey siteKey = this.m_generator.generateKey();
            final String base64SiteKey = m_encoder.encode(siteKey.getEncoded());
            setSiteCookie(response, SessionAttributeKeys.USER_ID, 
                String.valueOf(userId));
            setSiteCookie(response, SessionAttributeKeys.SITE_KEY, 
                base64SiteKey);
            //this.m_resourceRepository.setKey(userId, base64SiteKey);
            
            JsonUtils.put(json, "success", true);
            JsonUtils.put(json, "notVerified", false);
            JsonUtils.put(json, "notFound", false);
            }
        catch (final UserNotVerifiedException e)
            {
            m_log.debug("The user is not verified.", command.getEmail());
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "notVerified", true);
            JsonUtils.put(json, "notFound", false);
            }
        catch (final UserNotFoundException e)
            {
            m_log.debug("Authentication failed for: {}", command.getEmail());
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "notVerified", false);
            JsonUtils.put(json, "notFound", true);
            }
        
        m_log.debug("Writing response using JSON controller...");
        JsonControllerUtils.writeResponse(request, response, json);
        m_log.debug("Wrote response...");
        return null;
        }

    private void setClientCookie(final HttpServletResponse response, 
        final String key, final String value)
        {
        final Cookie cookie = new Cookie(key, value);
        cookie.setDomain(".littleshoot.org");
        cookie.setPath("/api/client");
        cookie.setMaxAge(MAX_SITE_COOKIE_AGE);
        response.addCookie(cookie);
        }
    
    private void setSiteCookie(final HttpServletResponse response, 
        final String key, final String value)
        {
        final Cookie cookie = new Cookie(key, value);
        cookie.setDomain(".littleshoot.org");
        cookie.setPath("/");
        cookie.setMaxAge(MAX_SITE_COOKIE_AGE);
        response.addCookie(cookie);
        }
    }
