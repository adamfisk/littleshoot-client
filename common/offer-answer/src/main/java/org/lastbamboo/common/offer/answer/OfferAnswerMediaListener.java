package org.lastbamboo.common.offer.answer;

/**
 * Interface for classes that listen for media events for offer/answer
 * exchanges that generate media. 
 */
public interface OfferAnswerMediaListener
    {

    /**
     * Notifies the listener that a media session has been established.
     * 
     * @param media The media session.
     */
    void onMedia(OfferAnswerMedia media);
    }
