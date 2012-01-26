package org.lastbamboo.integration.tests.stubs;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Queue;

import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoService;
import org.littleshoot.mina.common.IoServiceConfig;
import org.littleshoot.mina.common.IoServiceListener;
import org.littleshoot.mina.common.IoSession;
import org.lastbamboo.common.ice.IceCheckList;
import org.lastbamboo.common.ice.IceCheckListState;
import org.lastbamboo.common.ice.IceCheckScheduler;
import org.lastbamboo.common.ice.IceMediaStream;
import org.lastbamboo.common.ice.IceStunUdpPeer;
import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidatePair;
import org.littleshoot.stun.stack.message.BindingRequest;

public class IceMediaStreamStub implements IceMediaStream
    {

    public void addLocalCandidate(IceCandidate localCandidate)
        {
        // TODO Auto-generated method stub

        }

    public void addPair(IceCandidatePair pair)
        {
        // TODO Auto-generated method stub

        }

    public IceCandidate addRemotePeerReflexive(BindingRequest request,
            InetSocketAddress localAddress, InetSocketAddress remoteAddress,
            boolean isUdp)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void addTriggeredPair(IceCandidatePair pair)
        {
        // TODO Auto-generated method stub

        }

    public void addValidPair(IceCandidatePair pair)
        {
        // TODO Auto-generated method stub

        }

    public byte[] encodeCandidates()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void establishStream(Collection<IceCandidate> remoteCandidates)
        {
        // TODO Auto-generated method stub

        }

    public IceCheckListState getCheckListState()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public IceCandidate getLocalCandidate(InetSocketAddress localAddress,
            boolean isUdp)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Queue<IceCandidatePair> getNominatedPairs()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public IceCandidate getRemoteCandidate(InetSocketAddress remoteAddress,
            boolean isUdp)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Queue<IceCandidatePair> getValidPairs()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public boolean hasHigherPriorityPendingPair(IceCandidatePair pair)
        {
        // TODO Auto-generated method stub
        return false;
        }

    public boolean hasRemoteCandidate(InetSocketAddress remoteAddress,
            boolean isUdp)
        {
        // TODO Auto-generated method stub
        return false;
        }

    public void onNominated(IceCandidatePair pair)
        {
        // TODO Auto-generated method stub

        }

    public void recomputePairPriorities(boolean controlling)
        {
        // TODO Auto-generated method stub

        }

    public void setCheckListState(IceCheckListState state)
        {
        // TODO Auto-generated method stub

        }

    public void updateCheckListAndTimerStates()
        {
        // TODO Auto-generated method stub

        }

    public void updatePairStates(IceCandidatePair generatingPair)
        {
        // TODO Auto-generated method stub

        }

    public void serviceActivated(IoService service, SocketAddress serviceAddress, IoHandler handler, IoServiceConfig config)
        {
        // TODO Auto-generated method stub
        
        }

    public void serviceDeactivated(IoService service, SocketAddress serviceAddress, IoHandler handler, IoServiceConfig config)
        {
        // TODO Auto-generated method stub
        
        }

    public void sessionCreated(IoSession session)
        {
        // TODO Auto-generated method stub
        
        }

    public void sessionDestroyed(IoSession session)
        {
        // TODO Auto-generated method stub
        
        }

    public void start(IoServiceListener ioServiceListener)
        {
        // TODO Auto-generated method stub
        
        }

    public void close()
        {
        // TODO Auto-generated method stub
        
        }

    public boolean hasRemoteCandidateInSdp(InetSocketAddress remoteAddress, boolean isUdp)
        {
        // TODO Auto-generated method stub
        return false;
        }

    public void start(IceCheckList checkList, Collection<IceCandidate> localCandidates, IceCheckScheduler scheduler)
        {
        // TODO Auto-generated method stub
        
        }

    public IceCandidatePair getPair(InetSocketAddress localAddress, InetSocketAddress remoteAddress, boolean udp)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Collection<IceCandidate> getLocalCandidates() {
        // TODO Auto-generated method stub
        return null;
    }

    public InetAddress getPublicAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    public IceStunUdpPeer getStunUdpPeer() {
        // TODO Auto-generated method stub
        return null;
    }

    }
