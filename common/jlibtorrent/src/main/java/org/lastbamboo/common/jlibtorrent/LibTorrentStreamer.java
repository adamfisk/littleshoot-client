package org.lastbamboo.common.jlibtorrent;

import java.io.File;
import java.io.OutputStream;

public interface LibTorrentStreamer
    {

    void write(OutputStream os, boolean cancelOnStreamClose);

    void stop();

    File getIncompleteFile();

    }
