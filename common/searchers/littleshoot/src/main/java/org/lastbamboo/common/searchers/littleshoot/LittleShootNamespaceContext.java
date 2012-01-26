package org.lastbamboo.common.searchers.littleshoot;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for resolving namespaces in XML documents.
 */
public class LittleShootNamespaceContext implements NamespaceContext
    {

    private final Logger LOG = LoggerFactory.getLogger(LittleShootNamespaceContext.class);
    
    private static final String OPEN_SEARCH_NS = 
        "http://a9.com/-/spec/opensearch/1.1/";
    private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
    
    public String getNamespaceURI(final String prefix)
        {
        LOG.debug("Resolving prefix: "+prefix);
        if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("atom".equals(prefix)) return ATOM_NS;
        else if ("opensearch".equals(prefix)) return OPEN_SEARCH_NS;
        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
        return XMLConstants.NULL_NS_URI;
        }

    public String getPrefix(String arg0)
        {
        throw new UnsupportedOperationException();
        }

    public Iterator getPrefixes(String arg0)
        {
        throw new UnsupportedOperationException();
        }

    }
