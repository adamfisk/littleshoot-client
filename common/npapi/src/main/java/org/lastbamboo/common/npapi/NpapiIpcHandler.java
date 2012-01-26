package org.lastbamboo.common.npapi;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.SystemUtils;
import org.littleshoot.util.BufferReader;
import org.littleshoot.util.LockedFileProcessor;
import org.littleshoot.util.LockedFileProcessorImpl;
import org.littleshoot.util.LockedFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * High-level class that manages the components for IPC with the LittleShoot
 * plugin.
 */
public class NpapiIpcHandler
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final NpapiStreamFileConsumer m_consumer;

    /**
     * Creates a new plugin IPC handler.
     * @param consumer Class that consumes IPC data.
     */
    public NpapiIpcHandler(final NpapiStreamFileConsumer consumer) 
        {
        this.m_consumer = consumer;
        }

    /**
     * Starts processing the IPC files.
     */
    public void start()
        {
        final File ipcLockFile = createFile("plugin_littleshoot_ipc.lck");
        final File ipcDataile = createFile("plugin_littleshoot_ipc.dat");

        final BufferReader streamBufferReader = 
            new NpapiStreamBufferReader(this.m_consumer);
        final LockedFileReader streamReader = 
            new NpapiFileProcessor(streamBufferReader);
        final LockedFileProcessor streamProcessor = new LockedFileProcessorImpl(
            streamReader, ipcLockFile, ipcDataile);
        streamProcessor.processFile();
        }

    private File createFile(final String name)
        {
        final File parentDir;
        if (SystemUtils.IS_OS_WINDOWS)
            {
            final File baseParent = 
                new File (System.getenv("APPDATA"), "LittleShoot");
            if (!baseParent.isDirectory())
                {
                if (!baseParent.mkdirs())
                    {
                    m_log.error("Could not create parent at: ", baseParent);
                    }
                }
            parentDir = new File (baseParent, "littleshoot");
            if (!parentDir.isDirectory())
                {
                if (!parentDir.mkdirs())
                    {
                    m_log.error("Could not create win lock file at: ", parentDir);
                    }
                }
            }
        else
            {
            parentDir = new File(SystemUtils.USER_HOME, ".littleshoot");
            }
        if (!parentDir.isDirectory())
            {
            if (!parentDir.mkdirs()) 
                {
                m_log.warn("Could not create the .littleshoot directory");
                }
            }
        final File file = new File(parentDir, name);
        try
            {
            file.createNewFile();
            }
        catch (final IOException e)
            {
            m_log.warn("Exception creating new file: "+file, e);
            }
        return file;
        }
    
    
    }
