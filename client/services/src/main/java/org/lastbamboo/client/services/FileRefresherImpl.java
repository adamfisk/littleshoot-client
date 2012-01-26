package org.lastbamboo.client.services;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.client.services.download.DownloadTracker;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that continually keeps the database up-to-date with resources
 * on disk.
 */
public class FileRefresherImpl implements FileRefresher, HeartbeatListener
    {
    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * Directories to periodically update.
     */
    private final Map<File, DirectoryUpdateTracker> m_directories = 
        new ConcurrentHashMap<File, DirectoryUpdateTracker>();

    private final FileMapper m_fileMapper;
    
    /**
     * The remote repository.
     */
    private final RemoteResourceRepository m_remoteRepository;

    private final boolean m_active;

    private final DownloadTracker<MoverDState<Sha1DState<MsDState>>, 
        Downloader<MoverDState<Sha1DState<MsDState>>>> m_downloadTracker;

    private final Heartbeat m_heartbeat;
    
    
    /**
     * Creates a new class for refreshing resources.
     * 
     * @param localRepository The class for accessing local database resources.
     * @param remoteRepository The remote resource repository.
     * @param active Whether or not the refresher is active.
     */
    public FileRefresherImpl(final FileMapper localRepository,
        final RemoteResourceRepository remoteRepository, 
        final DownloadTracker <MoverDState<Sha1DState<MsDState>>,
        Downloader<MoverDState<Sha1DState<MsDState>>>> downloadTracker,
        final boolean active, final Heartbeat heartbeat)
        {
        this.m_fileMapper = localRepository;
        this.m_remoteRepository = remoteRepository;
        this.m_downloadTracker = downloadTracker;
        this.m_active = active;
        this.m_heartbeat = heartbeat;
        
        addDefaultSharedDirectories ();
        //addCustomSharedDirectories ();
        this.m_heartbeat.addListener(this);
        }

    public void onHeartbeat()
        {
        final Collection<DirectoryUpdateTracker> trackers = 
            this.m_directories.values();
        for (final DirectoryUpdateTracker tracker : trackers)
            {
            if (tracker.markedForRemoval() && tracker.removed())
                {
                this.m_directories.remove(tracker.getDirectory());
                }
            }
        final DirectoryUpdater updater = 
            new DirectoryUpdater(this.m_fileMapper, this.m_remoteRepository);
        for (final DirectoryUpdateTracker dir : this.m_directories.values()) {
            updater.execute(dir);
        }
        
        final FilesPurger filesUpdater = new FilesPurgerImpl(
            this.m_fileMapper, this.m_remoteRepository,
            this.m_downloadTracker);
        filesUpdater.purgeFiles();
        }
    
    /*
    private void addCustomSharedDirectories()
        {
        final Collection<DirectoryResource> drs = 
            this.m_localRepository.getDirectoryResources();
        for (final DirectoryResource dr : drs)
            {
            final File dir = new File(dr.getDir());
            if (!dir.isDirectory())
                {
                // This will happen if a user deletes a directory.
                this.m_localRepository.removeDirectoryResource(dir);
                }
            else
                {
                final DirectoryUpdateTracker rd = 
                    new DirectoryUpdateTracker(dir);
                this.m_directories.put(dir, rd);
                }
            }
        }
    */
    
    /**
     * Creates shared directories.  
     */
    private void addDefaultSharedDirectories()
        {
        m_log.debug("Creating shared directories...");

        // Note that files added to the downloads directory are shared 
        // separately.
        
        if (!this.m_active)
            {
            m_log.debug("Not adding public shared dir because we're inactive");
            return;
            }
        
        final Preferences prefs = Preferences.userRoot ();
        final File publicDir = new File(prefs.get (PrefKeys.SHARED_DIR, ""));
        
        if (!publicDir.isDirectory())
            {
            m_log.error("Public shared dir does not exist at: {}", publicDir);
            return;
            }
        m_log.debug("Will continually refresh directory at: "+publicDir);
        try
            {
            addDirectory(publicDir, "Default Shared");
            //this.m_fileMapper.
            //this.m_fileMapper.insertDirectoryResource(publicDir);
            }
        catch (final IOException e)
            {
            m_log.warn("Error adding default shared dir");
            }
        }

    public void addDirectory(final File dir, final String tags) 
        throws IOException
        {
        if (!dir.isDirectory())
            {
            m_log.error("Attempting to add a resource that's not a directory: " + 
                dir);
            }
        
        this.m_directories.put(dir, new DirectoryUpdateTracker(dir));
        }

    public void removeDirectory(final File dir)
        {
        final DirectoryUpdateTracker dur = this.m_directories.get(dir);
        dur.markForRemoval();
        }

    public void onSleep()
        {
        // We ignore sleep events.
        }
    }
