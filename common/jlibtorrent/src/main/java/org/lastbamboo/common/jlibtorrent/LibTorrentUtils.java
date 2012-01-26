package org.lastbamboo.common.jlibtorrent;

/**
 * Utility methods for LibTorrent.
 */
public class LibTorrentUtils
    {
    
    /*
     * Here's the enum of states as a reference.
     * enum state_t
        {
            queued_for_checking,
            checking_files,
            downloading_metadata,
            downloading,
            finished,
            seeding,
            allocating
        };
     */
    public static final int QUEUED_FOR_CHECKING = 0;
    public static final int CHECKING_FILES = 1;
    public static final int DOWNLOADING_METADATA = 2;
    public static final int DOWNLOADING = 3;
    public static final int FINISHED = 4;
    public static final int SEEDING = 5;
    public static final int ALLOCATING = 6;
    
    
    public static final int PAUSED = 200;
    public static final int FAILED = 201;
    public static final int INVALID_HANDLE = -1;

    /**
     * Determines if the specified state is complete.
     * 
     * @param state The state to check.
     * @return <code>true</code> if the state is considered complete, 
     * otherwise <code>false</code>.
     */
    public static boolean isComplete(final int state)
        {
        return 
            state == LibTorrentUtils.FINISHED || 
            state == LibTorrentUtils.SEEDING;
        }

    }
