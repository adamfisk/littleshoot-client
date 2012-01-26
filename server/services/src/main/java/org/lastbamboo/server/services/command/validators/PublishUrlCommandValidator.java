package org.lastbamboo.server.services.command.validators;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.server.services.command.PublishUrlCommand;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates the fields of a publish request.
 */
public final class PublishUrlCommandValidator implements Validator
    {

    /**
     * Logger for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger(PublishUrlCommandValidator.class);
    
    public boolean supports(final Class clazz)
        {
        return PublishUrlCommand.class.isAssignableFrom(clazz);
        }

    public void validate(final Object obj, final Errors errors)
        {
        LOG.trace("Validating publish request...errors: "+errors);
        final PublishUrlCommand command = (PublishUrlCommand) obj;
        
        final URI url = command.getUrl();
        if (url == null)
            {
            LOG.warn("Null URL in command..");
            errors.rejectValue("url", "URL is null");
            return;
            }
        
        final String scheme = url.getScheme();
        if (StringUtils.isBlank(scheme))
            {
            LOG.warn("Rejecting URL: "+url);
            errors.rejectValue("url", "Bad URL: "+url);
            }
        else if (!scheme.equals("http"))
            {
            LOG.warn("Rejecting URL -- not HTTP: "+url);
            errors.rejectValue("url", "Could not read URL: "+url);
            }
        }
    }
