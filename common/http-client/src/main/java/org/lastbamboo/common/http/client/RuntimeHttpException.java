package org.lastbamboo.common.http.client;

import org.apache.commons.httpclient.HttpException;

/**
 * A runtime wrapper around an <code>HttpException</code>.
 */
public class RuntimeHttpException extends RuntimeException
    {
    /**
     * Constructs a new runtime wrapper for an <code>HttpException</code>.
     * 
     * @param original The original <code>HttpException</code> to wrap.
     */
    public RuntimeHttpException (final HttpException original)
        {
        super (original);
        }

    /**
     * Constructs a new runtime wrapper for an <code>HttpException</code>.
     * 
     * @param message Any message to display with the exception.
     * @param original The original <code>HttpException</code> to wrap.
     */
    public RuntimeHttpException(final String message, 
        final HttpException original)
        {
        super (message, original);
        }

    /**
     * Constructs a new runtime wrapper for an <code>HttpException</code>.
     * 
     * @param message Any message to display with the exception.
     */
    public RuntimeHttpException(final String message)
        {
        super(message);
        }
    }
