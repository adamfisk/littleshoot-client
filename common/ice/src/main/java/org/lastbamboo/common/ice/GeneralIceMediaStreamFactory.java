package org.lastbamboo.common.ice;

import org.lastbamboo.common.turn.client.TurnClientListener;

/**
 * Factory for creating ICE media streams.
 */
public interface GeneralIceMediaStreamFactory
    {

    <T> IceMediaStream newIceMediaStream(IceMediaStreamDesc streamDesc,
        IceAgent iceAgent, TurnClientListener delegateListener) 
        throws IceUdpConnectException;

    }
