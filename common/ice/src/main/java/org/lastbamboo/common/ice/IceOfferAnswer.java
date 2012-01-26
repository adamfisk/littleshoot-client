package org.lastbamboo.common.ice;

import java.net.InetAddress;
import java.util.Collection;

import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.offer.answer.OfferAnswer;

/**
 * Specialized offer/answer interface for ICE.
 */
public interface IceOfferAnswer extends OfferAnswer
    {

    /**
     * Gathers candidates for this specific offer/answer implementation.
     * 
     * @return The collection of {@link IceCandidate}s.
     */
    Collection<? extends IceCandidate> gatherCandidates();

    InetAddress getPublicAdress();

    }
