package org.lastbamboo.server.db;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.resource.Instance;

/**
 * Class for LittleShoot instances stored in the database.
 */
public class InstanceImpl implements Instance
    {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceImpl.class);
    
    /**
     * The ID of this user resource.
     */
    private Long m_id;
    
    private long m_instanceId;
    private boolean m_online;

    private String m_baseUri;

    private boolean m_repeatInfringer;

    /**
     * Required no-argument constructor Hibernate uses to generate new 
     * instances.
     */
    public InstanceImpl() 
        {
        // No argument constructor for hibernate to use.
        }
    
    /**
     * Creates a new instance.
     * 
     * @param instanceId The ID of the instance.
     * @param baseUri The base URI to use for accessing this user.
     * @param online Whether or not the instance is online. 
     */
    public InstanceImpl(final long instanceId, final String baseUri, 
        final boolean online)
        {
        if (StringUtils.isBlank(baseUri))
            {
            throw new IllegalArgumentException("Blank base URI!!");
            }
        this.m_instanceId = instanceId;
        this.m_online = online;
        this.m_baseUri = baseUri;
        this.m_repeatInfringer = false;
        }

    public long getInstanceId()
        {
        return this.m_instanceId;
        }

    public boolean isOnline()
        {
        return this.m_online;
        }

    public void setOnline(final boolean online)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Setting online status to: "+online);
            }
        this.m_online = online;
        }

    public void setInstanceId(final long userId)
        {
        this.m_instanceId = userId;
        }
    
    public String getBaseUri()
        {
        return m_baseUri;
        }

    public void setBaseUri(String baseUri)
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
    
    @Override
    public String toString()
        {
        return getClass().getSimpleName() + " id: "+getInstanceId()+
            " online: "+isOnline();
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
        final InstanceImpl other = (InstanceImpl) obj;
        if (m_instanceId != other.m_instanceId)
            return false;
        return true;
        }
    }
