package org.lastbamboo.client.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.services.command.DownloadFolderCommand;
import org.lastbamboo.client.services.download.DownloadUtils;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.json.JsonUtils;
import org.littleshoot.util.NativeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for opening the containing folder of a multi-file download.
 */
public final class DownloadFolderController extends HttpServlet {
    
    private static final long serialVersionUID = -789462451727501285L;
    
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(final HttpServletRequest request, 
        final HttpServletResponse response) throws ServletException, 
        IOException {
        if (m_log.isDebugEnabled()) {
            m_log.debug ("Initiating download with query: "+ 
                request.getQueryString());
            ControllerUtils.printRequestHeaders(request);
        }

        ControllerUtils.preventCaching(response);

        final DownloadFolderCommand downloadRequest = 
            new DownloadFolderCommand();
        ControllerUtils.populate(downloadRequest, request);
        
        final boolean opened = 
            openFolder(downloadRequest.getUri(), downloadRequest.getName());
        final JSONObject json = new JSONObject();
        JsonUtils.put(json, "success", opened);
        try {
            JsonControllerUtils.writeResponse(request, response, json);
        }
        catch (final IOException e) {
            m_log.warn("Exception writing response", e);
        }
    }

    private boolean openFolder(final URI uri, final String name) {
        final File folder;
        final Downloader downloader = 
            LittleShootModule.getDownloadTracker().getActiveOrSucceededDownloader(uri);
        
        if (downloader != null) {
            final File file = downloader.getCompleteFile();
            if (file == null) {
                m_log.error("Null complete file for URI: "+uri);
                return false;
            }
            else if (file.isDirectory()) {
                folder = file;
            }
            else {
                m_log.warn("Odd we're opening a folder/downlaod that's " +
                    "not a directory: "+file);
                folder = file.getParentFile();
            }
        }
        else {
            final File mappedFile = 
                LittleShootModule.getFileMapper().getFile(uri);
            
            if (mappedFile == null) {
                m_log.warn("File not mapped...");
                if (StringUtils.isBlank(name)) {
                    return false;
                }
                final File defaultFileOrFolder = DownloadUtils.defaultFile(name);
                m_log.debug("Opening folder at: {}", defaultFileOrFolder);
                if (defaultFileOrFolder.isDirectory()) {
                    folder = defaultFileOrFolder;
                }
                else if (defaultFileOrFolder.isFile()) {
                    m_log.debug("Found file: {}", defaultFileOrFolder);
                    folder = defaultFileOrFolder.getParentFile();
                }
                else {
                    m_log.debug("Could not find unmapped file or folder either.");
                    return false;
                }
            }
            else {
                m_log.debug("Found file in mapper...");
                // I think we do this for canonical path reasons...
                final File file = new File(mappedFile.getPath());
                if (file.isDirectory()) {
                    folder = file;
                }
                
                else if (file.isFile()) {
                    folder = file.getParentFile();
                }
                else {
                    m_log.warn("Could not create folder from mapping for: {}",file);
                    return false;
                }
            }
        }
        try {
            NativeUtils.openFolder(folder);
            return true;
        }
        catch (final IOException e) {
            m_log.warn("Failed to open folder", e);
            return false;
        }
    }
}
