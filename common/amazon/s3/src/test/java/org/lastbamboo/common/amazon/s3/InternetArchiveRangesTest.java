package org.lastbamboo.common.amazon.s3;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.junit.Test;


public class InternetArchiveRangesTest 
    {

    @Test public void testRanges() throws Exception 
        {
        final String url = "http://archive.org/download/afisk-test-4/pom.xml";
        
        final HeadMethod head = new HeadMethod(url);
        final HttpClient client = new HttpClient();
        client.executeMethod(head);
        
        final long length = head.getResponseContentLength();
        
        final long maxRange = length/2;
        
        final GetMethod get = new GetMethod(url);
        get.addRequestHeader("Range", "bytes=0-"+maxRange);
        
        client.executeMethod(get);
        
        assertNotNull(get.getResponseHeader("Content-Range"));
        
        final String response = get.getResponseBodyAsString();
        
        // Note byte ranges are inclusive in the range header.
        assertEquals(maxRange + 1, (long)response.length());
        }
    
    /**
     * Test if we can request ranges before a file has fully uploaded.
     * 
     * @throws Exception If an unexpected error occurs.
     */
    public void testRangesActiveUpload() throws Exception 
        {
        startUpload();
        final String url = 
            "http://archive.org/download/afisk-test-4/littleshoot_first_half.mov";
        Thread.sleep(10000);

        final HttpClient client = new HttpClient();
        
        for (int i = 0; i < 4; i++)
            {
            System.out.println("Sending HEAD");
            final HeadMethod head = new HeadMethod(url);
            client.executeMethod(head);
            System.out.println(head.getStatusLine());
            final Header[] headers = head.getResponseHeaders();
            for (final Header header : headers)
                {
                System.out.println(header.getName()+": "+header.getValue());
                }
            Thread.sleep(10000);
            }
        }

    private void startUpload() throws Exception 
        {
        final AmazonS3 s3 = new AmazonS3Impl();
        final File file =
            new File("/Users/afisk/Downloads/littleshoot_first_half.mov");
        
        final Runnable run = new Runnable() {

            public void run() {
                try {
                    s3.putPublicFile("afisk-test-4", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        final Thread t = new Thread(run, "Internet-Archive-Upload");
        t.setDaemon(true);
        t.start();
        }
    }
