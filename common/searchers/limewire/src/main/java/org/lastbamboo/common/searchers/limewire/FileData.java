package org.lastbamboo.common.searchers.limewire;

import java.util.Collection;

import org.limewire.io.IpPort;

import com.limegroup.gnutella.RemoteFileDesc;

public interface FileData
    {

    RemoteFileDesc[] getAllRemoteFileDescs();

    Collection<? extends IpPort> getAlts();
    
    }
