package org.lastbamboo.server.services;

import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * E-mail service implementations.
 */
public class EmailServiceImpl implements EmailService
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final String m_pwd;
    private final String m_userName;

    private final VelocityService m_velocityService;

    /**
     * Creates a new e-mail service.
     * 
     * @param userName The user name to authenticate with.
     * @param pwd The password.
     * @param velocityService The velocity handler.
     */
    public EmailServiceImpl(final String userName, final String pwd, 
        final VelocityService velocityService)
        {
        this.m_userName = userName;
        this.m_pwd = pwd;
        this.m_velocityService = velocityService;
        }

    public boolean sendEmail(final String address, final String subject, 
        final Map<String, String> map, final String templateName)
        {
        final HtmlEmail email = new HtmlEmail();
        email.setHostName("smtp.gmail.com");
        email.setAuthentication(this.m_userName, this.m_pwd);
        email.setSSL(true);
        
        try
            {
            email.addTo(address);
            email.setFrom("support@littleshoot.org");
            email.setSubject(subject);
            final String text = this.m_velocityService.createString(map, templateName);
            email.setHtmlMsg(text);
            //email.setMsg("text body");
            email.send();
            return true;
            }
        catch (final EmailException e)
            {
            m_log.warn("Could not send e-mail to: " + address, e);
            return false;
            }
        catch (final Exception e)
            {
            m_log.warn("Could not send e-mail to: " + address, e);
            return false;
            }
        }
    }
