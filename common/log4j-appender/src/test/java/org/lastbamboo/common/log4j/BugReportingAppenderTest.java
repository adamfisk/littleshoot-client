package org.lastbamboo.common.log4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

/**
 * Test the appender.
 */
public class BugReportingAppenderTest
    {
    
    private volatile boolean m_serverStarted;

    private final int SERVER_PORT = 7895;

    private String m_throwable;
    
    @Test public void dummyTest() throws Exception
        {
        assertTrue("dummy", true);
        }
    
    public void testGae() throws Exception
        {
        final Logger root = Logger.getRootLogger();
        final Throwable throwable = new IOException("test");
        throwable.initCause(new IllegalArgumentException("test"));
        
        final LoggingEvent le = new LoggingEvent(getClass().getName(), root, 
            Level.WARN, "Hello, world.", throwable);
        final String throwableString = Log4jUtils.getThrowableString(le);
        
        final BugReportingAppender appender = new BugReportingAppender();
        appender.setUrl("http://127.0.0.1:"+8080+"/submit");
        
        //appender.setUrl("http://littleshootbugs.appspot.com/submit");
        appender.append(le);
        Thread.sleep(5000);
        
        /*
        synchronized (this)
            {
            if (StringUtils.isEmpty(m_throwable))
                {
                wait(6000);
                }
            }
        assertEquals(throwableString, m_throwable);
        */
        }
    
    public void testAppending() throws Exception
        {
        startServer();
        
        synchronized (this)
            {
            if (!this.m_serverStarted)
                {
                wait(6000);
                }
            if (!this.m_serverStarted)
                {
                fail("Did not start server???");
                return;
                }
            }

        Thread.sleep(200);
        
        final Logger root = Logger.getRootLogger();
        final Throwable throwable = new IOException("test");
        throwable.initCause(new IllegalArgumentException("test"));
        
        final LoggingEvent le = new LoggingEvent(getClass().getName(), root, 
            Level.WARN, "Hello, world.", throwable);
        final String throwableString = Log4jUtils.getThrowableString(le);
        
        final BugReportingAppender appender = new BugReportingAppender();
        appender.setUrl("http://127.0.0.1:"+SERVER_PORT);
        appender.append(le);
        
        synchronized (this)
            {
            if (StringUtils.isEmpty(m_throwable))
                {
                wait(6000);
                }
            }
        assertEquals(throwableString, m_throwable);
        }

    private void startServer() throws IOException
        {
        final ServerSocket server = 
            new ServerSocket(SERVER_PORT,20, InetAddress.getByName("127.0.0.1"));
        final Runnable runner = new Runnable()
            {
            public void run()
                {
                synchronized (BugReportingAppenderTest.this)
                    {
                    m_serverStarted = true;
                    BugReportingAppenderTest.this.notifyAll();
                    }
                while (true)
                    {
                    try
                        {
                        //System.out.println("About to accept on "+server.getLocalSocketAddress());
                        final Socket client = server.accept();
                        //System.out.println("Accepted client...");
                        processSocket(client);
                        }
                    catch (IOException e)
                        {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        }
                    }
                }
            };
        final Thread serverThread = new Thread(runner, "Server-Thread-Test");
        serverThread.setDaemon(true);
        serverThread.start();
        }

    private void processSocket(final Socket client) throws IOException
        {
        final InputStream is = client.getInputStream();
        //final BufferedInputStream bis = new BufferedInputStream(is);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String current = reader.readLine();
        int contentLength = -1;
        while (StringUtils.isNotBlank(current))
            {
            //System.out.println(current);
            if (current.toLowerCase().startsWith("content-length"))
                {
                contentLength = Integer.parseInt(StringUtils.substringAfter(current, ":").trim());
                }
            current = reader.readLine();

            }
        //System.out.println("Finished reading headers..");
        
        if (contentLength == -1)
            {
            //System.out.println("No content length");
            return;
            }
        
        final char[] cbuf = new char[contentLength];
        reader.read(cbuf);
        
        final String body = new String(cbuf);
        //System.out.println("Read:\n"+body);
        final Map<String, String> map = createBugMap(body);
        
        m_throwable = map.get("throwable");
        
        synchronized (BugReportingAppenderTest.this)
            {
            BugReportingAppenderTest.this.notifyAll();
            }
        }

    private Map<String, String> createBugMap(final String bugString)
        {
        final Map<String, String> map = new HashMap<String, String>();
        final Scanner scanner = new Scanner(bugString);
        
        scanner.useDelimiter("&");
        
        while(scanner.hasNext())
            {
            final String nameValue = scanner.next();
            final String name = StringUtils.substringBefore(nameValue, "=");
            final String value = StringUtils.substringAfter(nameValue, "=");
            try
                {
                map.put(name, URLDecoder.decode(value, "UTF-8"));
                }
            catch (final UnsupportedEncodingException e)
                {
                map.put(name, value);
                //LOG.error("Encoding not supported??", e);
                }
            }
        return map;
        }
    }
