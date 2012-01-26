package org.lastbamboo.server.db;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.resource.AudioFileResource;
import org.lastbamboo.server.resource.FileAndInstances;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.OnlineInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Visitor that returns only the required information to download from a 
 * specific source.  The requirements of Atom formatting don't enable us to
 * eliminate too many fields here, unfortunately.
 */
public class OpenSearchSourceOnlyResourceVisitor extends
    AbstractOpenSearchResourceVisitor
    {

    private final Logger LOG = LoggerFactory.getLogger(OpenSearchSourceOnlyResourceVisitor.class);
    
    /**
     * Creates a new visitor for generating results in Amazon OpenSearch format.
     * 
     * @param fileAndInstances The resources to create results for.
     * @param doc The XML document for the results.
     */
    public OpenSearchSourceOnlyResourceVisitor(
        final FileAndInstances fileAndInstances, final Document doc)
        {
        super(doc);
        
        final MetaFileResource mfr = fileAndInstances.getMetaFileResource();
        final Set<OnlineInstance> users = fileAndInstances.getOnlineInstances();
        LOG.debug("Creating feed from "+users.size()+" users...");
        for (final OnlineInstance ur : users)
            {
            LOG.debug("Visiting user...");
            final Element entry = super.createEntry(mfr, ur);
            this.m_feed.appendChild(entry);
            }
        }

    @Override
    public Element visitAudioFileResource(final AudioFileResource afr)
        {
        // Does nothing for now.
        return null;
        }

    @Override
    public Element visitFileResource(final FileResource resource)
        {
        // Does nothing for now.        
        return null;
        }
    
    public Element visitMetaFileResource(final MetaFileResource resource)
        {
        LOG.debug("Visiting meta file resource...");
        final Element entry = createEntry(resource);
        this.m_feed.appendChild(entry);
        return entry;
        }

    }
