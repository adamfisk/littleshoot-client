package org.lastbamboo.server.services.command;

/**
 * Command class for incoming search requests.
 */
public class SearchCommand
    {

    private String m_keywords;
    private int m_startPage;
    private int m_itemsPerPage;
    
    private String m_os;
    private boolean m_applications;
    private boolean m_audio;
    private boolean m_documents;
    private boolean m_images;
    private boolean m_video;
    
    private long m_userId;
    private long m_instanceId;
    private String m_groupName;

    public boolean isApplications()
        {
        return m_applications;
        }

    public void setApplications(final boolean applications)
        {
        m_applications = applications;
        }

    public boolean isAudio()
        {
        return m_audio;
        }

    public void setAudio(final boolean audio)
        {
        m_audio = audio;
        }

    public boolean isDocuments()
        {
        return m_documents;
        }

    public void setDocuments(final boolean documents)
        {
        m_documents = documents;
        }

    public boolean isImages()
        {
        return m_images;
        }

    public void setImages(final boolean images)
        {
        m_images = images;
        }

    public boolean isVideo()
        {
        return m_video;
        }

    public void setVideo(final boolean videos)
        {
        m_video = videos;
        }

    public void setKeywords(final String keywords)
        {
        m_keywords = keywords;
        }

    public String getKeywords()
        {
        return this.m_keywords;
        }

    public String getOs()
        {
        return m_os;
        }

    public void setOs(final String os)
        {
        m_os = os.toLowerCase();
        }

    public int getItemsPerPage()
        {
        return m_itemsPerPage;
        }

    public void setItemsPerPage(int itemsPerPage)
        {
        m_itemsPerPage = itemsPerPage;
        }

    public int getStartPage()
        {
        return m_startPage;
        }

    public void setStartPage(int startPage)
        {
        m_startPage = startPage;
        }

    public void setGroupName(String groupName)
        {
        m_groupName = groupName;
        }

    public String getGroupName()
        {
        return m_groupName;
        }

    public void setInstanceId(long instanceId)
        {
        m_instanceId = instanceId;
        }

    public long getInstanceId()
        {
        return m_instanceId;
        }

    public long getUserId()
        {
        return m_userId;
        }

    public void setUserId(long userId)
        {
        m_userId = userId;
        }
    }
