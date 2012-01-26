package org.lastbamboo.common.stun.stack.message.attributes.turn;

import org.lastbamboo.common.stun.stack.message.attributes.AbstractStunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;

/**
 * Class that wraps incoming data from a remote host.  It adds the address of 
 * the remote host so that the TURN client can demultiplex incoming data to the
 * hosts that data is arriving from.
 */
public final class DataAttribute extends AbstractStunAttribute 
    {

    private final byte[] m_data;

    /**
     * Creates a new data attribute wraping the specified data from a remote 
     * host.
     * @param bodyBytes The data to wrap.
     */
    public DataAttribute(final byte[] bodyBytes)
        {
        super(StunAttributeType.DATA, bodyBytes.length);
        this.m_data = bodyBytes;
        }

    /**
     * Accesses the raw data.
     * 
     * @return The raw data encapsulated in the attribute.
     */
    public byte[] getData()
        {
        return this.m_data;
        }

    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visitData(this);
        }
    
    @Override
    public String toString()
        {
        return getClass().getSimpleName() + " with "+ 
            this.m_data.length +" bytes of data...";
        }
    }
