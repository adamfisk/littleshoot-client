package org.lastbamboo.common.rudp.state;

/**
 * A visitor for a state.
 * 
 * @param <T>
 *      The type of the result of visitation.
 */
public interface StateVisitor<T>
    {
    /**
     * Visits the closed state.
     * 
     * @param state
     *      The state.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitClosed
            (State state);
    
    /**
     * Visits the close wait state.
     * 
     * @param state
     *      The state.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitCloseWait
            (State state);
    
    /**
     * Visits the listen state.
     * 
     * @param state
     *      The state.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitListen
            (State state);
    
    /**
     * Visits the open state.
     * 
     * @param state
     *      The state.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitOpen
            (State state);
    
    /**
     * Visits the SYN received state.
     * 
     * @param state
     *      The state.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitSynRcvd
            (State state);
    
    /**
     * Visits the SYN sent state.
     * 
     * @param state
     *      The state.
     *      
     * @return
     *      The result of the visitation.
     */
    T visitSynSent
            (State state);
    }
