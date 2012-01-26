package org.lastbamboo.common.stun.stack.encoder;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.attributes.ErrorCodeAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.MappedAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControlledAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControllingAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IcePriorityAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceUseCandidateAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatus;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatusAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.DataAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RelayAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RemoteAddressAttribute;
import org.littleshoot.util.mina.MinaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes STUN attributes.
 */
public class StunAttributeEncoder implements StunAttributeVisitor
    {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    private static final CharsetEncoder UTF_8_ENCODER =
        Charset.forName("UTF-8").newEncoder();
    
    private final ByteBuffer m_buf;

    /**
     * Creates a new class for writing STUN attributes.
     * 
     * @param buf The attribute buffer.
     */
    public StunAttributeEncoder(final ByteBuffer buf)
        {
        m_buf = buf;
        }
    
    public void visitData(final DataAttribute data)
        {
        writeHeader(data);
        final byte[] dataBytes = data.getData();
        m_buf.put(dataBytes);
        }
    
    public void visitConnectionStatus(final ConnectionStatusAttribute attribute)
        {
        LOG.debug("Writing connection status attribute: {}", attribute);
        writeHeader(attribute);
        final ConnectionStatus status = attribute.getConnectionStatus();
        MinaUtils.putUnsignedInt(m_buf, status.toLong());
        }

    public void visitIceControlled(final IceControlledAttribute attribute)
        {
        writeHeader(attribute);
        final byte[] tieBreaker = attribute.getTieBreaker();
        LOG.debug("Encoding controlled: {}", tieBreaker);
        m_buf.put(tieBreaker);
        }

    public void visitIceControlling(final IceControllingAttribute attribute)
        {
        writeHeader(attribute);
        final byte[] tieBreaker = attribute.getTieBreaker();
        LOG.debug("Encoding controlling: {}", tieBreaker);
        m_buf.put(tieBreaker);
        }

    public void visitIcePriority(final IcePriorityAttribute attribute)
        {
        writeHeader(attribute);
        final long priority = attribute.getPriority();
        MinaUtils.putUnsignedInt(m_buf, priority);
        }

    public void visitIceUseCandidate(final IceUseCandidateAttribute attribute)
        {
        writeHeader(attribute);
        // Note that USE-CANDIDATE doesn't have any body.
        }
    
    public void visiteErrorCode(final ErrorCodeAttribute attribute)
        {
        writeHeader(attribute);
        
        // The first 21 bits are zero-filled for 32 bit alignment.  We skip
        // the first 16 here so we can just write a full byte for the class
        // on the next call.
        m_buf.skip(2);
        MinaUtils.putUnsignedByte(this.m_buf, attribute.getErrorClass());
        MinaUtils.putUnsignedByte(this.m_buf, attribute.getErrorNumber());
        try
            {
            m_buf.putString(attribute.getReasonPhrase(), UTF_8_ENCODER);
            }
        catch (final CharacterCodingException e)
            {
            LOG.error("Could not encode reason phrase", e);
            throw new IllegalArgumentException(
                "Could not encode reason phrase", e);
            }
        }

    public void visitRelayAddress(final RelayAddressAttribute address)
        {
        visitAddressAttribute(address);
        }
    
    public void visitMappedAddress(final MappedAddressAttribute address)
        {
        visitAddressAttribute(address);
        }
    
    public void visitRemoteAddress(final RemoteAddressAttribute address)
        {
        visitAddressAttribute(address);
        }
    
    private void visitAddressAttribute(final StunAddressAttribute address)
        {
        writeHeader(address);

        // Now put the attribute body.
        
        // The first byte is ignored in address attributes.
        MinaUtils.putUnsignedByte(m_buf, 0x00);
        
        final int family = address.getAddressFamily();
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing family: "+family);
            }
        final InetSocketAddress socketAddress = address.getInetSocketAddress();
        final int port = socketAddress.getPort();
        final InetAddress ia = socketAddress.getAddress();
        final byte[] addressBytes = ia.getAddress();
        MinaUtils.putUnsignedByte(m_buf, family);
        MinaUtils.putUnsignedShort(m_buf, port);
        m_buf.put(addressBytes);
        }

    private void writeHeader(final StunAttribute sa)
        {
        MinaUtils.putUnsignedShort(m_buf, sa.getAttributeType().toInt());
        MinaUtils.putUnsignedShort(m_buf, sa.getBodyLength());
        }
    }
