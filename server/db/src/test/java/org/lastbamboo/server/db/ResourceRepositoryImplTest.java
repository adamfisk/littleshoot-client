package org.lastbamboo.server.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lastbamboo.common.util.ResourceTypeTranslatorImpl;
import org.lastbamboo.common.util.Sha1Hasher;
import org.lastbamboo.common.util.ShootConstants;
import org.lastbamboo.server.db.stubs.JmxMonitorStub;
import org.lastbamboo.server.resource.BadPasswordResetIdException;
import org.lastbamboo.server.resource.FileAndInstances;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.FileResourceResult;
import org.lastbamboo.server.resource.MetaFileResource;
import org.lastbamboo.server.resource.MetaFileResourceResult;
import org.lastbamboo.server.resource.OnlineInstance;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.resource.UserExistsException;
import org.lastbamboo.server.resource.UserNotFoundException;
import org.lastbamboo.server.resource.UserNotVerifiedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the resource repository.
 * 
 * NOTES: This test is tricky because the database always stores information
 * about MetaFileResources even when all the FileResources have been 
 * deleted.  Tests can affect each other as a result.  One example is inserting
 * a file with the same SHA-1 but with different media types.  The first
 * media type is the one recorded, but that can lead to confusion if we happen
 * to use the same SHA-1 in a subsequent test, but a different media type.
 * 
 * The same thing can also happen when files with the same SHA-1 have different
 * URIs.
 * <p>
 * In general, many of the issues here can be traced back to conflicts with
 * other fields for files with the same SHA-1.
 * 
 * NOTE2: Sometimes checkClean hides failures in the tests -- remove it to 
 * see the actual failure!
 */
