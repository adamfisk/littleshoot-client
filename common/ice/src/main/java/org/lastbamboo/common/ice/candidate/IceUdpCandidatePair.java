package org.lastbamboo.common.ice.candidate;

import java.net.InetSocketAddress;

import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.ice.IceStunChecker;
import org.lastbamboo.common.ice.IceStunCheckerFactory;
import org.lastbamboo.common.ice.transport.IceConnector;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.CanceledStunMessage;
import org.lastbamboo.common.stun.stack.message.ConnectErrorStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.turn.client.TurnStunMessageMapper;
import org.littleshoot.util.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A UDP ICE candidate pair. 
 */
public class IceUdpCandidatePair implements IceCandidatePair
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final IceCandidate m_localCandidate;
    private final IceCandidate m_remoteCandidate;
    private volatile long m_priority;
    private volatile IceCandidatePairState m_state;
    private final String m_foundation;
    private final int m_componentId;
    private volatile boolean m_nominated = false;
    private volatile IceStunChecker m_currentStunChecker;
    
    /**
     * Flag indicating whether or not this pair should include the 
     * USE CANDIDATE attribute in its Binding Requests during checks.
     */
    private volatile boolean m_useCandidate = false;

    protected volatile IoSession m_ioSession;

    private final IceStunCheckerFactory m_stunCheckerFactory;

    private final IceConnector m_iceConnector;

    private boolean m_turnPair;

    private volatile boolean m_transactionCanceled = false;

    private boolean m_nominateOnSuccess;
    
    
    /**
     * Creates a new pair without an existing connection between the endpoints.
     * 
     * @param localCandidate The local candidate.
     * @param remoteCandidate The candidate from the remote agent.
     * @param stunCheckerFactory The class for creating new STUN checkers.
     * @param iceConnector The class for creating a connection between the 
     * candidates.
     */
    public IceUdpCandidatePair(final IceCandidate localCandidate, 
        final IceCandidate remoteCandidate, 
        final IceStunCheckerFactory stunCheckerFactory,
        final IceConnector iceConnector)
        {
        this(localCandidate, remoteCandidate, 
            IceCandidatePairPriorityCalculator.calculatePriority(
                localCandidate, remoteCandidate), 
            null, stunCheckerFactory, iceConnector);
        }
    
    /**
     * Creates a new pair.
     * 
     * @param localCandidate The local candidate.
     * @param remoteCandidate The candidate from the remote agent.
     * @param ioSession The {@link IoSession} connecting to the two endpoints.
     * @param stunCheckerFactory The class for creating new STUN checkers.
     */
    public IceUdpCandidatePair(final IceCandidate localCandidate, 
        final IceCandidate remoteCandidate, final IoSession ioSession,
        final IceStunCheckerFactory stunCheckerFactory)
        {
        this(localCandidate, remoteCandidate, 
            IceCandidatePairPriorityCalculator.calculatePriority(
                localCandidate, remoteCandidate),
            ioSession, stunCheckerFactory, null);
        }
    
    /**
     * Creates a new pair.
     * 
     * @param localCandidate The local candidate.
     * @param remoteCandidate The candidate from the remote agent.
     * @param priority The priority of the pair.
     * @param ioSession The {@link IoSession} connecting to the two endpoints.
     */
    private IceUdpCandidatePair(final IceCandidate localCandidate, 
        final IceCandidate remoteCandidate, final long priority,
        final IoSession ioSession, 
        final IceStunCheckerFactory stunCheckerFactory,
        final IceConnector iceConnector)
        {
        m_localCandidate = localCandidate;
        m_remoteCandidate = remoteCandidate;
        m_ioSession = ioSession;
        
        // Note both candidates always have the same component ID, so we just
        // choose one for the pair.
        m_componentId = localCandidate.getComponentId();
        m_priority = priority;
        m_state = IceCandidatePairState.FROZEN;
        m_foundation = String.valueOf(localCandidate.getFoundation()) + 
            String.valueOf(remoteCandidate.getFoundation());
        this.m_stunCheckerFactory = stunCheckerFactory;
        this.m_iceConnector = iceConnector;
        }
    

    public StunMessage check(final BindingRequest request, final long rto)
        {
        // We set the state here instead of in the check list scheduler
        // because it's more precise and because the scheduler doesn't perform
        // a subsequent check until after this check has executed, so it won't
        // think a pair is waiting for frozen twice in a row (as opposed to
        // in progress).
        setState(IceCandidatePairState.IN_PROGRESS);
        
        final InetSocketAddress remoteAddress = 
            this.m_remoteCandidate.getSocketAddress();
        if (this.m_ioSession == null)
            {
            if (!(this.m_localCandidate instanceof IceTcpActiveCandidate) &&
                !this.m_localCandidate.isUdp())
                {
                m_log.error("Connecting with non-active TCP candidate: {}",
                    this.m_localCandidate);
                throw new IllegalStateException(
                    "Local candidate is not active: "+this.m_localCandidate);
                }
            final InetSocketAddress localAddress = 
                this.m_localCandidate.getSocketAddress();
            
            this.m_ioSession = 
                this.m_iceConnector.connect(localAddress, remoteAddress);
            }

        if (this.m_ioSession == null)
            {
            // This will be the case if the connection attempt simply failed.
            m_log.debug("Could not connect to the remote host: {}", 
                this.m_remoteCandidate.getSocketAddress());
            return new ConnectErrorStunMessage();
            }
        
        // We need to handle TURN here.  If this is running over a TURN
        // connection, we don't have anything telling the Data Indication
        // encoder the REMOTE-ADDRESS to send this request to.  The remote
        // address is the remote candidate of the pair, and we record that here.
        final TurnStunMessageMapper mapper =
            (TurnStunMessageMapper) this.m_ioSession.getAttribute(
                "REMOTE_ADDRESS_MAP");
        if (mapper != null)
            {
            m_log.debug("Mapping Binding Request to the REMOTE-ADDRESS for " +
                "this pair for use in the Send Indication");
            mapper.mapMessage(request, remoteAddress);
            m_turnPair = true;
            }
        
        m_log.debug("Creating new STUN checker...");
        this.m_currentStunChecker = 
            this.m_stunCheckerFactory.newChecker(this.m_ioSession);
        
        // This check is necessary because it's possible for the transaction
        // to be canceled before the STUN checker has been constructed.
        if (!this.m_transactionCanceled)
            {
            m_log.debug("Writing request...");
            return this.m_currentStunChecker.write(request, rto);
            }
        else
            {
            // A single cancellation works for only one transaction, so reset
            // the canceled state here to false.
            m_log.debug("The transaction was canceled...");
            this.m_transactionCanceled = false;
            return new CanceledStunMessage();
            }
        }
    
    public void useCandidate()
        {
        this.m_useCandidate = true;
        }
    
    public boolean useCandidateSet()
        {
        return this.m_useCandidate;
        }
    
    public void cancelStunTransaction()
        {
        this.m_transactionCanceled = true;
        if (this.m_currentStunChecker != null)
            {
            this.m_currentStunChecker.cancelTransaction();
            // Reset cancellation state -- our work is done.
            this.m_transactionCanceled = false;
            }
        }
    
    public void recomputePriority()
        {
        this.m_priority = IceCandidatePairPriorityCalculator.calculatePriority(
            this.m_localCandidate, this.m_remoteCandidate);
        }

    public IceCandidate getLocalCandidate()
        {
        return m_localCandidate;
        }

    public IceCandidate getRemoteCandidate()
        {
        return m_remoteCandidate;
        }

    public long getPriority()
        {
        return this.m_priority;
        }
    
    public IceCandidatePairState getState()
        {
        return this.m_state;
        }
    
    public String getFoundation()
        {
        return this.m_foundation;
        }
    
    public void setState(final IceCandidatePairState state)
        {
        if (this.m_nominated)
            {
            m_log.debug("Trying to change the state of a nominated pair to: {}", 
                state);
            }
        this.m_state = state;
        if (state == IceCandidatePairState.FAILED)
            {
            m_log.debug("Setting state to failed, closing checker");
            close();
            }
        }
    
    public void setIoSession(final IoSession session)
        {
        if (this.m_ioSession != null)
            {
            m_log.warn("Ignoring set session because it already exists!!");
            return;
            }
        this.m_ioSession = session;
        }
    
    public int getComponentId()
        {
        return m_componentId;
        }
    
    public void nominate()
        {
        this.m_nominated = true;
        }
    
    public boolean isNominated()
        {
        return this.m_nominated;
        }
    
    public boolean isTcp()
        {
        return false;
        }
    
    public void nominateOnSuccess() 
        {
        this.m_nominateOnSuccess = true;
        }
    
    public boolean isNominateOnSuccess() 
        {
        return m_nominateOnSuccess;
        }
    
    public void close()
        {
        m_log.debug("Closing pair:{}", this);
        if (this.m_currentStunChecker !=  null)
            {
            this.m_currentStunChecker.close();
            }
        }
    
    public IoSession getIoSession()
        {
        return this.m_ioSession;
        }
    
    @Override
    public String toString()
        {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("local:               ");
        sb.append(this.m_localCandidate);
        sb.append("\n");
        sb.append("remote:              ");
        sb.append(this.m_remoteCandidate);
        sb.append("\n");
        sb.append("state:               ");
        sb.append(this.m_state);
        sb.append("\n");
        sb.append("use-candidate:       ");
        sb.append(this.m_useCandidate);
        sb.append("\n");
        sb.append("nominated:           ");
        sb.append(this.m_nominated);
        return sb.toString();
        }


    
    @Override
    public int hashCode()
        {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + m_componentId;
        result = PRIME * result + ((m_foundation == null) ? 0 : m_foundation.hashCode());
        result = PRIME * result + ((m_localCandidate == null) ? 0 : m_localCandidate.hashCode());
        result = PRIME * result + (int) (m_priority ^ (m_priority >>> 32));
        result = PRIME * result + ((m_remoteCandidate == null) ? 0 : m_remoteCandidate.hashCode());
        return result;
        }

    @Override
    public boolean equals(Object obj)
        {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final IceUdpCandidatePair other = (IceUdpCandidatePair) obj;
        if (m_componentId != other.m_componentId)
            return false;
        if (m_foundation == null)
            {
            if (other.m_foundation != null)
                return false;
            }
        else if (!m_foundation.equals(other.m_foundation))
            return false;
        if (m_localCandidate == null)
            {
            if (other.m_localCandidate != null)
                return false;
            }
        else if (!m_localCandidate.equals(other.m_localCandidate))
            return false;
        if (m_priority != other.m_priority)
            return false;
        if (m_remoteCandidate == null)
            {
            if (other.m_remoteCandidate != null)
                return false;
            }
        else if (!m_remoteCandidate.equals(other.m_remoteCandidate))
            return false;
        return true;
        }

    public int compareTo(final IceCandidatePair other)
        {
        final Long priority1 = Long.valueOf(m_priority);
        final Long priority2 = Long.valueOf(other.getPriority());
        final int priorityComparison = priority1.compareTo(priority2);
        if (priorityComparison != 0)
            {
            // We reverse this because we want to go from highest to lowest.
            return -priorityComparison;
            }

        if (this.equals(other))
            {
            return 0;
            }
        
        // If our remote candidate has a public address, it should be use 
        // ahead of a pair without a public address for the remote candidate.
        // If both pairs have remote candidates with public addresses, it 
        // doesn't matter -- we can use either one.
        if (NetworkUtils.isPublicAddress(
            m_remoteCandidate.getSocketAddress().getAddress()))
            {
            return -1;
            }
        
        else if (NetworkUtils.isPublicAddress(
            other.getRemoteCandidate().getSocketAddress().getAddress()))
            {
            return 1;
            }
        
        // Just use the other one -- it could have a public address for the
        // remote candidate or not.
        return -1;
        }

    public boolean isTurnPair()
        {
        return m_turnPair;
        }

    public <T> T accept(final IceCandidatePairVisitor<T> visitor)
        {
        return visitor.visitUdpIceCandidatePair(this);
        }

    }
