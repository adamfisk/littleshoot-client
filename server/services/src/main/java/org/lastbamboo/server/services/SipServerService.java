package org.lastbamboo.server.services;

import java.net.InetAddress;
import java.util.Collection;

import org.json.JSONObject;
import org.lastbamboo.common.services.JsonProvider;
import org.lastbamboo.common.sip.proxy.SipConstants;
import org.lastbamboo.common.util.CandidateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that monitors server status. 
 */
public class SipServerService implements JsonProvider
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final CandidateProvider<InetAddress> m_provider;

    /**
     * Creates a TURN server accessing service.
     * 
     * @param provider The class that provides TURN server data.
     */
    public SipServerService(final CandidateProvider<InetAddress> provider)
        {
        this.m_provider = provider;
        }

    public String getJson()
        {
        final Collection<InetAddress> servers = this.m_provider.getCandidates();
        m_log.debug("Transforming data to json: {}", servers);
        final JSONObject json = 
            MoniterServiceUtils.toJson(servers, SipConstants.SIP_PORT);
        return json.toString();
        }
    }
