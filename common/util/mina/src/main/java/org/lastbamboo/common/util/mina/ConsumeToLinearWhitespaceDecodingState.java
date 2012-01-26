package org.lastbamboo.common.util.mina;

import org.littleshoot.mina.common.ByteBuffer;


/**
 * Consumes a {@link ByteBuffer} up to linear whitespace.<p>
 * 
 * Taken from the AsyncWeb project.  
 */
public abstract class ConsumeToLinearWhitespaceDecodingState extends
    ConsumeToTerminatorDecodingState
    {

    /**
     * Creates a new LWS decoding state.  This only checks for a single space
     * because that's what SIP typically requires.
     */
    public ConsumeToLinearWhitespaceDecodingState()
        {
        super(MinaCodecUtils.SPACE);
        }
    }
