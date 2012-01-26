package org.lastbamboo.common.ice;

import org.littleshoot.mina.common.IoSession;

/**
 * Interface for classes that create new ICE STUN connectivity check classes
 * for different transports. 
 */
public interface IceStunCheckerFactory
    {

    /**
     * Creates a new STUN checker.
     * 
     * @param session The {@link IoSession} for the checker.
     * @return The new STUN checker.
     */
    IceStunChecker newChecker(IoSession session);
    
    }
