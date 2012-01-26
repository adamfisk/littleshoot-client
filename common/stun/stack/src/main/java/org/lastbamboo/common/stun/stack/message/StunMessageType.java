package org.lastbamboo.common.stun.stack.message;

import java.util.HashMap;
import java.util.Map;

/**
 * STUN attribute types.  These are converted to the full values including
 * the class bits for ease of use.  This is just a little easier that forcing
 * implementors to deal with the STUN encoding structure specified in 
 * draft-ietf-behave-rfc3489bis-08.txt section 6.
 */
public enum StunMessageType
    {
    
    /**
     * Binding request type ID.
     */
    BINDING_REQUEST(0x0001),
    
    /**
     * Successful binding response bits.
     */
    BINDING_SUCCESS_RESPONSE(0x0101),
    
    /**
     * Error binding response bits.
     */
    BINDING_ERROR_RESPONSE(0x0111),
    
    /**
     * TURN allocate request method.
     */
    ALLOCATE_REQUEST(0x0003),

    /**
     * TURN response to a successful allocate request.
     */
    ALLOCATE_SUCCESS_RESPONSE(0x0103),
    
    /**
     * A TURN message indicating an error with allocating a new address.
     */
    ALLOCATE_ERROR_RESPONSE(0x0113),
    
    /**
     * A TURN send indication message.
     */
    SEND_INDICATION(0x0004),
    
    /**
     * A TURN data indication message.
     */
    DATA_INDICATION(0x0115),

    /**
     * TURN connection status indication message.  TODO: The number for this
     * message is wrong and is just chosen to be unique.  There's a conflict
     * in draft-ietf-behave-turn-03.txt where allocate requests have the
     * same message type ID.
     */
    CONNECTION_STATUS_INDICATION(0x008),

    /**
     * A TURN connection request.
     */
    CONNECT_REQUEST(0x005)
    
    ;
    
    private static final Map<Integer, StunMessageType> s_intsToEnums =
        new HashMap<Integer, StunMessageType>();
    
    
    static
        {
        for (final StunMessageType type : values())
            {
            s_intsToEnums.put(new Integer(type.toInt()), type);
            }
        }
    
    private final int m_messageType;

    private StunMessageType(final int messageType)
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
    public static StunMessageType toType(final int typeInt)
        {
        return s_intsToEnums.get(new Integer(typeInt));
        }

    }
