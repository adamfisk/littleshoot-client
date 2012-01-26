package org.lastbamboo.common.stun.stack.message.attributes;



/**
 * Interface for the various STUN attributes.
 */
public interface StunAttribute
    {

    /**
     * Returns the length of the attribute in bytes.
     * 
     * @return The length of the attribute in bytes;
     */
    int getBodyLength();

    /**
     * Allows attributes to accept visitors.
     * 
     * @param visitor The visitor to accept.
     */
    void accept(StunAttributeVisitor visitor);

    /**
     * Gets the total length of the attribute including the header and the 
     * body in bytes.
     * 
     * @return The total length of the attribute including the header and the 
     * body.
     */
    int getTotalLength();

    /**
     * Accessor for the type of the attribute.
     * 
     * @return The type of the attribute.
     */
    StunAttributeType getAttributeType();

    }
