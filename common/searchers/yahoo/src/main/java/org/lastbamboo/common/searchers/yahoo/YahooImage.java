package org.lastbamboo.common.searchers.yahoo;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.rest.AbstractRestResult;
import org.lastbamboo.common.rest.NodeUtils;
import org.w3c.dom.Element;

/**
 * Class that's responsible for create a Yahoo photo instance from returned
 * XML data.
 */
public final class YahooImage extends AbstractRestResult 
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(YahooImage.class);
    
    private static final XPathFactory s_xPathFactory = 
        XPathFactory.newInstance();
    private static final XPath s_xPath = s_xPathFactory.newXPath();
    
    private static final String YAHOO_IMAGES_NS = "urn:yahoo:srchmi";
    
    static
        {
        s_xPath.setNamespaceContext(new YahooNamespaceContext(YAHOO_IMAGES_NS));
        }

    /**
     * Creates a new photo instance.
     * 
     * @param element The XML element to create a photo from.
     * @param index The index of the result in the set.
     * @throws URISyntaxException If we could not parse the URIs in the
     * XML.
     */
    public YahooImage(final Element element, final int index) 
        throws URISyntaxException
        {
        super(YahooUtils.createTitle(element), 
            NodeUtils.createUriValue(element, "ClickUrl"),
            createThumbnailUrl(element, index),
            createThumbnailWidth(element, index),
            createThumbnailHeight(element, index),
            NodeUtils.createLongValue(element, "FileSize"), 
            "yahoo", null, null);
        }
    
    /**
     * Utility method for creating a thumbnail URL from Yahoo results.
     * 
     * @param element The XML.
     * @param index The index of this result in the parent document.
     * @return The URL for the thumbnail image, or <code>null</code> if there
     * was an error generating it.
     * @throws URISyntaxException If the returned value is not in the expected
     * URL format.
     */
    private static URI createThumbnailUrl(final Element element, final int index) 
        throws URISyntaxException
        {        
        // XPath indexing starts at 1, not 0.
        final int resultIndex = index + 1;
        final String xPath = 
            "/yahoo:ResultSet/yahoo:Result["+resultIndex+
            "]/yahoo:Thumbnail/yahoo:Url/text()";
        final String urlNode;
        try
            {
            urlNode = (String) s_xPath.evaluate(xPath, element, 
                XPathConstants.STRING);
            }
        catch (final XPathExpressionException e)
            {
            LOG.error("Bad XPath", e);
            return null;
            }
        LOG.debug("Found node: "+urlNode);
        if (StringUtils.isBlank(urlNode))
            {
            throw new IllegalArgumentException("Could not get URL for node: "+
                YahooUtils.createTitle(element));
            }
        return new URI(urlNode);
        }
    
    /**
     * Utility method for creating a thumbnail width from Yahoo results.
     * 
     * @param element The XML.
     * @param index The index of this result in the parent document.
     * @return The URL for the thumbnail image, or <code>null</code> if there
     * was an error generating it.
     * @throws URISyntaxException If the returned value is not in the expected
     * URL format.
     */
    private static int createThumbnailWidth(final Element element, 
        final int index) throws URISyntaxException
        {        
        final String xPathSuffix = "]/yahoo:Thumbnail/yahoo:Width/text()";
        return toInt(xPathSuffix, element, index);
        }

    /**
     * Utility method for creating a thumbnail width from Yahoo results.
     * 
     * @param element The XML.
     * @param index The index of this result in the parent document.
     * @return The URL for the thumbnail image, or <code>null</code> if there
     * was an error generating it.
     * @throws URISyntaxException If the returned value is not in the expected
     * URL format.
     */
    private static int createThumbnailHeight(final Element element, 
        final int index) throws URISyntaxException
        {        
        final String xPathSuffix = "]/yahoo:Thumbnail/yahoo:Height/text()";
        return toInt(xPathSuffix, element, index);
        }
    
    /**
     * Utility method for creating a thumbnail width from Yahoo results.
     * 
     * @param element The XML.
     * @param index The index of this result in the parent document.
     * @return The URL for the thumbnail image, or <code>null</code> if there
     * was an error generating it.
     * @throws URISyntaxException If the returned value is not in the expected
     * URL format.
     */
    private static int toInt(final String xPathSuffix, final Element element, 
        final int index) throws URISyntaxException
        {        
        // XPath indexing starts at 1, not 0.
        final int resultIndex = index + 1;
        final String xPath = 
            "/yahoo:ResultSet/yahoo:Result["+resultIndex+xPathSuffix;
            
        String stringVal = null;
        try
            {
            stringVal = (String) s_xPath.evaluate(xPath, element, 
                XPathConstants.STRING);
            return Integer.parseInt(stringVal);
            }
        catch (final XPathExpressionException e)
            {
            LOG.error("Could not parse.", e);
            return -1;
            }
        }
    }
