package org.lastbamboo.server.services.command.validators;

import org.lastbamboo.server.services.command.PublishFileCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for published files.
 */
public class PublishFileValidator implements Validator
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public boolean supports(final Class clazz)
        {
        return PublishFileCommand.class.isAssignableFrom(clazz);
        }

    public void validate(final Object obj, final Errors errors)
        {
        m_log.debug("Validating file publish attempt...");
        ValidationUtils.rejectIfEmpty(errors, "signature", "signature.empty", 
            "No signature");
        ValidationUtils.rejectIfEmpty(errors, "name", "name.empty", "No name");
        //ValidationUtils.rejectIfEmpty(errors, "userId", "userId.invalid", 
          //  "No user ID");
        ValidationUtils.rejectIfEmpty(errors, "instanceId", 
            "instanceId.invalid", "No instance ID");
        ValidationUtils.rejectIfEmpty(errors, "sha1", "sha1.empty", 
            "No SHA-1");
        m_log.debug("Request passed validation.");
        }

    }
