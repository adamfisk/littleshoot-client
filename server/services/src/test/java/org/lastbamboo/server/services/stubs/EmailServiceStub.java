package org.lastbamboo.server.services.stubs;

import java.util.Map;

import org.lastbamboo.server.services.EmailService;

public class EmailServiceStub implements EmailService
    {

    private final boolean m_toReturn;

    public EmailServiceStub(boolean toReturn)
        {
        this.m_toReturn = toReturn;
        }

    public boolean sendEmail(String address, String subject,
            Map<String, String> map, String templateName)
        {
        return this.m_toReturn;
        }

    }
