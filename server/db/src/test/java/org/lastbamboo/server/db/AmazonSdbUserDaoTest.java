package org.lastbamboo.server.db;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lastbamboo.common.amazon.sdb.AmazonSdbImpl;
import org.lastbamboo.common.amazon.stack.AwsUtils;
import org.lastbamboo.server.resource.BadPasswordResetIdException;
import org.lastbamboo.server.resource.UserExistsException;
import org.lastbamboo.server.resource.UserNotFoundException;
import org.lastbamboo.server.resource.UserNotVerifiedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the resource repository for parts of the database that use 
 * Amazon's Simple DB.
 */
public class AmazonSdbUserDaoTest
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(AmazonSdbUserDaoTest.class);
    
    private static UserDao s_userDao;
    
    private static final long s_eventualConsistencySleep = 1800;
    
    private static final boolean TEST_ACTIVE = false;

    private static String s_domainName = AmazonSdbUserDaoTest.class.getName();

    private static AmazonSdbImpl s_simpleDb;
    
    @BeforeClass public static void setUpAll() throws Exception
        {
        if (!TEST_ACTIVE) return;
        s_simpleDb = new AmazonSdbImpl(s_domainName);
        s_simpleDb.deleteDomain(s_domainName);
        Thread.sleep(s_eventualConsistencySleep);        
        
        s_userDao = new AmazonSdbUserDao(s_simpleDb);
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
        if (!TEST_ACTIVE) return;
        s_simpleDb.deleteDomain();
        }
    
    public void testNewAdminGroup() throws InterruptedException
        {
        if (!AwsUtils.hasPropsFile())
            {
            return;
            }
        
        final String email = "fdafdaa@28972anflja.com";
        final String password = "fdaafda;l";
        long userId;
        try
            {
            userId = s_userDao.newWebUser(email, password);
            Thread.sleep(s_eventualConsistencySleep);
            }
        catch (final UserExistsException e)
            {
            fail("User should not exist"+e);
            return;
            }
        assertTrue(userId != -1);
        
        Thread.sleep(this.s_eventualConsistencySleep);
        try
            {
            s_userDao.newAminGroup(userId, "newGroupID1");
            s_userDao.newAminGroup(userId, "newGroupID2");
            s_userDao.newAminGroup(userId, "newGroupID3");
            s_userDao.newAminGroup(userId, "newGroupID4");
            }
        catch (IOException e)
            {
            fail("Could not create group"+e);
            }
        
        Thread.sleep(this.s_eventualConsistencySleep);
        try
            {
            final Map<String, Collection<String>> attributes = 
                s_userDao.getAttributes(userId);
            final Collection<String> adminGroups = 
                attributes.get(UserAttributes.ADMIN_GROUPS);
            assertNotNull("No admin groups attribute in: "+attributes, adminGroups);
            final Iterator<String> iter = adminGroups.iterator();
            assertEquals("newGroupID1", iter.next());
            assertEquals("newGroupID2", iter.next());
            assertEquals("newGroupID3", iter.next());
            assertEquals("newGroupID4", iter.next());
            }
        catch (IOException e)
            {
            fail("Could not get attributes: "+e);
            }
        }
    
    public void testResetPassword() throws InterruptedException
        {
        if (!AwsUtils.hasPropsFile())
            {
            return;
            }
        final String email = "ueiurqp@addrefdasss.com";
        final String password = "fjioqka;l";
        long resetId = 42890472L;
        
        try
            {
            s_userDao.resetPassword(email, password, resetId);
            fail("Should have thrown an exception");
            }
        catch (final UserNotFoundException e)
            {
            // Expected.
            }
        catch (final BadPasswordResetIdException e)
            {
            fail("Unexpected exception"+e);
            }
        catch (IOException e)
            {
            fail("Unexpected exception"+e);
            }
        
        long userId;
        try
            {
            userId = s_userDao.newWebUser(email, password);
            Thread.sleep(s_eventualConsistencySleep);
            }
        catch (final UserExistsException e2)
            {
            fail("User should not exist");
            return;
            }
        assertTrue(userId != -1);
        
        // Mostly just make sure we don't get an exception.
        try
            {
            resetId = s_userDao.generatePasswordResetId(email);
            }
        catch (final UserNotFoundException e1)
            {
            fail("User should exist");
            }
        
        assertTrue(resetId != -1);
        Thread.sleep(s_eventualConsistencySleep);
        
        final String newPassword = "fjaoffdadaj92";
        // Try a bad reset ID.
        try
            {
            // Note this also changes the reset ID -- you only get one guess!!
            s_userDao.resetPassword(email, newPassword, 47297429L);
            fail("Should have thrown an exception");
            }
        catch (final UserNotFoundException e)
            {
            fail("user should exist");
            }
        catch (final BadPasswordResetIdException e)
            {
            // Expected.
            }
        catch (IOException e)
            {
            fail("Unexpected exception"+e);
            }
        
        
        // Now try the real reset ID.
        try
            {
            resetId = s_userDao.generatePasswordResetId(email);
            }
        catch (final UserNotFoundException e1)
            {
            fail("User should exist");
            }
        
        Thread.sleep(s_eventualConsistencySleep);
        try
            {
            s_userDao.resetPassword(email, newPassword, resetId);
            }
        catch (final UserNotFoundException e)
            {
            fail("user should exist");
            }
        catch (final BadPasswordResetIdException e)
            {
            fail("Should not be a reset ID problem");
            }
        catch (IOException e)
            {
            fail("Unexpected exception"+e);
            }
        
        Thread.sleep(s_eventualConsistencySleep);
        // Now make sure resetting the password also confirmed the account.
        try
            {
            s_userDao.authenticateWebUser(email, newPassword);
            }
        catch (final UserNotVerifiedException e)
            {
            fail("Should now be verified.");
            }
        catch (final UserNotFoundException e)
            {
            fail("Unexpected exception: "+e);
            }

        // Reset ID should not work twice.
        try
            {
            s_userDao.resetPassword(email, newPassword, resetId);
            }
        catch (final UserNotFoundException e)
            {
            fail("Unexpected exception: "+e);
            }
        catch (final BadPasswordResetIdException e)
            {
            // Expected.
            }
        catch (IOException e)
            {
            fail("Unexpected exception"+e);
            }
        
        // Should not be able to authenticate with the old password.
        try
            {
            s_userDao.authenticateWebUser(email, password);
            fail("Should have thrown an exception");
            }
        catch (final UserNotVerifiedException e)
            {
            fail("User should be verified.");
            }
        catch (final UserNotFoundException e)
            {
            // Expected.
            }
        
        // This should succeed.
        try
            {
            s_userDao.authenticateWebUser(email, newPassword);
            }
        catch (final UserNotVerifiedException e)
            {
            fail("Unexpected exception: "+e);
            }
        catch (final UserNotFoundException e)
            {
            fail("Unexpected exception: "+e);
            }
        }
    
    public void testGeneratePasswordResetId() throws Exception
        {
        if (!AwsUtils.hasPropsFile())
            {
            return;
            }
        final String email = "ufafjqreqp@jfdlafdaaajf.com";
        final String password = "fjfkdafkldja";
        
        final long userId = s_userDao.newWebUser(email, password);
        assertTrue(userId != -1);
        Thread.sleep(s_eventualConsistencySleep);
        
        // Mostly just make sure we don't get an exception.
        long resetId = s_userDao.generatePasswordResetId(email);
        
        assertTrue(resetId != -1);
        
        Thread.sleep(s_eventualConsistencySleep);
        
        s_userDao.resetPassword(email, password, resetId);
        
        assertTrue(s_userDao.deleteWebUser(email));
        }
    
    public void testConfirmNewUser() throws Exception
        {
        if (!AwsUtils.hasPropsFile())
            {
            return;
            }
        final String email = "billfaeawy@ad47292249.com";
        final String password = "289472";
        
        Thread.sleep(s_eventualConsistencySleep);
        final long userId = s_userDao.newWebUser(email, password);
        assertTrue(userId != -1);
        Thread.sleep(s_eventualConsistencySleep);

        assertFalse(s_userDao.confirmNewUser(47289L));
        
        assertTrue(s_userDao.confirmNewUser(userId));
        
        Thread.sleep(s_eventualConsistencySleep);
        assertEquals(userId, s_userDao.authenticateWebUser(email, password));
        
        assertTrue(s_userDao.deleteWebUser(email));
        
        }

    public void testNewUser() throws Exception
        {
        if (!AwsUtils.hasPropsFile())
            {
            return;
            }
        final String email = "bfadaob@bob4792729.org";
        final String password = "289472";
        
        //Thread.sleep(s_eventualConsistencySleep);
        // Just make sure this doesn't throw an exception.
        final long id = s_userDao.newWebUser(email, password); 
        assertTrue(id != -1);
        
        Thread.sleep(s_eventualConsistencySleep);
        try
            {
            s_userDao.newWebUser(email, password);
            fail("The user should already exist.");
            }
        catch (final UserExistsException e)
            {
            // Expected.
            }
        
        try
            {
            s_userDao.authenticateWebUser(email, password);
            fail("Should need verification");
            }
        catch (UserNotVerifiedException e)
            {
            // expected.
            }
        catch (UserNotFoundException e)
            {
            fail("Should need verification");
            }
        Thread.sleep(s_eventualConsistencySleep);
        s_userDao.confirmNewUser(id);
        
        Thread.sleep(s_eventualConsistencySleep);
        assertEquals("Should have authenticated after confirmation!!", 
            id, s_userDao.authenticateWebUser(email, password));
        
        //assertTrue(s_repo.newWebUser(email, password) == -1);
        
        assertTrue(s_userDao.deleteWebUser(email));
        
        Thread.sleep(s_eventualConsistencySleep);
        Thread.sleep(s_eventualConsistencySleep);
        
        // Now make sure we can insert the user again.
        assertTrue("Should have been able to create a new user", 
            s_userDao.newWebUser(email, password) != -1);
        
        Thread.sleep(s_eventualConsistencySleep);
        assertTrue(s_userDao.deleteWebUser(email));
        }
    
    public void testAuthenticateUser() throws Exception
        {
        if (!AwsUtils.hasPropsFile())
            {
            return;
            }
        final String email = "roadfdafan@rdfafaon.org";
        final String password = "289472";
        
        try
            {
            s_userDao.authenticateWebUser(email, password);
            fail("User should not exist.");
            }
        catch (UserNotVerifiedException e)
            {
            fail("User should not exist.");
            }
        catch (UserNotFoundException e)
            {
            // expected
            }
        
        // Now create a user and make sure we can authenticate.
        final long id = s_userDao.newWebUser(email, password);
        Thread.sleep(this.s_eventualConsistencySleep);
        assertTrue(id != -1);
        try
            {
            s_userDao.authenticateWebUser(email, password);
            fail("User should not be verified.");
            }
        catch (UserNotVerifiedException e)
            {
            // expected
            }
        catch (UserNotFoundException e)
            {
            fail("Should not be confirmed.");
            }
        
        assertTrue("Could not confirm user", s_userDao.confirmNewUser(id));
        Thread.sleep(s_eventualConsistencySleep);
        assertEquals(id, s_userDao.authenticateWebUser(email, password));
        
        Thread.sleep(s_eventualConsistencySleep);
        // Clean up the user.
        assertTrue(s_userDao.deleteWebUser(email));
        
        Thread.sleep(s_eventualConsistencySleep);
        try
            {
            s_userDao.authenticateWebUser(email, password);
            fail("User should not exist.");
            }
        catch (UserNotVerifiedException e)
            {
            fail("User should not exist.");
            }
        catch (UserNotFoundException e)
            {
            // expected.
            }
        }
    
    @Test public void dummyTest() throws Exception
        {
        assertFalse(false);
        }
    }
