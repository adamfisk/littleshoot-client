package org.lastbamboo.common.rudp.segment;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Collection;
import java.util.LinkedList;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolDecoder;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lastbamboo.common.rudp.RudpConstants;
import org.lastbamboo.common.rudp.RudpUtils;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UIntImpl;
import org.littleshoot.util.UShortImpl;

/**
 * A test for our RUDP decoder.
 */
public final class RudpDecoderTest
    {
    /**
     * The decoder to test.
     */
    private ProtocolDecoder m_decoder;
    
    /**
     * The mock protocol decoder output.
     */
    private ProtocolDecoderOutput m_output;
    
    /**
     * The mock session.
     */
    private IoSession m_session;
    
    /**
     * Tests the decoding of a segment.
     * 
     * @param rudpUtils
     *      RUDP utilities.
     * @param buffer
     *      The buffer to decode.
     * @param expected
     *      The expected segment.
     * @param session
     *      The mock session.
     * @param decoder
     *      The decoder to test.
     * @param output
     *      The mock output.
     */
    private static void testSegment
            (final ByteBuffer buffer,
             final Segment expected,
             final IoSession session,
             final ProtocolDecoder decoder,
             final ProtocolDecoderOutput output)
        {
        buffer.flip ();
        buffer.putInt (RudpConstants.CHECKSUM_INDEX,
                RudpUtils.getChecksum (buffer).toInt ());
        
        output.write (expected);
        
        replay (output);
        
        try
            {
            decoder.decode (session, buffer, output);
            
            verify (output);
            }
        catch (final Exception e)
            {
            Assert.fail ("Unexpected exception: " + e);
            }
        }
    
    /**
     * Sets up this test.
     */
    @Before
    public void setUp
            ()
        {
        m_decoder = new RudpDecoder ();
        
        m_session = createMock (IoSession.class);
        m_output = createMock (ProtocolDecoderOutput.class);
        }
    
    /**
     * Tests ACK decoding.
     */
    @Test
    public void testAck
            ()
        {
        final byte[] data =
                new byte[] { 1, 20, 3, -100, 3, 11 };
        
        final byte headerLength = 8;
        
        final ByteBuffer buffer =
                ByteBuffer.allocate ((2 * headerLength) + data.length);
        
        buffer.put (RudpConstants.ACK);
        buffer.put (headerLength);
        buffer.putShort ((short) data.length);
        buffer.putInt (255);
        buffer.putInt (231);
        buffer.putInt (0);
        buffer.put (data);
        
        final AckSegment ack = new AckSegmentImpl (new UIntImpl (255),
                                     new UIntImpl (231),
                                     data);
        
        testSegment (buffer, ack, m_session, m_decoder, m_output);
        }
    
    /**
     * Tests EACK decoding.
     */
    @Test
    public void testEack
            ()
        {
        final Collection<UInt> receivedSeqNums = new LinkedList<UInt> ();
        
        receivedSeqNums.add (new UIntImpl (7));
        receivedSeqNums.add (new UIntImpl (13));
        receivedSeqNums.add (new UIntImpl (8));
        receivedSeqNums.add (new UIntImpl (2));
        receivedSeqNums.add (new UIntImpl (100));
        
        final byte[] data =
                new byte[] { 34, 19, 33, 1, 25, 25, 30 };
        
        final byte headerLength = (byte) (8 + (2 * receivedSeqNums.size ()));
        final int seqNumsSize = 4 * receivedSeqNums.size ();
        
        final ByteBuffer buffer =
                ByteBuffer.allocate ((2 * headerLength) + seqNumsSize +
                                         data.length);
        
        buffer.put (RudpConstants.EACK);
        buffer.put (headerLength);
        buffer.putShort ((short) data.length);
        buffer.putInt (12);
        buffer.putInt (8);
        buffer.putInt (0);
        
        for (final UInt receivedSeqNum : receivedSeqNums)
            {
            buffer.putInt (receivedSeqNum.toInt ());
            }
        
        buffer.put (data);
        
        final EackSegment eack = new EackSegmentImpl (new UIntImpl (12),
                                        new UIntImpl (8),
                                        receivedSeqNums,
                                        data);
        
        testSegment (buffer, eack, m_session, m_decoder, m_output);
        }
    
    /**
     * Tests NUL decoding.
     */
    @Test
    public void testNul
            ()
        {
        final byte headerLength = 8;
        final ByteBuffer buffer = ByteBuffer.allocate (2 * headerLength);
        
        buffer.put (RudpConstants.NUL);
        buffer.put (headerLength);
        buffer.putShort ((short) 0);
        buffer.putInt (22);
        buffer.putInt (23);
        buffer.putInt (0);
        
        final NulSegment nul = new NulSegmentImpl (new UIntImpl (22), new UIntImpl (23));
        
        testSegment (buffer, nul, m_session, m_decoder, m_output);
        }
    
    /**
     * Tests RST decoding.
     */
    @Test
    public void testRst
            ()
        {
        final byte headerLength = 8;
        final ByteBuffer buffer = ByteBuffer.allocate (2 * headerLength);
        
        buffer.put (RudpConstants.RST);
        buffer.put (headerLength);
        buffer.putShort ((short) 0);
        buffer.putInt (15);
        buffer.putInt (16);
        buffer.putInt (0);
        
        final RstSegment rst = new RstSegmentImpl (new UIntImpl (15), new UIntImpl (16));
        
        testSegment (buffer, rst, m_session, m_decoder, m_output);
        }
    
    /**
     * Tests SYN decoding.
     */
    @Test
    public void testSyn
            ()
        {
        final byte headerLength = 11;
        final ByteBuffer buffer = ByteBuffer.allocate (2 * headerLength);
        
        buffer.put (RudpConstants.SYN);
        buffer.put (headerLength);
        buffer.putShort ((short) 0);
        buffer.putInt (244);
        buffer.putInt (16);
        buffer.putInt (0);
        buffer.putShort ((short) 20);
        buffer.putShort ((short) 33);
        buffer.putShort ((short) 0);
        
        final SynSegment syn = new SynSegmentImpl (new UIntImpl (244),
                                     new UIntImpl (16),
                                     new UShortImpl (20),
                                     new UShortImpl (33));
        
        testSegment (buffer, syn, m_session, m_decoder, m_output);
        }
    }
