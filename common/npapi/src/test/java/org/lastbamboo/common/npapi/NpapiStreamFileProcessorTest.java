package org.lastbamboo.common.npapi;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.littleshoot.util.BufferReader;
import org.littleshoot.util.ByteBufferUtils;
import org.littleshoot.util.HttpUtils;
import org.littleshoot.util.LockedFileReader;
import org.littleshoot.util.Unsigned;

/**
 * Test for class that processes the plugin stream file.
 */
public class NpapiStreamFileProcessorTest
    {

    @Test public void testExistingFile() throws Exception 
        {
        final Map<String, String> httpHeaders = HttpUtils.toHeaderMap(
            "HTTP/1.1 200 OK\r\n"+
            "Age: 0\r\n"+
            "Cache-Control: max-age=7776000\r\n"+
            "Connection: keep-alive\r\n"+
            "Content-Length: 51064\r\n"+
            "Content-Type: application/x-bittorrent\r\n"+
            "Date: Thu, 15 Jan 2009 22:41:04 GMT\r\n"+
            "Etag: \"2397116603\"\r\n"+
            "Expires: Wed, 15 Apr 2009 22:41:03 GMT\r\n"+
            "Last-Modified: Thu, 11 Dec 2008 09:57:41 GMT\r\n"+
            "Server: lighttpd\r\n"+
            "Via: 1.1 varnish\r\n"+
            "X-Varnish: 762709227\r\n"+
            "\r\n");

        final AtomicInteger numConsumeCalls = new AtomicInteger(0);
        final NpapiStreamFileConsumer consumer = 
            new NpapiStreamFileConsumer()
            {

            public void consume(final String urlArg,
                final Map<String, String> httpHeadersArg, 
                final String streamNameArg,
                final String streamPathArg, final String streamTempPathArg)
                {
                assertEquals("http://torrents.thepiratebay.org/3990006/Elephants_Dream_Production_Files.3990006.TPB.torrent", urlArg);
                assertEquals(httpHeaders, httpHeadersArg);
                assertEquals("TORRENT STREAM TEST", streamNameArg);
                assertEquals("/Users/julian/.littleshoot/read/2297363448.torrent", streamPathArg);
                assertEquals("2297363448.tmp", streamTempPathArg);
                numConsumeCalls.incrementAndGet();
                }

            public void addListener(final NpapiStreamListener listener)
                {
                
                }
            };
            
        final BufferReader bufferReader =
            new NpapiStreamBufferReader(consumer);
        
        final LockedFileReader processor =  new NpapiFileProcessor(bufferReader);
        final File testDir = new File("src/test/resources");
        final File testDataFile = new File(testDir, "plugin_littleshoot_ipc.dat");
        testDataFile.deleteOnExit();
        final RandomAccessFile raf = new RandomAccessFile(testDataFile, "rw");
        final FileChannel fc = raf.getChannel();
        processor.readFile(fc);
        }
    
    @Test public void testProcessing() throws Exception
        {
        
        /*
         * Here's the data we're reading.
            
        struct npstream_entry
            {
            unsigned short version; // 16 bit - version of this struct
            unsigned short type; // 16 bit type of stream
            unsigned int bodyLength; // 32 bit - length of the body to follow
            unsigned int transactionId; // 32 bit - 
            char * url; // url
            unsigned short httpHeaderLength; // 16 bit - length of HTTP headers
            char * httpHeaders; // HTTP headers
            unsigned short length; // 16 bit - length of stream name
            char * streamName; // stream name
            unsigned short streamPathlen; // 16 bit - The length of the path string.
            char * streamPath; // The path to the stream file on disk.
            unsigned short streamTmpPathlen; // 16 bit - The length of the tmp path string.
            char * streamTmpPath; // The path to the tmp stream file on disk.
            };
         */
        final String url = "http://somesiteoutthere.com/newfile.torrent";
        final Map<String, String> httpHeaders = new TreeMap<String, String>();
        httpHeaders.put("content-length", "10");
        httpHeaders.put("content-type", "application/x-bittorrent");
        final StringBuilder unicode = new StringBuilder();
        //unicode.appendCodePoint(0x038E);
        //unicode.appendCodePoint(0x0680);
        //unicode.appendCodePoint(0x0687);
        //unicode.appendCodePoint(0x07E9);
        //unicode.appendCodePoint(0x041F);
        
        unicode.appendCodePoint(0x5C11);
        unicode.appendCodePoint(0x3057);
        unicode.appendCodePoint(0x20);
        unicode.appendCodePoint(0x0412);
        unicode.appendCodePoint(0x0441);
        unicode.appendCodePoint(0x0445);
        unicode.appendCodePoint(0x043E);
        unicode.appendCodePoint(0x0434);
        unicode.appendCodePoint(0x20);
        unicode.appendCodePoint(0x4E);
        unicode.appendCodePoint(0x50);
        unicode.appendCodePoint(0x41);
        unicode.appendCodePoint(0x50);
        unicode.appendCodePoint(0x49);
        unicode.appendCodePoint(0x20);
        unicode.appendCodePoint(0x6D41);
        unicode.appendCodePoint(0x308C);

        System.out.println(unicode.toString().getBytes("UTF-8").length);
        
        final String streamName = unicode.toString();
        final String streamPath = "/Users/adamfisk/test";
        final String streamTempPath = "/Users/adamfisk/testTemp";
        
        
        final AtomicInteger numConsumeCalls = new AtomicInteger(0);
        final NpapiStreamFileConsumer consumer = 
            new NpapiStreamFileConsumer()
            {

            public void consume(final String urlArg,
                final Map<String, String> httpHeadersArg, 
                final String streamNameArg,
                final String streamPathArg, final String streamTempPathArg)
                {
                System.out.println("Asked to consume data...");
                assertEquals(url, urlArg);
                assertEquals(httpHeaders, httpHeadersArg);
                assertEquals(streamName, streamNameArg);
                assertEquals(streamPath, streamPathArg);
                assertEquals(streamTempPath, streamTempPathArg);
                numConsumeCalls.incrementAndGet();
                }

            public void addListener(final NpapiStreamListener listener)
                {
                
                }
            };
            
        final BufferReader bufferReader =
            new NpapiStreamBufferReader(consumer);
        
        final LockedFileReader processor = 
            new NpapiFileProcessor(bufferReader);
        final File testDataFile = 
            new File("Testing-"+getClass().getSimpleName()+".dat");
        testDataFile.deleteOnExit();
        
        final FileOutputStream fos = new FileOutputStream(testDataFile);
        final FileChannel fc = fos.getChannel();
        
        
        final byte[] httpHeaderBytes = getMapBytes(httpHeaders);
        
        final ByteBuffer src = ByteBuffer.allocate(10000);
        
        final long bodyLength = 
            calculateBodyLength(2 + httpHeaderBytes.length, url, streamName, 
                streamPath, 
                streamTempPath);
        
        final int numDataBlocks = 6;
        
        final ByteBuffer testBuf = ByteBuffer.allocate(4);
        Unsigned.putUnsignedInt(testBuf, bodyLength);
        testBuf.flip();
        System.out.println("body length: "+bodyLength);
        System.out.println(ByteBufferUtils.getHexdump(testBuf));
        System.out.println();
        
        for (int i = 0; i < numDataBlocks; i++)
            {
            Unsigned.putUnsignedShort(src, 1); // version
            Unsigned.putUnsignedShort(src, 1); // type
            Unsigned.putUnsignedInt(src, bodyLength); // body length
            Unsigned.putUnsignedInt(src, 2147483647); // transaction id
            addString(src, url);
            addData(src, httpHeaderBytes);
            addString(src, streamName);
            addString(src, streamPath);
            addString(src, streamTempPath);
            
            // We add these extra data paths here to just throw extra random
            // data at the back to make sure we ignore future extensions we
            // don't currently understand.
            //addString(src, streamTempPath);
            //addString(src, streamTempPath);
            //addString(src, streamTempPath);
            //System.out.println("***************");
            System.out.println();
            }
        
        src.flip();
        fc.write(src);
        fc.close();
        fos.close();
        
        final RandomAccessFile raf = new RandomAccessFile(testDataFile, "rw");
        final FileChannel channel = raf.getChannel();
        processor.readFile(channel);
        //processor.readFile(testDataFile);
        
        assertEquals(numConsumeCalls.get(), numDataBlocks);
        }

    private long calculateBodyLength(final int startLength, final String... strs) throws UnsupportedEncodingException
        {
        int length = startLength;
        for (final String str : strs)
            {
            length += 2;
            length += str.getBytes("UTF-8").length;
            }
        return length;
        }

    private void addString(final ByteBuffer src, final String str) 
        throws UnsupportedEncodingException
        {
        final byte[] bytes = str.getBytes("UTF-8");
        addData(src, bytes);
        }

    private void addData(final ByteBuffer src, final byte[] bytes)
        {
        final ByteBuffer length = ByteBuffer.allocate(2);
        Unsigned.putUnsignedShort(length, bytes.length);
        length.flip();
        System.out.println(ByteBufferUtils.getHexdump(length) +" = length of "+new String(bytes));
        
        Unsigned.putUnsignedShort(src, bytes.length);
        src.put(bytes);
        }

    private byte[] getMapBytes(final Map<String, String> httpHeaders) 
        throws UnsupportedEncodingException
        {
        final StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\r\n");
        for (final Map.Entry<String, String> entry : httpHeaders.entrySet())
            {
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
            sb.append("\r\n");
            }
        sb.append("\r\n");
        final String entries = sb.toString();
        return entries.getBytes("UTF-8");
        }
    }
