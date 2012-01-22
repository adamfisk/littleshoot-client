package org.lastbamboo.client.handlers;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.services.command.StopDownloadCommand;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for stopping downloads.
 */
public class StopDownloadController extends HttpServlet {

    private static final long serialVersionUID = 3396201662430596219L;
    
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
        final StopDownloadCommand bean = new StopDownloadCommand();
        ControllerUtils.populate(bean, request);

        final URI uri = bean.getUri();
        final boolean removeFiles = bean.isRemoveFiles();
        final Downloader<MoverDState<Sha1DState<MsDState>>> dl = 
            LittleShootModule.getDownloadTracker().getActiveOrSucceededDownloader(uri);
        final JSONObject obj = new JSONObject();
        if (dl == null) {
            m_log.error("We don't know anything about the downloader");
            JsonUtils.put(obj, "success", false);
        } else {
            dl.stop(removeFiles);
            JsonUtils.put(obj, "success", true);
        }

        m_log.debug("Returning from controller");
        JsonControllerUtils.writeResponse(request, response, obj);
    }
}
