package org.lastbamboo.common.ice;

import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.BindingErrorResponse;
import org.lastbamboo.common.stun.stack.message.BindingRequest;

/**
 * Class that checks ICE roles.
 */
public interface IceRoleChecker
    {

    /**
     * Checks to make sure controlling roles are as expected for incoming
     * requests and repairs them with a Binding Error Response if not.
     * 
     * @param request The incoming Binding Request.
     * @param agent The ICE agent.
     * @param ioSession The session the request arrived on. 
     * @return A {@link BindingErrorResponse}, or <code>null</code> if no
     * roles need repairing.
     */
    BindingErrorResponse checkAndRepairRoles(BindingRequest request, 
        IceAgent agent, IoSession ioSession);

    }
