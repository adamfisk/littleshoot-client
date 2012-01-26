package org.lastbamboo.common.rudp.segment;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Collection;
import java.util.LinkedList;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.littleshoot.mina.filter.codec.ProtocolEncoderOutput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lastbamboo.common.rudp.RudpConstants;
import org.lastbamboo.common.rudp.RudpUtils;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UIntImpl;
import org.littleshoot.util.UShortImpl;

/**
 * A test for the reliable UDP encoder for our MINA implementation.
 */
public final class RudpEncoderTest
    {
    /**
     * The encoder to test.
     */
    private ProtocolEncoder m_encoder;
    
    /**
     * The mock output used to verify our encoder.
     */
    private ProtocolEncoderOutput m_output;
    
    /**
     * RUDP utilities.
     */
    private RudpUtils m_rudpUtils;
    
    /**
     * The mock session used to verify our encoder.
     */
    private IoSession m_session;
    
    /**
     * Tests the encoding of a segment.
     * 
     * @param rudpUtils
     *      RUDP utilities.
     * @param expected
     *      The expected result of encoding.
     * @param segment
     *      The segment to encode.
     * @param session
     *      The mock session.
     * @param encoder
     *      The encoder to test.
     * @param output
     *      The mock output.
     */
    private static void testSegment
            (final RudpUtils rudpUtils,
             final ByteBuffer expected,
             final Segment segment,
             final IoSession session,
             final ProtocolEncoder encoder,
             final ProtocolEncoderOutput output)
        {
        expected.flip ();
        expected.putInt (RudpConstants.CHECKSUM_INDEX,
            RudpUtils.getChecksum (expected).toInt ());
        
        output.write (expected);
        
        replay (output);
        
        try
            {
            encoder.encode (session, segment, output);
            
            verify (output);
            }
        catch (final Exception e)
            {
            Assert.fail ("Unexpected exception: " + e);
            }
        }
    
    /**
     * Sets up the fields for our test.
     */
    @Before
    public void setUp
            ()
        {
        m_encoder = new RudpEncoder ();
        m_session = createMock (IoSession.class);
        m_output = createMock (ProtocolEncoderOutput.class);
        }
    
    /**
     * Tests encoding of an ACK segment.
     */
    @Test
    public void testAck
            ()
        {
        final byte[] data =
                new byte[] { 13, 17, 20, 23, 18, -19, 38, -10 };
        
        final AckSegment ack = new AckSegmentImpl (new UIntImpl (12),
                                     new UIntImpl (14),
                                     data);
        
        final byte headerLength = 8;
        
        final ByteBuffer expected =
                ByteBuffer.allocate ((2 * headerLength) + data.length);
        
        expected.put (RudpConstants.ACK);
        expected.put (headerLength);
        expected.putShort ((short) data.length);
        expected.putInt (12);
        expected.putInt (14);
        expected.putInt (0);
        expected.put (data);
        
        testSegment (m_rudpUtils,
                     expected,
                     ack,
                     m_session,
                     m_encoder,
                     m_output);
        }
    
    /**
     * Tests encoding of an EACK segment.
     */
    @Test
    public void testEack
            ()
        {
        final Collection<UInt> receivedSeqNums = new LinkedList<UInt> ();
        
        receivedSeqNums.add (new UIntImpl (10));
        receivedSeqNums.add (new UIntImpl (11));
        receivedSeqNums.add (new UIntImpl (17));
        receivedSeqNums.add (new UIntImpl (19));
        receivedSeqNums.add (new UIntImpl (2));
        receivedSeqNums.add (new UIntImpl (55));
        receivedSeqNums.add (new UIntImpl (23));
        
        final byte[] data =
                new byte[] { 10, 9, 7, 44, 42, 30, 100, 101 };
        
        final EackSegment eack = new EackSegmentImpl (new UIntImpl (21),
                                        new UIntImpl (73),
                                        receivedSeqNums,
                                        data);
        
        final byte headerLength = (byte) (8 + (2 * receivedSeqNums.size ()));
        final int seqNumsSize = 4 * receivedSeqNums.size ();
        
        final ByteBuffer expected =
                ByteBuffer.allocate ((2 * headerLength) + seqNumsSize +
                                         data.length);
        
        expected.put (RudpConstants.EACK);
        expected.put (headerLength);
        expected.putShort ((short) data.length);
        expected.putInt (21);
        expected.putInt (73);
        expected.putInt (0);
        
        for (final UInt receivedSeqNum : receivedSeqNums)
            {
            expected.putInt (receivedSeqNum.toInt ());
            }
        
        expected.put (data);
        
        testSegment (m_rudpUtils,
                     expected,
                     eack,
                     m_session,
                     m_encoder,
                     m_output);
        }
    
    /**
     * Test encoding of a NUL segment.
     */
    @Test
    public void testNul
            ()
        {
        final NulSegment nul = new NulSegmentImpl (new UIntImpl (77), new UIntImpl (79));
        
        final byte headerLength = 8;
        
        final ByteBuffer expected = ByteBuffer.allocate (2 * headerLength);
        
        expected.put (RudpConstants.NUL);
        expected.put (headerLength);
        expected.putShort ((short) 0);
        expected.putInt (77);
        expected.putInt (79);
        expected.putInt (0);
        
        testSegment (m_rudpUtils,
                     expected,
                     nul,
                     m_session,
                     m_encoder,
                     m_output);
        }
    
    /**
     * Test encoding of a RST segment.
     */
    @Test
    public void testRst
            ()
        {
        final RstSegment rst = new RstSegmentImpl (new UIntImpl (53), new UIntImpl (1));
        
        final byte headerLength = 8;
        
        final ByteBuffer expected = ByteBuffer.allocate (2 * headerLength);
        
        expected.put (RudpConstants.RST);
        expected.put (headerLength);
        expected.putShort ((short) 0);
        expected.putInt (53);
        expected.putInt (1);
        expected.putInt (0);
        
        testSegment (m_rudpUtils,
                     expected,
                     rst,
                     m_session,
                     m_encoder,
                     m_output);
        }
    
    /**
     * Test encoding of a SYN segment.
     */
    @Test
    public void testSyn
            ()
        {
        final SynSegment syn = new SynSegmentImpl (new UIntImpl (567),
                                     new UIntImpl (234),
                                     new UShortImpl (17),
                                     new UShortImpl (40000));
        
        final byte headerLength = 11;
        
        final ByteBuffer expected = ByteBuffer.allocate (2 * headerLength);
        
        expected.put (RudpConstants.SYN);
        expected.put (headerLength);
        expected.putShort ((short) 0);
        expected.putInt (567);
        expected.putInt (234);
        expected.putInt (0);
        expected.putShort ((short) 17);
        expected.putShort ((short) 40000);
        expected.putShort ((short) 0);
        
        testSegment (m_rudpUtils,
                     expected,
                     syn,
                     m_session,
                     m_encoder,
                     m_output);
        }
    }
