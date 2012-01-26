package org.lastbamboo.integration.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.io.IOUtils;
import org.littleshoot.mina.common.ConnectFuture;
import org.littleshoot.mina.transport.socket.nio.SocketConnector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lastbamboo.common.http.client.CommonsHttpClient;
import org.lastbamboo.common.http.client.CommonsHttpClientImpl;
import org.lastbamboo.common.http.client.HttpClientRunner;
import org.lastbamboo.common.http.client.HttpListener;
import org.littleshoot.util.InputStreamHandler;
import org.littleshoot.util.mina.SocketIoHandler;
import org.lastbamboo.integration.tests.stubs.HttpListenerStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration test for our MINA sockets and HTTP client code. 
 */
public class MinaSocketTest
    {

    private final static Logger LOG = LoggerFactory.getLogger(MinaSocketTest.class);
    private static Protocol s_baseHttpProtocol;
    
    @BeforeClass public static void establishTest() throws Exception
        {
        LOG.debug("Starting test...");
        s_baseHttpProtocol = Protocol.getProtocol("http");
        }
    
    @AfterClass public static void afterTest() throws Exception
        {
        LOG.debug("Test complete!");
        // We can't keep our custom protocol registered -- reset it.
        Protocol.registerProtocol("http", s_baseHttpProtocol);
        }
    
    /**
     * Quick test to make sure our sockets work when hitting a normal web site.
     * @throws Exception If any unexpected error occurs.
     */
    @Test public void testSocket() throws Exception
        {
        final SocketConnector connector = new SocketConnector();
        final SocketIoHandler ioHandler = new SocketIoHandler();
        
        final ProtocolSocketFactory socketFactory = new ProtocolSocketFactory()
            {

            public Socket createSocket(final String host, final int port) 
                throws IOException, UnknownHostException
                {
                LOG.debug("Creating socket...");
                final InetSocketAddress socketAddress =
                    new InetSocketAddress(host, port);
                final ConnectFuture wr = 
                    connector.connect(socketAddress, ioHandler);
                wr.join(10000);
                return (Socket) wr.getSession().getAttribute("SOCKET");
                }

            public Socket createSocket(String host, int port, 
                final InetAddress arg2, final int arg3) throws IOException, 
                UnknownHostException
                {
                LOG.debug("Creating socket...");
                final InetSocketAddress socketAddress =
                    new InetSocketAddress(host, port);
                final ConnectFuture wr = 
                    connector.connect(socketAddress, ioHandler);
                wr.join(10000);
                return (Socket) wr.getSession().getAttribute("SOCKET");
                }

            public Socket createSocket(final String host, final int port, 
                final InetAddress arg2, int arg3, HttpConnectionParams arg4) 
                throws IOException, UnknownHostException, ConnectTimeoutException
                {
                LOG.debug("Creating socket...");
                final InetSocketAddress socketAddress =
                    new InetSocketAddress(host, port);
                final ConnectFuture wr = 
                    connector.connect(socketAddress, ioHandler);
                wr.join(10000);
                if (!wr.isConnected())
                    {
                    LOG.warn("Not connected!!");
                    }
                return (Socket) wr.getSession().getAttribute("SOCKET");
                }
            };
        final Protocol protocol = new Protocol("http", socketFactory, 80);
        Protocol.registerProtocol("http", protocol);
        
        final CommonsHttpClient client = 
            new CommonsHttpClientImpl(new MultiThreadedHttpConnectionManager());
        client.getHttpConnectionManager().getParams().setBooleanParameter(
            HttpConnectionManagerParams.STALE_CONNECTION_CHECK, false);
        final String url = "http://www.google.com";
        final HeadMethod head = new HeadMethod(url);
        final int headCode = client.executeMethod(head);
        assertEquals(200, headCode);
        LOG.debug("Got response...");
        
        final GetMethod method = new GetMethod(url);
        final HttpListener listener = new HttpListenerStub();
        final InputStreamHandler streamHandler = new InputStreamHandler()
            {
            public void handleInputStream(final InputStream is) 
                throws IOException
                {
                LOG.debug(IOUtils.toString(is));
                }
            };
        final Runnable runner = 
            new HttpClientRunner(streamHandler, client, method, listener);
        
        runner.run();
        }
    }
