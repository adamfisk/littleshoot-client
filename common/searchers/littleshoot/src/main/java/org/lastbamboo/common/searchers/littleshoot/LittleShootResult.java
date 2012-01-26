package org.lastbamboo.common.searchers.littleshoot;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.AbstractRestResult;
import org.lastbamboo.common.rest.NodeUtils;
import org.littleshoot.util.ResourceTypeTranslatorImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class containing data for a single LittleShoot search result.
 */
public final class LittleShootResult extends AbstractRestResult
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LittleShootResult.class);
    
    private static final XPathFactory s_xPathFactory = 
        XPathFactory.newInstance();
    private static final XPath s_path = s_xPathFactory.newXPath();
    
    private static final String URI_PREFIX =
        "http://www.littleshoot.org/images/icons/";
    
    static
        {
        s_path.setNamespaceContext(new LittleShootNamespaceContext());
        }
    
    /**
     * Creates a LittleShoot result from XML data.
     * 
     * @param doc The XML document containing all the results.
     * @param element The XML element to extract image data from.
     * @param index The index of the result in the returned set.
     * @throws URISyntaxException If there's any problem with the URIs contained
     * in the XML.
     */
    public LittleShootResult(final Document doc, final Element element, 
        final int index) throws URISyntaxException
        {
        super (getTitle (element),
               extractUrl(doc, index),
               getThumbnailUrl (element), -1, -1,
               extractSize(doc, index), 
               "littleshoot", 
               NodeUtils.createUriValue(element, "id"),
               extractMimeType(doc, index));
        }


    private static String getTitle(final Element element)
        {
        return NodeUtils.createStringValue (element, "title");
        }
    
    private static String getMediaType(final Element element)
        {
        return new ResourceTypeTranslatorImpl ().getType (getTitle (element));
        }
    
    private static URI getThumbnailUrl(final Element element) 
        throws URISyntaxException
        {
        final String mediaType = getMediaType (element);
        
        final Map<String,String> typesToUris = new HashMap<String,String> ();
        
        typesToUris.put ("document", "document.png");
        typesToUris.put ("audio", "audio.png");
        typesToUris.put ("video", "video.png");
        typesToUris.put ("image", "image.png");
        typesToUris.put ("application/mac", "application.png");
        typesToUris.put ("application/linux", "application.png");
        typesToUris.put ("application/win", "application.png");
        
        if (typesToUris.containsKey (mediaType))
            {
            final String suffix = typesToUris.get (mediaType);
            return new URI (URI_PREFIX + suffix);
            }
        else
            {
            return new URI (URI_PREFIX + "document.png");
            }
        }
    
    private static URI extractUrl(final Document doc, final int index)
        {
        final int xPathIndex = index + 1;
        final String sizePath = 
            "/atom:feed/atom:entry["+xPathIndex+"]/atom:link/@href";
        try
            {
            final String uriString = 
                (String) s_path.evaluate(sizePath, doc, XPathConstants.STRING);
            
            LOG.debug("length: "+uriString);
            return new URI(uriString);
            }
        catch (final XPathExpressionException e)
            {
            LOG.error("Bad XPath", e);
            throw new IllegalArgumentException("Bad XPath", e);
            }
        catch (final URISyntaxException e)
            {
            LOG.error("Bad URI", e);
            throw new IllegalArgumentException("Bad URI", e);
            }
        }
    
    private static long extractSize(final Document doc, final int index)
        {
        final int xPathIndex = index + 1;
        final String sizePath = 
            "/atom:feed/atom:entry["+xPathIndex+"]/atom:link/@length";
        try
            {
            final String sizeString = 
                (String) s_path.evaluate(sizePath, doc, XPathConstants.STRING);
            
            LOG.debug("length: "+sizeString);
            return Long.parseLong(sizeString);
            }
        catch (final XPathExpressionException e)
            {
            LOG.error("Bad XPath", e);
            throw new IllegalArgumentException("Bad document", e);
            }
        }
    
    private static String extractMimeType(final Document doc, final int index)
        {
        final int xPathIndex = index + 1;
        final String sizePath = 
            "/atom:feed/atom:entry["+xPathIndex+"]/atom:type/text()";
        try
            {
            final String mimeString = 
                (String) s_path.evaluate(sizePath, doc, XPathConstants.STRING);
            
            LOG.debug("mime: "+mimeString);
            return mimeString;
            }
        catch (final XPathExpressionException e)
            {
            LOG.error("Bad XPath", e);
            throw new IllegalArgumentException("Bad document", e);
            }
        }
    }
