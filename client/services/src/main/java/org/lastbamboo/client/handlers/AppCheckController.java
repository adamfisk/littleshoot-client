package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.client.prefs.Prefs;
import org.lastbamboo.client.services.SessionAttributeKeys;
import org.lastbamboo.common.json.JsonUtils;
import org.littleshoot.util.DaemonThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for responding to requests about the status of the application.
 */
public class AppCheckController extends HttpServlet
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = -1766526945201737349L;
    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.trace("Handling app check request: {}",request.getQueryString());
        ControllerUtils.preventCaching(response);

        ControllerUtils.printCookies(request);
        ControllerUtils.printRequestHeaders(request);
        
        //System.out.println(ControllerUtils.getRequestHeaders(request));

        final Preferences prefs = Preferences.userRoot();
        
        final double version = prefs.getDouble(PrefKeys.APP_VERSION, 0.0);
        
        final JSONObject json = new JSONObject();
        JsonUtils.put(json, SessionAttributeKeys.CLIENT_PRESENT, true);
        JsonUtils.put(json, SessionAttributeKeys.CLIENT_VERSION, version);
        JsonUtils.put(json, SessionAttributeKeys.INSTANCE_ID, Prefs.getId());
        JsonUtils.put(json, "isPro", false);
        final Collection<String> searchers = new LinkedList<String>();
        searchers.add("YouTube");
        searchers.add("Yahoo");
        searchers.add("Flickr");
        searchers.add("LimeWire");
        //searchers.add("LittleShoot");
        searchers.add("IsoHunt");
        JsonUtils.put(json, "searchers", searchers);
        
        /*
        setSiteCookie(response, SessionAttributeKeys.CLIENT_VERSION,
            String.valueOf(version));
        setSiteCookie(response, SessionAttributeKeys.INSTANCE_ID,
            String.valueOf(Prefs.getId()));
        setSiteCookie(response, SessionAttributeKeys.CLIENT_PRESENT, "true");
        */
        setSiteCookie(response, "littleShootClientCookie", json.toString());
        
        JsonControllerUtils.writeResponse(request, response, json);
        
        /*
        if (shouldStop(request, version)) {
            System.out.println(
                "RECEIVED STOP CALL FROM ANOTHER LITTELSHOOT INSTANCE!!");
            threadedStop();
        }
        else if (ControllerUtils.hasParam(request, "stop")) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
        }
        */
    }
        
    private void setSiteCookie(final HttpServletResponse response,
        final String key, final String value) {
        // TODO: These are blocked in IE7!! 
        final Cookie cookie = new Cookie(key, value);
        cookie.setDomain(".littleshoot.org");
        cookie.setPath("/");
        
        // This is in seconds.
        final int oneDay = 60 * 60 * 24;
        final int age = oneDay * 30;
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }
    
    private void threadedStop()
        {
        final Runnable stop = new Runnable()
            {
            public void run()
                {
                // Give the HTTP response a little time to go through.
                try
                    {
                    Thread.sleep(300);
                    } 
                catch (final InterruptedException e)
                    {
                    }
                System.exit(0);
                }
            };
        final Thread thread = new DaemonThread(stop, "App-Stop-Thread");
        thread.start();
        }

    private boolean shouldStop(final HttpServletRequest request, 
        final double version)
        {
        final String stop = request.getParameter("stop");
        if (StringUtils.isBlank(stop)) return false;
        if (!stop.equals("true"))
            {
            return false;
            }
        
        final String versionHeader = request.getHeader("X-LittleShoot-Version");
        if (StringUtils.isBlank(versionHeader))
            {
            m_log.debug("Version header missing...");
            return false;
            }
        
        final double incomingVersion = Double.parseDouble(versionHeader.trim());
        if (version >= incomingVersion)
            {
            m_log.debug("Ignoring incoming version with lower version number");
            return false;
            }
        if (version == 0.0)
            {
            m_log.debug("We're a mainline version, so ignoring.");
            return false;
            }
        
        final String idHeader = request.getHeader("X-Instance-ID");
        if (StringUtils.isBlank(idHeader)) {
            m_log.debug("Header missing...");
            return false;
        }
        
        final String idString = idHeader.trim();
        final long id = Long.parseLong(idString);
        
        if (id == Prefs.getId()) {
            m_log.debug("IDs are equal");
            return true;
        }
        m_log.debug("IDs did not match");
        return false;
    }
}
