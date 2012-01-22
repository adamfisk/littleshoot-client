package org.lastbamboo.client.services.command;

/**
 * Bean containing data for a request for a list of downloads on a given
 * page.
 */
public class DownloadsCommand {

    private int m_pageIndex;
    private int m_resultsPerPage;

    public int getPageIndex() {
        return m_pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        m_pageIndex = pageIndex;
    }

    public int getResultsPerPage() {
        return m_resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        m_resultsPerPage = resultsPerPage;
    }
}
