package org.lastbamboo.common.rest;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for different result source IDs.  The order of these IDs is important
 * because it determines the default display order.
 */
public enum RestResultSources
    {

    /**
     * ID for LittleShoot results.
     */
    LITTLE_SHOOT(0),
    
    /**
     * ID for LimeWire results.
     */
    LIMEWIRE(1),
    
    /**
     * ID for YouTube results.
     */
    YOU_TUBE(2),
    
    /**
     * ID for Yahoo video results.
     */
    YAHOO_VIDEOS(3),
    
    /**
     * ID for Flickr results.
     */
    FLICKR(4),
    
    /**
     * ID for Yahoo image results.
     */
    YAHOO_IMAGES(5),

    /**
     * ID for Odeo results.
     */
    ODEO(6), 
    
    /**
     * ID for isohunt results.
     */
    ISO_HUNT(7), 
    
    /**
     * ID for imeem results.
     */
    IMEEM(8),
    
    ;

    private static final Map<Integer, RestResultSources> s_intsToEnums =
        new HashMap<Integer, RestResultSources>();
    
    static
        {
        for (final RestResultSources type : values())
            {
            final Integer source = new Integer(type.toInt());
            if (s_intsToEnums.containsKey(source))
                {
                throw new IllegalArgumentException("Source exists!!! "+source);
                }
            s_intsToEnums.put(source, type);
            }
        }
    
    private final int m_messageType;
    
    private RestResultSources(final int messageType)
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
    public static RestResultSources toType(final int typeInt)
        {
        return s_intsToEnums.get(new Integer(typeInt));
        }
    }
