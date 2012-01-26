package org.lastbamboo.common.bug.server.processors;

import java.io.InputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * Bug processor that sends e-mail to the appropriate recipients.
 */
public class MailBugProcessor implements BugProcessor
    {

    private final JavaMailSender m_mailSender;

    /**
     * Creates a new processor for sending e-mail about the bug.
     * 
     * @param mailSender The class that sends the e-mails.
     */
    public MailBugProcessor(final JavaMailSender mailSender)
        {
        this.m_mailSender = mailSender;
        }
    
    public void processBug(final InputStream is,
        final HttpServletRequest request)
        {
        final StringBuffer sb = new StringBuffer();
        /*
        final Set entries = bugs.entrySet();
        for (final Iterator iter = entries.iterator(); iter.hasNext();)
            {
            final Map.Entry entry = (Map.Entry) iter.next();
            sb.append(entry.getKey());
            sb.append("=");
            final String[] value = (String[]) entry.getValue();
            for (int i = 0; i < value.length; i++)
                {
                sb.append(value[i]);
                sb.append(", ");
                }
            sb.append("\n");
            }
        
        final String body = sb.toString();
        
        final String[] version = (String[]) bugs.get("version");
        if (version != null)
            {
            sendBody(body, "LittleShoot "+version[0]+" Bug");
            }
        else 
            {
            sendBody(body, "LittleShoot Unknown Version Bug");
            }
            */
        }

    private void sendBody(final String body, final String subject)
        {
        final MimeMessagePreparator preparator = new MimeMessagePreparator() 
            {
            public void prepare(final MimeMessage mimeMessage) 
                throws MessagingException 
                {
                mimeMessage.setRecipient(Message.RecipientType.TO, 
                    new InternetAddress("a@lastbamboo.org"));
                mimeMessage.addRecipient(Message.RecipientType.TO, 
                    new InternetAddress("j@lastbamboo.org"));
                mimeMessage.setFrom(new InternetAddress("bugs@lastbamboo.org"));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(body);
                }
            };

        this.m_mailSender.send(preparator);
        try
            {
            this.m_mailSender.send(preparator);
            System.out.println("\n\n\nSent mail!!!!\n");
            System.out.println(body);
            }
        catch (final MailException e) 
            {
            //log it and go on
            System.err.println("Could not send mail:"+e.getMessage());            
            }
        }
    }
