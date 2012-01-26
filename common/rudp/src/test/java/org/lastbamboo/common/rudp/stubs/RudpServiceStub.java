package org.lastbamboo.common.rudp.stubs;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoService;
import org.littleshoot.mina.common.IoServiceConfig;
import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.rudp.RudpConnectionId;
import org.lastbamboo.common.rudp.RudpListener;
import org.lastbamboo.common.rudp.RudpListeningConnectionId;
import org.lastbamboo.common.rudp.RudpManager;
import org.lastbamboo.common.rudp.RudpService;
import org.lastbamboo.common.rudp.RudpSocket;
import org.littleshoot.util.Future;
import org.littleshoot.util.Optional;

public class RudpServiceStub implements RudpService
    {

    public Future<RudpConnectionId> accept(RudpListeningConnectionId id,
            RudpListener listener)
        {
        // TODO Auto-generated method stub
        return null;
        }
    
    public Future<RudpConnectionId> accept(IoSession session,
            RudpListener listener)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void close(RudpConnectionId id)
        {
        // TODO Auto-generated method stub

        }

    public RudpManager getManager()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public RudpListeningConnectionId listen(IoSession session)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public RudpListeningConnectionId listen(int port, int backlog)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Future<RudpConnectionId> open(IoSession session)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Future<RudpConnectionId> open(InetSocketAddress address,
            RudpListener listener)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public byte[] receive(RudpConnectionId id)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void send(RudpConnectionId id, byte[] data)
        {
        // TODO Auto-generated method stub

        }

    public void send(RudpConnectionId id, byte[] data, long timeout)
        {
        // TODO Auto-generated method stub

        }

    public Optional<byte[]> tryReceive(RudpConnectionId id)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void serviceActivated(IoService service, SocketAddress serviceAddress, IoHandler handler, IoServiceConfig config)
        {
        // TODO Auto-generated method stub
        
        }

    public void serviceDeactivated(IoService service, SocketAddress serviceAddress, IoHandler handler, IoServiceConfig config)
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

    public Socket newSocket(Future<RudpConnectionId> future, IoSession session)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void socketClosed(RudpSocket rudpSocket)
        {
        // TODO Auto-generated method stub
        
        }

    }
