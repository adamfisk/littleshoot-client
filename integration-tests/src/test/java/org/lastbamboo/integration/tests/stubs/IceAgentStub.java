package org.lastbamboo.integration.tests.stubs;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Queue;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.ice.IceAgent;
import org.lastbamboo.common.ice.IceMediaStream;
import org.lastbamboo.common.ice.IceState;
import org.lastbamboo.common.ice.IceTieBreaker;
import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidatePair;
import org.lastbamboo.common.offer.answer.OfferAnswerListener;
import org.lastbamboo.common.offer.answer.OfferAnswerMediaListener;

public class IceAgentStub implements IceAgent
    {

    public long calculateDelay(int Ta_i)
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public IceState getIceState()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Collection<IceMediaStream> getMediaStreams()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Queue<IceCandidatePair> getNominatedPairs()
        {
        // TODO Auto-generated method stub
        return null;
        }

    private final IceTieBreaker m_tieBreaker = new IceTieBreaker();
    public IceTieBreaker getTieBreaker()
        {
        return m_tieBreaker;
        }

    public boolean isControlling()
        {
        // TODO Auto-generated method stub
        return false;
        }

    public void onNominatedPair(IceCandidatePair pair,
            IceMediaStream iceMediaStream)
        {
        // TODO Auto-generated method stub

        }

    public void onUnfreezeCheckLists(IceMediaStream mediaStream)
        {
        // TODO Auto-generated method stub

        }

    public void onValidPairs(IceMediaStream mediaStream)
        {
        // TODO Auto-generated method stub

        }

    public void checkValidPairsForAllComponents(IceMediaStream mediaStream)
        {
        // TODO Auto-generated method stub

        }

    public void recomputePairPriorities()
        {
        // TODO Auto-generated method stub

        }

    public void setControlling(boolean controlling)
        {
        // TODO Auto-generated method stub

        }

    public void startMedia(OfferAnswerMediaListener mediaListener)
        {
        // TODO Auto-generated method stub

        }

    public byte[] generateAnswer()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public byte[] generateOffer()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void processAnswer(ByteBuffer answer,
            OfferAnswerListener offerAnswerListener)
        {
        // TODO Auto-generated method stub

        }

    public void processOffer(ByteBuffer offer,
            OfferAnswerListener offerAnswerListener)
        {
        // TODO Auto-generated method stub

        }

    public void close()
        {
        // TODO Auto-generated method stub
        
        }

    public void onNoMorePairs()
        {
        // TODO Auto-generated method stub
        
        }

    public Collection<? extends IceCandidate> gatherCandidates() {
        // TODO Auto-generated method stub
        return null;
    }

    public InetAddress getPublicAdress() {
        // TODO Auto-generated method stub
        return null;
    }

    public void closeTcp() {
        // TODO Auto-generated method stub
        
    }

    public void closeUdp() {
        // TODO Auto-generated method stub
        
    }

    public void processAnswer(ByteBuffer answer) {
        // TODO Auto-generated method stub
        
    }

    public void processOffer(ByteBuffer offer) {
        // TODO Auto-generated method stub
        
    }

    public void useRelay() {
        // TODO Auto-generated method stub
        
    }

    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    }
