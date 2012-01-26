package org.lastbamboo.common.npapi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.littleshoot.util.BufferReader;
import org.littleshoot.util.ByteBufferUtils;
import org.littleshoot.util.LockedFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for processing stream data the LittleShoot NPAPI plugin has
 * written.
 */
public class NpapiFileProcessor implements LockedFileReader
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final BufferReader m_reader;
    
    /**
     * Creates a new class for processing data written by the NPAPI plugin to
     * our file data stream.
     * 
     * @param reader The class that actually reads the byte data.
     */
    public NpapiFileProcessor(final BufferReader reader)
        {
        if (reader == null)
            {
            m_log.error("Null reader!!");
            throw new NullPointerException("Null reader!!");
            }
        this.m_reader = reader;
        }
    
    public void readFile(final FileChannel fc) throws IOException
        {
        
        // NOTE: We can't use a MappedByteBuffer here because mapped byte 
        // buffers fail with locks on some operating systems.
        final int size;
        if (fc.size() > Integer.MAX_VALUE)
            {
            m_log.error("The data file is larger than the max int size!!");
            size = Integer.MAX_VALUE;
            }
        else
            {
            size = (int) fc.size();
            }
        if (size == 0)
            {
            m_log.error("No data in IPC buffer at for channel: {}", fc);
            return;
            }
        
        // We assume the file is smaller than 4,294,967,296 bytes here.  This
        // is a valid assumption in all cases barring a bug or some 
        // future fast Internet connection that can download at 4 GB/s.
        // We also don't need to be too worried about memory consumption here,
        // again because it's hard to write that much data in such a short 
        // time. 
        final ByteBuffer buf = ByteBuffer.allocate(size);
        fc.read(buf);
        buf.flip();
        
        m_log.info("Reading buffer: ");
        //m_log.debug(buf.toString());
        m_log.debug("\n"+ByteBufferUtils.toString(buf));
        m_log.debug("\n"+ByteBufferUtils.getHexdump(buf));
        System.out.println(getClass().getSimpleName() + " writing torrent hex");
        System.out.println(ByteBufferUtils.getHexdump(buf));
        while (buf.hasRemaining())
            {
            m_log.info("Sending buffer to reader: {}", this.m_reader);
            this.m_reader.readBuffer(buf);
            }
        
        /*
        final MappedByteBuffer map = 
            fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        
        while (map.hasRemaining())
            {
            final int version = map.getInt();
            final byte[] url = readBytes16(map);
            final byte[] httpHeaders = readBytes16(map);
            final byte[] streamName = readBytes16(map);
            final byte[] streamPath = readBytes16(map);
            final byte[] streamTempPath = readBytes16(map);
            processStream(url, httpHeaders, streamName, streamPath, streamTempPath);
            }
            */
        }

    }
