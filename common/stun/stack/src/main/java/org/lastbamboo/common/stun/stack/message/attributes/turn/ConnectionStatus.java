package org.lastbamboo.common.stun.stack.message.attributes.turn;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the various connection statuses.
 */
public enum ConnectionStatus
    {

    
    /**
     * Status when we're listening for connections from the remote address.
     */
    LISTEN (0x00000000L),
    
    /**
     * Status for when a connection is established with the remote address.
     */
    ESTABLISHED (0x00000001L),
    
    /**
     * Status for when a connection is closed with the remote address.
     */
    CLOSED (0x00000002L);
    
    private static final Map<Long, ConnectionStatus> s_longsToEnums =
        new HashMap<Long, ConnectionStatus>();
    
    static
        {
        s_longsToEnums.put(new Long(LISTEN.toLong()), LISTEN);
        s_longsToEnums.put(new Long(ESTABLISHED.toLong()), ESTABLISHED);
        s_longsToEnums.put(new Long(CLOSED.toLong()), CLOSED);
        }
    
    private final long m_value;

    private ConnectionStatus(final long value)
        {
        this.m_value = value;
        }
    
    /**
     * Converts this status to an unsigned int.
     * 
     * @return This status as an unsigned int.
     */
    public long toLong()
        {
        return this.m_value;
        }
    
    /**
     * Returns the {@link ConnectionStatus} for the specified long value.
     * 
     * @param value The long value to get the {@link ConnectionStatus} of.
     * @return The {@link ConnectionStatus} for the long value.
     */
    public static ConnectionStatus valueOf(final long value)
        {
        return s_longsToEnums.get(new Long(value));
        }
    }
