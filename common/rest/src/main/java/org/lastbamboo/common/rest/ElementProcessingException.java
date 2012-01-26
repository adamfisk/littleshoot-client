package org.lastbamboo.common.rest;


/**
 * Specialized exception for errors converting XML Element data to our own
 * classes.
 */
public class ElementProcessingException extends Exception
    {

    public ElementProcessingException(final String msg, final Exception e)
        {
        super(msg, e);
        }

    public ElementProcessingException(final String msg)
        {
        super(msg);
        }

    }
