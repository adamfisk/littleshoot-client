package org.lastbamboo.client.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.lastbamboo.client.handlers.ControllerUtils;
import org.lastbamboo.client.handlers.JsonCommandProvider;
import org.lastbamboo.client.services.command.PublishFileCommand;
import org.lastbamboo.common.http.client.ForbiddenException;
import org.lastbamboo.common.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for publishing files.
 */
public class PublishFileService implements JsonCommandProvider,
    PublishedCallback {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass()); 
    
    private final RemoteResourceRepository m_remoteRepository;

    private final FileMapper m_fileMapper;

    private final Map<String, String> m_cookieMap;

    private final Map<String, String> m_paramMap;

    private final PublishedFilesTracker m_pendingFilesTracker;

    private PublishFileCommand m_command;

    private final Map<String, String> m_headerMap;

    /**
     * Creates a new service for publishing file resources.
     * 
     * @param mapper The class that caches local file resource paths.
     * @param rrr The remote repository for accessing resources.
     * @param pendingFilesTracker Keeps track of files that are still 
     * hashing. 
     * @param cookieMap The {@link Map} of cookies received with the request. 
     * @param paramMap Map of the original request parameters.
     * @param headerMap The map of header names to values.
     */
    public PublishFileService(final FileMapper mapper,
            final RemoteResourceRepository rrr,
            final PublishedFilesTracker pendingFilesTracker,
            final Map<String, String> paramMap,
            final Map<String, String> cookieMap,
            final Map<String, String> headerMap) {
        this.m_fileMapper = mapper;
        this.m_remoteRepository = rrr;
        this.m_pendingFilesTracker = pendingFilesTracker;
        this.m_paramMap = paramMap;
        this.m_cookieMap = cookieMap;
        this.m_headerMap = headerMap;
    }

    public String getJson(final HttpServletRequest request) {
        // Note the signature is already verified at this point.
        // this.m_command = (PublishFileCommand) command;
        this.m_command = new PublishFileCommand();
        ControllerUtils.populate(this.m_command, request);

        final File file = m_command.getFile();
        final JSONObject json = new JSONObject();
        JsonUtils.put(json, "fileName", file.getName());
        if (!file.isFile()) {
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "message", "\"" + file.getName()
                    + "\" does not appear to be a valid file.");
        }

        try {
            insertFile(file);
            JsonUtils.put(json, "success", true);
            JsonUtils.put(json, "message",
                    "Successfully published \"" + file.getName() + "\"");
        } catch (final IOException e) {
            m_log.warn("Could not insert file", e);
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "message",
                    "Could not publish \"" + file.getName() + "\"");
        } catch (final ForbiddenException e) {
            m_log.warn("Forbidden to publish file", e);
            JsonUtils.put(json, "success", false);
            JsonUtils.put(json, "message",
                    "You don't have permissions to publish to this group.");
        }

        return json.toString();
    }

    private void insertFile(final File file) throws IOException,
            ForbiddenException {
        this.m_paramMap.put("downloaded", Boolean.toString(false));

        // We just send in all of the parameters from the client. This
        // makes the client just a conduit to the server.
        this.m_remoteRepository.insertResource(file, this.m_paramMap,
                this.m_cookieMap, this.m_pendingFilesTracker,
                this.m_fileMapper, this, this.m_headerMap);
        // return sha1;
    }

    public void onFilePublished(final File file, final URI sha1) {
        m_log.debug("Got file published callback...");
    }
}
