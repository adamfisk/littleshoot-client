package org.lastbamboo.common.bug.server.processors;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.bug.server.Bug;
import org.lastbamboo.common.bug.server.BugImpl;
import org.lastbamboo.common.bug.server.BugRepository;

/**
 * Bug processor that inserts bugs into the database.
 */
public class DatabaseBugProcessor implements BugProcessor
    {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final BugRepository m_bugRepository;
    
    /**
     * Creates a new bug processor that inserts bugs in the database.
     * 
     * @param repository The repository, providing access to the database.
     */
    public DatabaseBugProcessor(final BugRepository repository)
        {
        this.m_bugRepository = repository;
        }
    
    public void processBug(final InputStream is, 
        final HttpServletRequest request)
        {
        
        try
            {
            final String bugString = IOUtils.toString(is);
            final Map<String, String> bugData = createBugMap(bugString);
            addRequestData(bugData, request);
            final Bug bug = new BugImpl(bugData);
            this.m_bugRepository.insertBug(bug);
            }
        catch (final IOException e)
            {
            LOG.warn("Could not read string", e);
            }
        }

    private void addRequestData(final Map<String, String> bugData, 
        final HttpServletRequest request)
        {
        bugData.put("remoteAddress", request.getRemoteAddr());
        }

    private Map<String, String> createBugMap(final String bugString)
        {
        final Map<String, String> map = new HashMap<String, String>();
        final Scanner scanner = new Scanner(bugString);
        
        scanner.useDelimiter("&");
        
        while(scanner.hasNext())
            {
            final String nameValue = scanner.next();
            final String name = StringUtils.substringBefore(nameValue, "=");
            final String value = StringUtils.substringAfter(nameValue, "=");
            try
                {
                map.put(name, URLDecoder.decode(value, "UTF-8"));
                }
            catch (final UnsupportedEncodingException e)
                {
                map.put(name, value);
                LOG.error("Encoding not supported??", e);
                }
            }
        return map;
        }

    }
