package org.lastbamboo.common.npapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of message types for communicating between the NPAPI plugin
 * and LittleShoot.
 */
public enum NpapiLittleShootMessageType
    {

    /**
     * Notification of a new torrent download.
     */
    TORRENT_NOTIFY(0x0001),
    
    ;
    
    private static final Map<Integer, NpapiLittleShootMessageType> s_intsToEnums =
        new HashMap<Integer, NpapiLittleShootMessageType>();
    
    
    static
        {
        for (final NpapiLittleShootMessageType type : values())
            {
            s_intsToEnums.put(new Integer(type.toInt()), type);
            }
        }
    
    private final int m_messageType;

    private NpapiLittleShootMessageType(final int messageType)
        {
        m_messageType = messageType;
        }

    /**
     * Accessor for the int value of the message type.
     * 
     * @return The int value of the message type.
     */
    public int toInt()
        {
        return this.m_messageType;
        }

    /**
     * Returns the enum for the corresponding int value, or <code>null</code>
     * if no corresponding value exists.
     * 
     * @param typeInt The type as an int.
     * @return The corresponding enum value or <code>null</code> if no
     * corresponding value exists.
     */
    public static NpapiLittleShootMessageType toType(final int typeInt)
        {
        return s_intsToEnums.get(new Integer(typeInt));
        }
    }
