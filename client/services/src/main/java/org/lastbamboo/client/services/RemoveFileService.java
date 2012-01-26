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
import org.lastbamboo.client.services.command.RemoveFileCommand;
import org.lastbamboo.common.http.client.ForbiddenException;
import org.lastbamboo.common.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for requests to stop sharing files.
 */
public class RemoveFileService implements JsonCommandProvider
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass()); 
    
    private final RemoteResourceRepository m_remoteRepository;

    private final FileMapper m_fileMapper;

    private Map<String, String> m_paramMap;

    private Map<String, String> m_cookieMap;
    
    /**
     * Creates a new controller for accessing file resources.
     * 
     * @param mapper The class that caches local file resource paths.
     * @param rrr The remote repository for accessing resources.
     * @param cookieMap The {@link Map} of cookies received with the request. 
     * @param paramMap Map of the original request parameters.
     */
    public RemoveFileService(final FileMapper mapper,
        final RemoteResourceRepository rrr,
        final Map<String, String> paramMap,
        final Map<String, String> cookieMap)
        {
        this.m_fileMapper = mapper;
        this.m_remoteRepository = rrr;
        this.m_paramMap = paramMap;
        this.m_cookieMap = cookieMap;
        }

    public String getJson(final HttpServletRequest request)
        {
        final RemoveFileCommand bean = new RemoveFileCommand();
        ControllerUtils.populate(bean, request);
        
        final URI urn = bean.getSha1();
        final File file = this.m_fileMapper.getFile(urn);
        
        final JSONObject json = new JSONObject();
        boolean stoppedSharing;
        if (file == null)
            {
            // This basically means the browser has data for a file that we
            // don't know about, so there's been some error. 
            m_log.error("No file for URN: {}", urn);
            m_log.error("Existing files: {}", this.m_fileMapper.getAllFiles());
            stoppedSharing = false;
            }

        // Delete it remotely regardless of whether or not we found it locally.
        JsonUtils.put(json, "fileName", bean.getName());
            
        try
            {
            // This call only works because the SHA-1 is passed from 
            // the browser.
            this.m_remoteRepository.deleteResource(this.m_paramMap, 
                this.m_cookieMap);
            this.m_fileMapper.removeFile(urn);
            stoppedSharing = true;
            }
        catch (final IOException e)
            {
            m_log.warn("Error deleting file", e);
            JsonUtils.put(json, "message", "We're sorry, but there was " +
                "an error accessing our servers.  Please try again later.  " +
                "We apologize for the inconvenience, and thank you for " +
                "using LittleShoot.");
            stoppedSharing = false;
            }
        catch (final ForbiddenException e)
            {
            m_log.warn("Forbidden to delete the file", e);
            JsonUtils.put(json, "message", "We're sorry, but there was " +
                "an error accessing our servers.  Please try again later.  " +
                "We apologize for the inconvenience, and thank you for " +
                "using LittleShoot.");
            stoppedSharing = false;
            }
        
        JsonUtils.put(json, "success", stoppedSharing);
        return json.toString();
        }

    }
