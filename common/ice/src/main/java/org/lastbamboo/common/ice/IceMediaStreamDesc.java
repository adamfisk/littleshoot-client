package org.lastbamboo.common.ice;

/**
 * Describes an individual media stream.
 */
public final class IceMediaStreamDesc
    {
    
    private final boolean m_udp;
    private final boolean m_tcp;
    private final String m_mimeContentType;
    private final String m_mimeContentSubtype;
    private final int m_numComponents;
    private final boolean m_useRelay;

    /**
     * Creates a new media stream description with all the information 
     * necessary for ICE to establish the stream.
     * 
     * @param tcp Whether or not the stream will use TCP.
     * @param udp Whether or not the stream will use UDP.
     * @param mimeContentType The MIME content type for SDP.
     * @param mimeContentSubtype The MIME content subtype.
     * @param numComponents The number of components in the media stream.
     * @param useRelay Whether or not to use relay (TURN) servers.
     */
    public IceMediaStreamDesc(final boolean tcp, final boolean udp, 
        final String mimeContentType, final String mimeContentSubtype,
        final int numComponents, final boolean useRelay)
        {
        m_tcp = tcp;
        m_udp = udp;
        m_mimeContentType = mimeContentType;
        m_mimeContentSubtype = mimeContentSubtype;
        m_numComponents = numComponents;
        this.m_useRelay = useRelay;
        }

    public String getMimeContentSubtype()
        {
        return m_mimeContentSubtype;
        }

    public String getMimeContentType()
        {
        return m_mimeContentType;
        }

    public boolean isTcp()
        {
        return m_tcp;
        }

    public boolean isUdp()
        {
        return m_udp;
        }

    public int getNumComponents()
        {
        return m_numComponents;
        }

    public boolean isUseRelay() 
        {
        return m_useRelay;
        }
    
    }
