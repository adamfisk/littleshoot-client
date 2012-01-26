package org.lastbamboo.common.http.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.math.LongRange;
import org.littleshoot.util.WriteListener;

/**
 * Interface for listening to download events, such as setting of the 
 * download content length, new data downloaded, etc.
 */
public interface HttpListener extends WriteListener
    {

    /**
     * Called when the downloader has determined the content length of the 
     * resource.
     * @param contentLength The length of the content in bytes.
     */
    void onContentLength(final long contentLength);

    /**
     * Called when the status of the the download has changed.
     * @param status The status of the download.
     */
    void onStatusEvent(final String status);

    /**
     * Called when the download has started.
     */
    void onDownloadStarted();

    /**
     * Called when we could not connect to the remote host.
     */
    void onCouldNotConnect();

    /**
     * Called when a connection is made to the remote host.
     * 
     * @param ms The number of milliseconds it took to connect.
     */
    void onConnect(final long ms);
    
    /**
     * Called when there's an HTTP exception while connecting to the remote 
     * host.
     * @param httpException The exception from attempting the HTTP connection.
     */
    void onHttpException(final HttpException httpException);

    /**
     * Called when we receive a response code other than 200 OK.
     * @param responseCode The response code received.
     */
    void onNoTwoHundredOk(final int responseCode);
    
    /**
     * Called when the HTTP message body has been read.
     */
    void onMessageBodyRead();

    void onBadHeader(final String header);

    void onContentRange(final LongRange range) throws IOException;

    void onFailure();

    void onPermanentFailure();
    }
