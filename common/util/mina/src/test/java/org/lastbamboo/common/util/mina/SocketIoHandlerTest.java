package org.lastbamboo.common.util.mina;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.transport.socket.nio.SocketConnector;

/**
 * Test for socket IO handling. 
 */
public class SocketIoHandlerTest
    {

    @Test public void testSocket() throws Exception
        {
        final AtomicReference<Socket> ref = new AtomicReference<Socket>();
        final SocketIoHandler handler = new SocketIoHandler()
            {
            @Override
            public void sessionOpened(final IoSession session)
                {
                super.sessionOpened(session);
                ref.set((Socket) session.getAttribute("SOCKET"));
                synchronized (ref)
                    {
                    ref.notifyAll();
                    }
                }
            };
        
        final SocketConnector connector = new SocketConnector();
        
        // NOTE: This does not work outside the US because Google redirects us.
        final SocketAddress socketAddress = 
            new InetSocketAddress("www.google.com", 80);
        connector.connect(socketAddress, handler);
        
        synchronized (ref)
            {
            if (ref.get() == null)
                {
                ref.wait(20000);
                }
            }
        
        final Socket sock = ref.get();
        assertFalse(sock == null);
        
        final OutputStream os = sock.getOutputStream();
        final InputStream is = sock.getInputStream();
        
        final String httpRequest = "GET / HTTP/1.1\r\n" +
            "Host: www.google.com\r\n" +
            "Connection: close\r\n" +
            "Accept: text/html\r\n" +
            "\r\n";
        os.write(httpRequest.getBytes("US-ASCII"));
        os.flush();
        
        final byte[] httpResponseBytes = new byte[1000];
        is.read(httpResponseBytes);
        final String httpResponse = new String(httpResponseBytes,"US-ASCII");
        assertTrue("Unexpected response: "+httpResponse, 
              httpResponse.startsWith("HTTP/1.1 200 OK") ||
              httpResponse.startsWith("HTTP/1.1 302 Found"));
        }
    }
