package org.lastbamboo.common.rudp.segment;

import static org.lastbamboo.common.rudp.RudpConstants.ACK;
import static org.lastbamboo.common.rudp.RudpConstants.ACK_HEADER_LENGTH;
import static org.lastbamboo.common.rudp.RudpConstants.CHECKSUM_INDEX;
import static org.lastbamboo.common.rudp.RudpConstants.EACK;
import static org.lastbamboo.common.rudp.RudpConstants.EACK_HEADER_LENGTH;
import static org.lastbamboo.common.rudp.RudpConstants.NUL;
import static org.lastbamboo.common.rudp.RudpConstants.NUL_HEADER_LENGTH;
import static org.lastbamboo.common.rudp.RudpConstants.RST;
import static org.lastbamboo.common.rudp.RudpConstants.RST_HEADER_LENGTH;
import static org.lastbamboo.common.rudp.RudpConstants.SYN;
import static org.lastbamboo.common.rudp.RudpConstants.SYN_HEADER_LENGTH;

import java.util.Collection;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.lastbamboo.common.rudp.RudpUtils;
import org.littleshoot.util.UByte;
import org.littleshoot.util.UByteImpl;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UShort;
import org.littleshoot.util.UShortImpl;
import org.littleshoot.util.mina.ByteBufferExt;
import org.littleshoot.util.mina.ByteBufferExtImpl;

/**
 * Encoding class for RUDP segments.  This is separated from the 
 * {@link ProtocolEncoder} for easier testing.
 */
public final class RudpSegmentEncoder
    {

    /**
     * The visitor used to convert segments to byte buffers for our protocol.
     */
    private static class ToByteBuffer implements SegmentVisitor<ByteBufferExt>
        {
        /**
         * Puts a RUDP checksum into the RUDP header.  The byte buffer must be a
         * fully formed RUDP segment that is missing its checksum.
         * 
         * @param rudpUtils
         *      RUDP utilities.
         * @param buffer
         *      The byte buffer containing a RUDP segment.
         */
        private static void putChecksum (final ByteBufferExt buffer)
            {
            buffer.flip ();
            buffer.putInt (CHECKSUM_INDEX,
                RudpUtils.getChecksum (buffer).toInt ());
            }
        
        /**
         * Puts a RUDP header into a given byte buffer.
         * 
         * @param controlFlags
         *      The control flags of the header to put.
         * @param headerLength
         *      The length of the header to put.
         * @param dataLength
         *      The length of the data in the segment of which this header is a
         *      part.
         * @param segment The segment.
         * @param buffer The byte buffer into which to put the header.
         */
        private static void putHeader (final byte controlFlags,
            final UByte headerLength, final UShort dataLength,
            final Segment segment, final ByteBufferExt buffer)
            {
            buffer.put (controlFlags);
            buffer.putUByte (headerLength);
            buffer.putUShort (dataLength);
            buffer.putUInt (segment.getSeqNum ());
            buffer.putUInt (segment.getAckNum ());
            
            // Put the 0 placeholder in for the checksum.
            buffer.putInt (0);
            }
        
        /**
         * Constructs a new visitor.
         */
        public ToByteBuffer ()
            {
            }
        
        /**
         * {@inheritDoc}
         */
        public ByteBufferExt visitAck (final AckSegment ack)
            {
            final byte[] data = ack.getData ();
            
            final int segmentLength =
                    (2 * ACK_HEADER_LENGTH.toInt ()) + data.length;
            
            final ByteBufferExt buffer =
                    new ByteBufferExtImpl (ByteBuffer.allocate (segmentLength));
            
            putHeader (ACK,
                       ACK_HEADER_LENGTH,
                       new UShortImpl (data.length),
                       ack,
                       buffer);
            
            buffer.put (data);
            putChecksum (buffer);
            
            return buffer;
            }
        
        /**
         * {@inheritDoc}
         */
        public ByteBufferExt visitEack (final EackSegment eack)
            {
            final Collection<UInt> receivedSeqNums = eack.getReceivedSeqNums ();
            final byte[] data = eack.getData ();
            
            final int seqNumsSize = 4 * receivedSeqNums.size ();
            
            final int segmentLength =
                    (2 * EACK_HEADER_LENGTH.toInt ()) + seqNumsSize +
                        data.length;
            
            final ByteBufferExt buffer =
                    new ByteBufferExtImpl (ByteBuffer.allocate (segmentLength));
            
            final UByte headerLength =
                    new UByteImpl
                        (EACK_HEADER_LENGTH.toInt () + 
                             (2 * receivedSeqNums.size ()));
            
            putHeader ((byte) (EACK | ACK),
                       headerLength,
                       new UShortImpl (data.length),
                       eack,
                       buffer);
            
            for (final UInt receivedSeqNum : receivedSeqNums)
                {
                buffer.putUInt (receivedSeqNum);
                }
            
            buffer.put (data);
            putChecksum (buffer);
            
            return buffer;
            }
    
        /**
         * {@inheritDoc}
         */
        public ByteBufferExt visitNul (final NulSegment nul)
            {
            final int segmentLength = (2 * NUL_HEADER_LENGTH.toInt ());
            
            final ByteBufferExt buffer =
                    new ByteBufferExtImpl
                        (ByteBuffer.allocate (segmentLength));
            
            putHeader (NUL,
                       NUL_HEADER_LENGTH,
                       new UShortImpl (0),
                       nul,
                       buffer);
            
            putChecksum (buffer);
            
            return buffer;
            }

        /**
         * {@inheritDoc}
         */
        public ByteBufferExt visitRst (final RstSegment rst)
            {
            final int segmentLength = (2 * RST_HEADER_LENGTH.toInt ());
            
            final ByteBufferExt buffer =
                    new ByteBufferExtImpl
                        (ByteBuffer.allocate (segmentLength));
            
            putHeader (RST,
                       RST_HEADER_LENGTH,
                       new UShortImpl (0),
                       rst,
                       buffer);
            
            putChecksum (buffer);
            
            return buffer;
            }

        /**
         * {@inheritDoc}
         */
        public ByteBufferExt visitSyn (final SynSegment syn)
            {
            final int segmentLength = (2 * SYN_HEADER_LENGTH.toInt ());
            
            final ByteBufferExt buffer =
                    new ByteBufferExtImpl
                        (ByteBuffer.allocate (segmentLength));
            
            final byte controlByte = syn.isAck () ? (byte) (SYN | ACK) : SYN;
            
            putHeader (controlByte,
                       SYN_HEADER_LENGTH,
                       new UShortImpl (0),
                       syn,
                       buffer);
            
            buffer.putShort (syn.getMaxOutstanding ().toShort ());
            buffer.putShort (syn.getMaxSegmentSize ().toShort ());
            buffer.putShort ((short) 0);
            putChecksum (buffer);
            
            return buffer;
            }
        }

    /**
     * Encodes the specified RUDP segment.
     * 
     * @param segment The segment to encode.
     * @return The segment encoded in a {@link ByteBuffer}.
     */
    public ByteBuffer encode (final Segment segment)
        {
        final SegmentVisitor<ByteBufferExt> visitor = new ToByteBuffer ();
        
        final ByteBufferExt buffer = segment.accept (visitor);
        return buffer.getDelegate();
        }
    }
