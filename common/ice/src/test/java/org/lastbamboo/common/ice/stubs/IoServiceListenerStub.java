package org.lastbamboo.common.ice.stubs;

import java.net.SocketAddress;

import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoService;
import org.littleshoot.mina.common.IoServiceConfig;
import org.littleshoot.mina.common.IoServiceListener;
import org.littleshoot.mina.common.IoSession;

public class IoServiceListenerStub implements IoServiceListener
    {

    public void serviceActivated(IoService service,
            SocketAddress serviceAddress, IoHandler handler,
            IoServiceConfig config)
        {
        // TODO Auto-generated method stub

        }

    public void serviceDeactivated(IoService service,
            SocketAddress serviceAddress, IoHandler handler,
            IoServiceConfig config)
        {
        // TODO Auto-generated method stub

        }

    public void sessionCreated(IoSession session)
        {
        // TODO Auto-generated method stub

        }

    public void sessionDestroyed(IoSession session)
        {
        // TODO Auto-generated method stub

        }

    }
