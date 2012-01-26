package org.lastbamboo.common.stun.stack.message.attributes;

/**
 * The STUN server attribute.
 */
public class StunServerAttribute extends AbstractStunAttribute
    {

    private final String m_serverText;

    /**
     * Creates a new STUN server attribute.
     * 
     * @param bodyLength The length of the STUN server attribute body.
     * @param serverText The text description of the server.
     */
    public StunServerAttribute(final int bodyLength, final String serverText)
        {
        super(StunAttributeType.SERVER, bodyLength);
        m_serverText = serverText;
        }

    public void accept(final StunAttributeVisitor visitor)
        {
        // This attribute is informational only, so ignore it for nw.
        }

    /**
     * Gets the description of the server.
     * 
     * @return The description of the server.
     */
    public String getServerText()
        {
        return m_serverText;
        }

    }
