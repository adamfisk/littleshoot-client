package org.lastbamboo.common.sipturn;

import java.net.InetAddress;
import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.amazon.ec2.AmazonEc2Utils;
import org.lastbamboo.common.online.OnlineStatusUpdater;
import org.lastbamboo.common.sip.proxy.RegistrationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates online status based on registration events.
 */
public class OnlineStatusRegistrationListener implements RegistrationListener
    {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    private final OnlineStatusUpdater m_updater;

    /**
     * Creates a new listener.
     * 
     * @param updater The class that actually performs the update.
     */
    public OnlineStatusRegistrationListener(final OnlineStatusUpdater updater)
        {
        m_updater = updater;
        
        }
    
    public void onRegistered(final URI uri)
        {
        updateStatus(uri, true);
        }
    
    public void onUnregistered(final URI uri)
        {
        updateStatus(uri, false);
        }

    private void updateStatus(final URI uri, final boolean online)
        {
        final String sipUri = uri.toASCIIString();
        final String userId = 
            StringUtils.substringBetween(sipUri, "sip:", "@lastbamboo.org");
        final String baseUri = "sip://" + userId;
        final InetAddress ia = AmazonEc2Utils.getPublicAddress();
        this.m_updater.updateStatus(userId, baseUri, online, ia);
        }

    public void onOffline(final InetAddress serverAddress)
        {
        LOG.debug("Calling server offline....");
        this.m_updater.allOffline(serverAddress);
        }
    }
