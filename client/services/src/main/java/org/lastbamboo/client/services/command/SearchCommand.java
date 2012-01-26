package org.lastbamboo.client.services.command;

import org.lastbamboo.common.rest.SearchRequestBean;

/**
 * Command for an individual search request.
 */
public class SearchCommand implements SearchRequestBean
    {

    private String m_keywords;
    private boolean m_images;
    private boolean m_video;
    private boolean m_audio;
    private boolean m_documents;
    private boolean m_applications;
    private String m_searchString;
    private long m_instanceId;
    private String m_groupName;
    private long m_userId;

    private boolean m_youTube;
    private boolean m_yahoo;
    private boolean m_littleShoot;
    private boolean m_flickr;
    private boolean m_limeWire;
    private boolean m_isoHunt;
    private boolean m_imeem;
    private boolean m_safeSearch;
    
    
    public String getKeywords()
        {
        return m_keywords;
        }

    public void setKeywords(final String keyword)
        {
        m_keywords = keyword;
        }

    public boolean isApplications()
        {
        return m_applications;
        }

    public void setApplications(boolean applications)
        {
        m_applications = applications;
        }

    public boolean isAudio()
        {
        return m_audio;
        }

    public void setAudio(final boolean audios)
        {
        m_audio = audios;
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

    public String getSearchString()
        {
        return this.m_searchString;
        }

    public void setSearchString(final String terms)
        {
        this.m_searchString = terms;
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

    public void setUserId(long userId)
        {
        m_userId = userId;
        }

    public long getUserId()
        {
        return m_userId;
        }

    public void setYouTube(boolean youTube)
        {
        m_youTube = youTube;
        }

    public boolean isYouTube()
        {
        return m_youTube;
        }

    public void setYahoo(boolean yahoo)
        {
        m_yahoo = yahoo;
        }

    public boolean isYahoo()
        {
        return m_yahoo;
        }

    public void setLittleShoot(boolean littleShoot)
        {
        m_littleShoot = littleShoot;
        }

    public boolean isLittleShoot()
        {
        return m_littleShoot;
        }

    public void setFlickr(boolean flickr)
        {
        m_flickr = flickr;
        }

    public boolean isFlickr()
        {
        return m_flickr;
        }

    public boolean isLimeWire()
        {
        return m_limeWire;
        }

    public void setLimeWire(boolean limeWire)
        {
        m_limeWire = limeWire;
        }

    public boolean isAllTypes()
        {
        return isApplications() &&
            isAudio() &&
            isDocuments() &&
            isImages() &&
            isVideo();
        }

    public boolean isAnySingleType()
        {
        int total = 0;
        if (isApplications()) total++;
        if (isAudio()) total++;
        if (isDocuments()) total++;
        if (isImages()) total++;
        if (isVideo()) total++;
        return total == 1;
        }

    public void setSafeSearch(boolean safeSearch)
        {
        m_safeSearch = safeSearch;
        }

    public boolean isSafeSearch()
        {
        return m_safeSearch;
        }

    public void setIsoHunt(final boolean isoHunt)
        {
        m_isoHunt = isoHunt;
        }

    public boolean isIsoHunt()
        {
        return m_isoHunt;
        }

    public void setIMeem(boolean imeem)
        {
        m_imeem = imeem;
        }

    public boolean isIMeem()
        {
        return m_imeem;
        }

    }