public class ResourceRepositoryImplTest
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass()); 
    
    private static SessionFactory s_sessionFactory;

    private static ResourceRepository s_repo;
    private static final boolean TEST_ACTIVE = false;
    
    /**
     * Allows you to use MySQL for the test if you have it running on your 
     * local system.
     */
    private static final boolean USE_MYSQL = false;
    
    /**
     * This determines the number of page loop tests to run.  We've experienced
     * problems with MySQL connections dying over time, and this can simulate
     * longer running times.
     */
    private static final int NUM_PAGE_LOOPS = 1;
    
    private static final String[] MY_SQL_PROPS =
        {
        "org.hibernate.dialect.MySQLDialect",
        "com.mysql.jdbc.Driver",
        "jdbc:mysql://localhost/bamboo",
        "bamboo",
        "bamboo"
        };
    
    private static final String[] HSQL_PROPS =
        {
        "org.hibernate.dialect.HSQLDialect",
        "org.hsqldb.jdbcDriver",
        "jdbc:hsqldb:mem:test",
        "sa",
        ""
        };

    private static final Configuration CONFIG = new Configuration();
    static 
        {
        
        final String[] dbProps;
        if (USE_MYSQL)
            {
            dbProps = MY_SQL_PROPS;
            }
        else
            {
            dbProps = HSQL_PROPS;
            }
        
        CONFIG.setProperty("hibernate.dialect", dbProps[0]);
        CONFIG.setProperty("hibernate.connection.driver_class", dbProps[1]);
        CONFIG.setProperty("hibernate.connection.url", dbProps[2]);
        CONFIG.setProperty("hibernate.connection.username", dbProps[3]);
        CONFIG.setProperty("hibernate.connection.password", dbProps[4]);
        }
    static 
        {
        CONFIG.
            setProperty("hibernate.hbm2ddl.auto", "create-drop").
            setProperty("hibernate.show_sql", "false").
            setProperty("hibernate.format_sql", "true").
            setProperty("hibernate.current_session_context_class", "thread").
            setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider").
            setProperty("hibernate.cache.use_second_level_cache", "false").
            setProperty("hibernate.cache.use_query_cache", "false").
            setProperty("hibernate.c3p0.min_size","5").
            setProperty("hibernate.c3p0.max_size","20").
            setProperty("hibernate.c3p0.timeout","300").
            setProperty("hibernate.c3p0.max_statements","50").
            setProperty("hibernate.c3p0.idle_test_period","3000").
            addClass(MetaFileResourceImpl.class).
            addClass(FileResourceImpl.class).
            addClass(InstanceImpl.class).
            addClass(OnlineInstanceImpl.class).
            addClass(UserImpl.class);
    
        s_sessionFactory = CONFIG.buildSessionFactory();
        }

    private Collection<String> m_testFileNames = new HashSet<String>();
    
    private static long s_nextUserId = 77L;
    
    @BeforeClass public static void setUpAll() throws Exception
        {
        if (!TEST_ACTIVE) return;
        s_repo = createRepository();
        }
    
    @Before public void setUp() throws Exception
        {
        if (!TEST_ACTIVE) return;
        s_nextUserId++;
        checkClean();
        }
    
    private static ResourceRepositoryImpl createRepository()
        {
        if (!TEST_ACTIVE) return null;
        final MetaFileResourceDaoImpl metaFrDao = 
            new MetaFileResourceDaoImpl(s_sessionFactory);
        final FileResourceDaoImpl frDao = 
            new FileResourceDaoImpl(s_sessionFactory);
        final InstanceDaoImpl urDao = 
            new InstanceDaoImpl(s_sessionFactory);
        final UserDao userDao = new UserDaoImpl(s_sessionFactory);
        final ResourceSearchDaoImpl rsDao =
            new ResourceSearchDaoImpl(s_sessionFactory);
        final ResourceRepositoryImpl repository =
            new ResourceRepositoryImpl(metaFrDao, frDao, urDao, userDao, null,
                rsDao, 
                s_sessionFactory, new JmxMonitorStub());
        return repository;
        }
    
    @After public void tearDown() throws Exception
        {
        if (!TEST_ACTIVE) return;
        checkClean();
        }
    
    private void checkClean()
        {
        assureNoResults("test");
        assureNoResults("name");
        assureNoResults("tag");
        final Collection<OnlineInstance> onlineInstances = 
            s_repo.getOnlineInstances();
        
        if (!onlineInstances.isEmpty())
            {
            final String msg = onlineInstances.size() + " online instances(s)." + 
                "\n Instances: "+onlineInstances;
            assertEquals(msg, 0, onlineInstances.size());
            }
        
        for (final String name : this.m_testFileNames)
            {
            assureNoResults(name);
            }
        }

    
    private void assureNoResults(final String searchTerm)
        {
        final Collection<MetaFileResource> results = 
            s_repo.search(searchTerm, 0, 10, SystemUtils.OS_NAME, 
                RandomUtils.nextLong(), true, 
                true, true, true, true).getResults();
        
        if (!results.isEmpty())
            {
            final String msg = "Found result: "+results.iterator().next();
            assertEquals(msg, 0, results.size());
            }
        }
    
    public void testEditResource() throws Exception
        {
        if (!TEST_ACTIVE) return;
        final FileResource fr = makeFileResource();
        final String sha1 = fr.getSha1Urn();
        s_repo.insertResource(fr);
        
        final String tags = 
            s_repo.getFileAndInstances(new URI(sha1)).getMetaFileResource().getTags();
        
        final FileResource fileResource = 
            s_repo.getFileResource(fr.getInstanceId(), sha1);
        
        assertNotNull("Null resource", fileResource);
        final String newTag = "dj239ur29u20j2";
        
        s_repo.editResource(fr.getInstanceId(), fr.getSha1Urn(), newTag, 
            "http://www.littleshoot.org");
        
        final String newTags = 
            s_repo.getFileAndInstances(new URI(fr.getSha1Urn())).getMetaFileResource().getTags();
        
        final String expected = tags + " " + newTag;
        assertEquals("Unexpected tags", expected, newTags);
        
        final String newFrTags = 
            s_repo.getFileResource(fr.getInstanceId(), fr.getSha1Urn()).getTags();
        assertEquals("Unexpected tags", newTag, newFrTags);
        
        s_repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        }

    
    public void testResetPassword()
        {
        if (!TEST_ACTIVE) return;
        final String email = "ueiurqp@address.com";
        final String password = "fjioqka;l";
        long resetId = 42890472L;
        
        try
            {
            s_repo.resetPassword(email, password, resetId);
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
            userId = s_repo.newWebUser(email, password);
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
            resetId = s_repo.generatePasswordResetId(email);
            }
        catch (final UserNotFoundException e1)
            {
            fail("User should exist");
            }
        
        assertTrue(resetId != -1);
        
        final String newPassword = "fjaofj92";
        // Try a bad reset ID.
        try
            {
            s_repo.resetPassword(email, newPassword, 47297429L);
            fail("Should have thrown an exception");
            }
        catch (UserNotFoundException e)
            {
            fail("user should exist");
            }
        catch (BadPasswordResetIdException e)
            {
            // Expected.
            }
        catch (IOException e)
            {
            fail("Unexpected exception"+e);
            }
        
        
        try
            {
            s_repo.resetPassword(email, newPassword, resetId);
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
        
        // Now make sure resetting the password also confirmed the account.
        try
            {
            s_repo.authenticateWebUser(email, newPassword);
            }
        catch (UserNotVerifiedException e)
            {
            fail("Should now be verified.");
            }
        catch (UserNotFoundException e)
            {
            fail("Unexpected exception: "+e);
            }

        // Reset ID should not work twice.
        try
            {
            s_repo.resetPassword(email, newPassword, resetId);
            }
        catch (UserNotFoundException e)
            {
            fail("Unexpected exception: "+e);
            }
        catch (BadPasswordResetIdException e)
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
            s_repo.authenticateWebUser(email, password);
            fail("Should have thrown an exception");
            }
        catch (UserNotVerifiedException e)
            {
            fail("User should be verified.");
            }
        catch (UserNotFoundException e)
            {
            // Expected.
            }
        
        // This should succeed.
        try
            {
            s_repo.authenticateWebUser(email, newPassword);
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
        if (!TEST_ACTIVE) return;
        final String email = "ufafjqreqp@jfdlajf.com";
        final String password = "fjfkdafkldja";
        
        // We should get an exception here because the user doesn't exist.
        long resetId = -1;
        try
            {
            resetId = s_repo.generatePasswordResetId(email);
            fail("Should have not found the user");
            }
        catch (final UserNotFoundException e)
            {
            // Expected.
            }
        
        final long userId = s_repo.newWebUser(email, password);
        assertTrue(userId != -1);
        
        // Mostly just make sure we don't get an exception.
        resetId = s_repo.generatePasswordResetId(email);
        assertTrue(resetId != -1);
        
        assertTrue(s_repo.deleteWebUser(email));
        }
    
    public void testConfirmNewUser() throws Exception
        {
        final String email = "billy@address.com";
        final String password = "289472";
        
        final long userId = s_repo.newWebUser(email, password);
        assertTrue(userId != -1);

        assertFalse(s_repo.confirmNewUser(47289L));
        
        assertTrue(s_repo.confirmNewUser(userId));
        
        assertEquals(userId, s_repo.authenticateWebUser(email, password));
        
        assertTrue(s_repo.deleteWebUser(email));
        }

    public void testNewUser() throws Exception
        {
        final String email = "bob@bob.org";
        final String password = "289472";
        
        // Just make sure this doesn't throw an exception.
        final long id = s_repo.newWebUser(email, password); 
        assertTrue(id != -1);
        
        try
            {
            s_repo.newWebUser(email, password);
            fail("The user should already exist.");
            }
        catch (final UserExistsException e)
            {
            // Expected.
            }
        
        try
            {
            s_repo.authenticateWebUser(email, password);
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
        s_repo.confirmNewUser(id);
        
        assertEquals("Should have authenticated after confirmation!!", 
            id, s_repo.authenticateWebUser(email, password));
        
        //assertTrue(s_repo.newWebUser(email, password) == -1);
        
        assertTrue(s_repo.deleteWebUser(email));
        
        // Now make sure we can insert the user again.
        assertTrue(s_repo.newWebUser(email, password) != -1);
        assertTrue(s_repo.deleteWebUser(email));
        }
    
    public void testAuthenticateUser() throws Exception
        {
        final String email = "ron@ron.org";
        final String password = "289472";
        
        try
            {
            s_repo.authenticateWebUser(email, password);
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
        final long id = s_repo.newWebUser(email, password);
        assertTrue(id != -1);
        try
            {
            s_repo.authenticateWebUser(email, password);
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
        
        assertTrue("Could not confirm user", s_repo.confirmNewUser(id));
        assertEquals(id, s_repo.authenticateWebUser(email, password));
        
        // Clean up the user.
        assertTrue(s_repo.deleteWebUser(email));
        
        try
            {
            s_repo.authenticateWebUser(email, password);
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
    
    public void testGetFileResource() throws Exception
        {
        final long userId = 4782912L;
        final FileResource fr = makeFileResource("file.txt", userId);
        final String groupName = "dhquihdqihfqilqfqf";
        fr.setGroupName(groupName);
        final String sha1 = fr.getSha1Urn();
        
        assertEquals(null, s_repo.getFileResource(userId, sha1));
        
        // This will throw an exception if it fails.
        s_repo.insertResource(fr);
        
        final FileResource resource = s_repo.getFileResource(userId, sha1);
        assertEquals(groupName, fr.getGroupName());
        assertNotNull(resource);
        s_repo.deleteResource(userId, sha1);
        }
    
    public void testPagedFiles() throws Exception
        {
        final long userId = 478921L;
        Collection<FileResource> files = 
            s_repo.getFileResources(0, 10, userId).getResources();
        assertEquals(0, files.size());
        final FileResource fr = makeFileResource("file2.txt", userId);
        
        // This will throw an exception if it fails.
        s_repo.insertResource(fr);
        
        final FileResource fr2 = makeFileResource("file1.txt", userId);
        
        // This will throw an exception if it fails.
        s_repo.insertResource(fr2);
        
        FileResourceResult result = s_repo.getFileResources(0, 10, userId);
        assertEquals(2, result.getResources().size());
        assertEquals(2, result.getTotalResults());
        
        // Try with a tiny page size.  Note the results are ordered 
        // alphabetically.
        result = s_repo.getFileResources(0, 1, userId);
        
        Collection<FileResource> resources = result.getResources();
        FileResource lfr = resources.iterator().next();
        assertEquals(fr2.getName(), lfr.getTitle());
        assertEquals(1, resources.size());
        assertEquals(2, result.getTotalResults());
        
        // Try the next tiny page.  Note the results are ordered 
        // alphabetically.
        result = s_repo.getFileResources(1, 1, userId);
        
        resources = result.getResources();
        lfr = resources.iterator().next();
        
        assertEquals(userId, lfr.getInstanceId());
        assertEquals(fr.getTitle(), lfr.getTitle());
        assertEquals(1, resources.size());
        assertEquals(2, result.getTotalResults());
        
        s_repo.deleteResource(userId, fr.getSha1Urn());
        s_repo.deleteResource(userId, fr2.getSha1Urn());
        
        files = s_repo.getFileResources(0, 10, userId).getResources();
        
        if (m_log.isDebugEnabled())
            {
            for (final FileResource resource : files)
                {
                m_log.debug("We still have a resource for user: " + resource.getInstanceId());
                }
            }
        assertEquals(0, files.size());   
        }
    
    public void testPagedFilesGroup() throws Exception
        {
        final String groupName = RandomUtils.nextInt() + "RandomName";
        final long userId = 478921L;
        Collection<FileResource> files = 
            s_repo.getFileResources(0, 10, userId, groupName).getResources();
        assertEquals(0, files.size());
        final FileResource fr = makeFileResource("file2.txt", userId, groupName);
        
        // This will throw an exception if it fails.
        s_repo.insertResource(fr);
        
        final FileResource fr2 = makeFileResource("file1.txt", userId, groupName);
        
        // This will throw an exception if it fails.
        s_repo.insertResource(fr2);
        
        FileResourceResult result = 
            s_repo.getFileResources(0, 10, userId, groupName);
        assertEquals(2, result.getResources().size());
        assertEquals(2, result.getTotalResults());
        
        // Test to make sure we don't get them if we don't specify the group
        // name.
        result = s_repo.getFileResources(0, 10, userId);
        assertEquals(0, result.getResources().size());
        assertEquals(0, result.getTotalResults());
        
        // Try with a tiny page size.  Note the results are ordered 
        // alphabetically.
        result = s_repo.getFileResources(0, 1, userId, groupName);
        
        Collection<FileResource> resources = result.getResources();
        FileResource lfr = resources.iterator().next();
        assertEquals(fr2.getName(), lfr.getTitle());
        assertEquals(1, resources.size());
        assertEquals(2, result.getTotalResults());
        
        // Try the next tiny page.  Note the results are ordered 
        // alphabetically.
        result = s_repo.getFileResources(1, 1, userId, groupName);
        
        resources = result.getResources();
        lfr = resources.iterator().next();
        
        assertEquals(userId, lfr.getInstanceId());
        assertEquals(fr.getTitle(), lfr.getTitle());
        assertEquals(1, resources.size());
        assertEquals(2, result.getTotalResults());
        
        s_repo.deleteResource(userId, fr.getSha1Urn());
        s_repo.deleteResource(userId, fr2.getSha1Urn());
        
        files = s_repo.getFileResources(0, 10, userId).getResources();
        
        if (m_log.isDebugEnabled())
            {
            for (final FileResource resource : files)
                {
                m_log.debug("We still have a resource for user: " + resource.getInstanceId());
                }
            }
        assertEquals(0, files.size());   
        }
    
    private FileResource makeFileResource(final String testFileName, 
        final long instanceId, final String groupName) throws IOException
        {
        final File testFile = makeRandomFile(testFileName);
        
        assertTrue(testFile.isFile());
        
        final String name = testFile.getName();
        final String uri = 
            "http://www.site.org/file-"+RandomUtils.nextInt()+".mov";
        final String sha1 = Sha1Hasher.createSha1Urn(testFile).toASCIIString();
        final long bytes = testFile.length();
        
        // Flag for whether or not this file was published as the result of being downloaded.
        final boolean downloaded = false;
        final String remoteHost = "78.32.65.1";
        final String tags = "test tags";
        final String language = SystemUtils.USER_LANGUAGE;
        final String country = SystemUtils.USER_COUNTRY;
        final String timeZone = SystemUtils.USER_TIMEZONE;
        
        final String mediaType = new ResourceTypeTranslatorImpl().getType(name);

        final String mimeType = "text/plain";
        
        m_log.debug("Inserting resource");
        final FileResource fr = 
            new FileResourceImpl(uri, name, mimeType, sha1, bytes, instanceId, 
                remoteHost, tags, mediaType, language, country, timeZone, 
                downloaded, 4729L, Permission.PUBLIC, groupName);
        
        this.m_testFileNames.add(name);
        return fr;
        }

    private FileResource makeFileResource() throws IOException
        {
        return makeFileResource("filename"+RandomUtils.nextInt(), 
            RandomUtils.nextInt(), ShootConstants.WORLD_GROUP);
        }

    private FileResource makeFileResource(final String testFileName, 
        final long instanceId) throws IOException
        {
        return makeFileResource(testFileName, instanceId, 
            ShootConstants.WORLD_GROUP);
        }

    private File makeRandomFile(final String name) throws IOException
        {
        return makeRandomFile(new File("."), name);
        }
    
    private File makeRandomFile(final File dir, final String name) 
        throws IOException
        {
        final File testFile = new File(dir, name);
        testFile.deleteOnExit();
        final Writer fw = new FileWriter(testFile);
        fw.write("Give the file something RANDOM to work with"+new Random().nextLong());
        fw.close();
        return testFile;
        }
    
    public void testTypeSearch() throws Exception
        {
        final long instanceId = s_nextUserId;
        final String baseUri1 = "sip://"+instanceId;
        final String serverAddress = "48.5.24.2";
        s_repo.setInstanceOnline(instanceId, baseUri1, true, serverAddress);
        final String fileName = "test.tgz";
        final String type = new ResourceTypeTranslatorImpl().getType(fileName);
        FileResource fr1 = new FileResourceImpl(fileName, 
            "video/mpeg", "urn:sha1:"+type.hashCode(), 24927L, instanceId, 
            "http://example.com", "tags", type, SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.insertResource(fr1);
        
        MetaFileResourceResult result = 
            s_repo.search(fr1.getTitle(), 0, 10, SystemUtils.OS_NAME, instanceId, 
                true, true, true, true, true);  
        assertEquals(1, result.getTotalResults());
        
        
        // Applications only.
        result = 
            s_repo.search(fr1.getTitle(), 0, 10, SystemUtils.OS_NAME, instanceId, 
                true, false, false, false, false);
        assertEquals(1, result.getTotalResults());
        
        // Documents only.  TGZ files should appear in both.
        result = 
            s_repo.search(fr1.getTitle(), 0, 10, SystemUtils.OS_NAME, instanceId, 
                false, false, true, false, false);
        assertEquals(1, result.getTotalResults());
        
        s_repo.setInstanceOnline(instanceId, baseUri1, false, serverAddress);
        s_repo.deleteResource(fr1.getInstanceId(), fr1.getSha1Urn());
        result = 
            s_repo.search(fr1.getTitle(), 0, 10, SystemUtils.OS_NAME, instanceId, 
                false, false, true, false, false);
        assertEquals(0, result.getTotalResults());
        }
    
    /**
     * Test for bringing an entire server on and offline.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testServerOffline() throws Exception
        {
        final long user1 = s_nextUserId;
        final long user2 = 79584L;
        final String baseUri1 = "sip://"+user1;
        final String baseUri2 = "sip://"+user2;
        final String serverAddress = "50.53.24.2";
        s_repo.setInstanceOnline(user1, baseUri1, true, serverAddress);
        s_repo.setInstanceOnline(user2, baseUri2, true, serverAddress);
        FileResource fr1 = new FileResourceImpl("tsunami_phuket.wmv", 
            "video/mpeg", "urn:sha1:SHA1_EXAMPLE", 24927L, user1, 
            "http://example.com", "tags", "video", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.insertResource(fr1);
        
        Collection<OnlineInstance> users = s_repo.getOnlineInstances();
        
        assertEquals(2, users.size());
        MetaFileResourceResult result = 
            s_repo.search(fr1.getTitle(), 0, 10, SystemUtils.OS_NAME, user1, 
                true, true, true, true, true);  
        assertEquals(1, result.getTotalResults());
        
        FileAndInstances fileAndUsers = 
            s_repo.getFileAndInstances(new URI(fr1.getSha1Urn()));
        assertEquals(1, fileAndUsers.getOnlineInstances().size());
        assertTrue(fileAndUsers.getMetaFileResource() != null);
        
        // Now set the server offline.  All files for all users for that server
        // should also reflect that there are no online users anymore.
        s_repo.setServerOnline(false, serverAddress);
        
        users = s_repo.getOnlineInstances();
        assertEquals(0, users.size());
        
        
        // Now check the file.
        result = 
            s_repo.search(fr1.getTitle(), 0, 10, SystemUtils.OS_NAME, user1, 
                true, true, true, true, true);  
        assertEquals(0, result.getTotalResults());
        
        fileAndUsers = s_repo.getFileAndInstances(new URI(fr1.getSha1Urn()));
        assertEquals(0, fileAndUsers.getOnlineInstances().size());
        }
    
    public void testUpdateFile() throws Exception
        {
        final long user1 = s_nextUserId;
        
        FileResource fr1 = new FileResourceImpl("tsunami_phuket.wmv", 
            "video/mpeg", "urn:sha1:SHA1_EXAMPLE", 24927L, user1, 
            "http://example.com", "tags", "video", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        
        final String baseUri1 = "sip://"+fr1.getInstanceId();
        
        s_repo.insertResource(fr1);
        s_repo.setInstanceOnline(user1, baseUri1, true, "14.53.24.2");
        
        final String crazyNewTag ="wruwitoewjre";
        
        MetaFileResourceResult result = 
            s_repo.search(crazyNewTag, 0, 10, SystemUtils.OS_NAME, user1, 
                true, true, true, true, true);  
        
        assertEquals(0, result.getTotalResults());
        
        fr1.setTags(fr1.getTags() + " " + crazyNewTag);
        
        // This should add the new tag.
        s_repo.insertResource(fr1);
        
        result = 
            s_repo.search(crazyNewTag, 0, 10, SystemUtils.OS_NAME, user1, 
                true, true, true, true, true);  
        
        assertEquals(1, result.getTotalResults());
        
        s_repo.setInstanceOnline(user1, baseUri1, false, "14.53.24.2");
        s_repo.deleteResource(fr1.getInstanceId(), fr1.getSha1Urn());
        }
    
    public void testGetOnlineInstances() throws Exception
        {
        final long user1 = 89353L;
        final String baseUri1 = "sip://"+user1;
        s_repo.setInstanceOnline(user1, baseUri1, true, "14.53.24.2");
        final Collection<OnlineInstance> users = 
            s_repo.getOnlineInstances();
        
        assertEquals(1, users.size());
        
        s_repo.setInstanceOnline(user1, baseUri1, false, "14.53.24.2");
        }
    
    public void testNumOnlineUsers() throws Exception
        {
        final long user1 = 95343L;
        final long user2 = 2732L;
        final String serverAddress = "41.53.4.2";
        
        final String SAME_SHA1 = "urn:sha1:SHA1_EXAMPLE";
        
        final FileResource fr1 = new FileResourceImpl("tsunami_phuket.wmv", 
            "video/mpeg", SAME_SHA1, 24927L, user1, 
            "http://example.com", "tags", "video", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        
        final FileResource fr2 = new FileResourceImpl("different name", 
            "video/mpeg", SAME_SHA1, 24927L, user2, 
            "http://example.com", "more tags", "video", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        final String baseUri1 = "sip://"+fr1.getInstanceId();
        final String baseUri2 = "sip://"+fr2.getInstanceId();
        
        s_repo.insertResource(fr1);
        s_repo.insertResource(fr2);
        s_repo.setInstanceOnline(user1, baseUri1, true, serverAddress);
        s_repo.setInstanceOnline(user2, baseUri2, true, serverAddress);
        final String keywords = fr1.getName();
        
        m_log.debug("Launching search for: "+keywords);
        MetaFileResourceResult result = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user2, 
                false, false, false, false, true);  
        
        MetaFileResource mfr1 = result.getResults().iterator().next();   
        assertEquals(2, mfr1.getNumOnlineInstances());
        
        s_repo.setInstanceOnline(user1, baseUri1, false, serverAddress);
        
        result = s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user2, 
            false, false, false, false, true);  
        
        m_log.debug("Got results: "+result.getTotalResults());
        mfr1 = result.getResults().iterator().next();   
        assertEquals(1, mfr1.getNumOnlineInstances());
        
        s_repo.setInstanceOnline(user2, baseUri2, false, serverAddress);
        
        m_log.debug("Deleting resources...");
        s_repo.deleteResource(fr1.getInstanceId(), fr1.getSha1Urn());
        s_repo.deleteResource(fr2.getInstanceId(), fr2.getSha1Urn());
        
        m_log.debug("Verifying no online users...");
        Collection<OnlineInstance> onlineUsers = s_repo.getOnlineInstances();
        assertEquals(0, onlineUsers.size());
        m_log.debug("Verified no online users...");
        checkClean();
        }
    
    public void testCaseInsensitive() throws Exception
        {
        // Tests to make sure searches are case insensitive but that the 
        // database preserves the published case in results.
        final long user1 = s_nextUserId;
        m_log.debug("Testing case insensitive query");
        
        final FileResource fr1 = new FileResourceImpl("Case Sensitive Name.wmv", 
            "video/mpeg", "urn:sha1:forcasesensitive", 24927L, user1, 
            "http://example.com", "tags", "video", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);

        final String baseUri1 = "sip://"+fr1.getInstanceId();
        
        s_repo.insertResource(fr1);
        s_repo.setInstanceOnline(user1, baseUri1, true, "14.53.24.2");
        final String keywords = "case sensitive";
        
        m_log.debug("Launching search for: "+keywords);
        MetaFileResourceResult result = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user1, 
                false, false, false, false, true);  
        
        m_log.debug("Total results: "+result.getTotalResults());
        assertEquals(1, result.getTotalResults());
        
        final MetaFileResource mfr1 = result.getResults().iterator().next();
        assertEquals("Incorrect title", fr1.getName(), mfr1.getTitle());
        
        // Cleanup.
        s_repo.setInstanceOnline(user1, baseUri1, false, "14.53.24.2");
        s_repo.deleteResource(fr1.getInstanceId(), fr1.getSha1Urn());    
        }
    
    
    public void testSameUrnMultipleNamesTags() throws Exception
        {
        // Tests that the same URN with multiple titles and tags are handled
        // properly.
        m_log.debug("Testing multiple names and tags");
        final long user1 = s_nextUserId;
        final long user2 = 274932L;
        
        final String SAME_SHA1 = "urn:sha1:SHA1_EXAMPLE";
        
        final FileResource fr1 = new FileResourceImpl("tsunami_phuket.wmv", 
            "video/mpeg", SAME_SHA1, 24927L, user1, 
            "http://example.com", "tags", "video", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        
        final FileResource fr2 = new FileResourceImpl("totally_different", 
            "video/mpeg", SAME_SHA1, 24927L, user2, 
            "http://example.com", "different", "video", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        final String baseUri1 = "sip://"+fr1.getInstanceId();
        final String baseUri2 = "sip://"+fr2.getInstanceId();
        
        s_repo.insertResource(fr1);
        s_repo.insertResource(fr2);
        s_repo.setInstanceOnline(user1, baseUri1, true, "14.53.24.2");
        s_repo.setInstanceOnline(user2, baseUri2, true, "14.53.24.2");
        final String keywords = fr1.getName();
        
        m_log.debug("Launching search for: "+keywords);
        MetaFileResourceResult result = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user2, 
                false, false, false, false, true);  
        
        final MetaFileResource mfr1 = result.getResults().iterator().next();
        m_log.debug("Total results: "+result.getTotalResults());
        assertEquals(1, result.getTotalResults());
        
        Collection<MetaFileResource> results = result.getResults();
        m_log.debug("Received "+results.size()+" results...");
        assertEquals(1, results.size());
        
        result = s_repo.search("totally_different", 0, 10, 
            SystemUtils.OS_NAME, user2, false, false, false, false, true);
        m_log.debug("Total results: "+result.getTotalResults());
        assertEquals(1, result.getTotalResults());
        final MetaFileResource mfr2 = result.getResults().iterator().next();
        assertEquals(mfr1, mfr2);
        
        // Cleanup.
        s_repo.setInstanceOnline(user1, baseUri1, false, "14.53.24.2");
        s_repo.setInstanceOnline(user2, baseUri2, false, "14.53.24.2");
        s_repo.deleteResource(fr1.getInstanceId(), fr1.getSha1Urn());    
        s_repo.deleteResource(fr2.getInstanceId(), fr2.getSha1Urn()); 
        }
    
    public void testTotalResults() throws Exception
        {
        final long user1 = 472894L;
        final long user2 = 88894L;
        final String sha1String1 = "urn:sha1:EXAMPLE1";
        final String sha1String2 = "urn:sha1:EXAMPLE2";
        final String baseUri1 = "sip://"+user1+"/uri-res/N2R?"+sha1String1;
        s_repo.setInstanceOnline(user1, baseUri1, true, "14.53.24.2");
        final String baseUri2 = "sip://"+user2+"/uri-res/N2R?"+sha1String2;
        s_repo.setInstanceOnline(user2, baseUri2, true, "14.53.24.2");
        final FileResource fr1 = new FileResourceImpl("name", "text/xml", 
            sha1String1, 31232L, user1, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.insertResource(fr1);
        final FileResource fr2 = new FileResourceImpl("name", "text/xml", 
            sha1String2, 31232L, user2, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.insertResource(fr2);
        
        final String keywords = fr1.getName();
        MetaFileResourceResult result = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user2, 
                false, false, true, false, false);
        assertNotNull(result);
        assertEquals(2, result.getTotalResults());
        
        result = s_repo.search(keywords, 0, 1, SystemUtils.OS_NAME, user2, 
            false, false, true, false, false);
        assertNotNull(result);
        assertEquals(2, result.getTotalResults());
        
        result = s_repo.search(keywords, 1, 1, SystemUtils.OS_NAME, user2, 
            false, false, true, false, false);
        assertNotNull(result);
        assertEquals(2, result.getTotalResults());
        
        // Make sure we don't get results when we set the users offline.
        s_repo.setInstanceOnline(fr1.getInstanceId(), baseUri1, false, "14.53.24.2");
        s_repo.setInstanceOnline(fr2.getInstanceId(), baseUri2, false, "14.53.24.2");
        final Collection<MetaFileResource> results4 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user2, false, 
                false, true, false, false).getResults();
        assertEquals(0, results4.size());
        
        s_repo.deleteResource(fr1.getInstanceId(), fr1.getSha1Urn());
        s_repo.deleteResource(fr2.getInstanceId(), fr2.getSha1Urn());
        
        // Make sure the cleanup worked... 
        s_repo.setInstanceOnline(fr1.getInstanceId(), baseUri1, true, "14.53.24.2");
        s_repo.setInstanceOnline(fr2.getInstanceId(), baseUri2, true, "14.53.24.2");
        Collection<MetaFileResource> results = 
            s_repo.search(fr1.getTitle(), 0, 10, SystemUtils.OS_NAME, user2, 
                false, false, true, false, false).getResults(); 
        assertEquals(0, results.size());
        
        s_repo.setInstanceOnline(fr1.getInstanceId(), baseUri1, false, "14.53.24.2");
        s_repo.setInstanceOnline(fr2.getInstanceId(), baseUri2, false, "14.53.24.2");
        }
    
    public void testSortingUserFirst() throws Exception
        {
        
        
        final Collection<OnlineInstance> onlineUsers = s_repo.getOnlineInstances();
        assertEquals(0, onlineUsers.size());
        
        final Collection<UserWithFiles> users = new LinkedList<UserWithFiles>();
        
        final int numUsers = 5;
        for (int i = 0; i < numUsers; i++)
            {
            final UserWithFiles uwf = new UserWithFiles(s_repo, i, true);
            users.add(uwf);
            }
        
        runSortingTest(s_repo, numUsers);
        
        // Cleanup
        cleanup(s_repo, users);
        }
        

    public void testSortingFilesFirst() throws Exception
        {
        
        
        final Collection<OnlineInstance> onlineUsers = s_repo.getOnlineInstances();
        assertEquals(0, onlineUsers.size());
        
        final Collection<UserWithFiles> users = new LinkedList<UserWithFiles>();
        final int numUsers = 5;
        for (int i = 0; i < numUsers; i++)
            {
            // This will not insert the user.
            final UserWithFiles uwf = new UserWithFiles(s_repo, i, false);
            users.add(uwf);
            s_repo.setInstanceOnline(i, "sip://"+i, true, "14.53.24.2");
            }
        
        runSortingTest(s_repo, numUsers);
        
        // Cleanup
        cleanup(s_repo, users);
        }
       
    
    private void runSortingTest(final ResourceRepository repo, 
        final int numUsers) throws Exception
        {
        MetaFileResourceResult metaResult = pagedSearch(repo, 0, 10);
        assertEquals(numUsers, metaResult.getTotalResults());
        Collection<MetaFileResource> results = metaResult.getResults();
        
        assertEquals(numUsers, results.size());
        
        int count = 0;
        for (final MetaFileResource mfr : results)
            {
            m_log.debug("Testing "+mfr.getTitle());
            final String expectedName = "test_name" +count +".mov";
            assertEquals("Got unexpected name.  Expected: "+
                expectedName+" but was: "+mfr.getTitle(), 
                expectedName, mfr.getTitle());
            
            // Files with lower indeces have more users with them.
            assertEquals((numUsers-count), mfr.getNumOnlineInstances());
            count++;
            }
        
        // Now delete a bunch of files and make sure the order changes.
        // Everyone has file 0.  Delete it from a bunch of people and see
        // where it lands.
        FileResource fr = createFileResource(0, 4L);
        repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        fr = createFileResource(0, 3L);
        repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        fr = createFileResource(0, 2L);
        repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        fr = createFileResource(0, 1L);
        repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        
        metaResult = pagedSearch(repo, 0, 10);
        assertEquals(numUsers, metaResult.getTotalResults());
        results = metaResult.getResults();
        
        assertEquals(numUsers, results.size());
        
        count = 1;
        for (final MetaFileResource mfr : results)
            {
            if (count == 4)
                {
                break;
                }
            m_log.debug("Testing "+mfr.getTitle());
            final String expectedName = "test_name" +count +".mov";
            assertEquals("Got unexpected name.  Expected: "+
                expectedName+" but was: "+mfr.getTitle(), 
                expectedName, mfr.getTitle());
            
            // Files with lower indeces have more users with them.
            assertEquals((numUsers-count), mfr.getNumOnlineInstances());
            count++;
            }
        }
    
    private void cleanup(final ResourceRepository repo, 
        final Collection<UserWithFiles> users) throws Exception
        {
        for (final UserWithFiles uwf : users)
            {
            uwf.removeAll();
            }
        
        final Collection<OnlineInstance> onlineUsers = repo.getOnlineInstances();
        assertEquals(0, onlineUsers.size());
        
        // Make sure the cleanup worked...
        final MetaFileResourceResult metaResults = pagedSearch(repo, 0, 10); 
        final Collection<MetaFileResource> results = metaResults.getResults();     
        
        m_log.debug("Looping through "+results.size()+" results...");
        for (final MetaFileResource resource : results)
            {
            m_log.debug("Still a resource with "+resource.getNumOnlineInstances()+" users..");
            //LOG.debug("Still a resource with "+resource.getUsers().size()+" online users..");
            }
        
        assertEquals(0, metaResults.getTotalResults());
        assertEquals(0, results.size());
        }

    // Just puts a user online with a bunch of files, with the number of
    // files corresponding to the user's ID.
    private class UserWithFiles
        {
        
        private final long m_userId;
        private final ResourceRepository m_repo;
        private final long m_numFiles;

        private UserWithFiles(final ResourceRepository repo, 
            final long numFiles, final boolean insertUser) throws IOException
            {
            this.m_repo = repo;
            this.m_userId = numFiles;
            this.m_numFiles = numFiles;
            
            if (insertUser)
                {
                final String sipUri = "sip://"+this.m_userId;
                repo.setInstanceOnline(this.m_userId, sipUri, true, "14.53.24.2");
                m_log.debug("Set user "+this.m_userId+" to online");
                }
            for (int i = 0; i <= numFiles; i++)
                {
                final FileResource fr = createFileResource(i, this.m_userId);
                repo.insertResource(fr);
                m_log.debug("Inserted resource: "+fr.getTitle());
                }
            }

        private void removeAll() 
            {
            this.m_repo.setInstanceOnline(this.m_userId, "sip://"+this.m_userId, false, 
                    "14.53.24.2");
            for (int i = 0; i <= this.m_numFiles; i++)
                {
                final FileResource fr = createFileResource(i, this.m_userId);
                try
                    {
                    this.m_repo.deleteResource(this.m_userId, fr.getSha1Urn());
                    }
                catch (IOException e)
                    {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    }
                m_log.debug("Removed resource: "+fr.getTitle());
                }
            }
        }
    
    private FileResource createFileResource(final int index, final long userId)
        {
        final FileResource fr = new FileResourceImpl("test_name"+index+".mov", 
            "video/mpeg", 
            "urn:sha1:WEIRDSHA1"+index, 7294732L, userId, 
            "http://example.com", "tags", "video", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        
        return fr;
        }
    
    public void testPaging() throws Exception
        {
        for (int i = 0; i < NUM_PAGE_LOOPS; i++)
            {
            m_log.debug("Running test: "+i);
            runPageTest();
            if (i + 1 < NUM_PAGE_LOOPS)
                {
                Thread.sleep(1000 * 60 * 1);
                }
            m_log.debug("Finished test: "+i);
            }
        }

    private void runPageTest() throws Exception
        {
        
        final int numUsers = 31;
        
        final Collection<UserWithFiles> users = new LinkedList<UserWithFiles>();
        for (int i = 0; i < numUsers; i++)
            {
            final UserWithFiles uwf = new UserWithFiles(s_repo, i, true);
            users.add(uwf);
            }
        
        runTestForPage(s_repo, 0, 31, 10);
        runTestForPage(s_repo, 1, 21, 10);
        runTestForPage(s_repo, 2, 11, 10);
        runTestForPage(s_repo, 3, 1, 1);
        
        // Cleanup
        cleanup(s_repo, users);
        }
    
    private void runTestForPage(
        final ResourceRepository repo, final int page, final int onlineUsers, 
        final int expectedResults)
        {
        final int resultsPerPage = 10;
        final MetaFileResourceResult metaResult = 
            pagedSearch(repo, page, resultsPerPage);
        assertEquals(31, metaResult.getTotalResults());
        Collection<MetaFileResource> results = metaResult.getResults();
        assertEquals(expectedResults, results.size());
        final Iterator<MetaFileResource> iter = results.iterator();
        MetaFileResource mfr = iter.next();
        String expectedName = "test_name"+(resultsPerPage*page)+".mov";
        assertEquals("Got unexpected name.  Expected: "+
            expectedName+" but was: "+mfr.getTitle(), 
            expectedName, mfr.getTitle());
        assertEquals(onlineUsers, mfr.getNumOnlineInstances());
        
        if (iter.hasNext())
            {
            mfr = iter.next();
            assertEquals(onlineUsers-1, mfr.getNumOnlineInstances());
            }
        }

    private MetaFileResourceResult pagedSearch(
        final ResourceRepository repo, final int startPage, 
        final int itemsPerPage)
        {
        return repo.search("test_name", startPage, itemsPerPage, 
            SystemUtils.OS_NAME, 73894L, 
            true, true, true, true, true); 
        }

    public void testWeirdNameSearch() throws Exception
        {
        m_log.debug("Testing weird name...");
        
        final long user1 = s_nextUserId;
        final long user3 = 274932L;
        
        final FileResource fr = new FileResourceImpl("tsunami_phuket.wmv", 
            "video/mpeg", "urn:sha1:EXAMPLE2", 24927L, user1, 
            "http://example.com", "tags", "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        final String baseUri = "sip://"+fr.getInstanceId();
        
        s_repo.insertResource(fr);
        s_repo.setInstanceOnline(user1, baseUri, true, "14.53.24.2");
        final String keywords = fr.getName();
        
        m_log.debug("Launching search for: "+keywords);
        MetaFileResourceResult result = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user3, 
                false, false, true, false, false);  
        
        m_log.debug("Total results: "+result.getTotalResults());
        
        Collection<MetaFileResource> results = result.getResults();
        m_log.debug("Received "+results.size()+" results...");
        assertEquals(1, results.size());
        
        // Cleanup.
        s_repo.setInstanceOnline(user1, baseUri, false, "14.53.24.2");
        s_repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());       
        }
    
    public void testTakedownNoticeHandling() throws Exception
        {
        
        final long user1 = s_nextUserId;
        final long user3 = 274932L;
        final FileResource fr = new FileResourceImpl("name", "text/xml", 
            "urn:sha1:EXAMPLE", 24927L, user1, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
    
        final String baseUri = 
            "sip://"+fr.getInstanceId()+"/uri-res/N2R?"+fr.getSha1Urn();
        s_repo.insertResource(fr);
        final String keywords = fr.getName();        
        
        s_repo.setInstanceOnline(user1, baseUri, true, "14.53.24.2");
        Collection<MetaFileResource> results = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user3, 
                false, false, true, false, false).getResults();
        assertEquals(1, results.size());
        
        s_repo.takeDown(fr.getSha1Urn(), true);
        
        results = s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user3, 
            false, false, true, false, false).getResults();
        
        assertEquals(0, results.size());
        
        s_repo.takeDown(fr.getSha1Urn(), false);
        
        results = s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user3, 
            false, false, true, false, false).getResults();
        
        assertEquals(1, results.size());
        
        // Cleanup.
        s_repo.setInstanceOnline(user1, baseUri, false, "14.53.24.2");
        s_repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        }


    public void testFilePublishedBeforeUserOnline() throws Exception
        {
        
        final long instance1 = RandomUtils.nextLong();
        final long instance2 = RandomUtils.nextLong();
        final FileResource fr = new FileResourceImpl("name", "text/xml", 
            "urn:sha1:EXAMPLE"+RandomUtils.nextInt(), 24927L, instance1, 
            "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);

        final String baseUri = 
            "sip://"+fr.getInstanceId()+"/uri-res/N2R?"+fr.getSha1Urn();
        s_repo.insertResource(fr);
        final String keywords = fr.getName();
        
        final Collection<MetaFileResource> results = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, instance2, 
                false, false, true, false, false).getResults();  
        assertEquals(0, results.size());
        
        
        s_repo.setInstanceOnline(instance1, baseUri, true, "14.53.24.2");
        final Collection<MetaFileResource> results2 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, instance2, 
                false, false, true, false, false).getResults();
        assertEquals(1, results2.size());
        
        
        // Cleanup.
        s_repo.setInstanceOnline(instance1, baseUri, false, "14.53.24.2");
        s_repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        }
    
    
    public void testGetFileAndInstances() throws Exception
        {
        
        final long instance1 = s_nextUserId;
        final long instance2 = 943782929L;
        final String sha1String = "urn:sha1:EXAMPLE" + RandomUtils.nextInt();
        final String baseUri1 = "sip://"+instance1+"/uri-res/N2R?"+sha1String;
        final String baseUri2 = "sip://"+instance2+"/uri-res/N2R?"+sha1String;
        s_repo.setInstanceOnline(instance1, baseUri1, true, "14.53.24.2");
        s_repo.setInstanceOnline(instance2, baseUri2, true, "14.53.24.2");
        
        final String uri1String = "http://site.org/test1.mov";
        
        final FileResource fr = new FileResourceImpl(uri1String, "name", "text/xml", 
             sha1String, 24927L, instance1, "http://example.com", "tags",
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        
        final URI uri1 = new URI(uri1String);
        final URI sha1 = new URI(fr.getSha1Urn());
        
        s_repo.insertResource(fr);
        final FileAndInstances fu = s_repo.getFileAndInstances(sha1);
        final Set<OnlineInstance> users = fu.getOnlineInstances();
        assertEquals(1, users.size());
        
        final FileAndInstances frUri = s_repo.getFileAndInstances(uri1);
        final Set<OnlineInstance> usersUri = frUri.getOnlineInstances();
        assertEquals(1, usersUri.size());
        
        s_repo.setInstanceOnline(instance1, baseUri1, false, "14.53.24.2");
        final FileAndInstances fu2 = s_repo.getFileAndInstances(sha1);
        final Set<OnlineInstance> users2 = fu2.getOnlineInstances();
        assertEquals(0, users2.size());
        
        s_repo.setInstanceOnline(instance1, baseUri1, true, "14.53.24.2");
        final FileAndInstances fu3 = s_repo.getFileAndInstances(sha1);
        final Set<OnlineInstance> users3 = fu3.getOnlineInstances();
        assertEquals(1, users3.size());

        final FileResource fr2 = new FileResourceImpl("name", "text/xml", 
            sha1String, 24927L, instance2, "http://example.com", "tags",
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        
        s_repo.insertResource(fr2);
        
        final FileAndInstances fu4 = s_repo.getFileAndInstances(sha1);
        final Set<OnlineInstance> instances4 = fu4.getOnlineInstances();
        assertEquals("Unexpected number of instances", 2, instances4.size());     
        
        s_repo.setInstanceOnline(instance2, baseUri2, false, "14.53.24.2");
        final FileAndInstances fu5 = s_repo.getFileAndInstances(sha1);
        final Set<OnlineInstance> users5 = fu5.getOnlineInstances();
        assertEquals(1, users5.size());
        
        // Cleanup.
        s_repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        s_repo.deleteResource(fr2.getInstanceId(), fr2.getSha1Urn());
        
        s_repo.setInstanceOnline(instance1, baseUri1, false, "14.53.24.2");
        }

    
    public void testSearchDistinctResults() throws Exception
        {
        final long user1 = s_nextUserId;
        final long user2 = 2894392L;
        
        final long user3 = 27947L;
        
        final String sha1String = "urn:sha1:EXAMPLE";
        final String baseUri1 = "sip://"+user1+"/uri-res/N2R?"+sha1String;
        final String baseUri2 = "sip://"+user2+"/uri-res/N2R?"+sha1String;
        
        s_repo.setInstanceOnline(user1, baseUri1, true, "14.53.24.2");
        final FileResource fr = new FileResourceImpl("name", "text/xml", 
            sha1String, 24927L, user1, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.insertResource(fr);
        final String keywords = fr.getName();
        
        final Collection<MetaFileResource> results0 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user3, false, 
                false, true, false, false).getResults();   
        assertEquals(1, results0.size());
        final MetaFileResource mfr0 = results0.iterator().next();
        assertEquals("name", mfr0.getTitle());
        
        // Same resource from a different user.
        final FileResource fr2 = new FileResourceImpl("name", "text/xml", 
            "urn:sha1:EXAMPLE", 24927L, user2, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.insertResource(fr2);
        s_repo.setInstanceOnline(user2, baseUri2, true, "14.53.24.2");
        
        final Collection<MetaFileResource> results1 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user3, false, 
                false, true, false, false).getResults();
        final Iterator<MetaFileResource> iter = results1.iterator();
        final MetaFileResource mfr1 = iter.next();
        assertEquals("name", mfr1.getTitle());        
        
        s_repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        s_repo.deleteResource(fr2.getInstanceId(), fr2.getSha1Urn());
        assertEquals(1, results1.size());
        
        final Collection<MetaFileResource> results2 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user3, false, 
                false, true, false, false).getResults();
        assertEquals(0, results2.size());
        
        final FileResource fr3 = new FileResourceImpl("name", "text/xml", 
            "urn:sha1:EXAMPLE", 9857L, user2, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.insertResource(fr3);
        final Collection<MetaFileResource> results3 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user3, false, 
                false, true, false, false).getResults();
        assertEquals(1, results3.size());
        s_repo.setInstanceOnline(fr3.getInstanceId(), baseUri2, false, "14.53.24.2");
        final Collection<MetaFileResource> results4 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, user3, false, 
                false, true, false, false).getResults();
        assertEquals(0, results4.size());
        
        s_repo.deleteResource(fr3.getInstanceId(), fr3.getSha1Urn());
        
        s_repo.setInstanceOnline(user1, baseUri1, false, "14.53.24.2");
        }
    
    public void testDeleteFileResource() throws Exception
        {
        final long user1 = 729857L;
        final long user2 = 5397L;
        final long user3 = 27947L;
        final long user4 = 47290473L;
        final String sha1String = "urn:sha1:EXAMPLE";
        final String baseUri1 = "sip://"+user1+"/uri-res/N2R?"+sha1String;
        final String baseUri2 = "sip://"+user2+"/uri-res/N2R?"+sha1String;
        final String baseUri3 = "sip://"+user3+"/uri-res/N2R?"+sha1String;
        
        final FileResource fr1 = new FileResourceImpl("name", "text/xml", 
            "urn:sha1:EXAMPLE", 31232L, user1, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.setInstanceOnline(fr1.getInstanceId(), baseUri1, true, "14.53.24.2");
        
        
        // Make sure it's not there before we insert it -- dummy check.
        final Collection<MetaFileResource> resultsTest = 
            s_repo.search(fr1.getTags(), 0, 100, SystemUtils.OS_NAME, user4, 
               false, false, true, false, false).getResults();
        assertTrue(resultsTest.isEmpty());
        
        s_repo.insertResource(fr1);
        final Collection<MetaFileResource> resultsTest2 = 
            s_repo.search(fr1.getTags(), 0, 100, SystemUtils.OS_NAME, user4, 
                false, false, true, false, false).getResults();
        assertEquals(1, resultsTest2.size());
        
        s_repo.deleteResource(fr1.getInstanceId(), fr1.getSha1Urn());
        final Collection<MetaFileResource> resultsTest3 = 
            s_repo.search(fr1.getTags(), 0, 100, SystemUtils.OS_NAME, user4, 
                false, false, true, false, false).getResults();
        assertTrue(resultsTest3.isEmpty());
        
        
        // Now test to make sure deleting one resource does not delete all of
        // them in the case where there are multiple sources.
        final FileResource fr2 = new FileResourceImpl("name", "text/xml", 
            "urn:sha1:EXAMPLE", 31232L, user2, "http://example.com", 
            "tags", "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.insertResource(fr2);
        s_repo.setInstanceOnline(fr2.getInstanceId(), baseUri2, true, "14.53.24.2");
        final Collection<MetaFileResource> resultsTest4 = 
            s_repo.search(fr2.getTags(), 0, 100, SystemUtils.OS_NAME, user4, 
                false, false, true, false, false).getResults();
        assertEquals(1, resultsTest4.size());
        
        final FileResource fr3 = new FileResourceImpl("name", "text/xml", 
            "urn:sha1:EXAMPLE", 31232L, user3, "http://example.com", 
            "tags", "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.insertResource(fr3);
        s_repo.setInstanceOnline(fr3.getInstanceId(), baseUri3, true, "14.53.24.2");
        final Collection<MetaFileResource> resultsTest5 = 
            s_repo.search(fr3.getTags(), 0, 100, SystemUtils.OS_NAME, user4, 
                false, false, true, false, false).getResults();
        assertEquals(1, resultsTest5.size());
        
        s_repo.deleteResource(fr2.getInstanceId(), fr2.getSha1Urn());
        final Collection<MetaFileResource> resultsTest6 = 
            s_repo.search(fr2.getTags(), 0, 100, SystemUtils.OS_NAME, user4, 
                false, false, true, false, false).getResults();
        assertEquals(1, resultsTest6.size());
        
        s_repo.setInstanceOnline(fr1.getInstanceId(), baseUri1, false, "14.53.24.2");
        s_repo.setInstanceOnline(fr2.getInstanceId(), baseUri2, false, "14.53.24.2");
        s_repo.setInstanceOnline(fr3.getInstanceId(), baseUri3, false, "14.53.24.2");
        }
    
    public void testSearch() throws Exception
        {
        final long instance1 = RandomUtils.nextLong();
        final long instance2 = RandomUtils.nextLong();
        final long instance3 = RandomUtils.nextLong();
        final String sha1String = "urn:sha1:"+RandomUtils.nextInt();
        
        final String baseUri1 = "sip://"+instance1+"/uri-res/N2R?"+sha1String;
        s_repo.setInstanceOnline(instance1, baseUri1, true, "14.53.24.2");
        final FileResource fr = new FileResourceImpl("name", "text/xml", 
            sha1String, 31232L, instance1, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        s_repo.insertResource(fr);
        final FileResource fr2 = new FileResourceImpl("name", "text/xml", 
            sha1String, 31232L, instance2, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L);
        
        final String keywords = fr.getName();
        final Collection<MetaFileResource> results = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, instance3, false, 
                false, true, false, false).getResults();
        assertNotNull(results);
        assertEquals(1, results.size());
        
        // This should still return one result because the resource has the 
        // same hash.
        s_repo.insertResource(fr2);
        final Collection<MetaFileResource> files = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, instance3, false, 
                false, true, false, false).getResults();
        assertEquals(1, files.size());
        
        // Now make sure we don't get results when not searching for documents.
        final Collection<MetaFileResource> results22 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, instance3, false, 
                true, false, false, false).getResults();
        assertTrue(results22.isEmpty());

        final MetaFileResource resourceResult = files.iterator().next();
        assertEquals(fr2.getSha1Urn(), resourceResult.getSha1Urn());
        
        final Collection<MetaFileResource> results2 = 
            s_repo.search("doesnotmatch", 0, 10, SystemUtils.OS_NAME, instance3, false, 
                false, true, false, false).getResults();
        assertTrue("Results not empty!!", results2.isEmpty());

        final Collection<MetaFileResource> results3 = 
            s_repo.search(fr.getTags(), 0, 10, SystemUtils.OS_NAME, instance3, false, 
                false, true, false, false).getResults();
        assertFalse(results3.isEmpty());
        
        // Make sure we don't get results when we set the user offline.
        s_repo.setInstanceOnline(fr.getInstanceId(), baseUri1, false, "14.53.24.2");

        final Collection<MetaFileResource> results4 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, instance3, 
                false, false, true, false, false).getResults();
        assertEquals(0, results4.size());
        
        s_repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        s_repo.deleteResource(fr2.getInstanceId(), fr2.getSha1Urn());
        }
    
    public void testGroupSearch() throws Exception
        {
        final String groupName = RandomUtils.nextInt() + "RandomGroup";
        final long instance1 = RandomUtils.nextLong();
        final long instance2 = RandomUtils.nextLong();
        final long instance3 = RandomUtils.nextLong();
        final String sha1String = "urn:sha1:"+RandomUtils.nextInt();;
        final String baseUri1 = "sip://"+instance1+"/uri-res/N2R?"+sha1String;
        s_repo.setInstanceOnline(instance1, baseUri1, true, "14.53.24.2");
        final FileResource fr = new FileResourceImpl("name", "text/xml", 
            sha1String, 31232L, instance1, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L, 
            Permission.GROUP, groupName);
        s_repo.insertResource(fr);
        final FileResource fr2 = new FileResourceImpl("name", "text/xml", 
            sha1String, 31232L, instance2, "http://example.com", "tags", 
            "document", SystemUtils.USER_TIMEZONE, 
            SystemUtils.USER_COUNTRY, SystemUtils.USER_LANGUAGE, false, 4729L, 
            Permission.GROUP, groupName);
        
        // Test searching outside the group.
        final String keywords = fr.getName();
        final Collection<MetaFileResource> results = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, instance3, false, 
                false, true, false, false).getResults();
        assertNotNull(results);
        assertEquals("Got result: "+results, 0, results.size());
        
        // Test searching inside the group.
        Collection<MetaFileResource> files =
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, instance3, groupName, 
                false, false, true, false, false).getResults();
        assertNotNull(files);
        assertEquals(1, files.size());
        
        // Dummy check searching within the group for a name that does not 
        // exist.
        files =
            s_repo.search("NotInDatabase", 0, 10, SystemUtils.OS_NAME, instance3, groupName, 
                false, false, true, false, false).getResults();
        assertNotNull(files);
        assertEquals(0, files.size());
        
        // Test searching for the group name itself.
        files =
            s_repo.search(groupName, 0, 10, SystemUtils.OS_NAME, instance3, groupName, 
                false, false, true, false, false).getResults();
        assertNotNull(files);
        assertEquals(1, files.size());

        s_repo.insertResource(fr2);
        // Now make sure we don't get results when not searching for documents.
        final Collection<MetaFileResource> results22 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, instance3, false, 
                true, false, false, false).getResults();
        assertTrue(results22.isEmpty());
        
        final MetaFileResource resourceResult = files.iterator().next();
        assertEquals(fr2.getSha1Urn(), resourceResult.getSha1Urn());
        
        final Collection<MetaFileResource> results2 = 
            s_repo.search("doesnotmatch", 0, 10, SystemUtils.OS_NAME, instance3, false, 
                false, true, false, false).getResults();
        assertTrue(results2.isEmpty());
        
        final Collection<MetaFileResource> results3 = 
            s_repo.search(fr.getTags(), 0, 10, SystemUtils.OS_NAME, instance3, false, 
                false, true, false, false).getResults();
        assertTrue("World search should have failed", results3.isEmpty());
        
        // Make sure we don't get results when we set the user offline.
        s_repo.setInstanceOnline(fr.getInstanceId(), baseUri1, false, "14.53.24.2");
        
        s_repo.setInstanceOnline(fr2.getInstanceId(), baseUri1, false, "14.53.24.2");
        final Collection<MetaFileResource> results4 = 
            s_repo.search(keywords, 0, 10, SystemUtils.OS_NAME, instance3, 
                false, false, true, false, false).getResults();
        assertEquals(0, results4.size());
        
        s_repo.deleteResource(fr.getInstanceId(), fr.getSha1Urn());
        s_repo.deleteResource(fr2.getInstanceId(), fr2.getSha1Urn());
        }

    @Test public void dummyTest() throws Exception
        {
        assertFalse(false);
        }
    }
