package org.lastbamboo.client.services.command;

/**
 * Bean containing data for a request for search results.
 */
public class SearchResultsCommand
    {

    private int m_pageIndex;
    private boolean m_images;
    private boolean m_video;
    private boolean m_audio;
    private boolean m_documents;
    private boolean m_applications;
    private int m_resultsPerPage;
    private int m_callbackId;
    private String m_guid;
    
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
    public void setAudio(boolean audio)
        {
        m_audio = audio;
        }
    public boolean isDocuments()
        {
        return m_documents;
        }
    public void setDocuments(boolean documents)
        {
        m_documents = documents;
        }
    public boolean isImages()
        {
        return m_images;
        }
    public void setImages(boolean images)
        {
        m_images = images;
        }
    public boolean isVideo()
        {
        return m_video;
        }
    public void setVideo(boolean video)
        {
        m_video = video;
        }
    public int getCallbackId()
        {
        return this.m_callbackId;
        }
    public void setCallbackId(int callbackId)
        {
        m_callbackId = callbackId;
        }
    public int getPageIndex()
        {
        return m_pageIndex;
        }
    public void setPageIndex(int pageIndex)
        {
        m_pageIndex = pageIndex;
        }
    public int getResultsPerPage()
        {
        return m_resultsPerPage;
        }
    public void setResultsPerPage(int resultsPerPage)
        {
        m_resultsPerPage = resultsPerPage;
        }
    public void setGuid(final String guid)
        {
        m_guid = guid;
        }
    public String getGuid()
        {
        return m_guid;
        }
    
    }
