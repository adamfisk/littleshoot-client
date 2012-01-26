package org.lastbamboo.common.bug.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.littleshoot.util.Pair;

/**
 * Tests the bug repository.
 */
public class BugRepositoryImplTest
    {

	private final Logger LOG = LoggerFactory.getLogger(BugRepositoryImplTest.class);
	
    private static SessionFactory s_sessionFactory;

    private static BugRepository s_repo;
    
    private static final boolean TEST_ACTIVE = false;
    
    /**
     * Allows you to use MySQL for the test if you have it running on your 
     * local system.  This should run it: sudo mysqld_safe &
     */
    private static final boolean USE_MYSQL = false;
    
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
            addClass(BugImpl.class);
        s_sessionFactory = CONFIG.buildSessionFactory();
        }

    @BeforeClass public static void setUp() throws Exception
        {
        if (!TEST_ACTIVE) return;
        s_repo = createRepository();
        checkClean();
        }
    
    @After public void tearDown() throws Exception
        {
        if (!TEST_ACTIVE) return;
        s_repo.clearBugs();
        checkClean();
        }
    
    private static void checkClean() throws IOException
        {
        final Collection<Bug> bugs = s_repo.getBugs();
        
        if (!bugs.isEmpty())
            {
            final String msg = bugs.size() + " bug(s)." + 
                "\n First bug: "+bugs.iterator().next().getClassName();
            assertEquals(msg, 0, bugs.size());
            }
        }

    private static BugRepository createRepository()
        {
        final BugDao bugDao = new BugDaoImpl(s_sessionFactory);
        final BugRepository repository =
            new BugRepositoryImpl(bugDao, s_sessionFactory);
        return repository;
        }
    
    public void testGetBugs() throws Exception
        {
        Collection<Bug> bugs = s_repo.getBugs();
        assertEquals(0, bugs.size());
        final String className1 = "org.lastbamboo.test.Test";
        final String className2 = "org.lastbamboo.test.Test2";
        final String className3 = "org.lastbamboo.test.Test3";
        insertBug(className1, 77);
        insertBug(className1, 77);
        insertBug(className1, 77);
        insertBug(className1, 77);
        insertBug(className1, 77);
        insertBug(className1, 47829);
        insertBug(className1, 47829);
        insertBug(className2, 77);
        insertBug(className2, 77);
        insertBug(className2, 77);
        insertBug(className3, 77);
        
        bugs = s_repo.getBugs();
        assertEquals(11, bugs.size());
        
        final Collection<Pair<Long, Bug>> orderedBugs = 
            s_repo.getOrderedGroupedBugs();
        assertEquals("Unexpected bugs: "+orderedBugs, 4, orderedBugs.size());
        
        final Iterator<Pair<Long, Bug>> iter = orderedBugs.iterator();
        
        Pair<Long, Bug> bug = iter.next();
        assertEquals(5L, bug.getFirst());
        assertEquals(className1, bug.getSecond().getClassName());
        assertEquals(77, bug.getSecond().getLineNumber());
        
        bug = iter.next();
        assertEquals(3L, bug.getFirst());
        assertEquals(className2, bug.getSecond().getClassName());
        assertEquals(77, bug.getSecond().getLineNumber());
        
        bug = iter.next();
        assertEquals(2L, bug.getFirst());
        assertEquals(className1, bug.getSecond().getClassName());
        assertEquals(47829, bug.getSecond().getLineNumber());
        
        bug = iter.next();
        assertEquals(1L, bug.getFirst());
        assertEquals(className3, bug.getSecond().getClassName());
        assertEquals(77, bug.getSecond().getLineNumber());
        }

    /**
     * Tests bug insertion.
     * 
     * @throws Exception If there's any unexpected error.
     */
    public void testInsertBug() throws Exception
        {
        insertBug("org.lastbamboo.test.TestInsertBug", 77);
        final Collection<Bug> bugs = s_repo.getBugs();
        assertFalse(bugs.isEmpty());
        }
    
    
    private void insertBug(final String className, final int lineNumber) 
        throws Exception
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
        bugsMap.put("startTime", df.format(new Date()));
        bugsMap.put("timeStamp", df.format(new Date()));

        final Bug bug = new BugImpl(bugsMap);
        s_repo.insertBug(bug);
        }
    
    
    @Test public void dummyTest() throws Exception
        {
        assertFalse(false);
        }
    }
