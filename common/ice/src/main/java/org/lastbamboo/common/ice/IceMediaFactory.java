package org.lastbamboo.common.ice;

import org.lastbamboo.common.ice.candidate.IceCandidatePair;
import org.lastbamboo.common.offer.answer.OfferAnswerMediaListener;


/**
 * Factory for creating media once an ICE exchange has completed. 
 */
public interface IceMediaFactory
    {
    
    /**
     * Starts an ICE media session.
     * 
     * @param nominatedPair The nominated ICE candidate pair to use for media.
     * @param client Whether or not this agent is the client -- useful for
     * media that are handled differently on the client and server sides.
     * @param mediaListener The class to notify of media events.
     */
    void newMedia(IceCandidatePair nominatedPair, boolean client, 
        OfferAnswerMediaListener mediaListener);

    }
