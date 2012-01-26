package org.lastbamboo.common.sipturn;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.BasicConfigurator;
import org.lastbamboo.common.amazon.ec2.AmazonEc2Utils;
import org.lastbamboo.common.log4j.AppenderCallback;
import org.lastbamboo.common.log4j.BugReportingAppender;
import org.lastbamboo.common.online.RobustOnlineStatusUpdater;
import org.lastbamboo.common.sip.proxy.LastBambooLocationService;
import org.lastbamboo.common.sip.proxy.LocationService;
import org.lastbamboo.common.sip.proxy.LocationServiceChain;
import org.lastbamboo.common.sip.proxy.SipProxy;
import org.lastbamboo.common.sip.proxy.SipProxyImpl;
import org.lastbamboo.common.sip.proxy.SipRegistrar;
import org.lastbamboo.common.sip.proxy.SipRegistrarImpl;
import org.lastbamboo.common.sip.proxy.SipRequestAndResponseForwarder;
import org.lastbamboo.common.sip.proxy.SipRequestForwarder;
import org.lastbamboo.common.sip.proxy.stateless.ExternalDomainForwarder;
import org.lastbamboo.common.sip.proxy.stateless.StatelessSipProxy;
import org.lastbamboo.common.sip.proxy.stateless.UnregisteredUriForwarder;
import org.lastbamboo.common.sip.stack.message.SipMessageFactory;
import org.lastbamboo.common.sip.stack.message.SipMessageFactoryImpl;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactoryImpl;
import org.lastbamboo.common.sip.stack.transaction.client.SipTransactionFactory;
import org.lastbamboo.common.sip.stack.transaction.client.SipTransactionFactoryImpl;
import org.lastbamboo.common.sip.stack.transaction.client.SipTransactionTracker;
import org.lastbamboo.common.sip.stack.transaction.client.SipTransactionTrackerImpl;
import org.lastbamboo.common.sip.stack.transport.SipTcpTransportLayer;
import org.lastbamboo.common.sip.stack.transport.SipTcpTransportLayerImpl;
import org.lastbamboo.common.sip.stack.util.UriUtils;
import org.lastbamboo.common.sip.stack.util.UriUtilsImpl;
import org.lastbamboo.common.turn.server.TcpTurnServer;
import org.lastbamboo.common.turn.server.TurnClientManagerImpl;
import org.lastbamboo.common.turn.server.TurnServer;
import org.littleshoot.util.ShootConstants;

/**
 * Launcher class for launching a SIP and a TURN server.
 */
public class Launcher
    {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);
    
    /**
     * Launches the SIP and TURN servers.
     * 
     * @param args The command line arguments.
     */
    public static void main(final String[] args)
        {
        LOG.debug("Launching SIP and TURN servers...");
        final Launcher launcher = new Launcher();
        LOG.debug("Created launcher");
        try
            {
            launcher.start();
            LOG.debug("Started launcher");
            }
        catch (final Throwable t)
            {
            LOG.error("Could not start!!!", t);
            }
        }

    private TurnServer m_turnServer;

    private SipProxy m_sipProxy;

    private OnlineStatusRegistrationListener m_onlineStatusListener;

    private SipRegistrar m_registrar;

    private Launcher()
        {
        final UncaughtExceptionHandler ueh = new UncaughtExceptionHandler()
            {
            public void uncaughtException(final Thread t, final Throwable e)
                {
                LOG.error("Uncaught exception on Thread "+t.getName(), e);
                }
            };
        Thread.setDefaultUncaughtExceptionHandler(ueh);
        try
            {
            loadContexts();
            
            final AppenderCallback callback = new AppenderCallback()
                {
                public void addData(final Collection<NameValuePair> dataList)
                    {
                    dataList.add(new NameValuePair("version", 
                        String.valueOf(0.11114)));
                    }
                };
            final BugReportingAppender bugAppender = 
                new BugReportingAppender(callback, false);
            bugAppender.setUrl(ShootConstants.BUGS_URL);
            BasicConfigurator.configure(bugAppender);
            
            // We do this at the beginning because this server cannot have been
            // offline if it's just starting up.  The users should have been
            // cleared anyway, but this makes sure.
            clearOldUsers(m_onlineStatusListener);
            
            // This shutdown hook attempts to inform the remote server that this
            // server is down.
            final Runnable offlineRunner = new Runnable()
                {
                public void run()
                    {
                    LOG.debug("Caught shutdown hook...clearing users...");
                    try
                        {
                        clearOldUsers(m_onlineStatusListener);
                        }
                    catch (final Throwable t)
                        {
                        LOG.error("Error updating old users", t);
                        }
                    }
                };
            final Thread offlineUpdater = 
                new Thread(offlineRunner, "Offline-Updater");
            Runtime.getRuntime().addShutdownHook(offlineUpdater);
            }
        catch (final Throwable t)
            {
            LOG.error("Error starting SIP server", t);
            }
        }
    
    private void loadContexts()
        {
        this.m_turnServer = new TcpTurnServer(new TurnClientManagerImpl());
        final UriUtils uriUtils = new UriUtilsImpl();
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = new SipMessageFactoryImpl();
        final SipTransactionTracker transactionTracker = 
            new SipTransactionTrackerImpl();
        final SipTransactionFactory transactionFactory = 
            new SipTransactionFactoryImpl(transactionTracker, messageFactory, 500);
        final SipTcpTransportLayer transportLayer = 
            new SipTcpTransportLayerImpl(transactionFactory, headerFactory, 
                messageFactory);
        this.m_registrar = 
            new SipRegistrarImpl(messageFactory, transportLayer);
        final LocationService locationService = 
            new LocationServiceChain(
                Arrays.asList(new LastBambooLocationService(uriUtils)));
        final SipRequestForwarder unregisteredUriForwarder =
            new UnregisteredUriForwarder(locationService, transportLayer, 
                uriUtils, messageFactory, m_registrar);
        final SipRequestForwarder externalDomainForwarder =
            new ExternalDomainForwarder();
        final SipRequestAndResponseForwarder forwarder = 
            new StatelessSipProxy(transportLayer, m_registrar, 
                unregisteredUriForwarder, externalDomainForwarder, 
                uriUtils, messageFactory);
        this.m_sipProxy = 
            new SipProxyImpl(forwarder, m_registrar, headerFactory, 
                messageFactory, transportLayer);
        
        this.m_onlineStatusListener = 
            new OnlineStatusRegistrationListener(new RobustOnlineStatusUpdater());
        this.m_registrar.addRegistrationListener(m_onlineStatusListener);
        LOG.debug("Loaded context...");
        }

    /**
     * Clears stale users associated with this server.
     * 
     * @param listener The class for updating online status.
     */
    private void clearOldUsers(
        final OnlineStatusRegistrationListener listener)
        {
        final InetAddress ia = AmazonEc2Utils.getPublicAddress();
        
        listener.onOffline(ia);
        
        }

    /**
     * Launches any services that should be launched only if this peer is on
     * the open Internet, such as running a TURN server or a SIP proxy.
     *
     * @param publicAddress The public address of this host on the open 
     * Internet.
     * @throws IOException If there's an error binding to any of the start 
     * ports.
     */
    private void start() throws IOException
        {
        // Launch the SIP proxy.
        m_sipProxy.start();

        // Launch the TURN server
        m_turnServer.start ();
        
        try 
            {
            synchronized(this) 
                {
                wait();
                }
            } 
        catch (InterruptedException e) 
            {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
        }
    }
