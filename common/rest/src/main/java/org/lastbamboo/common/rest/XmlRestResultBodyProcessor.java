package org.lastbamboo.common.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.littleshoot.util.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for processing REST results
 * 
 * @param <T> An instance of a REST result.
 */
public class XmlRestResultBodyProcessor<T extends RestResult> 
    implements RestResultBodyProcessor<T>
    {

    private final Logger LOG = LoggerFactory.getLogger(XmlRestResultBodyProcessor.class);
    private final String m_resultNodeName;
    private final RestResultFactory<T> m_resultFactory;
    private final DocumentBuilder m_documentBuilder;
    
    /**
     * Used for debugging to write XML feeds to a file.
     */
    private final static boolean WRITE_TO_FILE = false;
    
    /**
     * Creates a new processor that looks for XML nodes with the specified 
     * name for an individual result.
     * 
     * @param resultNodeName The name of a node indicating an individual result.
     * @param resultFactory The factory for creating our Java results from
     * an individual XML node.
     */
    public XmlRestResultBodyProcessor(final String resultNodeName, 
        final RestResultFactory<T> resultFactory)
        {
        this.m_resultNodeName = resultNodeName;
        this.m_resultFactory = resultFactory;
        final DocumentBuilderFactory factory = 
            DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        try
            {
            this.m_documentBuilder = factory.newDocumentBuilder();
            }
        catch (final ParserConfigurationException e)
            {
            LOG.error("Could not create doc builder", e);
            throw new IllegalArgumentException(
                "Could not create doc builder", e);
            }     
       
        }

    public RestResults<T> processResults(final InputStream is) 
        throws IOException
        {
        LOG.debug("Processing input stream...");
        writeToFile(is);
        
        try
            {
            final Collection<T> results = new LinkedList<T>();
            final Document doc = this.m_documentBuilder.parse(is);
            final NodeList children = 
                doc.getElementsByTagName(this.m_resultNodeName);
            for (int i = 0; i < children.getLength(); i++)
                {
                final Node node = children.item(i);
                try
                    {
                    final T result = 
                        this.m_resultFactory.createResult(doc,(Element)node, i);
                    
                    if (LOG.isDebugEnabled())
                        {
                        LOG.debug("Adding result: "+result);
                        }
                    results.add(result);
                    }
                catch (final ElementProcessingException e)
                    {
                    LOG.error("Could not process element", e);
                    }
                }
            
            if (children.getLength() == 0)
                {
                LOG.debug("No results.  Received:\n"+XmlUtils.toString(doc));
                }
            final RestResultsMetadata<T> metadata = 
                this.m_resultFactory.createResultsMetadata(doc);
            return new RestResultsImpl<T>(metadata, results);
            }
        catch (final SAXException e)
            {
            LOG.warn("Could not build document", e);
            return null;
            }
        }

    /**
     * Just for debugging XML feeds.
     * 
     * @param is The input stream.
     * @throws IOException If there's an error reading or writing.
     */
    private void writeToFile(final InputStream is) throws IOException
        {
        if (!WRITE_TO_FILE)
            {
            return;
            }
        final File outFile = new File("rest.xml");
        outFile.delete();
        final OutputStream os = new FileOutputStream(outFile);
        IOUtils.copy(is, os);
        os.flush();
        os.close();
        is.close();
        }
    }
