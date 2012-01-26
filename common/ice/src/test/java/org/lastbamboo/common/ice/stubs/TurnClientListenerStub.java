package org.lastbamboo.common.ice.stubs;

import java.net.InetSocketAddress;

import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.turn.client.TurnClientListener;

public class TurnClientListenerStub implements TurnClientListener
    {

    public void close()
        {
        // TODO Auto-generated method stub

        }

    public void onData(InetSocketAddress remoteAddress, IoSession session,
            byte[] data)
        {
        // TODO Auto-generated method stub

        }

    public void onRemoteAddressClosed(InetSocketAddress remoteAddress)
        {
        // TODO Auto-generated method stub

        }

    public IoSession onRemoteAddressOpened(InetSocketAddress remoteAddress,
            IoSession session)
        {
        // TODO Auto-generated method stub
        return null;
        }

    }
