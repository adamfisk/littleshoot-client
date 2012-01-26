package org.lastbamboo.server.services.command.validators;

import org.lastbamboo.server.services.command.KeyCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for key requests.
 */
public class KeyValidator implements Validator
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public boolean supports(final Class clazz)
        {
        return KeyCommand.class.isAssignableFrom(clazz);
        }

    public void validate(final Object obj, final Errors errors)
        {
        m_log.debug("Validating file publish attempt...");
        ValidationUtils.rejectIfEmpty(errors, "keyId", "keyId.empty", 
            "No key");
        m_log.debug("Request validation complete.");
        }

    }
