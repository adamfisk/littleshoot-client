package org.lastbamboo.server.services;

import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * A {@link MimeMessagePreparator} for sending LittleShoot messages.
 */
public class DefaultMimeMessagePreparator //implements MimeMessagePreparator
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final String m_toAddress;

    private final String m_subject;

    private VelocityService m_velocityService;

    private final Map<String, String> m_velocityMap;

    private final String m_templateName;
    
    /**
     * Creates a new {@link MimeMessagePreparator}.
     * 
     * @param velocityEngine Templating engine.
     * @param toAddress The address to send the message to.
     * @param subject The subject of the message.
     * @param velocityMap The map of properties to replace in the template.
     * @param templateName The name of the template to use.
     */
    public DefaultMimeMessagePreparator(final VelocityEngine velocityEngine, 
        final String toAddress, final String subject, final Map<String, String> velocityMap,
        final String templateName)
        {
        this.m_toAddress = toAddress;
        this.m_subject = subject;
        this.m_velocityMap = velocityMap;
        this.m_templateName = templateName;
        this.m_velocityService = new VelocityService(velocityEngine);
        }
    
    public void prepare(final MimeMessage mimeMessage) throws Exception 
        {
        m_log.debug("Sending...");
        /*
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
        message.setTo(this.m_toAddress);
        message.setFrom("a@lastbamboo.org");
        message.setSubject(this.m_subject);
        final String text = 
            this.m_velocityService.createString(this.m_velocityMap, this.m_templateName);
        message.setText(text, true);
        */
        }

    }
