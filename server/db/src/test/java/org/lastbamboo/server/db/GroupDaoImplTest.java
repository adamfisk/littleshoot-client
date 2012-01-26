package org.lastbamboo.server.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lastbamboo.common.amazon.sdb.AmazonSdbImpl;
import org.lastbamboo.common.amazon.stack.AwsUtils;
import org.lastbamboo.server.resource.GroupExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the resource repository for parts of the database that use 
 * Amazon's Simple DB.
 */
public class GroupDaoImplTest
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(GroupDaoImplTest.class);
    
    private static GroupDao s_groupDao;
    
    private final long m_eventualConsistencySleep = 1200;
    
    private static final boolean TEST_ACTIVE = false;
    
    private static String s_domainName = GroupDaoImplTest.class.getName();

    private static AmazonSdbImpl s_simpleDb;
    
    @BeforeClass public static void setUpAll() throws Exception
        {
        if (!TEST_ACTIVE) return;
        s_simpleDb = new AmazonSdbImpl(s_domainName);
        s_groupDao = new GroupDaoImpl(s_simpleDb);
        }
    
    @Before public void setUp() throws Exception
        {
        if (!TEST_ACTIVE) return;
        //s_simpleDb.createDomain();
        }
    
    @After public void tearDown() throws Exception
        {
        if (!TEST_ACTIVE) return;
        //s_simpleDb.deleteDomain();
        }
    
    @AfterClass public static void tearDownClass() throws Exception
        {
        if (!TEST_ACTIVE) return;
        s_simpleDb.deleteDomain();
        }
    
    public void testNewGroup() throws InterruptedException 
        {
        if (!TEST_ACTIVE) return;
        if (!AwsUtils.hasPropsFile())
            {
            return;
            }
        long userId = 74290L;
        String name = "teareaa";
        String description = "testinginagajfkda";
        String permission = "public";
        String groupId = "";
        try
            {
            groupId = s_groupDao.newGroup(userId, name, description, permission);
            }
        catch (GroupExistsException e)
            {
            fail("Group should not exist."+e);
            }
        catch (IOException e)
            {
            fail("Could not access Simple DB." + e);
            }
        
        assertFalse(StringUtils.isBlank(groupId));
        
        Thread.sleep(this.m_eventualConsistencySleep);
        try
            {
            groupId = s_groupDao.newGroup(userId, name, description, permission);
            }
        catch (final GroupExistsException e)
            {
            // Expected.
            }
        catch (final IOException e)
            {
            fail("Could not access Simple DB." + e);
            }
        }
    
    @Test public void dummyTest() throws Exception
        {
        assertFalse(false);
        }
    }
