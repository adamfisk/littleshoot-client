package org.lastbamboo.common.bug.server.processors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bug processor that simply outputs bug data to a file.
 */
public class FileAppendingBugProcessor implements BugProcessor
    {

    private final Logger LOG = LoggerFactory.getLogger(FileAppendingBugProcessor.class);
    
    public void processBug(final InputStream is, 
        final HttpServletRequest request)
        {
        final File bugFile = new File("bugs.txt");
        
        try
            {
            final OutputStream os = new FileOutputStream(bugFile, true);
            IOUtils.copy(is, os);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            }
        catch (final IOException e)
            {
            LOG.error("Could not write to file.");
            }
        
        }

    }
