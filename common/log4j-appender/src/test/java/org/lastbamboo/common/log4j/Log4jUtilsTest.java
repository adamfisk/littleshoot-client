package org.lastbamboo.common.log4j;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;


public class Log4jUtilsTest
    {

    @Test public void testThrowableString() throws Exception
        {
        final Logger root = Logger.getRootLogger();
        final Throwable throwable = new IOException("test");
        throwable.initCause(new IllegalArgumentException("test"));
        final LoggingEvent le = new LoggingEvent(getClass().getName(), root, 
            Level.WARN, "Hello, world.", throwable);
        final String str = Log4jUtils.getThrowableString(le);
        
        final NameValuePair nv = new NameValuePair("throwable", str);
        
        assertEquals(str, nv.getValue());
        }
    }
