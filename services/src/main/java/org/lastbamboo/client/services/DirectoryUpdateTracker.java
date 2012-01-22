package org.lastbamboo.client.services;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class for a directory that keeps track up updates.
 */
final class DirectoryUpdateTracker 
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final File m_directory;
    private Collection<File> m_storedDirectoryContents;
    private Collection<File> m_newDirectoryContents;

    private boolean m_markedForRemoval = false;

    private boolean m_removed;

    /**
     * Creates a new instance for the specified directory and permission
     * level.
     * @param dir The directory.
     */
    public DirectoryUpdateTracker(final File dir)
        {
        if (!dir.isDirectory())
            {
            m_log.error("Not a directory: "+dir);
            throw new IllegalArgumentException("Not a directory: "+dir);
            }
        this.m_directory = dir;
        this.m_storedDirectoryContents = new LinkedList<File>();
        }
    
    /**
     * Accessor for the directory.
     * @return The directory.
     */
    public File getDirectory()
        {
        return this.m_directory;
        }

    /**
     * Returns whether or not to update this directory.  Note this supports 
     * only one layer of directories for now.
     * 
     * @return <code>true</code> if the directory should be updated, 
     * otherwise <code>false</code>.
     */
    public boolean shouldUpdate()
        {
        if (!this.m_directory.isDirectory())
            {
            m_log.error("Not a directory: {}", this.m_directory);
            }
        this.m_newDirectoryContents = getFiles(this.m_directory);
        
        final boolean contentsEqual = 
            this.m_newDirectoryContents.equals(this.m_storedDirectoryContents);
        
        m_log.debug("Should update: "+!contentsEqual);
        
        return !contentsEqual;
        }
    
    private Collection<File> getFiles(final File dir)
        {
        // Only count files that are not hidden.
        final IOFileFilter filter = 
            new AndFileFilter(FileFileFilter.FILE, HiddenFileFilter.VISIBLE);
        final Collection<File> files = FileUtils.listFiles(dir, filter, null);
        return files;
        }

    /**
     * Lets this know the application has been updated with what's on disk,
     * and this should synchronize with the last disk check.
     */
    public void updated()
        {
        this.m_storedDirectoryContents = this.m_newDirectoryContents;
        }
    
    /**
     * Marks this directory as one that has been removed, indicating we should
     * also remove all the directory contents.
     */
    public void markForRemoval()
        {
        this.m_markedForRemoval = true;
        }
    
    /**
     * Checks if this directory has been marked for removal.
     * 
     * @return <code>true</code> if this directory has been marked for removal,
     * otherwise <code>false</code>.
     */
    public boolean markedForRemoval()
        {
        return this.m_markedForRemoval;
        }
    
    /**
     * Checks whether this directory has been successfully removed.
     * @return <code>true</code> if this directory has been successfully
     * removed, otherwise <code>false</code>.
     */
    public boolean removed()
        {
        return this.m_removed;
        }
    
    /**
     * Sets this directory as having been successfully removed.
     */
    public void setRemoved()
        {
        this.m_removed = true;
        }
    
    @Override
    public int hashCode()
        {
        return 17 * this.m_directory.hashCode();
        }
    
    @Override
    public boolean equals(final Object obj)
        {
        if (obj == this) return true;
        if (!(obj instanceof DirectoryUpdateTracker)) return false;
        final DirectoryUpdateTracker rd = (DirectoryUpdateTracker) obj;
        return this.m_directory.equals(rd.m_directory);
        }
    }