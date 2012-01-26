package org.lastbamboo.common.log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests to make sure error logs get correctly sent to the locally running
 * process.  You can easily test this using a locally-running version of 
 * Tomcat and MySQL, for example.
 */
public class BugReportingLogTest
    {

    private static final Logger LOG = LoggerFactory.getLogger(BugReportingLogTest.class);
    
    public static void main(final String[] args)
        {
        LOG.error("Testing to make sure this log hits the server.");
        }
    }
