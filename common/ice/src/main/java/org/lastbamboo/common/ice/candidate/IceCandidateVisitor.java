package org.lastbamboo.common.ice.candidate;

import java.util.Collection;



/**
 * Visitor for connection candidates in the "Interactive Connectivity 
 * Establishment (ICE)" protocol.
 * 
 * @param <T> The class returned by a single visit method.
 */
public interface IceCandidateVisitor<T>
    {
    
    /**
     * Visits the specified <code>Collection</code> of ICE candidates.
     * @param candidates The <code>Collection</code> of candidates to visit.
     */
    void visitCandidates(Collection<IceCandidate> candidates);

    T visitUdpHostCandidate(IceUdpHostCandidate candidate);

    T visitUdpServerReflexiveCandidate(IceUdpServerReflexiveCandidate candidate);

    T visitUdpPeerReflexiveCandidate(IceUdpPeerReflexiveCandidate candidate);

    T visitUdpRelayCandidate(IceUdpRelayCandidate candidate);

    T visitTcpHostPassiveCandidate(IceTcpHostPassiveCandidate candidate);

    T visitTcpRelayPassiveCandidate(IceTcpRelayPassiveCandidate candidate);

    T visitTcpServerReflexiveSoCandidate(IceTcpServerReflexiveSoCandidate candidate);

    T visitTcpActiveCandidate(IceTcpActiveCandidate candidate);

    T visitTcpPeerReflexiveCandidate(IceTcpPeerReflexiveCandidate candidate);

    }
