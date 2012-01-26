package org.lastbamboo.server.db;

import java.util.Set;

import org.lastbamboo.server.resource.FileAndInstances;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.OnlineInstance;

/**
 * Class encapsulating data for a single file and all the online users with 
 * that file.
 */
public class FileAndInstancesImpl implements FileAndInstances
    {

    private final Set<OnlineInstance> m_onlineInstances;
    private final MetaFileResource m_metaFileResource;
    private final int m_size;

    /**
     * Creates a new bean for a file and the users with that file.
     * 
     * @param mfr The resource.  This can be <code>null</code> if the query 
     * returned no results.
     */
    public FileAndInstancesImpl(final MetaFileResource mfr)
        {
        m_metaFileResource = mfr;
        m_onlineInstances = mfr.getInstances();
        
        // This triggers the lazy loading of the data from the database.
        m_size = m_onlineInstances.size();
        }

    public MetaFileResource getMetaFileResource()
        {
        return m_metaFileResource;
        }

    public Set<OnlineInstance> getOnlineInstances()
        {
        return m_onlineInstances;
        }

    }
