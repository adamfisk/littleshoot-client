package org.lastbamboo.client.services.command;


/**
 * Bean containing data for stopping a download
 */
public class StopDownloadCommand extends UriDownloadCommand
    {

    private boolean m_removeFiles;

    public void setRemoveFiles(boolean removeFiles)
        {
        m_removeFiles = removeFiles;
        }

    public boolean isRemoveFiles()
        {
        return m_removeFiles;
        }
    
    }
