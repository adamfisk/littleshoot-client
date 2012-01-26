package org.lastbamboo.common.offer.answer;

/**
 * Visitor for media sessions.
 *  
 * @param <T> The type the visitor returns when visiting.
 */
public interface OfferAnswerMediaVisitor<T>
    {

    /**
     * Visits a media session that runs over a socket.
     * 
     * @param socketMedia The media session containing a socket.
     * @return An instance of the type the visitor returns.
     */
    T visitSocketMedia(OfferAnswerSocketMedia socketMedia);
    }
