package org.lastbamboo.common.searchers.limewire;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lastbamboo.common.rest.AbstractRestResult;
import org.littleshoot.util.ResourceTypeTranslatorImpl;
import org.limewire.io.GUID;
import org.limewire.io.IpPort;
import org.limewire.io.IpPortSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.limegroup.gnutella.RemoteFileDesc;

/**
 * A result from LimeWire.
 */
public class LimeWireResult extends AbstractRestResult 
    implements Comparable<LimeWireResult>, FileData
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final GUID m_queryGUID;
    private final Set<IpPort> m_alts = new IpPortSet();
    private final List<RemoteFileDesc> m_rfds = 
        new LinkedList<RemoteFileDesc>();

    // This is not really used in practice.
    private static final String URI_PREFIX =
        "http://www.littleshoot.org/images/icons/";

    /**
     * Creates a new LimeWire result.
     * 
     * @param sha1 The hash of the file.
     * @param rfd The base descriptor for the file.
     * @param hd The host data.
     * @param ipPorts The endpoints for the file.
     * @throws URISyntaxException If there's an error creating any associated
     * URIs.
     */
    public LimeWireResult(final URI sha1, final RemoteFileDesc rfd, 
        final GUID queryGUID, final Set<? extends IpPort> ipPorts) 
        throws URISyntaxException
        {
        super(rfd.getFileName(), sha1, 
            createThumbnailUrl(rfd.getFileName()), -1, -1, rfd.getSize(), 
            "limewire", sha1, null);

        this.m_queryGUID = queryGUID;
        this.m_alts.addAll(ipPorts);
        this.m_rfds.add(rfd);
        }

    private static URI createThumbnailUrl(final String title) 
        throws URISyntaxException
        {
        final String mediaType = 
            new ResourceTypeTranslatorImpl ().getType (title);
        
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

    public int compareTo(final LimeWireResult o)
        {
        final Integer count = new Integer(getNumSources());
        final Integer oCount = new Integer(o.getNumSources());
        if (count.equals(oCount))
            {
            return getTitle().compareToIgnoreCase(o.getTitle());
            }
        else
            {
            return oCount.compareTo(count);
            }
        }

    public RemoteFileDesc[] getAllRemoteFileDescs()
        {
        synchronized (this.m_rfds)
            {
            return this.m_rfds.toArray(new RemoteFileDesc[0]);
            }
        }

    public Collection<? extends IpPort> getAlts()
        {
        return this.m_alts;
        }
    
    @Override
    public int getNumSources()
        {
        return this.m_rfds.size() + this.m_alts.size();
        }

    public void addData(final RemoteFileDesc rfd, 
    		final Set<? extends IpPort> ipPorts)
        {
        synchronized (this.m_alts)
            {
            if (ipPorts != null)
                {
                this.m_alts.addAll(ipPorts);
                }
            }
        synchronized (this.m_rfds)
            {
            this.m_rfds.add(rfd);
            }
        }

	public GUID getQueryGUID() {
		return this.m_queryGUID;
	}
}
