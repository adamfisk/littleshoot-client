package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.services.command.UriDownloadCommand;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.littleshoot.util.DaemonThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for pausing downloads.
 */
public class PauseDownloadController extends HttpServlet {

    private static final long serialVersionUID = -9090694407962937746L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.debug("Stopping downlod: {}", request.getQueryString());
        ControllerUtils.preventCaching(response);
        final UriDownloadCommand bean = new UriDownloadCommand();
        ControllerUtils.populate(bean, request);
        
        final URI uri = bean.getUri();
        final Downloader<MoverDState<Sha1DState<MsDState>>> dl = 
            LittleShootModule.getDownloadTracker().getActiveDownloader(uri);
        if (dl == null) {
            m_log.error("We don't know anything about the downloader at: {}",
                uri);
        }
        else {
            pause(dl);
        }
        
        m_log.debug("Returning from controller");
        JsonControllerUtils.writeResponse(request, response, new JSONObject());
    }

    private void pause(final Downloader<MoverDState<Sha1DState<MsDState>>> dl) {
        final Runnable runner = new Runnable() {
            public void run() {
                dl.pause();
            }
        };
        final Thread thread = new DaemonThread(runner, "PauseThread");
        thread.start();
    }
    
}
