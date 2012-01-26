package org.lastbamboo.common.bug.server.services;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.bug.server.Bug;
import org.lastbamboo.common.bug.server.BugRepository;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.services.JsonProvider;
import org.littleshoot.util.BeanUtils;
import org.littleshoot.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing JSON for the top bugs on the server.
 */
public class TopBugsService implements JsonProvider
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final BugRepository m_bugRepository;

    /**
     * Creates a new top bug service class.
     * 
     * @param bugRepository The class for accessing the database.
     */
    public TopBugsService(final BugRepository bugRepository)
        {
        m_bugRepository = bugRepository;
        }

    public String getJson()
        {
        final Collection<Pair<Long, Bug>> bugs;
        final JSONObject json = new JSONObject();
        try
            {
            bugs = this.m_bugRepository.getOrderedGroupedBugs();
            }
        catch (final IOException e1)
            {
            JsonUtils.put(json, "totalBugs", 0);
            return json.toString();
            }

        JsonUtils.put(json, "totalBugs", bugs.size());
        for (final Pair<Long, Bug> bug : bugs)
            {
            encode(json, bug);
            }
        return json.toString();
        }

    private void encode(final JSONObject json, final Pair<Long, Bug> pair)
        {
        final Bug bug = pair.getSecond();
        final Map<String, String> bugProps = BeanUtils.mapBean(bug);
        
        bugProps.put("number", String.valueOf(pair.getFirst()));
        try
            {
            json.accumulate("bugs", bugProps);
            }
        catch (final JSONException e)
            {
            m_log.warn("Could not encode json from: "+bugProps, e);
            }
        }
    }
