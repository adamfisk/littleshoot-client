package org.lastbamboo.common.stun.stack.message.attributes;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The STUN ERROR CODE attribute.
 */
public class ErrorCodeAttribute extends AbstractStunAttribute
    {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(ErrorCodeAttribute.class);
    private final int m_errorClass;
    private final int m_errorNumber;
    private final CharSequence m_reasonPhrase;
    private int m_errorCode;

    /**
     * Creates a new STUN error code attribute.
     * 
     * @param code The error code.
     * @param reasonPhrase The reason for the error.
     */
    public ErrorCodeAttribute(final int code, final String reasonPhrase)
        {
        this((int)Math.floor(code / 100), code % 100, reasonPhrase);
        }

    /**
     * Creates a new STUN error code attribute.
     * 
     * @param errorClass The error class from 3 to 6.
     * @param errorNumber The error number from 0 to 99.
     * @param reasonPhrase The reason for the error.
     */
    public ErrorCodeAttribute(final int errorClass, final int errorNumber, 
        final String reasonPhrase)
        {
        super(StunAttributeType.ERROR_CODE, calculateBodyLength(reasonPhrase));
        if (errorClass < 3 ||  errorClass > 6)
            {
            throw new IllegalArgumentException("Bad class of "+errorClass);
            }
        if (errorNumber < 0 || errorNumber > 99)
            {
            throw new IllegalArgumentException("Bad error # of "+errorNumber);
            }
        this.m_errorClass = errorClass;
        this.m_errorNumber = errorNumber;
        this.m_reasonPhrase = reasonPhrase;
        }

    private static int calculateBodyLength(final String reasonPhrase)
        {
        if (reasonPhrase.length() > 128)
            {
            LOG.warn("Reason phrase is too long: "+reasonPhrase);
            throw new IllegalArgumentException(
                "Reason phrase is too long: "+reasonPhrase);
            }
        // The body length for error codes is just 4 bytes plus the length of
        // the reason phrase in bytes.  Note we multiply by 2 because each
        // char in the sequence is 2 bytes for UTF-8.
        try
            {
            return 4 + (reasonPhrase.getBytes("UTF-8").length);
            }
        catch (final UnsupportedEncodingException e)
            {
            LOG.error("Bad encoding?", e);
            return 4 + reasonPhrase.length();
            }
        }

    /**
     * Returns the full error code.
     * 
     * @return The full error code, including the class and the number.
     */
    public int getErrorCode()
        {
        return this.m_errorCode;
        }

    /**
     * The class of the error, from 3 to 6, inclusive.
     * 
     * @return The class of the error.
     */
    public int getErrorClass()
        {
        return this.m_errorClass;
        }

    /**
     * The number of the error, from 0 to 99, inclusive.
     * 
     * @return The number of the error.
     */
    public int getErrorNumber()
        {
        return this.m_errorNumber;
        }

    /**
     * Accessor for the reason phrase.
     * 
     * @return The reason phrase.
     */
    public CharSequence getReasonPhrase()
        {
        return this.m_reasonPhrase;
        }
    
    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visiteErrorCode(this);
        }
    
    public String toString()
        {
        return getClass().getSimpleName() + 
            ": " + this.m_errorClass + this.m_errorNumber + 
            " " + this.m_reasonPhrase;
        }

    }
