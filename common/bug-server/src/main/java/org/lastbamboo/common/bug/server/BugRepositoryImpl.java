package org.lastbamboo.common.bug.server;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.context.ThreadLocalSessionContext;
import org.littleshoot.util.Pair;

/**
 * Class that wraps new bug entries in transactions for the database.
 */
public class BugRepositoryImpl implements BugRepository
    {

    static final Logger LOG = LoggerFactory.getLogger(BugRepositoryImpl.class);
    
    private final SessionFactory m_sessionFactory;
    private final BugDao m_bugDao;

    /**
     * Creates a new bugs repository.
     * 
     * @param bugDao The bug DAO.
     * @param sessionFactory The Hibernate session factory.
     */
    public BugRepositoryImpl(final BugDao bugDao, 
        final SessionFactory sessionFactory)
        {
        m_bugDao = bugDao;
        m_sessionFactory = sessionFactory;
        }

    public void insertBug(final Bug bug) 
        throws IOException
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            this.m_bugDao.insertBug(bug);
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            }
        catch (final RuntimeException e)
            {
            LOG.debug("Exception accessing database", e);
            try 
                {
                this.m_sessionFactory.getCurrentSession().getTransaction().rollback();
                }
            catch (final RuntimeException re)
                {
                LOG.error ("Could now rollback transaction!", re);
                }
            throw e;
            }
        }

    public Collection<Bug> getBugs() throws IOException
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final Collection<Bug> bugs = this.m_bugDao.getBugs();
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return bugs;
            }
        catch (final RuntimeException e)
            {
            LOG.debug("Exception accessing database", e);
            try 
                {
                this.m_sessionFactory.getCurrentSession().getTransaction().rollback();
                }
            catch (final RuntimeException re)
                {
                LOG.error ("Could now rollback transaction!", re);
                }
            throw e;
            }
        catch (final IOException e)
            {
            LOG.error("Could not access data!!");
            throw e;
            }
        }
    
    public Collection<Pair<Long, Bug>> getOrderedGroupedBugs() 
        throws IOException
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            final Collection<Pair<Long, Bug>> bugs = 
                this.m_bugDao.getOrderedGroupedBugs();
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            return bugs;
            }
        catch (final RuntimeException e)
            {
            LOG.debug("Exception accessing database", e);
            try 
                {
                this.m_sessionFactory.getCurrentSession().getTransaction().rollback();
                }
            catch (final RuntimeException re)
                {
                LOG.error ("Could now rollback transaction!", re);
                }
            throw e;
            }
        catch (final IOException e)
            {
            LOG.warn("Could not access database", e);
            throw e;
            }
        }

    public void clearBugs()
        {
        try
            {
            this.m_sessionFactory.getCurrentSession().beginTransaction();
            this.m_bugDao.clearBugs();
            this.m_sessionFactory.getCurrentSession().getTransaction().commit();
            ThreadLocalSessionContext.unbind(this.m_sessionFactory);
            }
        catch (final RuntimeException e)
            {
            LOG.debug("Exception accessing database", e);
            try 
                {
                this.m_sessionFactory.getCurrentSession().getTransaction().rollback();
                }
            catch (final RuntimeException re)
                {
                LOG.error ("Could now rollback transaction!", re);
                }
            throw e;
            }
        }
    }
