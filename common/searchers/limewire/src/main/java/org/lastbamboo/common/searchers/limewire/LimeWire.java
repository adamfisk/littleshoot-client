package org.lastbamboo.common.searchers.limewire;

import java.net.URI;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.rest.SearchRequestBean;

import com.limegroup.gnutella.ConnectionManager;
import com.limegroup.gnutella.DownloadServices;
import com.limegroup.gnutella.downloader.RemoteFileDescFactory;

/**
 * Interface to the LimeWire backend.
 */
public interface LimeWire
    {

    void search(UUID uuid, String keywords, LimeWireSearchListener listener, 
        SearchRequestBean searchRequestBean);

    UUID newUuid();

    LimeWireJsonResult getResults(URI sha1);

    boolean isEnabled();

    /**
     * Sets whether LimeWire is enabled or not.
     * 
     * @param enabled <code>true</code> if LimeWire is enabled, otherwise
     * <code>false</code>.
     */
    void setEnabled(boolean enabled);
    
    /**
     * Starts LimeWire, including connecting to the network.
     */
    void start();

	ConnectionManager getConnectionManager();
	
	RemoteFileDescFactory getRemoteFileDescFactory();
	
	DownloadServices getDownloadServices();
    
    }
