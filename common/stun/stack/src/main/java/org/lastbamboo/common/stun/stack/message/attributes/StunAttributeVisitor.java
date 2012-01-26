package org.lastbamboo.common.stun.stack.message.attributes;

import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControlledAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControllingAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IcePriorityAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceUseCandidateAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatusAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.DataAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RelayAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RemoteAddressAttribute;


/**
 * Visits STUN attributes.
 */
public interface StunAttributeVisitor
    {

    /**
     * Visits the MAPPED ADDRESS attribute.
     * 
     * @param address The MAPPED ADDRESS.
     */
    void visitMappedAddress(MappedAddressAttribute address);

    /**
     * Visits the TURN RELAY ADDRESS attribute.
     * 
     * @param address The RELAY ADDRESS.
     */
    void visitRelayAddress(RelayAddressAttribute address);

    /**
     * Visits the TURN DATA attribute.
     * 
     * @param data The DATA attribute.
     */
    void visitData(DataAttribute data);

    /**
     * Visits the TURN REMOTE ADDRESS attribute.
     * 
     * @param address The TURN REMOTE ADDRESS attribute.
     */
    void visitRemoteAddress(RemoteAddressAttribute address);

    /**
     * Visits the connection status attribute.
     * 
     * @param attribute The connection status attribute.
     */
    void visitConnectionStatus(ConnectionStatusAttribute attribute);

    /**
     * Visits the priority attribute.
     * 
     * @param attribute The priority attribute.
     */
    void visitIcePriority(IcePriorityAttribute attribute);

    /**
     * Visits the ICE USE-CANDIDATE attribute.
     * 
     * @param attribute The attribute.
     */
    void visitIceUseCandidate(IceUseCandidateAttribute attribute);

    /**
     * Visits the ICE controlled attribute.
     * 
     * @param attribute The ICE controlled attribute.
     */
    void visitIceControlled(IceControlledAttribute attribute);

    /**
     * Visits the ICE controlling attribute.
     * 
     * @param attribute The ICE controlling attribute.
     */
    void visitIceControlling(IceControllingAttribute attribute);

    /**
     * Visits the STUN ERROR-CODE attribute.
     * 
     * @param attribute The STUN ERROR-CODE attribute.
     */
    void visiteErrorCode(ErrorCodeAttribute attribute);

    }
