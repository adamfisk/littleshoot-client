package org.lastbamboo.server.services.command.validators;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.util.ShootConstants;
import org.lastbamboo.server.services.command.SearchCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for searches.
 */
public class SearchValidator implements Validator
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public boolean supports(final Class clazz)
        {
        return SearchCommand.class.isAssignableFrom(clazz);
        }

    public void validate(final Object obj, final Errors errors)
        {
        m_log.debug("Validating search attempt...");
        ValidationUtils.rejectIfEmpty(errors, "keywords", "keywords.empty", 
            "No keywords to search for");
        
        final SearchCommand command = (SearchCommand) obj;
        final String groupName = command.getGroupName();
        
        // If we're searching in a group, we've got to have a user ID.
        if (!StringUtils.isBlank(groupName) && 
            !groupName.equals(ShootConstants.WORLD_GROUP))
            {
            ValidationUtils.rejectIfEmpty(errors, "userId", "userId.invalid", "Bad user ID");
            }
        
        m_log.debug("Request passed validation.");
        }

    }
