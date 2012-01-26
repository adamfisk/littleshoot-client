package org.lastbamboo.common.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

import junit.framework.TestCase;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.nio.AcceptorListener;
import org.lastbamboo.common.nio.NioReaderWriter;
import org.lastbamboo.common.nio.NioServer;
import org.lastbamboo.common.nio.NioServerImpl;
import org.lastbamboo.common.nio.SelectorManager;
import org.lastbamboo.common.nio.SelectorManagerImpl;
import org.lastbamboo.common.protocol.ProtocolHandler;
import org.lastbamboo.common.protocol.ReaderWriter;
import org.littleshoot.util.NetworkUtils;

/**
 * Test for NIO where the server implementation just echos all data back to the
 * client.
 */
public final class EchoTest extends TestCase
    {

    private static Log LOG = LogFactory.getLog(EchoTest.class);

    private final int PORT = 7891;

    /**
     * Tests a server implementation that just echos any data written to the
     * server back to the client.
     *
     * @throws Exception If any unexpected error occurs.
     */
    public void testEcho() throws Exception
        {
        LOG.trace("Running test...");
        startServer();

        final Socket client = new Socket(NetworkUtils.getLocalHost(), PORT);
        final OutputStream os = client.getOutputStream();
        final InputStream is = client.getInputStream();
        final WritableByteChannel writableChannel = Channels.newChannel(os);
        final ReadableByteChannel readableChannel = Channels.newChannel(is);

        final byte[] bigBytes = new byte[100000 * 4];
        write(writableChannel, bigBytes);
        final byte[] bigBytes2 = new byte[100000 * 4];
        write(writableChannel, bigBytes2);

        final byte[] receiveBytes = new byte[bigBytes.length];
        readBytes(readableChannel, receiveBytes);
        assertTrue(equals(bigBytes, receiveBytes));

        final byte[] receiveBytes2 = new byte[bigBytes2.length];
        readBytes(readableChannel, receiveBytes2);
        assertTrue(equals(bigBytes2, receiveBytes2));
        }

    private void readBytes(final ReadableByteChannel readableChannel,
        final byte[] receiveBytes) throws IOException
        {
        final ByteBuffer receiveBuffer = ByteBuffer.wrap(receiveBytes);

        int attempts = 0;
        int read = 0;
        while (read < receiveBytes.length && attempts < 200)
            {
            read += readableChannel.read(receiveBuffer);
            attempts++;
            }
        assertEquals(receiveBytes.length, read);

        LOG.trace("Read all bytes!!");
        }

    private void write(final WritableByteChannel writableChannel,
        final byte[] bigBytes) throws IOException
        {
        final ByteBuffer bigBuffer = ByteBuffer.wrap(bigBytes);
        final int limit = bigBuffer.capacity() / 4;
        for (int i = 0; i < limit; i++)
            {
            bigBuffer.putInt(i);
            }

        bigBuffer.rewind();
        writableChannel.write(bigBuffer);
        }

    private static boolean equals(byte[] a, byte[] a2)
        {
        if (a==a2)
            {
            LOG.trace("Arrays are equal");
            return true;
            }
        if (a==null || a2==null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        for (int i=0; i<length; i++)
            if (a[i] != a2[i])
                return false;

        LOG.trace("Every byte equal...");
        return true;
        }

    private void startServer()
        {
        try
            {
            final SelectorManager selector = new SelectorManagerImpl (
                ClassUtils.getShortClassName(getClass()));

            selector.start ();

            final AcceptorListener acceptorListener =
                    new TestAcceptorListener (selector);
            final NioServer server =
                    new NioServerImpl (PORT, selector, acceptorListener);

            server.startServer ();
            }
        catch (final IOException e)
            {
            fail ("Unexpected exception: " + e);
            }
        }

    private final class TestAcceptorListener implements AcceptorListener
        {

        private final SelectorManager m_selectorManager;

        public TestAcceptorListener(final SelectorManager selector)
            {
            this.m_selectorManager = selector;
            }

        public void onAccept(SocketChannel sc)
            {
            final ReaderWriter readerWriter;
            try
                {
                readerWriter = new NioReaderWriter(sc, this.m_selectorManager);
                }
            catch (final SocketException e)
                {
                handleException(sc, e);
                return;
                }
            catch (final IOException e)
                {
                handleException(sc, e);
                return;
                }
            final ProtocolHandler protocolHandler =
                new TestProtocolHandler(readerWriter);
            readerWriter.setProtocolHandler(protocolHandler);
            }

        private void handleException(final SocketChannel sc, final IOException e)
            {
            LOG.warn("Unexpected exception on the socket", e);
            try
                {
                sc.close();
                }
            catch (final IOException ioe)
                {
                LOG.warn("Unexpected exception closing socket", ioe);
                }
            }

        }

    private final class TestProtocolHandler implements ProtocolHandler
        {

        private final ReaderWriter m_readerWriter;

        public TestProtocolHandler(final ReaderWriter readerWriter)
            {
            this.m_readerWriter = readerWriter;
            }

        public void handleMessages(final ByteBuffer messageBuffer,
            final InetSocketAddress client) throws IOException
            {
            messageBuffer.flip();
            final ByteBuffer dataBuffer =
                ByteBuffer.allocate(messageBuffer.remaining());
            dataBuffer.put(messageBuffer);
            dataBuffer.rewind();
            this.m_readerWriter.write(dataBuffer);
            }

        }
    }
