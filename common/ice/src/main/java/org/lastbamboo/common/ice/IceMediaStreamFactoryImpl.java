package org.lastbamboo.common.ice;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.lastbamboo.common.ice.GeneralIceMediaStreamFactory;
import org.lastbamboo.common.ice.GeneralIceMediaStreamFactoryImpl;
import org.lastbamboo.common.ice.IceAgent;
import org.lastbamboo.common.ice.IceMediaStream;
import org.lastbamboo.common.ice.IceMediaStreamDesc;
import org.lastbamboo.common.ice.IceMediaStreamFactory;
import org.lastbamboo.common.ice.IceUdpConnectException;
import org.lastbamboo.common.turn.client.TurnClientListener;
import org.lastbamboo.common.turn.http.server.ServerDataFeeder;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.util.NetworkUtils;
import org.littleshoot.util.ShootConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ICE media stream factory class for creating streams where the ultimate
 * media exchanged can happen over RUDP or TCP.  This is packaged this way
 * because the ICE code does not know about RUDP.
 */
public class IceMediaStreamFactoryImpl implements IceMediaStreamFactory {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final IceMediaStreamDesc m_streamDesc;
    private final CandidateProvider<InetSocketAddress> m_stunServerCandidateProvider;

    /**
     * Creates a new ICE media stream factory.
     * 
     * @param streamDesc The description of the media stream to create.
     * @param rudpService The service for RUDP connections.
     */
    public IceMediaStreamFactoryImpl(
            final IceMediaStreamDesc streamDesc,
            final CandidateProvider<InetSocketAddress> stunServerCandidateProvider) {
        m_streamDesc = streamDesc;
        this.m_stunServerCandidateProvider = stunServerCandidateProvider;
    }

    public IceMediaStream newStream(final IceAgent iceAgent)
            throws IceUdpConnectException {
        final InetAddress serverAddress;
        try {
            serverAddress = NetworkUtils.getLocalHost();
        } catch (final UnknownHostException e) {
            m_log.warn("Could not get local host!!", e);
            return null;
        }
        final InetSocketAddress httpServerAddress = new InetSocketAddress(
                serverAddress, ShootConstants.FILES_PORT);
        final TurnClientListener delegateListener = new ServerDataFeeder(
                httpServerAddress);

        final GeneralIceMediaStreamFactory streamFactory = 
            new GeneralIceMediaStreamFactoryImpl(
                this.m_stunServerCandidateProvider);
        final IceMediaStream stream = streamFactory.newIceMediaStream(
                this.m_streamDesc, iceAgent, delegateListener);
        return stream;
    }
}
