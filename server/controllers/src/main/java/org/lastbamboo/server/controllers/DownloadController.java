package org.lastbamboo.server.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.controllers.ControllerUtils;
import org.lastbamboo.common.services.SessionAttributeKeys;
import org.lastbamboo.common.util.ShootConstants;
import org.lastbamboo.common.util.UriUtils;
import org.lastbamboo.server.services.command.DownloadCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

/**
 * Initiates a download.
 */
public class DownloadController extends AbstractCommandController 
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass()); 
    
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
        final BindException errors) throws IOException 
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


        final DownloadCommand bean = (DownloadCommand) command;
        final String uri = bean.getUri();
        
        final Cookie instanceCookie = 
            WebUtils.getCookie(request, "instanceData");
        
        boolean appInstalled = false;
        if (instanceCookie != null)
            {
            final String val = instanceCookie.getValue();
            if (StringUtils.isNotBlank(val))
                {
                try
                    {
                    final JSONObject json = new JSONObject(val);
                    appInstalled = 
                        json.getBoolean(SessionAttributeKeys.CLIENT_PRESENT);
                    }
                catch (final JSONException e)
                    {
                    m_log.warn("Error parsing value: {}", val);
                    appInstalled = false;
                    }
                }
            else
                {
                appInstalled = false;
                }
            }
        else
            {
            appInstalled = false;
            }
        
        
        final Map<String, String> paramMap = new TreeMap<String, String>();
        paramMap.put("uri", uri);
        
        final String baseUrl;
        final String url;
        if (appInstalled)
            {
            baseUrl = "http://client.littleshoot.org/api/client/download";
            url = UriUtils.newWwwUrlEncodedUrl(baseUrl, paramMap);
            }
        else
            {
            setClientCookie(response, SessionAttributeKeys.PENDING_DOWNLOAD, uri);
            url = ShootConstants.SERVER_BASE + "installAndDownload.html";
            }        
        return new ModelAndView(new RedirectView(url));

        }

    private void setClientCookie(final HttpServletResponse response, 
        final String key, final String value)
        {
        final Cookie cookie = new Cookie(key, value);
        cookie.setDomain(".littleshoot.org");
        cookie.setPath("/api/client");
        //cookie.setMaxAge(MAX_AGE);
        response.addCookie(cookie);
        }
    }
