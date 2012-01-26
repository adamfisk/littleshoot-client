package org.lastbamboo.client.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.client.services.download.DownloadTracker;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.http.client.ForbiddenException;

/**
 * Class for purging files.  If the user deletes or moves a file, for example,
 * this will remove that file from the remote and local databases.
 */
public class FilesPurgerImpl implements FilesPurger
    {

    private static final Logger LOG = LoggerFactory.getLogger(FilesPurgerImpl.class);
    
    private final RemoteResourceRepository m_remoteRepository;

    private final FileMapper m_fileMapper;

    private final DownloadTracker<MoverDState<Sha1DState<MsDState>>, 
        Downloader<MoverDState<Sha1DState<MsDState>>>> m_downloadTracker;

    /**
     * Class for updating shared files.
     * 
     * @param fileMapper The local database.
     * @param rrr The remote database.
     */
    public FilesPurgerImpl(final FileMapper fileMapper, 
        final RemoteResourceRepository rrr,
        final DownloadTracker
            <MoverDState<Sha1DState<MsDState>>,
            Downloader<MoverDState<Sha1DState<MsDState>>>> downloadTracker)
        {
        this.m_fileMapper = fileMapper;
        this.m_remoteRepository = rrr;
        this.m_downloadTracker = downloadTracker;
        }

    public void purgeFiles()
        {
        LOG.debug("Purging resources...");
        final Collection<File> resources = this.m_fileMapper.getAllFiles();
        LOG.debug("Checking "+resources.size()+" for purging...");
        for (final File file : resources)
            {
            if (!file.isFile())
                {
                LOG.debug("Removing file: "+file);
                try
                    {
                    // Delete the remote one first.  That way we make sure
                    // it's no longer searchable while still retaining the
                    // data for it locally.
                    final URI uri = this.m_fileMapper.getUri(file);
                    this.m_remoteRepository.deleteResource(file, uri);
                    this.m_fileMapper.removeFile(uri);
                    this.m_downloadTracker.deleteDownloader(uri);
                    }
                catch (final IOException e)
                    {
                    LOG.warn("Could not delete resource: "+file, e);
                    }
                catch (final ForbiddenException e)
                    {
                    LOG.warn("Forbidden to delete resource: "+file, e);
                    }
                }
            }        
        }
    }
