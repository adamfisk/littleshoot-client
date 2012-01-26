package org.lastbamboo.common.http.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.littleshoot.util.DefaultHttpClient;
import org.littleshoot.util.DefaultHttpClientImpl;
import org.littleshoot.util.RuntimeIoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the Apache Commons HTTP client interface.
 */
public final class CommonsHttpClientImpl implements CommonsHttpClient
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * The underlying Apache Commons client to which we delegate.
     */
    private final DefaultHttpClient m_commonsClient;
    
    /**
     * Constructs a new Apache Commons HTTP client.
     */
    public CommonsHttpClientImpl()
        {
        m_commonsClient = new DefaultHttpClientImpl();
        }
  
    /**
     * Constructs a new Apache Commons HTTP client.
     * 
     * @param connectionManager The Commons connection manager used to manage 
     * connections used by the underlying Commons HTTP client.
     */
    public CommonsHttpClientImpl(final HttpConnectionManager connectionManager)
        {
        m_commonsClient = new DefaultHttpClientImpl(connectionManager);
        }
    
    /**
     * {@inheritDoc}
     */
    public int executeMethod(final HttpMethod method)
        {
        try
            {
            return m_commonsClient.executeMethod(method);
            }
        catch(final HttpException e)
            {
            m_log.debug("HttpException executing method", e);
            throw new RuntimeHttpException("Exception executing method", e);
            }
        catch(final IOException e)
            {
            m_log.debug("IOException executing method", e);
            throw new RuntimeIoException("Exception executing method", e);
            }
        }

    /**
     * {@inheritDoc}
     */
    public HttpClientParams getParams ()
        {
        return m_commonsClient.getParams();
        }

    /**
     * {@inheritDoc}
     */
    public HttpConnectionManager getHttpConnectionManager ()
        {
        return m_commonsClient.getHttpConnectionManager();
        }
    }
