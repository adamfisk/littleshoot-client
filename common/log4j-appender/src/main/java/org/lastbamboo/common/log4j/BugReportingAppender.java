package org.lastbamboo.common.log4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Socket appender that sends errors to a server for processing.
 */
public class BugReportingAppender extends AppenderSkeleton
    {

    private final ExecutorService m_pool = Executors.newSingleThreadExecutor();
    private String m_url;
    
    private final Collection<Bug> m_recentBugs = 
        Collections.synchronizedSet(new LinkedHashSet<Bug>());
    
    private final HttpClient m_httpClient = new HttpClient(
        new ResettingMultiThreadedHttpConnectionManager());
    private final AppenderCallback m_appenderCallback;
    private final boolean m_filterDuplicates;
    
    /**
     * Creates a new appender.
     */
    public BugReportingAppender()
        {
        this (new AppenderCallback(){
            public void addData(final Collection<NameValuePair> dataList)
                {}});
        }
    
    /**
     * Creates an appender with the specified callback that will add any
     * custom data to the bug reports.
     * 
     * @param appenderCallback The callback.
     */
    public BugReportingAppender(final AppenderCallback appenderCallback)
        {
        this(appenderCallback, true);
        }
    
    /**
     * Creates an appender with the specified callback that will add any
     * custom data to the bug reports.
     * 
     * @param appenderCallback The callback.
     * @param filterDuplicates Whether or not to filter duplicate recent bugs.
     */
    public BugReportingAppender(final AppenderCallback appenderCallback,
        final boolean filterDuplicates)
        {
        if (appenderCallback == null)
            {
            throw new NullPointerException("No callback!!");
            }
        this.m_appenderCallback = appenderCallback;
        this.m_filterDuplicates = filterDuplicates;
        }

    /**
     * Accessor for the URL of the server that can accept bug reports for 
     * this appender.
     * 
     * @return The URL of the bug reporting server.
     */
    public String getUrl()
        {
        return m_url;
        }
    
    /**
     * Sets the URL of the server that should receive bug reports.
     * 
     * @param url The URL of the server.
     */
    public void setUrl(final String url)
        {
        System.out.println("Setting URL to: "+url);
        this.m_url = url;
        }
    
    @Override
    public void append(final LoggingEvent le)
        {
        
        // Only submit the bug under certain conditions.
        if (submitBug(le))
            {
            // Just submit it to the thread pool to avoid holding up the calling
            // thread.
            //System.out.println("Submitting bug to pool...");
            this.m_pool.submit(new BugRunner(this.m_url, le));            
            }
        else
            {
            //System.out.println("Not submitting bug...");
            }
        }

    private boolean submitBug(final LoggingEvent le)
        {
        // Ignore plain old logs.
        if (!le.getLevel().isGreaterOrEqual(Level.WARN))
            {
            return false;
            }
        
        final LocationInfo li = le.getLocationInformation();
        final Bug lastBug = new Bug(li);
        if (this.m_filterDuplicates && m_recentBugs.contains(lastBug))
            {
            // Don't send duplicates.  This should be configurable, but we
            // want to avoid hammering the server.
            return false;
            }
        synchronized (this.m_recentBugs)
            {
            // Remove the oldest bug.
            if (this.m_recentBugs.size() >= 200)
                {
                final Bug lastIn = this.m_recentBugs.iterator().next();
                this.m_recentBugs.remove(lastIn);
                }
            m_recentBugs.add(lastBug);
            return true;
            }
        }

    @Override
    public void close()
        {
        this.m_pool.shutdown();
        }

    @Override
    public boolean requiresLayout()
        {
        return false;
        }

    private final class BugRunner implements Runnable
        {

        private final LoggingEvent m_loggingEvent;
        private final String m_bugUrl;

        private BugRunner(final String url, final LoggingEvent le)
            {
            this.m_bugUrl = url;
            this.m_loggingEvent = le;
            }

        public void run()
            {
            try
                {
                submitBug(this.m_loggingEvent);
                }
            catch (final Throwable t)
                {
                System.err.println("Error submitting bug: "+t);
                }
            }
        
        private void submitBug(final LoggingEvent le)
            {
            System.err.println("Starting to submit bug...");
            final LocationInfo li = le.getLocationInformation();
            
            final String className = li.getClassName();
            final String methodName = li.getMethodName();
            final int lineNumber;
            final String ln = li.getLineNumber();
            if (NumberUtils.isNumber(ln))
                {
                lineNumber = Integer.parseInt(ln);
                }
            else
                {
                lineNumber = -1;
                }
            
            
            final String threadName = le.getThreadName();

            /*
            final String startTime = 
                DateFormat.getDateTimeInstance(DateFormat.DEFAULT, 
                    DateFormat.DEFAULT, Locale.US).format(
                    Long.valueOf(LoggingEvent.getStartTime()));
            final String timestamp =
                DateFormat.getDateTimeInstance(DateFormat.DEFAULT, 
                    DateFormat.DEFAULT, Locale.US).format(
                    Long.valueOf(le.timeStamp));
                    */

            final String throwableString = Log4jUtils.getThrowableString(le);
            
            final String osRoot = SystemUtils.IS_OS_WINDOWS ? "c:" : "/";
            long free = Long.MAX_VALUE;
            try
                {
                free = FileSystemUtils.freeSpaceKb(osRoot);
                // Convert to megabytes for easy reading.
                free = free/1024L;
                }
            catch (final IOException e)
                {
                }
            
            final Collection<NameValuePair> dataList = 
                new LinkedList<NameValuePair>();
                
            dataList.add(new NameValuePair("message", le.getMessage().toString()));
            dataList.add(new NameValuePair("logLevel", le.getLevel().toString()));
            dataList.add(new NameValuePair("className", className));
            dataList.add(new NameValuePair("methodName", methodName));
            dataList.add(new NameValuePair("lineNumber", String.valueOf(lineNumber)));
            dataList.add(new NameValuePair("threadName", threadName));
            //dataList.add(new NameValuePair("startTime", startTime));
            //dataList.add(new NameValuePair("timeStamp", timestamp));
            dataList.add(new NameValuePair("javaVersion", SystemUtils.JAVA_VERSION));
            dataList.add(new NameValuePair("osName", SystemUtils.OS_NAME));
            dataList.add(new NameValuePair("osArch", SystemUtils.OS_ARCH));
            dataList.add(new NameValuePair("osVersion", SystemUtils.OS_VERSION));
            dataList.add(new NameValuePair("language", SystemUtils.USER_LANGUAGE));
            dataList.add(new NameValuePair("country", SystemUtils.USER_COUNTRY));
            dataList.add(new NameValuePair("timeZone", SystemUtils.USER_TIMEZONE));
            dataList.add(new NameValuePair("userName", SystemUtils.USER_NAME));
            dataList.add(new NameValuePair("throwable", throwableString));
            dataList.add(new NameValuePair("disk_space", String.valueOf(free)));
            dataList.add(new NameValuePair("count", "1"));
            m_appenderCallback.addData(dataList);
            
            submitData(dataList, this.m_bugUrl);
            }

        }
    
    private void submitData(final Collection<NameValuePair> dataList, 
        final String bugUrl)
        {
        System.out.println("Submitting data...");

        final PostMethod method = new PostMethod(bugUrl);
        
        final NameValuePair[] data = dataList.toArray(new NameValuePair[0]);
        System.out.println("Sending "+data.length+" fields...");
        
        method.setRequestBody(data);
        InputStream is = null;
        try
            {
            System.err.println("Sending data to server...");
            m_httpClient.executeMethod(method);
            System.err.println("\n\nSent data to server...headers...");
            
            final int statusCode = method.getStatusCode();
            is = method.getResponseBodyAsStream();
            if (statusCode < 200 || statusCode > 299)
                {
                final String body = IOUtils.toString(is);
                InputStream bais = null;
                OutputStream fos = null;
                try 
                    {
                    bais = new ByteArrayInputStream(body.getBytes());
                    fos = new FileOutputStream(new File("bug_error.html"));
                    IOUtils.copy(bais, fos);
                    }
                finally
                    {
                    IOUtils.closeQuietly(bais);
                    IOUtils.closeQuietly(fos);
                    }
                
                System.err.println("Could not send bug:\n" + 
                    method.getStatusLine()+"\n"+ body);
                final Header[] headers = method.getResponseHeaders();
                for (int i = 0; i < headers.length; i++)
                    {
                    System.err.println(headers[i]);
                    }
                return;
                }
            
            // We always have to read the body.
            IOUtils.copy(is, new NullOutputStream());
            }
        catch (final HttpException e)
            {
            System.err.println("\n\nERROR::HTTP error" + e);
            }
        catch (final IOException e)
            {
            System.err.println(
                "\n\nERROR::IO error connecting to server" + e);
            }
        catch (final Throwable e)
            {
            System.err.println("Got error\n"+e);
            }
        finally
            {
            IOUtils.closeQuietly(is);
            method.releaseConnection();
            }        
        }

    private static final class Bug
        {

        private final String m_className;
        private final String m_methodName;
        private final String m_lineNumber;

        private Bug(final LocationInfo li)
            {
            this.m_className = li.getClassName();
            this.m_methodName = li.getMethodName();
            this.m_lineNumber = li.getLineNumber();
            }

        @Override
        public int hashCode()
            {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((m_className == null) ? 0 : m_className.hashCode());
            result = PRIME * result + ((m_lineNumber == null) ? 0 : m_lineNumber.hashCode());
            result = PRIME * result + ((m_methodName == null) ? 0 : m_methodName.hashCode());
            return result;
            }

        @Override
        public boolean equals(Object obj)
            {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Bug other = (Bug) obj;
            if (m_className == null)
                {
                if (other.m_className != null)
                    return false;
                }
            else if (!m_className.equals(other.m_className))
                return false;
            if (m_lineNumber == null)
                {
                if (other.m_lineNumber != null)
                    return false;
                }
            else if (!m_lineNumber.equals(other.m_lineNumber))
                return false;
            if (m_methodName == null)
                {
                if (other.m_methodName != null)
                    return false;
                }
            else if (!m_methodName.equals(other.m_methodName))
                return false;
            return true;
            }
        }
    }
