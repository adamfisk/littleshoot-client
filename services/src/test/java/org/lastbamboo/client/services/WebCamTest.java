package org.lastbamboo.client.services;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;


public class WebCamTest {

    @Test public void testWebCam() throws Exception {
        final HttpClient client = new HttpClient();
        final PostMethod method = 
            new PostMethod("http://127.0.0.1:8107/api/client/webcam?uri=7893137");
        client.executeMethod(method);
        final String body = method.getResponseBodyAsString();
        System.out.println(body);
        Thread.sleep(10000);
    }
}
