package org.lastbamboo.client.services;

import java.io.File;
import java.io.OutputStream;

import org.lastbamboo.common.download.DownloadVisitor;
import org.lastbamboo.common.download.DownloaderListener;
import org.lastbamboo.common.download.DownloaderState;
import org.lastbamboo.common.download.VisitableDownloader;

public class DownloaderTestAdaptor<DsT extends DownloaderState> implements VisitableDownloader<DsT>
    {

    public DownloaderTestAdaptor() 
        {
        
        }
    
    /*
    public void addListener(
            DownloaderListener<MoverDState<Sha1DState<MsDState>>> listener)
        {
        // TODO Auto-generated method stub

        }
        */

    public File getCompleteFile()
        {
        return new File("pom.xml");
        }

    public String getFinalName()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public File getIncompleteFile()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public long getSize()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    /*
    public MoverDState<Sha1DState<MsDState>> getState()
        {
        // TODO Auto-generated method stub
        return null;
        }
        */

    public boolean isStarted()
        {
        // TODO Auto-generated method stub
        return false;
        }

    /*
    public void removeListener(
            DownloaderListener<MoverDState<Sha1DState<MsDState>>> listener)
        {
        // TODO Auto-generated method stub

        }
        */

    public void start()
        {
        // TODO Auto-generated method stub

        }

    public void stop(final boolean removeFiles)
        {
        // TODO Auto-generated method stub

        }
    
    public void pause()
        {
        }
    
    public void resume()
        {
        }

    public void write(OutputStream os, boolean cancelOnStreamClose)
        {
        // TODO Auto-generated method stub

        }

    public <T> T accept(DownloadVisitor<T> visitor)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void addListener(DownloaderListener<DsT> listener)
        {
        // TODO Auto-generated method stub
        
        }

    public DsT getState()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void removeListener(DownloaderListener<DsT> listener)
        {
        // TODO Auto-generated method stub
        
        }

    public long getStartTime()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public long getTimeRemaining()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    }
