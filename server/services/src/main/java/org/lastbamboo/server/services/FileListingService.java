package org.lastbamboo.server.services;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.services.JsonCommandProvider;
import org.lastbamboo.common.util.ShootConstants;
import org.lastbamboo.server.resource.FileResourceResult;
import org.lastbamboo.server.resource.JsonResourceVisitor;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.services.command.FileListingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for listing files for a specific user.
 */
public class FileListingService implements JsonCommandProvider
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final ResourceRepository m_resourceRepository;

    /**
     * Creates a new controller for accessing file resources.
     * 
     * @param rr The class for accessing resources.
     */
    public FileListingService(final ResourceRepository rr)
        {
        this.m_resourceRepository = rr;
        }
    
    public String getJson(final Object command)
        {
        final FileListingCommand bean = (FileListingCommand) command;
        
        final int pageIndex = bean.getPageIndex();
        final int resultsPerPage = bean.getResultsPerPage();
        final long instanceId = bean.getInstanceId();
        String groupName = bean.getGroupName();
        if (StringUtils.isBlank(groupName))
            {
            m_log.debug("Getting files from world group.");
            groupName = ShootConstants.WORLD_GROUP;
            }
        
        m_log.debug("Getting files from group: {}", groupName);
        
        final FileResourceResult result = 
            this.m_resourceRepository.getFileResources(pageIndex, 
                resultsPerPage, instanceId, groupName);
        
        final JsonResourceVisitor jsonVisitor = 
            new JsonResourceVisitor(result.getResources(), 
                result.getTotalResults());
        
        final String json = jsonVisitor.getJson();
        return json;
        }

    }
