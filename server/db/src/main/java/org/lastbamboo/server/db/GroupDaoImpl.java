package org.lastbamboo.server.db;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.lastbamboo.common.amazon.sdb.AmazonSdb;
import org.lastbamboo.common.amazon.sdb.AmazonSdbUtils;
import org.lastbamboo.server.resource.GroupExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO implementation for groups.
 */
public class GroupDaoImpl implements GroupDao
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final SecureRandom m_idCreator;
    private final AmazonSdb m_simpleDb;

    /**
     * Creates a new group DAO.
     * 
     * @param simpleDb The Amazon Simple DB database.
     */
    public GroupDaoImpl(final AmazonSdb simpleDb)
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

    public String newGroup(final long userId, final String groupName, 
        final String description, final String permission) 
        throws GroupExistsException, IOException
        {
        final String queryExpression = 
            AmazonSdbUtils.createQuery(GroupAttributes.NAME, groupName);
        final Collection<String> items = 
            this.m_simpleDb.query(queryExpression);
        if (!items.isEmpty())
            {
            m_log.debug("Group already exists");
            throw new GroupExistsException("Group exists");
            }
        
        final Map<String, Collection<String>> map =
            new HashMap<String, Collection<String>>();
        
        final long rawId = this.m_idCreator.nextLong();
        final String id = String.valueOf(rawId); 
        
        AmazonSdbUtils.addAttribute(map, GroupAttributes.ID, id);
        AmazonSdbUtils.addAttribute(map, GroupAttributes.NAME, groupName);
        AmazonSdbUtils.addAttribute(map, GroupAttributes.DESCRIPTION, description);
        AmazonSdbUtils.addAttribute(map, GroupAttributes.DEFAULT_PERMISSION, permission);
        AmazonSdbUtils.addAttribute(map, GroupAttributes.ADMIN_USER, userId);
        AmazonSdbUtils.addAttribute(map, GroupAttributes.CREATION_TIME, new Date());
        
        final String itemName = createItemName(id);
        this.m_simpleDb.putAttributes(itemName, map);
        return itemName;
        }

    private String createItemName(final String id)
        {
        return GroupAttributes.ID+"."+id;
        }
    }
