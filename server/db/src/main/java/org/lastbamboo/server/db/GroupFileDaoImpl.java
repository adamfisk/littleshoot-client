package org.lastbamboo.server.db;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.lastbamboo.common.amazon.sdb.AmazonSdb;
import org.lastbamboo.common.amazon.sdb.AmazonSdbUtils;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.UnexpectedValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a DAO for group files.
 */
public class GroupFileDaoImpl implements GroupFileDao
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final SecureRandom m_idCreator;
    private final AmazonSdb m_simpleDb;

    /**
     * Creates a new group file DAO.
     * 
     * @param simpleDb The Amazon Simple DB database.
     */
    public GroupFileDaoImpl(final AmazonSdb simpleDb)
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

    public void insertFile(final FileResource fr) 
        throws UnexpectedValueException, IOException
        {
        final String itemName = itemName(fr);
        final Map<String, String> attributes = 
            this.m_simpleDb.getAttributes(itemName);
        if (!attributes.isEmpty())
            {
            m_log.debug("File with SHA-1 already exists");
            assertEqual(attributes, FileAttributes.SIZE, fr.getSize());
            }
        
        final Map<String, Collection<String>> map = 
            new HashMap<String, Collection<String>>();
        
        AmazonSdbUtils.addAttribute(map, FileAttributes.URI, fr.getUri());
        map.put(FileAttributes.TAGS, AmazonSdbUtils.parseTags(fr.getTags()));
        AmazonSdbUtils.addAttribute(map, FileAttributes.SIZE, fr.getSize());
        AmazonSdbUtils.addAttribute(map, FileAttributes.SHA1, fr.getSha1Urn());
        AmazonSdbUtils.addAttribute(map, FileAttributes.INSTANCE_ID, fr.getInstanceId());
        AmazonSdbUtils.addAttribute(map, FileAttributes.NAME, fr.getName());
        AmazonSdbUtils.addAttribute(map, FileAttributes.MIME_TYPE, fr.getMimeType());
        AmazonSdbUtils.addAttribute(map, FileAttributes.CREATOR, fr.getCreator());
        AmazonSdbUtils.addAttribute(map, FileAttributes.REMOTE_HOST, fr.getRemoteHost());
        AmazonSdbUtils.addAttribute(map, FileAttributes.MEDIA_TYPE, fr.getMediaType());
        AmazonSdbUtils.addAttribute(map, FileAttributes.LANGUAGE, fr.getLanguage());
        AmazonSdbUtils.addAttribute(map, FileAttributes.COUNTRY, fr.getCountry());
        AmazonSdbUtils.addAttribute(map, FileAttributes.TIME_ZONE, fr.getTimeZone());
        AmazonSdbUtils.addAttribute(map, FileAttributes.USER_ID, fr.getUserId());
        AmazonSdbUtils.addAttribute(map, FileAttributes.PERMISSION, fr.getPermission());
        AmazonSdbUtils.addAttribute(map, FileAttributes.DOWNLOADED, fr.getDownloaded());
        AmazonSdbUtils.addAttribute(map, FileAttributes.PUBLISH_TIME, fr.getPublishTime());
        AmazonSdbUtils.addAttribute(map, FileAttributes.GROUPS, fr.getGroupName());
        
        this.m_simpleDb.putAttributes(itemName, map);
        }

    private String itemName(final FileResource fr)
        {
        return FileAttributes.ITEM_NAME+"."+fr.getSha1Urn();
        }

    private void assertEqual(final Map<String, String> attributes, 
        final String key, final long expectedValue) 
        throws UnexpectedValueException
        {
        final String str = attributes.get(key);
        final boolean match = str.equals(String.valueOf(expectedValue));
        if (!match)
            {
            m_log.warn("Attributes don't match for "+key);
            m_log.warn("Expected "+expectedValue+" but was "+str);
            throw new UnexpectedValueException(
                "Expected "+expectedValue+" but was "+str);
            }
        }
    }
