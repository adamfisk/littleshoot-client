package org.lastbamboo.common.offer.answer;

/**
 * Interface for media sessions created with an offer/answer exchange. 
 */
public interface OfferAnswerMedia
    {

    /**
     * Accepts the specified media visitor.
     * 
     * @param <T> The type the visitor returns.
     * @param mediaVisitor The visitor.
     * @return The return value of the visitor.
     */
    <T> T accept(OfferAnswerMediaVisitor<T> mediaVisitor);
    }
