package org.lastbamboo.client.services;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.junit.Test;


/**
 * DevPay test.
 */
public class AmazonDevPayImplTest
    {

    public void testDevPay() throws Exception
        {
        final AmazonDevPay devpay = new AmazonDevPayImpl();
        
        final JSONObject response = devpay.activate("bogusKey");
        
        final boolean success = response.getBoolean("success");
        assertTrue(!success);
        }
    
    @Test public void testResponseParsing() throws Exception
        {
        final AmazonDevPay devpay = new AmazonDevPayImpl();
        final String expectedUserToken = "{UserToken}AAAHVXNlclRrbgfOpSykBAXO7g/zG";
        final String response = 
            "<ActivateDesktopProductResponse>\n"+
               "<ActivateDesktopProductResult>\n"+
                  "<UserToken>\n"+
                     expectedUserToken+
                  "</UserToken>\n"+
                  "<AWSAccessKeyId>\n"+
                     "YSJ4L29BRHP0V8J4A25\n"+
                  "</AWSAccessKeyId>\n"+
                  "<SecretAccessKey>\n"+
                     "kUdrlXJtn55OI/L3EHMGD/aVmHEXAMPLEKEY\n"+
                  "</SecretAccessKey>\n"+
               "</ActivateDesktopProductResult>\n"+
               "<ResponseMetadata>\n"+
                  "<RequestId>\n"+
                     "cb919c0a-9bce-4afe-9b48-9bdf2412bb67\n"+
                  "</RequestId>\n"+
               "</ResponseMetadata>\n"+
            "</ActivateDesktopProductResponse>";
        
        final JSONObject json = new JSONObject();
        final AmazonDevPayData jsonResponse = 
            devpay.parseResponse(json, response);
        
        final String userToken = jsonResponse.getUserToken();
        System.out.println(userToken);
        final String diff = StringUtils.difference(expectedUserToken, userToken);
        //System.out.println(diff);
        //assertEquals("Unexpected user token", expectedUserToken, userToken);
        }
    }
