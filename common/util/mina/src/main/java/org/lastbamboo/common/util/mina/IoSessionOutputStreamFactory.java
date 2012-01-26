package org.lastbamboo.common.util.mina;

import java.io.OutputStream;

import org.littleshoot.mina.common.IoSession;

/**
 * Factory for creating new {@link OutputStream}s from {@link IoSession}. 
 */
public interface IoSessionOutputStreamFactory
    {

    /**
     * Creates a new {@link OutputStream} for the specified session.
     * 
     * @param session The session to create a stream for.
     * @return The new {@link OutputStream} for the session.
     */
    OutputStream newStream(IoSession session);
    }
