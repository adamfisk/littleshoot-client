package org.lastbamboo.common.tcp.frame;

import java.util.Collection;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IdleStatus;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoHandlerAdapter;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.util.SessionUtil;
import org.littleshoot.util.mina.MinaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IoHandler} for local sockets to the HTTP server.  There is one
 * socket/session for each remote host we're exchanging data with.  This
 * effectively mimics the remote host connecting directly to the local
 * HTTP server, with the data already extracted from the TURN messages and
 * forwarded along these sockets.<p>
 * 
 * This class is also responsible for wraping data from the HTTP server
 * in TURN Send Indication messages.
 */
public class TcpFrameLocalIoHandler extends IoHandlerAdapter
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * This is the limit on the length of the data to encapsulate in a TCP 
     * frame.  TCP frame messages cannot be larger than 0xffff, but we leave
     * a little extra room just in case of any additional bytes needed for
     * the protocol.
     */
    private static final int LENGTH_LIMIT = 0xffff - 100;
    
    private final IoSession m_ioSession;

    /**
     * Creates a new TCP frame local IO handler.
     * 
     * @param ioSession The connection to the remote host.
     */
    public TcpFrameLocalIoHandler(final IoSession ioSession)
        {
        m_ioSession = ioSession;
        }

    public void messageReceived(final IoSession session, final Object message) 
        {
        m_log.debug("Received local data message: {}", message);
        // This is data received from the local HTTP server --
        // the raw data of an HTTP response.  It might be
        // larger than the maximum allowed size for TURN messages,
        // so we make sure to split it up.
        final ByteBuffer in = (ByteBuffer) message;
        
        // Send the data broken up into chunks if necessary.  This 
        // is because TURN messages cannot be larger than 0xffff.
        sendSplitBuffers(in);
        }
    
    public void messageSent(final IoSession session, final Object message) 
        {
        m_log.debug("Sent local TURN message number: {}", 
            session.getWrittenMessages());
        }
    
    public void sessionClosed(final IoSession session) 
        {
        // Remember this is only a local "proxied" session.  
        m_log.debug("Received **local** session closed!!");
        }
    
    public void sessionCreated(final IoSession session) 
        {
        SessionUtil.initialize(session);
        
        // We consider a connection to be idle if there's been no 
        // traffic in either direction for awhile.  
        session.setIdleTime(IdleStatus.BOTH_IDLE, 60 * 10);
        }

    public void sessionIdle(final IoSession session, 
        final IdleStatus status) throws Exception
        {
        // We close idle sessions to make sure we don't consume
        // too many client resources.
        // Note closing the session here will create the 
        // appropriate event handlers to clean up all mappings 
        // and references. 
        session.close();
        }

    public void exceptionCaught(final IoSession session, 
        final Throwable cause) 
        {
        m_log.error("Error processing data for **local** session: "+
            session, cause);
        }
    
    /**
     * Splits the main read buffer into smaller buffers that will 
     * fit in TCP frame messages.
     * 
     * @param buffer The main read buffer to split.
     */
    private void sendSplitBuffers(final ByteBuffer buffer)
        {
        // Break up the data into smaller chunks.
        final Collection<byte[]> buffers = 
            MinaUtils.splitToByteArrays(buffer, LENGTH_LIMIT);
        m_log.debug("Split single buffer into {}", buffers.size());
        for (final byte[] data : buffers)
            {
            m_log.debug("Sending buffer with capacity: {}", data.length);
            final TcpFrame frame = new TcpFrame(data);
            m_ioSession.write(frame);
            }
        }
    }
