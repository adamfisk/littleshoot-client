package org.lastbamboo.server.services;

import org.lastbamboo.common.services.BooleanService;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.services.command.ConfirmNewUserCommand;

/**
 * Service for verifying an e-mail address from a user.
 */
public class ConfirmNewUserService implements BooleanService
    {
    
    private final ResourceRepository m_resourceRepository;

    /**
     * Creates a service for creating new users.
     * 
     * @param rr The class for accessing resources.
     */
    public ConfirmNewUserService(final ResourceRepository rr)
        {
        this.m_resourceRepository = rr;
        }

    public boolean service(final Object obj)
        {
        final ConfirmNewUserCommand command = (ConfirmNewUserCommand) obj;
        return this.m_resourceRepository.confirmNewUser(command.getUserId());
        }

    }
