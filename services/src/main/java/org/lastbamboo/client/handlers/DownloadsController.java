package org.lastbamboo.client.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.client.services.JsonDownloadsVisitor;
import org.lastbamboo.client.services.command.DownloadsCommand;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.download.VisitableDownloader;
import org.littleshoot.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for requests for all downloading files.
 */
public class DownloadsController extends HttpServlet {
    
    private static final long serialVersionUID = -8559143739191284945L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        m_log.debug("Handling request: {}", request.getQueryString());
        ControllerUtils.preventCaching(response);
        
        final DownloadsCommand downloads = new DownloadsCommand();
        ControllerUtils.populate(downloads, ControllerUtils.toParamMap(request));
        
        // Note this returns all downloads currently happening on this 
        // machine, not just for the session.  A feature?
        final Preferences prefs = Preferences.userRoot ();
        final File path = new File(prefs.get (PrefKeys.DOWNLOAD_DIR, ""));
        if (!path.isDirectory()) {
            m_log.error("Downloads dir does not exist at: {}", path);
        }
        
        final Collection<Entry<URI, Pair<Downloader<MoverDState<Sha1DState<MsDState>>>, VisitableDownloader<MsDState>>>> 
            all = LittleShootModule.getDownloadTracker().getAll();
        final JsonDownloadsVisitor visitor =
            new JsonDownloadsVisitor(
                downloads.getResultsPerPage(), downloads.getPageIndex(),
                LittleShootModule.getFileMapper(), path, 
                LittleShootModule.getTorrentManager(), all);
        
        m_log.debug("Accessing downloads JSON");
        final String json = visitor.getJson();
        m_log.debug("Returning downloads JSON: {}", json);
        
        try {
            JsonControllerUtils.writeResponse(request, response, json);
        }
        catch (final IOException e) {
            m_log.debug("Could not write response", e);
        }
    }
}
