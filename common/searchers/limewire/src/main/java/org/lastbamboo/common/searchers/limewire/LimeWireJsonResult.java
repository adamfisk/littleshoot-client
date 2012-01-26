package org.lastbamboo.common.searchers.limewire;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.rest.AbstractJsonRestResult;
import org.limewire.io.GUID;
import org.limewire.io.IpPort;
import org.limewire.io.IpPortSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.limegroup.gnutella.RemoteFileDesc;
import com.limegroup.gnutella.xml.LimeXMLDocument;

/**
 * A result from LimeWire.
 */
public class LimeWireJsonResult extends AbstractJsonRestResult 
    implements Comparable<LimeWireJsonResult>, FileData
    {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(LimeWireJsonResult.class);
    
    private final GUID m_queryGUID;
    private final Set<IpPort> m_alts = new IpPortSet();
    private final List<RemoteFileDesc> m_rfds = 
        new LinkedList<RemoteFileDesc>();

    
    /**
     * Creates a new LimeWire result.
     * 
     * @param sha1 The hash of the file.
     * @param rfd The base descriptor for the file.
     * @param hd The host data.
     * @param ipPorts The endpoints for the file.
     * @param thumbnailUri 
     * @param mediaType The media type.
     * @throws URISyntaxException If there's an error creating any associated
     * URIs.
     */
    public LimeWireJsonResult(final URI sha1, final RemoteFileDesc rfd, 
        final GUID queryGUID, final Set<? extends IpPort> ipPorts, 
        final URI thumbnailUri, final String mediaType) 
        throws URISyntaxException
        {
        super(newJsonResult(sha1, rfd, mediaType), rfd.getFileName(), sha1, 
            thumbnailUri, "limewire");
        
        this.m_queryGUID = queryGUID;
        this.m_alts.addAll(ipPorts);
        this.m_rfds.add(rfd);
        }
        
    private static JSONObject newJsonResult(final URI sha1, 
        final RemoteFileDesc rfd, final String mediaType)
        {
        final JSONObject json = new JSONObject();
        JsonUtils.put(json, "title", rfd.getFileName());
        JsonUtils.put(json, "size", rfd.getSize());
        JsonUtils.put(json, "sha1", sha1);
        JsonUtils.put(json, "mediaType", mediaType);
        JsonUtils.put(json, "spamRating", rfd.getSpamRating());
        JsonUtils.put(json, "creationTime", rfd.getCreationTime());
        
        // Add all the XML data.
        final LimeXMLDocument xml = rfd.getXMLDocument();
        if (xml != null)
            {
            final Collection<Entry<String, String>> set = xml.getNameValueSet();
            
            for (final Entry<String, String> entry : set)
                {
                try
                    {
                    json.put(entry.getKey(), entry.getValue());
                    }
                catch (final JSONException e)
                    {
                    LOG.warn("Could not read entry: "+entry, e);
                    }
                }
            }
        return json;
        }

    public int compareTo(final LimeWireJsonResult o)
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

	public void addData(RemoteFileDesc rfd,
			Set<? extends IpPort> altLocs) {
		synchronized (this.m_alts) {
			if (altLocs != null) {
				this.m_alts.addAll(altLocs);
			}
		}
		synchronized (this.m_rfds) {
			this.m_rfds.add(rfd);
		}
	}

	public GUID getQueryGUID() {
		return this.m_queryGUID;
	}
}
