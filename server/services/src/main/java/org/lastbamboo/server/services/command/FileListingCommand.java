package org.lastbamboo.server.services.command;

/**
 * Command bean for requests for file listings.
 */
public class FileListingCommand
    {

    private int m_pageIndex;
    private int m_resultsPerPage;
    private long m_instanceId;
    private String m_groupName;

    public int getPageIndex()
        {
        return m_pageIndex;
        }

    public void setPageIndex(final int pageIndex)
        {
        m_pageIndex = pageIndex;
        }

    public int getResultsPerPage()
        {
        return m_resultsPerPage;
        }

    public void setResultsPerPage(final int resultsPerPage)
        {
        m_resultsPerPage = resultsPerPage;
        }

    public long getInstanceId()
        {
        return this.m_instanceId;
        }

    public void setInstanceId(long instanceId)
        {
        this.m_instanceId = instanceId;
        }

    public void setGroupName(String groupName)
        {
        m_groupName = groupName;
        }

    public String getGroupName()
        {
        return m_groupName;
        }
    }
