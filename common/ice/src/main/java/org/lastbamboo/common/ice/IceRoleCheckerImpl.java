package org.lastbamboo.common.ice;

import java.math.BigInteger;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.BindingErrorResponse;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControlledAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControllingAttribute;
import org.littleshoot.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that verifies ICE roles, as specified in  ICE section 
 * 7.2.1.1.  "Detecting and Repairing Role Conflicts" 
 */
public class IceRoleCheckerImpl implements IceRoleChecker
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public BindingErrorResponse checkAndRepairRoles(
        final BindingRequest request, final IceAgent agent, 
        final IoSession ioSession)
        {
        m_log.debug("Checking roles...");
        final Map<StunAttributeType, StunAttribute> remoteAttributes = 
            request.getAttributes();
        if (!remoteAttributes.containsKey(StunAttributeType.ICE_CONTROLLED) &&
            !remoteAttributes.containsKey(StunAttributeType.ICE_CONTROLLING))
            {
            m_log.warn("No control information.  Old ICE implementation?");
            // The agent may have implemented a previous version of ICE.
            // We have no way of detecting a role conflict if there is one.
            return null;
            }
        
        // Resolve the conflict if we think we're the controlling agent but
        // the remote host indicated in the request they also think they're 
        // controlling.
        else if (agent.isControlling() &&
            remoteAttributes.containsKey(StunAttributeType.ICE_CONTROLLING))
            {
            m_log.warn("We both think we're controlling...");
            final IceControllingAttribute attribute = 
                (IceControllingAttribute) remoteAttributes.get(
                    StunAttributeType.ICE_CONTROLLING);
            if (weWin(agent, new BigInteger(attribute.getTieBreaker())))
                {
                // We retain our role and send an error response.
                return createErrorResponse(request);
                }
            else
                {
                agent.setControlling(false);
                agent.recomputePairPriorities();
                }
            }
        
        // Otherwise, handle the case where we both think we're controlled.
        else if (!agent.isControlling() && 
            remoteAttributes.containsKey(StunAttributeType.ICE_CONTROLLED))
            {
            m_log.warn("We both think we're controlled...");
            m_log.debug("Transaction ID: {}", request.getTransactionId());
            final IceControlledAttribute attribute = 
                (IceControlledAttribute) remoteAttributes.get(
                    StunAttributeType.ICE_CONTROLLED);
            if (weWin(agent, new BigInteger(attribute.getTieBreaker())))
                {
                agent.setControlling(true);
                agent.recomputePairPriorities();
                }
            else
                {
                // We retain our role and send an error response.
                return createErrorResponse(request);
                }
            }
        
        // Otherwise, there was no conflict.  This is the normal case.
        return null;
        }

    private BindingErrorResponse createErrorResponse(
        final BindingRequest request)
        {
        // We need to send a Binding Error Response with a 
        // 487 Role Conflict ERROR CODE attribute.
        final UUID transactionId = request.getTransactionId();
        final BindingErrorResponse errorResponse = 
            new BindingErrorResponse(transactionId, 487, 
                "Role Conflict");
        return errorResponse;
        }

    private boolean weWin(final IceAgent agent, 
        final BigInteger remoteTieBreaker)
        {
        final BigInteger localTieBreaker = 
            new BigInteger(agent.getTieBreaker().toByteArray());
        return NumberUtils.isBiggerOrEqual(localTieBreaker, remoteTieBreaker);
        }

    }
