package org.lastbamboo.common.tcp.frame;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.ConnectFuture;
import org.littleshoot.mina.common.ExecutorThreadModel;
import org.littleshoot.mina.common.IdleStatus;
import org.littleshoot.mina.common.IoConnector;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.common.RuntimeIOException;
import org.littleshoot.mina.common.ThreadModel;
import org.littleshoot.mina.transport.socket.nio.SocketConnector;
import org.littleshoot.mina.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IoHandler} that reads framed TCP messages and makes the bytes from
 * those messages available to a server.
 */
public class TcpFrameServerIoHandler implements IoHandler
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final Map<IoSession, IoSession> m_remoteToLocalSessions =
        new ConcurrentHashMap<IoSession, IoSession>();
    
    private final InetSocketAddress m_serverAddress;

    /**
     * Creates a new {@link TcpFrameServerIoHandler}.
     * 
     * @param serverAddress The address of the server to relay data to.
     */
    public TcpFrameServerIoHandler(final InetSocketAddress serverAddress)
        {
        m_serverAddress = serverAddress;
        }
    
    public void messageReceived(final IoSession session, final Object message)
        {
        m_log.debug("Received message on TCP frame: {}", message);
        final TcpFrame frame = (TcpFrame) message;
        final byte[] data = frame.getData();
        
        final IoSession localSession = m_remoteToLocalSessions.get(session);
        m_log.debug("Writing raw data to local session");
        localSession.write(ByteBuffer.wrap(data));
        }

    public void exceptionCaught(final IoSession session, final Throwable cause) 
        {
        m_log.warn("Caught exception!!", cause);
        }

    public void messageSent(final IoSession session, final Object message) 
        {
        m_log.debug("Sent message: {}", message);
        }

    public void sessionClosed(final IoSession session) throws Exception
        {
        this.m_remoteToLocalSessions.remove(session);
        }

    public void sessionCreated(final IoSession session) throws Exception
        {
        m_log.debug("Session created!!");
        SessionUtil.initialize(session);
        
        // We consider a connection to be idle if there's been no 
        // traffic in either direction for awhile.  
        session.setIdleTime(IdleStatus.BOTH_IDLE, 60);
        }

    public void sessionIdle(final IoSession session, 
        final IdleStatus status) throws Exception
        {
        m_log.debug("Closing idle session");
        session.close();
        }

    public void sessionOpened(final IoSession ioSession) throws Exception
        {
        m_log.debug("Opened session with remote host...");
        // We don't synchronize here because we're processing data from
        // a single TCP connection.
        if (m_remoteToLocalSessions.containsKey(ioSession))
            {
            m_log.warn("We already have a session for: {}", ioSession);
            return;
            }
        
        m_log.debug("Opening new local socket for remote address: {}", 
                ioSession);
        final IoConnector connector = new SocketConnector();
        
        //connector.addListener(this);
        final ThreadModel threadModel = 
            ExecutorThreadModel.getInstance(
                "TCP-Frame-Local-Server-Socket");
        connector.getDefaultConfig().setThreadModel(threadModel);
        final IoHandler ioHandler = new TcpFrameLocalIoHandler(ioSession); 
        
        final ConnectFuture ioFuture = 
            connector.connect(this.m_serverAddress, ioHandler);
        
        // We're just connecting locally, so it should be much quicker 
        // than this unless there's something wrong.
        ioFuture.join(6000);
        final IoSession session;
        try
            {
            session = ioFuture.getSession();
            if (session == null)
                {
                throw new RuntimeIOException("Could not get session");
                }
            }
        catch (final RuntimeIOException e)
            {
            // This happens when we can't connect.
            m_log.debug("Could not connect to host: {}", this.m_serverAddress);
            m_log.debug("Reason for no connection: ", e);
            throw e;
            }
        if (!session.isConnected())
            {
            m_log.error("Could not connect to server: {}", 
                this.m_serverAddress);
            }
        else
            {
            m_log.debug("Connected to server: {}", this.m_serverAddress);
            this.m_remoteToLocalSessions.put(ioSession, session);
            }
        }
    
    
    @Override 
    public String toString()
        {
        return getClass().getSimpleName();
        }
    }
