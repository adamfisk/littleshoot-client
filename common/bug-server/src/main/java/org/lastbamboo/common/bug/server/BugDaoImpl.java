package org.lastbamboo.common.bug.server;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.littleshoot.util.Pair;
import org.littleshoot.util.PairImpl;

/**
 * DAO class for manipulating bugs in the database.
 */
public class BugDaoImpl implements BugDao
    {
    
    private final SessionFactory m_sessionFactory;

    /**
     * Creates a new DAO instance.
     * 
     * @param sessionFactory The Hibernate session factory.
     */
    public BugDaoImpl(final SessionFactory sessionFactory)
        {
        this.m_sessionFactory = sessionFactory;
        }

    public void insertBug(final Bug bug)
        {
        this.m_sessionFactory.getCurrentSession().persist(bug);
        }

    public Collection<Bug> getBugs()
        {
        final Criteria criteria = 
            this.m_sessionFactory.getCurrentSession().createCriteria(
                BugImpl.class);

        // Put the results in alphabetical order.
        return criteria.list();
        }
    
    public Collection<Pair<Long, Bug>> getOrderedGroupedBugs()
        {
        final String hql = 
            "select " +
            "count(bug.m_className), "+
            "bug.m_className, " +
            "bug.m_lineNumber, " +
            "bug.m_threadName, " +
            "bug.m_osName, " +
            "bug.m_javaVersion, " +
            "bug.m_timeStamp, " +
            "bug.m_methodName, " +
            "bug.m_osVersion " +
            "from BugImpl bug " +
            "group by " +
            "bug.m_className, " +
            "bug.m_lineNumber, " +
            "bug.m_threadName, " +
            "bug.m_osName, " +
            "bug.m_javaVersion, " +
            "bug.m_timeStamp, " +
            "bug.m_methodName, " +
            "bug.m_osVersion " +
            "order by count(bug.m_className) desc";
          
        /*
        final String hql = 
            "select " +
            "bug.m_className, " +
            "bug.m_lineNumber, " +
            "bug.m_threadName, " +
            "bug.m_osName, " +
            "bug.m_javaVersion, " +
            "bug.m_timeStamp, " +
            "bug.m_methodName, " +
            "bug.m_osVersion " +
            "from BugImpl bug " +
            "order by count(bug.m_className) desc ";
            */
            
        final Query query = 
            this.m_sessionFactory.getCurrentSession().createQuery(hql);
        final Collection<Object[]> rawBugs = query.list();
        final Collection<Pair<Long, Bug>> bugs = 
            new LinkedList<Pair<Long, Bug>>();
        final Map<String, String> data = new HashMap<String, String>();
        for (final Object[] raw : rawBugs)
            {
            final Long count = (Long) (raw[0]);
            
            final String timeStamp = formatTimeStamp((Timestamp) raw[6]);
            data.put("className", String.valueOf(raw[1]));
            data.put("lineNumber", String.valueOf(raw[2]));
            data.put("threadName", String.valueOf(raw[3]));
            data.put("osName", String.valueOf(raw[4]));
            data.put("javaVersion", String.valueOf(raw[5]));
            data.put("timeStamp", timeStamp);
            data.put("methodName", String.valueOf(raw[7]));
            data.put("osVersion", String.valueOf(raw[8]));
            final Bug bug = new BugImpl(data);
            bugs.add(new PairImpl<Long, Bug>(count, bug));
            }
        return bugs;
        }

    private String formatTimeStamp(final Timestamp ts)
        {
        final DateFormat df = 
                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
                return df.format(new Date(ts.getTime()));

        //return DateUtils.iso8601(new Date(ts.getTime()));
        }

    public void clearBugs()
        {
        final String hql = "delete from BugImpl";
        final Query query = 
            this.m_sessionFactory.getCurrentSession().createQuery(hql);
        
        query.executeUpdate();
        }

    }
