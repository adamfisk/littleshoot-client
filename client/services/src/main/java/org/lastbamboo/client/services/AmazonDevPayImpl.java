package org.lastbamboo.client.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.littleshoot.util.UriUtils;
import org.littleshoot.util.xml.XPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Implementation of interface to Amazon DevPay.
 */
public class AmazonDevPayImpl implements AmazonDevPay
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    public JSONObject activate(final String activationKey)
        {
        final String baseUrl = "https://ls.amazonaws.com";
        final Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("Action", "ActivateDesktopProduct");
        paramMap.put("ActivationKey", activationKey);
        paramMap.put("ProductToken", "");
        paramMap.put("Version", "2008-04-28");
        
        final String url = UriUtils.newUrl(baseUrl, paramMap);
        final JSONObject json = new JSONObject();
        final HttpClient client = new HttpClient();
        final GetMethod method = new GetMethod(url);
        
        String responseBody = "";
        try
            {
            final int statusCode = client.executeMethod(method);
            
            responseBody = method.getResponseBodyAsString(10000);
            System.out.println(responseBody);
            if (statusCode != 200)
                {
                JsonUtils.put(json, "success", false);
                }
            else
                {
                parseResponse(json, responseBody);
                JsonUtils.put(json, "success", true);
                JsonUtils.put(json, "message", "nice work.");
                }
            }
        catch (final HttpException e)
            {
            m_log.error("Error reading response: "+responseBody, e);
            JsonUtils.put(json, "success", false);
            }
        catch (final IOException e)
            {
            m_log.error("Error reading response: "+responseBody, e);
            JsonUtils.put(json, "success", false);
            }
        catch (XPathExpressionException e)
            {
            m_log.error("Error reading response: "+responseBody, e);
            JsonUtils.put(json, "success", false);
            }
        catch (SAXException e)
            {
            m_log.error("Error reading response: "+responseBody, e);
            JsonUtils.put(json, "success", false);
            }
        finally
            {
            method.releaseConnection();
            }
        
        return json;
        }

    public AmazonDevPayData parseResponse(final JSONObject json, final String response) 
        throws SAXException, IOException, XPathExpressionException
        {
        final XPathUtils xpath = XPathUtils.newXPath(response);
        final String accessKeyPath = 
            "/ActivateDesktopProductResponse/ActivateDesktopProductResult/AWSAccessKeyId";
        final String awsAccessKey = xpath.getString(accessKeyPath);
        final String secretAccessKeyPath = 
            "/ActivateDesktopProductResponse/ActivateDesktopProductResult/SecretAccessKey";
        final String secretAccessKey = xpath.getString(secretAccessKeyPath);
        final String userTokenPath = 
            "/ActivateDesktopProductResponse/ActivateDesktopProductResult/UserToken"; 
        final String userToken = xpath.getString(userTokenPath);
        

        final AmazonDevPayData data = 
            new AmazonDevPayData(awsAccessKey, secretAccessKey, userToken);
        //JsonUtils.put(json, "userToken", userToken);
        
        return data;
        }
    }
