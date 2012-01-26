package com.limegroup.gnutella.downloader;

import java.io.File;
import java.io.RandomAccessFile;

import org.limewire.collection.Range;

import com.limegroup.gnutella.Downloader.DownloadState;

/**
 * A listener for downloads.
 */
public interface DownloadListener
    {

    void onRangeRead(Range range);

    void onPendingClose(DownloadState status);

    void onData(File file, RandomAccessFile raf, int numberOfChunks);

    void onComplete();

    }
