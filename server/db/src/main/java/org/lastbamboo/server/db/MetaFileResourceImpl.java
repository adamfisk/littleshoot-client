package org.lastbamboo.server.db;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.OnlineInstance;
import org.lastbamboo.server.resource.ResourceVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of classes containing metadata for individual files.
 */
public class MetaFileResourceImpl implements MetaFileResource
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    /**
     * The Hibernate ID of this resource.
     */
    private Long m_id;
    
    private String m_uri;
   
    private Set<OnlineInstance> m_onlineInstances = 
        new HashSet<OnlineInstance>();
    
    /**
     * We track the size independently because otherwise determining the number
     * of users with the file requires a potentially expensive join with
     * the online users table.
     */
    private int m_numOnlineInstances;
    
    private long m_size;
    private String m_sha1;
    private boolean m_takenDown;

    private String m_title;

    private String m_tags;
    private String m_titles;
    
    private String m_mimeType;

    private String m_mediaType;
    
    private int m_numDownloads;
    
    private Date m_publishTime;
    
    private int m_permission;

    private String m_groupName;
    
    private int m_numRatings;
    
    private double m_averageRating;
    
    /**
     * Required no-argument constructor Hibernate uses to generate new 
     * instances.
     */
    public MetaFileResourceImpl() 
        {
        // No argument constructor for hibernate to use.
        }
    
    /**
     * Creates a new meta file resource.
     * 
     * @param fr The {@link FileResource} containing data about the file.
     */
    public MetaFileResourceImpl(final FileResource fr)
        {
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Creating new meta file resource from: "+fr.getName());
            }
        
        m_uri = fr.getUri();
        m_title = fr.getTitle();
        
        // Start with just the one title.
        m_titles = m_title;
        
        // Start with the original tags.
        m_tags = fr.getTags() + " littleshoot";
        m_mimeType = fr.getMimeType();
        m_mediaType = fr.getMediaType();
        m_sha1 = fr.getSha1Urn();
        m_size = fr.getSize();
        m_takenDown = false;
        m_numOnlineInstances = 0;
        m_numDownloads = 0;
        m_publishTime = fr.getPublishTime();
        m_permission = fr.getPermission();
        this.m_groupName = fr.getGroupName();
        }
    
    public void addTags(final String tags)
        {
        // Check for the MySQL medium text limit.
        if (this.m_tags.length() + tags.length() < 16777214)
            {
            if (!this.m_tags.contains(tags))
                {
                this.m_tags += " ";
                this.m_tags += tags;
                m_log.debug("Tags now: "+this.m_tags);
                }
            else
                {
                m_log.debug("We already have the tag");
                }
            }
        else
            {
            m_log.warn("Reached tag length limit for "+this);
            }
        }

    public void addTitle(final String title)
        {
        // Check for the MySQL varchar limit.
        if (this.m_titles.length() + title.length() < 65534)
            {
            if (!this.m_titles.contains(title))
                {
                this.m_titles += " ";
                this.m_titles += title;
                m_log.debug("Titles now: "+this.m_titles);
                }
            else
                {
                m_log.debug("We already have the title.");
                }
            }
        else
            {
            m_log.warn("Reached title length limit for "+this);
            }
        }

    public void addInstance(final OnlineInstance instance)
        {
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Adding online instance: "+instance);
            }
        instance.addMetaFileResource(this);
        
        this.m_onlineInstances.add(instance);
        this.m_numOnlineInstances = this.m_onlineInstances.size();
        }
    
    public void removeInstance(final OnlineInstance instance)
        {
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Removing online user: "+instance);
            }
        instance.removeMetaFileResource(this);
        this.m_onlineInstances.remove(instance);
        this.m_numOnlineInstances = this.m_onlineInstances.size();
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Now "+this.m_numOnlineInstances+" instances online...");
            }
        }

    public Set<OnlineInstance> getInstances()
        {
        // Callers must not modify the returned collection.
        return m_onlineInstances;
        }

    public void setInstances(final Set<OnlineInstance> instances)
        {
        this.m_onlineInstances = instances;
        this.m_numOnlineInstances = instances.size();
        }
    
    public String getSha1Urn()
        {
        return this.m_sha1;
        }
    
    public void setSha1Urn(final String urn)
        {
        this.m_sha1 = urn;
        }

    public void setUri(String uri)
        {
        m_uri = uri;
        }

    public String getUri()
        {
        return m_uri;
        }

    public long getSize()
        {
        return this.m_size;
        }

    public void setSize(final long size)
        {
        this.m_size = size;
        }
    
    public boolean isTakenDown()
        {
        return m_takenDown;
        }

    public void setTakenDown(boolean takenDown)
        {
        m_takenDown = takenDown;
        }

    public String getMimeType()
        {
        return m_mimeType;
        }

    public void setMimeType(String mimeType)
        {
        m_mimeType = mimeType;
        }

    public String getTitle()
        {
        return m_title;
        }

    public void setTitle(String title)
        {
        m_title = title;
        }
    
    public int getNumOnlineInstances()
        {
        return m_numOnlineInstances;
        }

    public void setNumOnlineInstances(final int numOnlineUsers)
        {
        m_numOnlineInstances = numOnlineUsers;
        }
    
    public void setNumDownloads(int numDownloads)
        {
        m_numDownloads = numDownloads;
        }

    public int getNumDownloads()
        {
        return m_numDownloads;
        }
    
    public String getTags()
        {
        return this.m_tags;
        }
    
    public void setPublishTime(Date publishTime)
        {
        m_publishTime = publishTime;
        }

    public Date getPublishTime()
        {
        return m_publishTime;
        }
    
    public void setPermission(int permission)
        {
        m_permission = permission;
        }

    public int getPermission()
        {
        return m_permission;
        }

    public void setNumRatings(int numRatings)
        {
        m_numRatings = numRatings;
        }

    public int getNumRatings()
        {
        return m_numRatings;
        }

    public void setAverageRating(double averageRating)
        {
        m_averageRating = averageRating;
        }

    public double getAverageRating()
        {
        return m_averageRating;
        }
    
    public <T> T accept(final ResourceVisitor<T> visitor)
        {
        return visitor.visitMetaFileResource(this);
        }
    
    @Override
    public String toString()
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" title: ");
        sb.append(getTitle());
        sb.append(" uri: ");
        sb.append(getUri());
        sb.append(" sha1: ");
        sb.append(getSha1Urn());
        sb.append(" group: ");
        sb.append(this.m_groupName);
        return sb.toString();
        }

    @Override
    public int hashCode()
        {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((m_mediaType == null) ? 0 : m_mediaType.hashCode());
        result = PRIME * result + ((m_mimeType == null) ? 0 : m_mimeType.hashCode());
        result = PRIME * result + ((m_sha1 == null) ? 0 : m_sha1.hashCode());
        result = PRIME * result + (int) (m_size ^ (m_size >>> 32));
        result = PRIME * result + ((m_tags == null) ? 0 : m_tags.hashCode());
        result = PRIME * result + (m_takenDown ? 1231 : 1237);
        result = PRIME * result + ((m_title == null) ? 0 : m_title.hashCode());
        result = PRIME * result + ((m_titles == null) ? 0 : m_titles.hashCode());
        return result;
        }

    @Override
    public boolean equals(Object obj)
        {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MetaFileResourceImpl other = (MetaFileResourceImpl) obj;
        if (m_mediaType == null)
            {
            if (other.m_mediaType != null)
                return false;
            }
        else if (!m_mediaType.equals(other.m_mediaType))
            return false;
        if (m_mimeType == null)
            {
            if (other.m_mimeType != null)
                return false;
            }
        else if (!m_mimeType.equals(other.m_mimeType))
            return false;
        if (m_sha1 == null)
            {
            if (other.m_sha1 != null)
                return false;
            }
        else if (!m_sha1.equals(other.m_sha1))
            return false;
        if (m_size != other.m_size)
            return false;
        if (m_tags == null)
            {
            if (other.m_tags != null)
                return false;
            }
        else if (!m_tags.equals(other.m_tags))
            return false;
        if (m_takenDown != other.m_takenDown)
            return false;
        if (m_title == null)
            {
            if (other.m_title != null)
                return false;
            }
        else if (!m_title.equals(other.m_title))
            return false;
        if (m_titles == null)
            {
            if (other.m_titles != null)
                return false;
            }
        else if (!m_titles.equals(other.m_titles))
            return false;
        return true;
        }
    }
