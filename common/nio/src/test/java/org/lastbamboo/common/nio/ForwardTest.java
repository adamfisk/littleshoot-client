package org.lastbamboo.common.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.protocol.ProtocolHandler;
import org.lastbamboo.common.protocol.ReaderWriter;
import org.littleshoot.util.NetworkUtils;
import org.littleshoot.util.Sha1Hasher;


/**
 * Test for NIO where the server implementation fprwards all data to a second
 * client socket.
 */
public final class ForwardTest extends TestCase
    {

    /**
     * Logger for this class.
     */
    private static Log LOG = LogFactory.getLog(ForwardTest.class);

    /**
     * The test file to send from one host to the other.
     */
    private static final File TEST_FILE = 
        new File("forward-file-test-original");

    /**
     * The port the server runs on and that the clients connect to.
     */
    private final int PORT = 7892;

    /**
     * The hash of the file we're transferring.
     */
    private URI m_sha1;


    protected void setUp() throws Exception
        {
        TEST_FILE.delete();
        m_sha1 = null;
        createTestFile();
        this.m_sha1 = Sha1Hasher.createSha1Urn(TEST_FILE);
        }

    private void createTestFile() throws IOException
        {
        // Just writes the alphabet a bunch of times to the file.
        final OutputStream os = new FileOutputStream(TEST_FILE);
        final String alphabet = "abcdefghijklmnopqrstuvwxyz\n";
        final byte[] alphabetBytes = alphabet.getBytes();
        for (int i=0; i < 10000; i++)
            {
            os.write(alphabetBytes);
            }
        os.close();
        
        TEST_FILE.deleteOnExit();
        }

    /**
     * Tests a server implementation that sends a file from one client to the
     * server.  The server then takes that data and forwards all of it to the
     * second client.  When the process completes, this verifies that both
     * files are exactly the same.
     *
     * @throws Exception If any unexpected error occurs.
     */
    public void testForward() throws Exception
        {
        LOG.trace("Running test...");
        startServer();

        final InputStream is = new FileInputStream(TEST_FILE);
        final File testFile = new File("forward-file-test-forwarded");
        testFile.delete();
        testFile.deleteOnExit();

        final Socket secondClient = new Socket(NetworkUtils.getLocalHost(),PORT);
        startSecondClient(secondClient, testFile);

        final Socket client = new Socket(NetworkUtils.getLocalHost(), PORT);
        final OutputStream os = client.getOutputStream();
        IOUtils.copy(is, os);

        int count = 0;

        while (testFile.length() != TEST_FILE.length() && count < 5)
            {
            Thread.sleep(2000);
            count++;
            }

        final URI sha1 = Sha1Hasher.createSha1Urn(testFile);

        assertEquals(TEST_FILE.length(), testFile.length());
        assertEquals(this.m_sha1, sha1);

        testFile.delete();
        }

    /**
     * Starts the second client socket that receives all data from the first
     * client socket.
     * @param socket The already-connected socket for reading data.
     * @throws Exception If any unexpected error occurs.
     */
    private void startSecondClient(final Socket socket,
        final File fileToWriteTo) throws Exception
        {
        final Runnable secondClient = new ClientReader(socket, fileToWriteTo);
        final Thread clientReaderThread =
            new Thread(secondClient, "client-reader-thread");
        clientReaderThread.setDaemon(true);
        clientReaderThread.start();
        }

    private final class ClientReader implements Runnable
        {

        private final File m_fileToWriteTo;
        private final Socket m_socket;

        private ClientReader(final Socket socket, final File fileToWriteTo)
            {
            this.m_socket = socket;
            this.m_fileToWriteTo = fileToWriteTo;
            }

        public void run()
            {
            try
                {
                runClient();
                }
            catch (final UnknownHostException e)
                {
                fail("Could not resolve host..."+e);
                }
            catch (final IOException e)
                {
                fail("Unexpected exception..."+e);
                }
            }

        private void runClient() throws UnknownHostException, IOException
            {
            final OutputStream os = new FileOutputStream(this.m_fileToWriteTo);
            final InputStream is = this.m_socket.getInputStream();
            int curByte = is.read();

            while (curByte != -1)
                {
                os.write(curByte);
                curByte = is.read();
                }

            os.close();
            }
        }

    private void startServer()
        {
        try
            {
            final SelectorManager selector =
                new SelectorManagerImpl (ClassUtils.getShortClassName(getClass()));

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
        private ReaderWriter m_clientToForwardTo;

        private TestAcceptorListener(final SelectorManager selector)
            {
            this.m_selectorManager = selector;
            }

        public void onAccept(final SocketChannel sc)
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

            // The following scheme works because only one client will ever
            // be writing any data.  All other clients are just consumers,
            // so it doesn't matter if they get a null argument here.
            final ProtocolHandler protocolHandler =
                new TestProtocolHandler(this.m_clientToForwardTo);
            readerWriter.setProtocolHandler(protocolHandler);

            if (this.m_clientToForwardTo == null)
                {
                this.m_clientToForwardTo = readerWriter;
                }
            }

        private void handleException(final SocketChannel sc,
            final IOException e)
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

        private TestProtocolHandler(final ReaderWriter readerWriter)
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
