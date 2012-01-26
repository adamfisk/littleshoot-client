package org.lastbamboo.common.bug.server;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.lastbamboo.common.amazon.sdb.AmazonSdb;
import org.lastbamboo.common.amazon.sdb.AmazonSdbDecoder;
import org.lastbamboo.common.amazon.sdb.AmazonSdbDecoderImpl;
import org.lastbamboo.common.amazon.sdb.AmazonSdbEncoder;
import org.lastbamboo.common.amazon.sdb.AmazonSdbEncoderImpl;
import org.lastbamboo.common.amazon.sdb.AmazonSdbImpl;
import org.lastbamboo.common.amazon.sdb.AmazonSdbUtils;
import org.littleshoot.util.Pair;
import org.littleshoot.util.PairImpl;
import org.littleshoot.util.RuntimeIoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO instance for persisting bugs to Amazon Simple DB.
 */
public class AmazonSdbBugDao implements BugDao
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final SecureRandom m_idCreator;
    private final AmazonSdb m_simpleDb;

    /**
     * Creates a new SDB DAO.
     */
    public AmazonSdbBugDao()
        {
        this (createSdb());
        }
    
    private static AmazonSdb createSdb()
        {
        // We use a different domain for bugs.
        final String domainName = "bugs";
        try
            {
            final AmazonSdb sdb = new AmazonSdbImpl(domainName);
            return sdb;
            }
        catch (final IOException e)
            {
            throw new RuntimeIoException("Could not create bugs DAO");
            }
        }

    /**
     * Creates a new SDB DAO.
     * 
     * @param simpleDb The Amazon Simple DB database.
     */
    public AmazonSdbBugDao(final AmazonSdb simpleDb)
        {
        m_log.debug("Creating new Amazon Simple DB DAO");
        this.m_simpleDb = simpleDb;
        this.m_idCreator = new SecureRandom();
        
        // The first request can take awhile, so do it now.
        m_idCreator.nextLong();
        try
            {
            this.m_simpleDb.createDomain();
            }
        catch (final IOException e)
            {
            throw new IllegalArgumentException("Could not create domain!!");
            }
        }
    
    public void clearBugs()
        {
        // TODO Auto-generated method stub

        }

    public Collection<Bug> getBugs() throws IOException
        {
        final String queryExpression = AmazonSdbUtils.queryFor(BugImpl.class);
        final Collection<String> items = this.m_simpleDb.query(queryExpression);
        final Collection<Bug> bugs = new LinkedList<Bug>();
        final AmazonSdbDecoder<BugImpl> decoder = 
            new AmazonSdbDecoderImpl<BugImpl>();
        for (final String itemName : items)
            {
            final Map<String, Collection<String>> attributes = 
                this.m_simpleDb.getMultiValueAttributes(itemName);
            m_log.debug("Got bug attributes: {}", attributes);
            
            final BugImpl bug = decoder.decode(BugImpl.class, attributes);
            bugs.add(bug);
            }
        return bugs;
        }

    public Collection<Pair<Long, Bug>> getOrderedGroupedBugs() 
        throws IOException
        {
        final String queryExpression = "";
        
        final Collection<Pair<Long, Bug>> bugs =
            new LinkedList<Pair<Long,Bug>>();
        final Collection<String> items = this.m_simpleDb.query(queryExpression);
        final AmazonSdbDecoder<BugImpl> decoder = 
            new AmazonSdbDecoderImpl<BugImpl>();
        for (final String itemName : items)
            {
            final Map<String, Collection<String>> attributes = 
                this.m_simpleDb.getMultiValueAttributes(itemName);
            
            final Bug bug = decoder.decode(BugImpl.class, attributes);
            final Pair<Long, Bug> pair = null;//new PairImpl<Long, Bug>()
            bugs.add(pair);
            }
        return bugs;
        }

    public void insertBug(final Bug bug)
        {
        final Collection<Pair<String, String>> pairs =
            new LinkedList<Pair<String,String>>();
        
        pairs.add(new PairImpl<String, String>("version", bug.getVersion()));
        pairs.add(new PairImpl<String, String>("className", bug.getClassName()));
        pairs.add(new PairImpl<String, String>("threadName", bug.getThreadName()));
        pairs.add(new PairImpl<String, String>("lineNumber", 
            String.valueOf(bug.getLineNumber())));
        pairs.add(new PairImpl<String, String>("osName", bug.getOsName()));
        final String queryExpression = AmazonSdbUtils.createQuery(bug, pairs);
        final Collection<String> items;
        try
            {
            items = this.m_simpleDb.query(queryExpression);
            }
        catch (final IOException e)
            {
            m_log.warn("Could not access Simple DB", e);
            return;
            }
        
        final AmazonSdbEncoder encoder = new AmazonSdbEncoderImpl();
        final long id;
        final String itemName;
        if (!items.isEmpty())
            {
            // There's already an entry for the bug.  Add a new one.
            itemName = items.iterator().next();
            id = Long.parseLong(itemName);
            }
        else
            {
            id = this.m_idCreator.nextLong();
            itemName = String.valueOf(id);
            }
        
        // TODO: This will create multiple values for a single ID for all 
        // fields.  This doesn't work with our current structure -- must 
        // change before production release.
        bug.setId(id);
        try
            {
            final Map<String, Collection<String>> encoded = encoder.encode(bug);
            this.m_simpleDb.putAttributes(itemName, encoded);
            }
        catch (final IOException e)
            {
            // Not a huge deal if we can't persist a single bug.
            m_log.warn("Could not persist bug: {}", bug);
            return;
            }
        }
    }
