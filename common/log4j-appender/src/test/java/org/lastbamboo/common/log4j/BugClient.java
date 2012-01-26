package org.lastbamboo.common.log4j;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.apache.commons.lang.SystemUtils;

/**
 * Simple client that just tries to hit the bug server to submit a basic bug.
 */
public class BugClient
    {

    /**
     * Creates a new client.
     * 
     * @param args Any command line arguments.
     */
    public static void main(final String[] args)
        {
        final BugClient bc = new BugClient();
        bc.submitBug();
        }
        
    private void submitBug()
        {
        System.err.println("Starting to submit bug...");
        
        final HttpClient client = new HttpClient();
        final String url = 
            "http://127.0.0.1:8080/lastbamboo-common-bug-server/client-bugs.html";
        
        final PostMethod method = new PostMethod(url);
        // We go through all of the following in case any other class
        // in the JVM has set custom protocol handlers for HTTP.  We 
        // want to make sure we use the defaults.
        final HostConfiguration hc = method.getHostConfiguration();
        final Protocol p = hc.getProtocol();
        final String scheme = p == null ? "" : p.getScheme();
        
        if (scheme.equalsIgnoreCase("http"))
            {
            final Protocol prot = 
                new Protocol("http", new DefaultProtocolSocketFactory(), 80);
            hc.setHost(hc.getHost(), hc.getPort(), prot);
            }
        else if (scheme.equalsIgnoreCase("https"))
            {
            final ProtocolSocketFactory sslFactory = 
                new SSLProtocolSocketFactory();
            final Protocol prot = new Protocol("https", sslFactory, 443);
            hc.setHost(hc.getHost(), hc.getPort(), prot);
            }
        
        final List<NameValuePair> dataList = 
            new LinkedList<NameValuePair>();
            
        dataList.add(new NameValuePair("javaVersion", SystemUtils.JAVA_VERSION));
        dataList.add(new NameValuePair("osName", SystemUtils.OS_NAME));
        dataList.add(new NameValuePair("osArch", SystemUtils.OS_ARCH));
        dataList.add(new NameValuePair("osVersion", SystemUtils.OS_VERSION));
        dataList.add(new NameValuePair("language", SystemUtils.USER_LANGUAGE));
        dataList.add(new NameValuePair("country", SystemUtils.USER_COUNTRY));
        dataList.add(new NameValuePair("timeZone", SystemUtils.USER_TIMEZONE));
        
        final NameValuePair[] data = dataList.toArray(new NameValuePair[0]);
        method.setRequestBody(data);
        try
            {
            System.err.println("Sending data to server...");
            client.executeMethod(method);
            System.err.println("\n\nSent data to server...headers...");
            
            final int statusCode = method.getStatusCode();
            if (statusCode < 200 || statusCode > 299)
                {
                System.err.println("Could not sent bug: " + 
                    method.getStatusLine());
                final Header[] headers = method.getResponseHeaders();
                for (int i = 0; i < headers.length; i++)
                    {
                    System.err.println(headers[i]);
                    }
                return;
                }
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
        finally
            {
            method.releaseConnection();
            }
        }
    }
