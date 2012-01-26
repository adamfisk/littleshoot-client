package org.lastbamboo.integration.tests.stubs;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.math.LongRange;
import org.lastbamboo.common.http.client.HttpListener;

public class HttpListenerStub implements HttpListener
    {

    public void onBadHeader(String header)
        {
        // TODO Auto-generated method stub

        }

    public void onConnect(long ms)
        {
        // TODO Auto-generated method stub

        }

    public void onContentLength(long contentLength)
        {
        // TODO Auto-generated method stub

        }

    public void onContentRange(LongRange range)
        {
        // TODO Auto-generated method stub

        }

    public void onCouldNotConnect()
        {
        // TODO Auto-generated method stub

        }

    public void onDownloadStarted()
        {
        // TODO Auto-generated method stub

        }

    public void onHttpException(HttpException httpException)
        {
        // TODO Auto-generated method stub

        }

    public void onMessageBodyRead()
        {
        // TODO Auto-generated method stub

        }

    public void onNoTwoHundredOk(int responseCode)
        {
        // TODO Auto-generated method stub

        }

    public void onStatusEvent(String status)
        {
        // TODO Auto-generated method stub

        }

    public void onBytesRead(int bytesRead)
        {
        // TODO Auto-generated method stub

        }

    public void onFailure()
        {
        // TODO Auto-generated method stub
        
        }

    public void onPermanentFailure()
        {
        // TODO Auto-generated method stub
        
        }

    }
