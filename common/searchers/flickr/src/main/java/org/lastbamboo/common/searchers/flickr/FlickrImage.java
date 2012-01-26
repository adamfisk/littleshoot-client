package org.lastbamboo.common.searchers.flickr;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.AbstractRestResult;
import org.littleshoot.util.FileUtils;
import org.w3c.dom.Element;

/**
 * Class containing data for a single Flickr photo.
 */
public final class FlickrImage extends AbstractRestResult
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FlickrImage.class);
    
    /**
     * Creates a Flickr image bean from XML data.
     * 
     * @param element The XML element to extract image data from.
     * @throws URISyntaxException If there's any problem with the URIs contained
     * in the XML.
     */
    public FlickrImage(final Element element) throws URISyntaxException
        {
        // We just create the same URI for the thumbnail and main link and 
        // handle any necessary appending for specific image versions 
        // on the frontend.
        super(createTitle(element), createFlickrUri(element), 
            createFlickrUri(element), -1, -1, 0L, "flickr", null, null);
        }

    private static String createTitle(final Element element)
        {
        final String title;
        final String titleAttr = element.getAttribute("title");
        if (StringUtils.isNotBlank(titleAttr))
            {
            title = titleAttr + ".jpg";
            }
        else
            {
            // TODO: Internationalize this!!
            title = "Flickr Photo Number "+element.getAttribute("id")+".jpg";
            }
        
        LOG.debug("Returning title: "+title);
        return FileUtils.removeIllegalCharsFromFileName(title);
        }
    
    private static URI createFlickrUri(final Element element) 
        throws URISyntaxException
        {
        final String farmId = element.getAttribute("farm");
        final String id = element.getAttribute("id");
        final String serverId = element.getAttribute("server");
        final String secret = element.getAttribute("secret");
        
        
        //final String photoUrl = "http://farm"+farmId+".static.flickr.com/" +
          //  serverId+"/"+id+"_"+secret+"_"+toAppend+".jpg";
        
        // We just let the frontend determine which version to use for 
        // thumbnails or anything else.
        final String photoUrl = "http://farm"+farmId+".static.flickr.com/" +
            serverId+"/"+id+"_"+secret;////+".jpg";
        
        LOG.debug("Returning URI value: "+photoUrl);
        return new URI(photoUrl);
        }
    }
