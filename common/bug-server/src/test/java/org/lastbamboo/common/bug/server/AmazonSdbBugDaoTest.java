package org.lastbamboo.common.bug.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lastbamboo.common.amazon.sdb.AmazonSdbImpl;
import org.lastbamboo.common.amazon.stack.AwsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the resource repository for parts of the database that use 
 * Amazon's Simple DB.
 */
public class AmazonSdbBugDaoTest
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(AmazonSdbBugDaoTest.class);
    
    private final long m_eventualConsistencySleep = 1200;
    

    private static String s_domainName = 
        AmazonSdbBugDaoTest.class.getName();

    private static AmazonSdbImpl s_simpleDb;

    private static AmazonSdbBugDao s_bugDao;
    
    @BeforeClass public static void setUpAll() throws Exception
        {
        //s_simpleDb = new AmazonSdbImpl(s_domainName);
        //s_bugDao = new AmazonSdbBugDao(s_simpleDb);
        }
    
    @Before public void setUp() throws Exception
        {
        //s_simpleDb.createDomain();
        }
    
    @After public void tearDown() throws Exception
        {
        //s_simpleDb.deleteDomain();
        }
    
    @AfterClass public static void tearDownClass() throws Exception
        {
        //s_simpleDb.deleteDomain();
        }
    
    @Test public void dummyTest() throws Exception
        {
    
        }
    
    public void testBugs() throws InterruptedException, IOException 
        {
        if (!AwsUtils.hasPropsFile())
            {
            return;
            }
        final String bugClass = getClass().getCanonicalName();
        final int lineNumber = RandomUtils.nextInt();
        final Bug bug = newBug(bugClass, lineNumber);
        s_bugDao.insertBug(bug);
        
        Thread.sleep(m_eventualConsistencySleep);
        Thread.sleep(m_eventualConsistencySleep);
        
        Collection<Bug> bugs = s_bugDao.getBugs();
        for (final Bug b : bugs)
            {
            System.out.println("Got bug: "+b);
            }
        
        assertEquals(1, bugs.size());
        
        final Bug bug2 = newBug(bugClass, lineNumber);
        s_bugDao.insertBug(bug2);
        
        Thread.sleep(m_eventualConsistencySleep);
        Thread.sleep(m_eventualConsistencySleep);
        bugs = s_bugDao.getBugs();
        for (final Bug b : bugs)
            {
            System.out.println("Got bug: "+b);
            }
        
        assertEquals(1, bugs.size());
        }
    
    private Bug newBug(final String className, final int lineNumber)
        {
        final DateFormat df = 
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        final Map<String, String> bugsMap = new HashMap<String, String>();
        bugsMap.put("message", "bug");
        bugsMap.put("logLevel", "warn");
        bugsMap.put("className", className);
        bugsMap.put("methodName", "testMethodName");
        bugsMap.put("lineNumber", String.valueOf(lineNumber));
        bugsMap.put("threadName", "TestThread");
        bugsMap.put("javaVersion", "6.0");
        bugsMap.put("osName", "Ubuntu");
        bugsMap.put("osArch", "Intel");
        bugsMap.put("osVersion", "7.0");
        bugsMap.put("language", "English");
        bugsMap.put("country", "United States");
        bugsMap.put("timeZone", "Eastern");
        bugsMap.put("throwable", "test");
        bugsMap.put("version", "0.1");
        //bugsMap.put("startTime", DateUtils.iso8601());
        //bugsMap.put("timeStamp", DateUtils.iso8601());
        bugsMap.put("startTime", df.format(new Date()));
        bugsMap.put("timeStamp", df.format(new Date()));
        bugsMap.put("thread", Thread.currentThread().toString());

        System.out.println(bugsMap);
        final Bug bug = new BugImpl(bugsMap);
        return bug;
        }
    }
