package org.lastbamboo.server.db;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.OnlineInstance;

/**
 * Resource class for online LittleShoot instances stored in the database.
 */
public class OnlineInstanceImpl implements OnlineInstance
    {

    private final Logger LOG = LoggerFactory.getLogger(OnlineInstanceImpl.class);
    
    /**
     * The HIbernate ID of this instance.
     */
    private Long m_id;
    
    private long m_instanceId;
    private Set<MetaFileResource> m_files = new HashSet<MetaFileResource>();

    private String m_baseUri;

    private boolean m_repeatInfringer;

    private String m_serverAddress;

    /**
     * Required no-argument constructor Hibernate uses to generate new 
     * instances.
     */
    public OnlineInstanceImpl() 
        {
        // No argument constructor for hibernate to use.
        }
    
    /**
     * Creates a new insetance
     * 
     * @param instanceId The ID of the instance.
     * @param baseUri The base URI to use for accessing this user.
     * @param serverAddress The address of the server the user is connected
     * to. 
     */
    public OnlineInstanceImpl(final long instanceId, final String baseUri, 
        final String serverAddress)
        {
        this.m_instanceId = instanceId;
        this.m_baseUri = baseUri;
        this.m_serverAddress = serverAddress;
        this.m_repeatInfringer = false;
        }

    public long getInstanceId()
        {
        return this.m_instanceId;
        }

    public void setInstanceId(final long userId)
        {
        this.m_instanceId = userId;
        }

    public void addMetaFileResource(final MetaFileResource mfr)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Adding meta file resource...");
            }
        this.m_files.add(mfr);
        }
    
    public void removeMetaFileResource(final MetaFileResource mfr)
        {
        this.m_files.remove(mfr);
        }

    public Set<MetaFileResource> getFiles()
        {
        return m_files;
        }

    public void setFiles(final Set<MetaFileResource> files)
        {
        m_files = files;
        }
    
    public String getBaseUri()
        {
        return m_baseUri;
        }

    public void setBaseUri(final String baseUri)
        {
        m_baseUri = baseUri;
        }
    
    public boolean isRepeatInfringer()
        {
        return this.m_repeatInfringer;
        }

    public void setRepeatInfringer(final boolean repeatInfringer)
        {
        this.m_repeatInfringer = repeatInfringer;
        }
    
    public String getServerAddress()
        {
        return m_serverAddress;
        }

    public void setServerAddress(final String serverAddress)
        {
        m_serverAddress = serverAddress;
        }
    
    @Override
    public String toString()
        {
        return getClass().getSimpleName() + " id: "+getInstanceId();
        }

    @Override
    public int hashCode()
        {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (m_instanceId ^ (m_instanceId >>> 32));
        return result;
        }

    @Override
    public boolean equals(final Object obj)
        {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OnlineInstanceImpl other = (OnlineInstanceImpl) obj;
        if (m_instanceId != other.m_instanceId)
            return false;
        return true;
        }
    }
