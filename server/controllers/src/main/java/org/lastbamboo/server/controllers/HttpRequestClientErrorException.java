package org.lastbamboo.server.controllers;

/**
 * Exception for client errors in HTTP request syntax.
 */
public class HttpRequestClientErrorException extends RuntimeException
    {

    /**
     * Generated serial ID.
     */
    private static final long serialVersionUID = 7575587229111683528L;
    
    private final int m_statusCode;
    private final String m_request;
    private final String m_statusMessage;

    /**
     * Creates a new exception with the specified status code, request line,
     * and status message.
     * @param statusCode The 4xx error code for the error in the request.
     * @param statusMessage The status message for the HTTP status.
     * @param request The request that caused the error.
     */
    public HttpRequestClientErrorException(final int statusCode, 
        final String statusMessage, final String request)
        {
        this.m_statusCode = statusCode;
        this.m_statusMessage = statusMessage;
        this.m_request = request;
        }

    /**
     * Accessor for the error code for the request error.
     * @return The 4xx level error code indicating an error in the client
     * request.
     */
    public int getStatusCode()
        {
        return this.m_statusCode;
        }
    
    /**
     * Accessor for the request that generated the error.
     * @return The request string that generated the error.
     */
    public String getRequest()
        {
        return this.m_request;
        }

    /**
     * Accessor for the status message to send the user.
     * @return The status message.
     */
    public String getStatusMessage()
        {
        return this.m_statusMessage;
        }
    }
