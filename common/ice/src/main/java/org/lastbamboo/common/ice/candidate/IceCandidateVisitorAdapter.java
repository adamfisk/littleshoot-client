package org.lastbamboo.common.ice.candidate;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adaptor for {@link IceCandidateVisitor}s.
 * 
 * @param <T> The class to return from visit methods.
 */
public abstract class IceCandidateVisitorAdapter<T> 
    implements IceCandidateVisitor<T>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final boolean m_warn;
    
    /**
     * Creates a new adaptor with the default log warning levels.
     */
    public IceCandidateVisitorAdapter()
        {
        m_warn = true;
        }

    /**
     * Creates a new adaptor with the specified log warning level when we
     * visit methods that are no overridden.
     * 
     * @param warn Whether or not to issue warning logs when methods are used
     * that are not overridden.
     */
    public IceCandidateVisitorAdapter(final boolean warn)
        {
        m_warn = warn;
        }

    public void visitCandidates(final Collection<IceCandidate> candidates)
        {
        if (this.m_warn)
            {
            m_log.warn("Not handling visit all candidates");
            }
        }

    public T visitTcpActiveCandidate(final IceTcpActiveCandidate candidate)
        {
        m_log.info("Visiting unhandled candidate: {}", candidate);
        return null;
        }

    public T visitTcpHostPassiveCandidate(
        final IceTcpHostPassiveCandidate candidate)
        {
        if (this.m_warn)
            {
            m_log.info("Visiting unhandled candidate: {}", candidate);
            }
        else
            {
            m_log.debug("Visiting unhandled candidate: {}", candidate);
            }
        return null;
        }

    public T visitTcpRelayPassiveCandidate(
        final IceTcpRelayPassiveCandidate candidate)
        {
        if (this.m_warn)
            {
            m_log.info("Visiting unhandled candidate: {}", candidate);
            }
        else
            {
            m_log.debug("Visiting unhandled candidate: {}", candidate);
            }
        return null;
        }

    public T visitTcpServerReflexiveSoCandidate(
        final IceTcpServerReflexiveSoCandidate candidate)
        {
        if (this.m_warn)
            {
            m_log.info("Visiting unhandled candidate: {}", candidate);
            }
        else
            {
            m_log.debug("Visiting unhandled candidate: {}", candidate);
            }
        return null;
        }
    
    public T visitTcpPeerReflexiveCandidate(
        final IceTcpPeerReflexiveCandidate candidate)
        {
        if (this.m_warn)
            {
            m_log.info("Visiting unhandled candidate: {}", candidate);
            }
        else
            {
            m_log.debug("Visiting unhandled candidate: {}", candidate);
            }
        return null;
        }

    public T visitUdpHostCandidate(final IceUdpHostCandidate candidate)
        {
        if (this.m_warn)
            {
            m_log.info("Visiting unhandled candidate: {}", candidate);
            }
        else
            {
            m_log.debug("Visiting unhandled candidate: {}", candidate);
            }
        return null;
        }

    public T visitUdpPeerReflexiveCandidate(
        final IceUdpPeerReflexiveCandidate candidate)
        {
        if (this.m_warn)
            {
            m_log.info("Visiting unhandled candidate: {}", candidate);
            }
        else
            {
            m_log.debug("Visiting unhandled candidate: {}", candidate);
            }
        return null;
        }

    public T visitUdpRelayCandidate(final IceUdpRelayCandidate candidate)
        {
        if (this.m_warn)
            {
            m_log.info("Visiting unhandled candidate: {}", candidate);
            }
        else
            {
            m_log.debug("Visiting unhandled candidate: {}", candidate);
            }
        return null;
        }

    public T visitUdpServerReflexiveCandidate(
        final IceUdpServerReflexiveCandidate candidate)
        {
        if (this.m_warn)
            {
            m_log.info("Visiting unhandled candidate: {}", candidate);
            }
        else
            {
            m_log.debug("Visiting unhandled candidate: {}", candidate);
            }
        return null;
        }

    }
