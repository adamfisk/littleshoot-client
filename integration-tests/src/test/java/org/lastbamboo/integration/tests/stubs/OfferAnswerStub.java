package org.lastbamboo.integration.tests.stubs;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.offer.answer.OfferAnswer;
import org.lastbamboo.common.offer.answer.OfferAnswerListener;

public class OfferAnswerStub implements OfferAnswer
    {

    private byte[] m_answer = new byte[0];

    public OfferAnswerStub(byte[] answer)
        {
        m_answer = answer;
        }

    public OfferAnswerStub()
        {
        // TODO Auto-generated constructor stub
        }

    public byte[] generateAnswer()
        {
        return this.m_answer;
        }

    public byte[] generateOffer()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void processAnswer(ByteBuffer answer, OfferAnswerListener offerAnswerListener)
        {
        // TODO Auto-generated method stub
        
        }

    public void processOffer(ByteBuffer offer, OfferAnswerListener offerAnswerListener)
        {
        // TODO Auto-generated method stub
        
        }

    public void close() {
        // TODO Auto-generated method stub
        
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

    }
