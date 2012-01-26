package org.lastbamboo.common.bug.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.lastbamboo.common.bug.server.services.TopBugsService;
import org.littleshoot.util.DateUtils;
import org.littleshoot.util.Pair;
import org.littleshoot.util.PairImpl;

/**
 * Test the bug service class. 
 */
public class TopBugsServiceTest
    {

    @Test public void testService() throws Exception
        {
        final BugRepository repo = new TestRepo();
        final TopBugsService service = new TopBugsService(repo);
        
        final String json = service.getJson();
        
        final JSONObject obj = new JSONObject(json);
        
        final JSONArray array = obj.getJSONArray("bugs");
        assertEquals(2, array.length());
        }
    
    private static final class TestRepo implements BugRepository
        {

        public void clearBugs()
            {
            // TODO Auto-generated method stub
            
            }

        public Collection<Bug> getBugs()
            {
            final Bug bug = newBug("testClass", 8420);
            final Collection<Bug> bugs = new LinkedList<Bug>();
            bugs.add(bug);
            return bugs;
            }

        public Collection<Pair<Long, Bug>> getOrderedGroupedBugs()
            {
            final Bug bug1 = newBug("OrderedBugClass1", 40200);
            final Pair<Long, Bug> bugPair1 = new PairImpl<Long, Bug>(4729L, bug1);
            final Bug bug2 = newBug("OrderedBugClass2", 321);
            final Pair<Long, Bug> bugPair2 = new PairImpl<Long, Bug>(47L, bug2);
            final Collection<Pair<Long, Bug>> pairs = 
                new LinkedList<Pair<Long,Bug>>();
            pairs.add(bugPair1);
            pairs.add(bugPair2);
            return pairs;
            }

        public void insertBug(Bug bug) throws IOException
            {
            // TODO Auto-generated method stub
            
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
            bugsMap.put("startTime", df.format(new Date()));
            bugsMap.put("timeStamp", df.format(new Date()));
            //bugsMap.put("startTime", DateUtils.iso8601());
            //bugsMap.put("timeStamp", DateUtils.iso8601());
    
            final Bug bug = new BugImpl(bugsMap);
            return bug;
            }
        }
    }
