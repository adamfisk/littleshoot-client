package org.lastbamboo.common.npapi;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.littleshoot.util.BufferReader;
import org.littleshoot.util.ByteBufferUtils;
import org.littleshoot.util.HttpUtils;
import org.littleshoot.util.Unsigned;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for reading plugin stream data from a byte buffer.  This does one
 * pass on the buffer -- it does not read all the data available.
 */
public class NpapiStreamBufferReader implements BufferReader
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final NpapiStreamFileConsumer m_consumer;

    /**
     * Creates a new reader.
     * 
     * @param consumer The class that will consume the read data.
     */
    public NpapiStreamBufferReader(final NpapiStreamFileConsumer consumer)
        {
        if (consumer == null)
            {
            m_log.error("Null consumer!!");
            throw new NullPointerException("Null consumer!!");
            }
        this.m_consumer = consumer;
        }

    public void readBuffer(final ByteBuffer buf)
        {
        /*
         * Here's the data we're reading.
            
        struct npstream_entry
            {
            unsigned short version; // 16 bit - version of this struct
            unsigned short type; // 16 bit type of stream
            unsigned int bodyLength; // 32 bit - length of the body to follow
            unsigned int transactionId; // 32 bit - if of the transaction
            unsigned short urlLength; // 16 bit - length of url
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
        final int version = Unsigned.getUnsignedShort(buf);
        final int type = Unsigned.getUnsignedShort(buf);
        final long bodyLength = Unsigned.getUnsignedInt(buf);
        final long transactionId = Unsigned.getUnsignedInt(buf);

        final int numFields = 5;
        final byte[][] data = readData(buf, numFields);
        final int readBodyLength = calculateReadSize(data);
        
        if (bodyLength > readBodyLength)
            {
            m_log.error("We've read more than the body length -- protocol error");
            }
        
        // There might be extra data from newer versions of the protocol we 
        // don't currently understand, so just read the extra data.
        if (readBodyLength < bodyLength)
            {
            final int toRead = (int) (bodyLength - readBodyLength);
            final int newPosition = buf.position() + toRead;
            buf.position(newPosition);
            }
        
        // Check for errors.
        if (version != 1)
            {
            m_log.error("Did not understand version: "+version);
            return;
            }
        final NpapiLittleShootMessageType messageType =
            NpapiLittleShootMessageType.toType(type);
        if (messageType == null)
            {
            m_log.error("Did not understand message type: "+ type);
            return;
            }
        
        m_log.info("Switching on message type for consumer: {}", this.m_consumer);
        switch (messageType)
            {
            case TORRENT_NOTIFY:
                this.m_consumer.consume(stringify(data[0]), 
                    HttpUtils.toHeaderMap(data[1]), 
                    stringify(data[2]), stringify(data[3]), 
                    stringify(data[4]));
                break;
            default:
                m_log.error("Did not understand message type: " + messageType+
                    " for type int: "+type);
            }
        
        if (bodyLength == 0x000000CC)
            {
            verifyTestFile(version, type, bodyLength, transactionId, data);
            }
        }

    private void verifyTestFile(int version, int type, long bodyLength,
            long transactionId, byte[][] data)
        {
        verify("version", version, 1);
        verify("type", type, 1);
        verify("bodyLength", bodyLength, 0x000000CC);
        verify("trans ID", transactionId, 0x7FFFFFFF);
        
        final StringBuilder unicode = new StringBuilder();
        
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
        byte[] uniBytes;
        try
            {
            uniBytes = unicode.toString().getBytes("UTF-8");
            }
        catch (final UnsupportedEncodingException e)
            {
            throw new RuntimeException("Bad encoding!!!?????", e);
            }

        final byte[] url = data[0];
        final byte[] http = data[1];
        final byte[] name = data[2];
        final byte[] path = data[3];
        final byte[] tempPath = data[4];
        verify ("url", url, "http://somesiteoutthere.com/newfile.torrent");
        verify ("http headers", http, "HTTP/1.1 200 OK\r\ncontent-length:10\r\ncontent-type:application/x-bittorrent\r\n\r\n");
        verifyRaw ("name", name, uniBytes);
        verify ("path", path, "/Users/adamfisk/test");
        verify ("tempPath", tempPath, "/Users/adamfisk/testTemp");
        
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("************  TEST SUCCEEDED!!!    ************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        
        m_log.warn("***********************************************");
        m_log.warn("***********************************************");
        m_log.warn("***********************************************");
        m_log.warn("***********************************************");
        m_log.warn("************  TEST SUCCEEDED!!!    ************");
        m_log.warn("***********************************************");
        m_log.warn("***********************************************");
        m_log.warn("***********************************************");
        m_log.warn("***********************************************");
        }

    private void verifyRaw(String field, byte[] data, byte[] test)
        {
        if (!Arrays.equals(data, test))
            {
            m_log.error("Test failed for field: "+field);
            throw new RuntimeException("Bad field for "+field);
            }
        }

    private void verify(final String field, final byte[] data, 
        final String test)
        {
        final String dataStr = stringify(data);
        if (!test.equals(dataStr))
            {
            m_log.error("Test failed for field: "+field);
            final String diff = StringUtils.difference(test, dataStr);
            throw new RuntimeException("Bad data for "+field+" expected\n'"+
                test+"'\nbut was\n'"+dataStr+"'\nwith difference: \n"+diff);
            }
        }

    private void verify(String field, long test, long i)
        {
        if (test != i)
            {
            m_log.error("Test failed for field: "+field);
            throw new RuntimeException("Bad field for "+field+" expected "+i+" but was "+test);
            }
        }

    private void verify(String field, int test, int i)
        {
        if (test != i)
            {
            m_log.error("Test failed for field: "+field);
            throw new RuntimeException("Bad field for "+field+" expected "+i+" but was "+test);
            }
        }

    private int calculateReadSize(final byte[][] data)
        {
        int size = 0;
        for (int i = 0; i < data.length; i++)
            {
            size += 2;
            size += data[i].length;
            }
        return size;
        }

    private byte[][] readData(final ByteBuffer buf, final int numFields)
        {
        final byte[][] data = new byte[numFields][];
        for (int i = 0; i < numFields; i++)
            {
            try 
                {
                final byte[] newData = ByteBufferUtils.readBytes16(buf);
                data[i] = newData;
                }
            catch (final BufferUnderflowException e)
                {
                m_log.error("Underflow field: "+toFieldName(i));
                throw e;
                }
            }
        return data;
        }

    private String toFieldName(final int i)
        {
        switch (i)
            {
            case 0: return "URL";
            case 1: return "HTTP Headers";
            case 2: return "File Name";
            case 3: return "Path";
            case 4: return "Temp path";
            }
        return "Unknown field!!";
        }

    private String stringify(final byte[] data)
        {
        try
            {
            return new String(data, "UTF-8");
            }
        catch (final UnsupportedEncodingException e)
            {
            m_log.error("No UTF-8???", e);
            return "";
            }
        }

    }
