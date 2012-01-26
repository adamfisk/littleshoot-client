//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006 Amazon Digital Services, Inc. or its
//  affiliates.

package org.lastbamboo.common.amazon.s3;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for using Amazon S3.  This is a modified version of the
 * utilities class that Amazon distributes as example code.
 */
public class AmazonS3Utils 
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(AmazonS3Utils.class);
    
    private static final String AMAZON_HEADER_PREFIX = "x-amz-";
    private static final String ALTERNATIVE_DATE_HEADER = "x-amz-date";
    
    public static String makeCanonicalString(final String method, 
       final String resource, final Header[] headers) 
        {
        return makeCanonicalString(method, resource, headers, null);
        }
    
    private static String makeCanonicalString(final String method, 
        final String resource, final Header[] headers, final String expires)
        {
        final StringBuilder buf = new StringBuilder();
        buf.append(method);
        buf.append("\n");

        // Add all interesting headers to a list, then sort them.  "Interesting"
        // is defined as Content-MD5, Content-Type, Date, and x-amz-
        final SortedMap<String, String> interestingHeaders = 
            new TreeMap<String, String>();
        if (headers != null) 
            {
            for (final Header curHeader : headers)
                {
                final String lk = curHeader.getName().toLowerCase();
                // Ignore any headers that are not particularly interesting.
                if (lk.equals("content-type") || lk.equals("content-md5") || 
                    lk.equals("date") ||
                    lk.startsWith(AMAZON_HEADER_PREFIX))
                    {
                    final String headersList = curHeader.getValue();
                    LOG.debug("Our headers list: "+headersList);
                    interestingHeaders.put(lk, headersList);
                    }
                }
            }

        if (interestingHeaders.containsKey(ALTERNATIVE_DATE_HEADER)) 
            {
            interestingHeaders.put("date", "");
            }

        // if the expires is non-null, use that for the date field.  this
        // trumps the x-amz-date behavior.
        if (expires != null) 
            {
            interestingHeaders.put("date", expires);
            }

        // these headers require that we still put a new line in after them,
        // even if they don't exist.
        if (! interestingHeaders.containsKey("content-type")) 
            {
            interestingHeaders.put("content-type", "");
            }
        if (! interestingHeaders.containsKey("content-md5")) 
            {
            interestingHeaders.put("content-md5", "");
            }

        // Finally, add all the interesting headers 
        // (i.e.: all that start with x-amz- ;-))
        for (final String key : interestingHeaders.keySet()) 
            {
            if (key.startsWith(AMAZON_HEADER_PREFIX)) 
                {
                buf.append(key).append(':').append(interestingHeaders.get(key));
                } 
            else 
                {
                buf.append(interestingHeaders.get(key));
                }
            buf.append("\n");
            }

        // don't include the query parameters...
        int queryIndex = resource.indexOf('?');
        if (queryIndex == -1) 
            {
            buf.append("/" + resource);
            } 
        else 
            {
            buf.append("/" + resource.substring(0, queryIndex));
            }

        // ...unless there is an acl or torrent parameter
        if (resource.matches(".*[&?]acl($|=|&).*")) 
            {
            buf.append("?acl");
            } 
        else if (resource.matches(".*[&?]torrent($|=|&).*")) 
            {
            buf.append("?torrent");
            } 
        else if (resource.matches(".*[&?]logging($|=|&).*")) 
            {
            buf.append("?logging");
            }

        return buf.toString();
        }
    }
