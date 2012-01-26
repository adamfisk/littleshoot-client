package org.lastbamboo.client.services;

public class AmazonDevPayData
    {

    private final String m_awsAccessKey;
    private final String m_secretAccessKey;
    private final String m_userToken;

    public AmazonDevPayData(final String awsAccessKey, 
        final String secretAccessKey, final String userToken)
        {
        this.m_awsAccessKey = awsAccessKey;
        this.m_secretAccessKey = secretAccessKey;
        this.m_userToken = userToken;
        }

    public String getAwsAccessKey()
        {
        return this.m_awsAccessKey;
        }

    public String getSecretAccessKey()
        {
        return this.m_secretAccessKey;
        }

    public String getUserToken()
        {
        return this.m_userToken;
        }

    }
