package org.lastbamboo.server.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.lastbamboo.common.services.SessionAttributeKeys;
import org.lastbamboo.server.services.command.KeyCommand;
import org.lastbamboo.server.services.command.validators.KeyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import sun.misc.BASE64Encoder;

/**
 * Controller that issues a key for publishing files.
 */
public class KeyController extends AbstractCommandController 
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final KeyGenerator m_generator;
    
    private final BASE64Encoder m_encoder = new BASE64Encoder();
    
    /**
     * Cookie expire time, in seconds.
     */
    private final int MAX_SITE_COOKIE_AGE = 40;
    
    /**
     * Creates a new controller for creating keys for publishing resources.
     */
    public KeyController()
        {
        try
            {
            this.m_generator = KeyGenerator.getInstance("AES");
            }
        catch (final NoSuchAlgorithmException e)
            {
            throw new RuntimeException("Could not create key generator");
            }
        m_generator.init(128);
        setSupportedMethods(new String[] {"POST"});
        setValidator(new KeyValidator());
        }

    @Override
    protected ModelAndView handle(final HttpServletRequest request,
        final HttpServletResponse response, final Object obj, 
        final BindException errors) throws IOException 
        {        
        m_log.debug("Received key request: {}",request.getQueryString());
        super.preventCaching(response);
        final boolean secure = request.isSecure();
        m_log.debug("Secure: "+secure);
        /*
        if (!secure)
            {
            m_log.warn("Attempt to login insecurely.");
            response.sendError(403, "HTTPS Required");
            return null;
            }
            */

        final KeyCommand command = (KeyCommand) obj;
        //final UserCommand command = (UserCommand) obj;
        //final JSONObject json = new JSONObject();
        final HttpSession session = request.getSession();

        final SecretKey key = this.m_generator.generateKey();
        final String base64Key = m_encoder.encode(key.getEncoded());
        
        m_log.debug("Setting key cookie.");
        final long id = command.getKeyId();
        if (id == -1)
            {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                "Invalid ID");
            return null;
            }
        session.setAttribute(String.valueOf(id), base64Key);
        
        setClientCookie(response, SessionAttributeKeys.KEY, base64Key);
        setClientCookie(response, SessionAttributeKeys.SESSION_ID, 
            session.getId());
        
        final SecretKey siteKey = this.m_generator.generateKey();
        final String base64SiteKey = m_encoder.encode(siteKey.getEncoded());
        setSiteCookie(response, SessionAttributeKeys.SITE_KEY, 
            base64SiteKey);
        //this.m_resourceRepository.setKey(userId, base64SiteKey);
        
        //JsonUtils.put(json, "success", true);
        //JsonUtils.put(json, "notVerified", false);
        //JsonUtils.put(json, "notFound", false);
            
        
        //m_log.debug("Writing response using JSON controller...");
        //JsonControllerUtils.writeResponse(request, response, json);
        //m_log.debug("Wrote response...");
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
