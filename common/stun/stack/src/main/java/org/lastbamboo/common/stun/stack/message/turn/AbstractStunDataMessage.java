package org.lastbamboo.common.stun.stack.message.turn;

import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.AbstractStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.turn.DataAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RemoteAddressAttribute;

/**
 * Abstract class for TURN messages containing data, particularly the 
 * Send Indication and the Data Indication messages.  These are essentially
 * the same message, just passed in different directions.
 */
public abstract class AbstractStunDataMessage extends AbstractStunMessage
    {
    
    private final InetSocketAddress m_remoteAddress;
    private final byte[] m_data;

    /**
     * Creates a new STUN data message.
     * 
     * @param transactionId The transaction ID.
     * @param messageType The message type;
     * @param attributes The message attributes.
     */
    public AbstractStunDataMessage(final UUID transactionId, 
        final StunMessageType messageType, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        super(transactionId, messageType, attributes);
        m_remoteAddress = 
            ((RemoteAddressAttribute) attributes.get(
                StunAttributeType.REMOTE_ADDRESS)).getInetSocketAddress();
        m_data = 
            ((DataAttribute) attributes.get(StunAttributeType.DATA)).getData();
        }
    
    /**
     * Creates a new abstract STUN data message.
     * 
     * @param messageType The type of message.
     * @param data The data.
     * @param remoteAddress The address the data came from or should be sent to.
     */
    public AbstractStunDataMessage(
        final StunMessageType messageType, final byte[] data, 
        final InetSocketAddress remoteAddress)
        {
        super(messageType, createDataAttributes(data, remoteAddress));
        if (data.length > 0xffff)
            {
            throw new IllegalArgumentException(
                "Data length must be smaller than: "+0xffff+" but is:"+
                data.length);
            }
        m_remoteAddress = remoteAddress;
        m_data = data;
        }

    private static Map<StunAttributeType, StunAttribute> createDataAttributes(
        final byte[] data, final InetSocketAddress remoteAddress)
        {
        final RemoteAddressAttribute raa = 
            new RemoteAddressAttribute(remoteAddress);
        
        final DataAttribute da = new DataAttribute(data);
        
        return createAttributes(raa, da);
        }


    /**
     * Accessor for the data.
     * 
     * @return The data.
     */
    public final byte[] getData()
        {
        return m_data;
        }

    /**
     * Accessor for the remote address.
     * 
     * @return The remote address.
     */
    public final InetSocketAddress getRemoteAddress()
        {
        return m_remoteAddress;
        }
    
    }
