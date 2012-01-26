package org.lastbamboo.common.rudp.segment;

/**
 * A visitor to a RUDP segment.
 * 
 * @param <T>
 *      The return value of the visitor.
 */
public interface SegmentVisitor<T>
    {
    /**
     * Visits an ACK segment.
     * 
     * @param ack
     *      The ACK segment.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitAck
            (AckSegment ack);
    
    /**
     * Visits an EACK segment.
     * 
     * @param eack
     *      The EACK segment.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitEack
            (EackSegment eack);
    
    /**
     * Visits a NUL segment.
     * 
     * @param nul
     *      The NUL segment.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitNul
            (NulSegment nul);
    
    /**
     * Visits a RST segment.
     * 
     * @param rst
     *      The RST segment.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitRst
            (RstSegment rst);
    
    /**
     * Visits a SYN segment.
     * 
     * @param syn
     *      The SYN segment.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitSyn
            (SynSegment syn);
    }
