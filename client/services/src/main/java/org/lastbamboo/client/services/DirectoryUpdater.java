package org.lastbamboo.client.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.lastbamboo.common.http.client.ForbiddenException;
import org.littleshoot.util.Sha1Hasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class for updating individual directories of file resources on disk.
 */
public class DirectoryUpdater
    {
    
    /**
     * Logger for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger(DirectoryUpdater.class);

    /**
     * The class for managing persistent resources.
     */
    private final FileMapper m_fileMapper;
    
    /**
     * The remote repository.
     */
    private final RemoteResourceRepository m_remoteRepository;


    /**
     * Creates a new updater with the specified class for accessing local
     * resources and with the specified ID for the logged in user.
     * 
     * @param localRepository The remote local database repository.
     * @param repository The remote resource repository.
     */
    public DirectoryUpdater(final FileMapper localRepository,
        final RemoteResourceRepository repository)
        {
        this.m_fileMapper = localRepository;
        this.m_remoteRepository = repository;
        }
    
    public void execute(final DirectoryUpdateTracker dut)
        {
        LOG.debug("Checking directory: "+dut.getDirectory());
        
        if (dut.markedForRemoval())
            {
            final File dir = dut.getDirectory();
            if (stopSharingContents(dir))
                {
                dut.setRemoved();
                }
            }
        else if (dut.shouldUpdate())
            {       
            final File dir = dut.getDirectory();
            //final DirectoryResource dr = 
                //this.m_localRepository.getDirectoryResource(dir);
            
            // This could happen due to thread scheduling, but it's fairly
            // unlikely.
            //if (dr == null)
                //{
                //LOG.warn("Directory no longer shared: "+dir);
                //return;
                //}
            
            updateFiles(dir);
            dut.updated();
            //if (refreshDirectory(dir))
                //{
               // dut.updated();
                //}
            }
        
        // At this point we've loaded all the resources.
        LOG.debug("Resources loaded...");
        }

    private boolean stopSharingContents(final File dir)
        {
        final Collection<File> files = listFiles(dir);
        boolean allDeleted = true;
        for (final File file : files)
            {
            LOG.debug("Updating resource...");
            if (!deleteFile(file))
                {
                allDeleted = false;
                }
            }
        return allDeleted;
        }

    /*
    private boolean refreshDirectory(final File dir)
        {
        LOG.trace("Refreshing dir: "+dir);
        if (dir == null)
            {
            throw new NullPointerException("null directory");
            }
        else
            {
            updateFiles(dir);
            }
        
        // Update the data for the directory.
        try
            {
            this.m_fileMapper.updateDirectoryData(dir);
            return true;
            }
        catch (final IOException e)
            {
            return false;
            }
        }
*/

    private void updateFiles(final File dir)
        {
        final Collection<File> files = listFiles(dir);
        for (final File file : files)
            {
            LOG.debug("Updating resource...");
            updateFile(file);
            }
        }

    private void updateFile(final File file)
        {
        // This call is key.  It returns true if the file was not previously 
        // already in the database.  In that case, it's a completely new file, 
        // and we need to publish it to the central server.
        if (this.m_fileMapper.updateDirectoryFile(file))
            {
            LOG.debug("Inserting into remote repository...");
            try
                {
                final URI sha1 = Sha1Hasher.createSha1Urn(file);
                this.m_remoteRepository.insertResource(file, sha1, sha1);
                }
            catch (final IOException e)
                {
                // If it didn't update remotely, don't consider it updated
                // locally.  
                LOG.warn("Server error updating file", e);
                this.m_fileMapper.removeFile(file);
                }
            catch (final ForbiddenException e)
                {
                LOG.warn("Forbidden to publish", e);
                this.m_fileMapper.removeFile(file);
                }
            catch (final RuntimeException e)
                {
                LOG.warn("Error inserting file", e);
                this.m_fileMapper.removeFile(file);
                }
            }
        }

    private boolean deleteFile(final File file)
        {   
        /*
        try
            {
            this.m_remoteRepository.deleteResource(file);
            this.m_fileMapper.removeFile(file);
            return true;
            }
        catch (final IOException e)
            {
            LOG.debug("Could not delete resource: "+file, e);
            return false;
            }
            */
        throw new UnsupportedOperationException("Not working for now");
        }
    
    private Collection<File> listFiles(final File dir)
        {
        final IOFileFilter filter = 
            new AndFileFilter(FileFileFilter.FILE, HiddenFileFilter.VISIBLE);
        final Collection<File> files = FileUtils.listFiles(dir, filter, null);
        return files;
        }
    }
