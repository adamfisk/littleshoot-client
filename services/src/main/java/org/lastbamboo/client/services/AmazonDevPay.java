package org.lastbamboo.client.services;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.json.JSONObject;
import org.xml.sax.SAXException;

/**
 * Interface for interacting with Amazon DevPay.
 */
public interface AmazonDevPay
    {

    JSONObject activate(String activationKey);

    AmazonDevPayData parseResponse(JSONObject json, String response) 
        throws SAXException, IOException, XPathExpressionException;

    }
