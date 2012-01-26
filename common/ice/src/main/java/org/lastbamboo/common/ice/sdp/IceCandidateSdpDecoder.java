package org.lastbamboo.common.ice.sdp;

import java.io.IOException;
import java.util.Collection;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.ice.candidate.IceCandidate;

/**
 * Factory for generating ICE candidate classes from SDP.
 */
public interface IceCandidateSdpDecoder
    {

    /**
     * Creates a new <code>Collection</code> of <code>IceCandidate</code>
     * classes from the specified SDP data.
     * 
     * @param buf The SDP data to create ICE candidates from.
     * @param controlling Whether or not to generate controlling candidates. 
     * @return A new <code>Collection</code> of ICE candidates.
     * @throws IOException If there's an error parsing out a candidate from 
     * the SDP.
     */
    Collection<IceCandidate> decode(ByteBuffer buf, boolean controlling) 
        throws IOException;
    }
