package org.lastbamboo.common.searchers.yahoo;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.NodeUtils;
import org.littleshoot.util.FileUtils;
import org.w3c.dom.Element;


/**
 * Utilities for accessing Yahoo API.
 */
public final class YahooUtils
    {

    private static final Logger LOG = LoggerFactory.getLogger(YahooUtils.class);
    
    /**
     * Creates a usable title from an XML element.
     * 
     * @param element The element to create a title from.
     * @return The title.
     * @throws URISyntaxException If we cannot parse any URIs involved in
     * resolving the title.
     */
    public static String createTitle(final Element element) 
        throws URISyntaxException
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Creating title for node: "+ element.getNodeName());
            }
        final URI uri = NodeUtils.createUriValue(element, "ClickUrl");
        String extension = FilenameUtils.getExtension(uri.getPath());
    
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Found extension on URL: "+extension);
            }
        
        if (StringUtils.isBlank(extension))
            {
            extension = NodeUtils.createStringValue(element, "FileFormat");
            }
        final String title = NodeUtils.createStringValue(element, "Title");
        
        if (!StringUtils.isBlank(FilenameUtils.getExtension(title)))
            {
            return FileUtils.removeIllegalCharsFromFileName(title);
            }
        
        return FileUtils.removeIllegalCharsFromFileName(title) + "." + 
            extension;
        }
    }
