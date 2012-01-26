package org.lastbamboo.common.searchers.youtube;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.AbstractRestResult;
import org.lastbamboo.common.rest.NodeUtils;
import org.littleshoot.util.FileUtils;
import org.w3c.dom.Element;


/**
 * Bean containing data for an individual YouTube video.
 */
public class YouTubeVideoImpl extends AbstractRestResult implements YouTubeVideo
    {

    private static final Logger LOG = LoggerFactory.getLogger(YouTubeVideoImpl.class);
    private final String m_description;
    private final String m_author;
    private final int m_lengthSeconds;
    private final int m_uploadTime;

    /**
     * Creates a new video from the specified XML Element.
     * 
     * @param element The XML Element to create a video from.
     *
     * @throws URISyntaxException If we could not parse any URI specified in
     * the XML.
     */
    public YouTubeVideoImpl(final Element element) throws URISyntaxException
        {
        super(createTitle(element), 
            createUrl(element),
            NodeUtils.createUriValue(element, "thumbnail_url"),  -1, -1, 
            NodeUtils.createLongValue(element, "length_seconds"), "youtube",
            null, null);
        
        this.m_author = 
            NodeUtils.createStringValue(element, "author");
        this.m_lengthSeconds = 
            NodeUtils.createIntValue(element, "length_seconds");
        this.m_description = 
            NodeUtils.createStringValue(element, "description");
        this.m_uploadTime = 
            NodeUtils.createIntValue(element, "upload_time");
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Created YouTube video result: "+this);
            }
        }

    private static URI createUrl(final Element element) 
        throws URISyntaxException
        {
        final String id = NodeUtils.createStringValue(element, "id");
        return new URI("http://www.youtube.com/v/"+id);
        }

    private static String createTitle(final Element element) 
        throws URISyntaxException
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Creating title for node: "+ element.getNodeName()); 
            }
        final String title;
        
        final String titleText = NodeUtils.createStringValue(element, "title");
        if (StringUtils.isNotBlank(titleText))
            {
            title = titleText;
            }
        else 
            {
            // Construct a unique name from the url.
            final URI uri = NodeUtils.createUriValue(element, "url");
            final String uriString = uri.toASCIIString();
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Query from uri: "+URIUtil.getPathQuery(uriString));
                }
            final String pathQuery = URIUtil.getPathQuery(uriString);
            final String id = StringUtils.substringAfterLast(pathQuery, "=");
            title = "YouTube Video "+id;
            }
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Returning title: "+title+".flv");
            }
        
        // We simply create html files for YouTube URLs when we go to 
        // download them.
        return FileUtils.removeIllegalCharsFromFileName(title) + ".flv";
        }
    
    public String getDescription()
        {
        return m_description;
        }

    public String getAuthor()
        {
        return m_author;
        }

    public int getLengthSeconds()
        {
        return m_lengthSeconds;
        }

    public int getUploadTime()
        {
        return m_uploadTime;
        }
    
    public long getFileSize()
        {
        return 0;
        }
    
    public String toString() 
        {
        return ClassUtils.getShortClassName(getClass()) + 
            " title: "+getTitle()+" at: "+getUrl();
        }

    }
