package org.lastbamboo.common.searchers.limewire;

import java.util.Set;

import org.limewire.io.GUID;
import org.limewire.io.IpPort;

import com.limegroup.gnutella.RemoteFileDesc;

public interface LimeWireQueryResultListener {

	void onSearchResult(RemoteFileDesc rfd,
			Set<? extends IpPort> altLocs,
			GUID queryGUID);
}
