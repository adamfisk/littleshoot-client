package org.lastbamboo.common.searchers.yahoo;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Namespace resolver for Yahoo XML results.
 */
public class YahooNamespaceContext implements NamespaceContext
    {

    private final Logger LOG = LoggerFactory.getLogger(YahooNamespaceContext.class);
    private final String m_namespace;
    
    /**
     * Creates a new Yahoo namespace context, resolving "yahoo" URIs to the 
     * specified namespace URI.
     * 
     * @param namespace The namespace URI.
     */
    public YahooNamespaceContext(final String namespace)
        {
        this.m_namespace = namespace;
        }

    public String getNamespaceURI(final String prefix)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Resolving prefix: "+prefix);
            }
        if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("yahoo".equals(prefix)) return m_namespace;
        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
        return XMLConstants.NULL_NS_URI;
        }

    public String getPrefix(String arg0)
        {
        throw new UnsupportedOperationException("prefixes not supported");
        }

    public Iterator getPrefixes(String arg0)
        {
        throw new UnsupportedOperationException("prefixes not supported");
        }

    }
