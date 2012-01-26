package org.lastbamboo.server.services;

import java.util.Map;

/**
 * Service for dealing with e-mails.
 */
public interface EmailService
    {

    /**
     * Sends an e-mail.
     * 
     * @param address The address to send to.
     * @param subject The subject of the e-mail.
     * @param map The mail of properties for the template.
     * @param templateName The Velocity template name.
     * @return <code>true</code> if the e-mail was sent successfully, otherwise
     * <code>false</code>.
     */
    boolean sendEmail(String address, String subject, Map<String, String> map, 
        String templateName);

    }
