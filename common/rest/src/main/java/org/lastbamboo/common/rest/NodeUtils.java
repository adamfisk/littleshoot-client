package org.lastbamboo.common.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Utility class for processing XML nodes.
 */
public final class NodeUtils
    {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NodeUtils.class);
    
    /**
     * Utility method for creating a {@link URI} from an XML element.
     * @param element The element to create the URI from.
     * @param tagName The name of the tag containing the {@link URI}.
     * @return The {@link URI} contained in the tag.
     * @throws URISyntaxException If the string does not match the expected
     * {@link URI}.
     */
    public static URI createUriValue(final Element element, 
        final String tagName) throws URISyntaxException
        {
        final String stringValue = createStringValue(element, tagName);
        if (StringUtils.isBlank(stringValue))
            {
            return null;
            }
        return new URI(stringValue);
        }

    public static int createIntValue(final Element element,
        final String tagName)
        {
        final String stringValue = createStringValue(element, tagName);
        if (StringUtils.isBlank(stringValue))
            {
            return -1;
            }
        else if (!NumberUtils.isNumber(stringValue))
            {
            return -1;
            }
        else 
            {
            return Integer.parseInt(stringValue);
            }
        }
    

    /**
     * Creates a float value by extracting the specified sub-element from the
     * specified element.
     * 
     * @param element The element to extract the value from.
     * @param tagName The name of the attribute to extract.
     * @return The value.
     */
    public static float createFloatValue(final Element element, 
        final String tagName)
        {
        final String stringValue = createStringValue(element, tagName);
        if (StringUtils.isBlank(stringValue))
            {
            return -1;
            }
        else if (!NumberUtils.isNumber(stringValue))
            {
            return -1;
            }
        else 
            {
            return Float.parseFloat(stringValue);
            }
        }
    
    public static long createLongValue(final Element element, 
        final String tagName)
        {
        final String stringValue = createStringValue(element, tagName);
        if (StringUtils.isBlank(stringValue))
            {
            return -1L;
            }
        else if (!NumberUtils.isNumber(stringValue))
            {
            return -1L;
            }
        else 
            {
            return Long.parseLong(stringValue);
            }
        }
    
    public static boolean createBooleanValue(final Element element, 
        final String tagName)
        {
        final String stringValue = createStringValue(element, tagName);
        if (StringUtils.isBlank(stringValue))
            {
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Could not find tag: "+stringValue);
                }
            return false;
            }
        return Boolean.parseBoolean(stringValue);
        }
    
    public static Date createDateValue(final Element element, 
        final String tagName)
        {
        final String stringValue = createStringValue(element, tagName);
        if (StringUtils.isBlank(stringValue))
            {
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Could not find tag: "+stringValue);
                }
            return null;
            }
        return parseDateString(stringValue);
        }
    
    public static Date parseDateString(final String dateString)
        {
        final DateFormat format = 
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try
            {
            return format.parse(dateString);
            }
        catch (final ParseException e)
            {
            LOG.error("Received bad string: "+dateString, e);
            return null;
            }
        }

    public static String createStringValue(final Element element, 
        final String tagName)
        {
        // TODO: Wow, this is inefficient, but XPath causes other problems.
        //LOG.debug("Element: "+element.getNodeName());
        
        final NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
            {
            final Node curNode = list.item(i);
            if (curNode instanceof Element)
                {
                final Element curElement = (Element) curNode;
                if (curElement.getTagName().equalsIgnoreCase(tagName))
                    {
                    final Text textNode = (Text) curElement.getFirstChild();
                    if (textNode == null)
                        {
                        return StringUtils.EMPTY;
                        }
                    final String data = textNode.getData();
                    if (data == null)
                        {
                        return StringUtils.EMPTY;
                        }
                    final String text = data.trim();
                    //LOG.debug("Returning value: "+text);
                    return text;
                    }
                }
            }
        return StringUtils.EMPTY;
        }

    /**
     * Accesses a float attribute from a tag name and an attribute name within
     * the element identified by that tag.
     * 
     * @param element The top-level element.
     * @param tagName The tag name of the element within that element.
     * @param attribute
     * @return The String value of the attribute.
     */
    public static String getStringAttribute(final Element element, 
        final String tagName, final String attribute)
        {
        final NodeList nodes = element.getElementsByTagName(tagName);
        final Element node =  (Element) nodes.item(0);
        if (node == null)
            {
            return StringUtils.EMPTY;
            }
        return node.getAttribute(attribute);
        }
    
    /**
     * Accesses a float attribute from a tag name and an attribute name within
     * the element identified by that tag.
     * 
     * @param element The top-level element.
     * @param tagName The tag name of the element within that element.
     * @param attribute The name of the attribute to retrieve.
     * @return The float value of the attribute.
     */
    public static float getFloatAttribute(final Element element, 
        final String tagName, final String attribute)
        {
        final String value = getStringAttribute(element, tagName, attribute);
        if (NumberUtils.isNumber(value))
            {
            return Float.parseFloat(value);
            }
        else
            {
            LOG.debug("Not a float: {}", value);
            return -1;
            }
        }
    }
