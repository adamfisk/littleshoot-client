package org.lastbamboo.common.stun.stack.message.attributes.turn;

import org.lastbamboo.common.stun.stack.message.attributes.AbstractStunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;

/**
 * Connection status attribute. 
 */
public class ConnectionStatusAttribute extends AbstractStunAttribute
    {

    private final ConnectionStatus m_connectionStatus;

    /**
     * Creates a new connection status attribute.
     * 
     * @param connectionStatus The connection status.
     */
    public ConnectionStatusAttribute(final ConnectionStatus connectionStatus)
        {
        // The 4 is the length of the unsigned int indicating the status.
        super(StunAttributeType.CONNECT_STAT, 4); 
        m_connectionStatus = connectionStatus;
        }
    
    /**
     * Accessor for the connection status.
     * 
     * @return The connection status.
     */
    public ConnectionStatus getConnectionStatus()
        {
        return m_connectionStatus;
        }
    
    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visitConnectionStatus(this);
        }

    }
