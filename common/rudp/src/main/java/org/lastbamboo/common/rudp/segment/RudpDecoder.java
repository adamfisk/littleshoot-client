package org.lastbamboo.common.rudp.segment;

import static org.lastbamboo.common.rudp.RudpConstants.ACK;
import static org.lastbamboo.common.rudp.RudpConstants.ACK_HEADER_LENGTH;
import static org.lastbamboo.common.rudp.RudpConstants.EACK;
import static org.lastbamboo.common.rudp.RudpConstants.EACK_HEADER_LENGTH;
import static org.lastbamboo.common.rudp.RudpConstants.NUL;
import static org.lastbamboo.common.rudp.RudpConstants.NUL_HEADER_LENGTH;
import static org.lastbamboo.common.rudp.RudpConstants.RST;
import static org.lastbamboo.common.rudp.RudpConstants.RST_HEADER_LENGTH;
import static org.lastbamboo.common.rudp.RudpConstants.SYN;
import static org.lastbamboo.common.rudp.RudpConstants.SYN_HEADER_LENGTH;

import java.util.ArrayList;
import java.util.Collection;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.rudp.RudpUtils;
import org.littleshoot.util.F3;
import org.littleshoot.util.F4;
import org.littleshoot.util.UByte;
import org.littleshoot.util.UInt;
import org.littleshoot.util.UShort;
import org.littleshoot.util.mina.ByteBufferExt;
import org.littleshoot.util.mina.ByteBufferExtImpl;
import org.littleshoot.util.mina.DemuxableProtocolDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The reliable UDP decoder for MINA.
 */
public final class RudpDecoder implements DemuxableProtocolDecoder
    {
    
    private final static Logger LOG = 
        LoggerFactory.getLogger(RudpDecoder.class);
    
    /**
     * An enumeration representing the different types of segments as determined
     * by the segment control flags.
     */
    private static enum Type
        {
        /**
         * An ACK.
         */
        ACK,
        
        /**
         * An EACK.
         */
        EACK,
        
        /**
         * The type that represents poorly-formed control flags.  For instance,
         * if the flags specify a SYN and RST segment, this type will be used,
         * since those segment types are mutually exclusive.
         */
        INVALID,
        
        /**
         * A NUL.
         */
        NUL,
        
        /**
         * A RST.
         */
        RST,
        
        /**
         * A SYN.
         */
        SYN,
        
        /**
         * A SYN/ACK.
         */
        SYN_ACK
        }
        
    /**
     * Returns the result of the decoding of a segment given in a byte buffer.
     * This function performs some work common to the decoding of all RUDP
     * segments with variable header length and calls a passed function to
     * perform segment-specific decoding.
     * 
     * @param <T> The type returned by the decoding.
     *      
     * @param buffer The byte buffer holding the segment.
     * @param f The function used to perform segment-specific decoding.
     *      
     * @return The segment decoded from the given byte buffer.
     */
    private static <T> T decode (final ByteBufferExt buffer,
        final F4<UByte,UShort,UInt,UInt,T> f)
        {
        final UByte headerLength = buffer.getUByte ();
        final UShort dataLength = buffer.getUShort ();
        final UInt seqNum = buffer.getUInt ();
        final UInt ackNum = buffer.getUInt ();
        
        // Skip the checksum, since it has already been checked.
        buffer.getUInt ();
        
        return f.run (headerLength, dataLength, seqNum, ackNum);
        }
    
    /**
     * Returns the result of the decoding of a segment given in a byte buffer.
     * This function performs some work common to the decoding of all RUDP
     * segments with static header length and calls a passed function to perform
     * segment-specific decoding.
     * 
     * @param <T>
     *      The type returned by the decoding.
     *      
     * @param buffer
     *      The byte buffer holding the segment.
     * @param expectedHeaderLength
     *      The expected header length.  This is checked against the header
     *      length stored in the segment itself.
     * @param f
     *      The function used to perform segment-specific decoding.
     *      
     * @return
     *      The segment decoded from the given byte buffer.
     */
    private static <T> T decode
            (final ByteBufferExt buffer,
             final UByte expectedHeaderLength,
             final F3<UShort,UInt,UInt,T> f)
        {
        final UByte headerLength = buffer.getUByte ();
        
        if (headerLength.toInt () == expectedHeaderLength.toInt ())
            {
            final UShort dataLength = buffer.getUShort ();
            final UInt seqNum = buffer.getUInt ();
            final UInt ackNum = buffer.getUInt ();
            
            // Skip the checksum, since it has already been checked.
            buffer.getUInt ();
            
            return f.run (dataLength, seqNum, ackNum);
            }
        else
            {
            LOG.warn("Unexpected header length..");
            throw new RuntimeException
                    ("Header length != " +
                         expectedHeaderLength.toInt () + " in segment");
            }
        }
    
    /**
     * Returns a segment decoded from a given byte buffer.  The byte buffer is
     * a RUDP segment without the control flags byte.
     * 
     * @param type
     *      The type of the segment to decode.
     * @param buffer
     *      The byte buffer containing the rest of the segment.
     *      
     * @return
     *      A segment decoded from the given byte buffer.
     */
    private static Segment decode
            (final Type type,
             final ByteBufferExt buffer)
        {
        switch (type)
            {
            case EACK:
                return decodeEack (buffer);
                
            case INVALID:
                throw new RuntimeException ("Invalid segment type");
                
            case NUL:
                return decodeNul (buffer);
                
            case RST:
                return decodeRst (buffer);
                
            case SYN:
                return decodeSyn (false, buffer);
                
            case SYN_ACK:
                return decodeSyn (true, buffer);
                
            case ACK:
                return decodeAck (buffer);
                
            default:
                throw new RuntimeException ("Unknown segment type");
            }
        }
    
    /**
     * Returns a decoded ACK segment.
     * 
     * @param buffer
     *      The byte buffer containing the segment information.
     *      
     * @return
     *      A ACK segment.
     */
    private static AckSegment decodeAck
            (final ByteBufferExt buffer)
        {
        final F3<UShort,UInt,UInt,AckSegment> f =
                new F3<UShort,UInt,UInt,AckSegment> ()
            {
            public AckSegment run
                    (final UShort dataLength,
                     final UInt seqNum,
                     final UInt ackNum)
                {
                final byte[] data = new byte[dataLength.toInt ()];
            
            	buffer.get (data);
            
            	return new AckSegmentImpl (seqNum, ackNum, data);
                }
            };
        
        return decode (buffer, ACK_HEADER_LENGTH, f);
        }
    
    /**
     * Returns a decoded EACK segment.
     * 
     * @param buffer
     *      The byte buffer containing the segment information.
     *      
     * @return
     *      A EACK segment.
     */
    private static EackSegment decodeEack
            (final ByteBufferExt buffer)
        {
        final F4<UByte,UShort,UInt,UInt,EackSegment> f =
                new F4<UByte,UShort,UInt,UInt,EackSegment> ()
            {
            public EackSegment run
                    (final UByte headerLength,
                     final UShort dataLength,
                     final UInt seqNum,
                     final UInt ackNum)
                {
                if (isEackHeaderLengthValid (headerLength))
                    {
                    // Multiply by 2 to get into bytes, since the header lengths
                    // are specified in 2-byte units.
                    final int seqNumsSize =
                            2 * (headerLength.toInt () -
                                    EACK_HEADER_LENGTH.toInt ());
                    
                    final int numReceivedSeqNums = seqNumsSize / 4;
                    
                    final Collection<UInt> receivedSeqNums =
                            new ArrayList<UInt> (numReceivedSeqNums);
                    
                    for (int i = 0; i < numReceivedSeqNums; ++i)
                        {
                        receivedSeqNums.add (buffer.getUInt ());
                        }
                    
                    final byte[] data = new byte[dataLength.toInt ()];
                    
                    buffer.get (data);
                    
                    return new EackSegmentImpl (seqNum,
                                                ackNum,
                                                receivedSeqNums,
                                                data);
                    }
                else
                    {
                    throw new RuntimeException ("Invalid EACK header length");
                    }
                }
            };
        
        return decode (buffer, f);
        }
    
    /**
     * Returns a decoded NUL segment.
     * 
     * @param buffer
     *      The byte buffer containing the segment information.
     *      
     * @return
     *      A NUL segment.
     */
    private static NulSegment decodeNul
            (final ByteBufferExt buffer)
        {
        final F3<UShort,UInt,UInt,NulSegment> f =
                new F3<UShort,UInt,UInt,NulSegment> ()
            {
            public NulSegment run
                    (final UShort dataLength,
                     final UInt seqNum,
                     final UInt ackNum)
                {
                return new NulSegmentImpl (seqNum, ackNum);
                }
            };
        
        return decode (buffer, NUL_HEADER_LENGTH, f);
        }
    
    /**
     * Returns a decoded RST segment.
     * 
     * @param buffer
     *      The byte buffer containing the segment information.
     *      
     * @return
     *      A RST segment.
     */
    private static RstSegment decodeRst
            (final ByteBufferExt buffer)
        {
        final F3<UShort,UInt,UInt,RstSegment> f =
                new F3<UShort,UInt,UInt,RstSegment> ()
            {
            public RstSegment run
                    (final UShort dataLength,
                     final UInt seqNum,
                     final UInt ackNum)
                {
                return new RstSegmentImpl (seqNum, ackNum);
                }
            };
        
        return decode (buffer, RST_HEADER_LENGTH, f);
        }
    
    /**
     * Returns a decoded SYN segment.
     * 
     * @param ack
     *      Whether the SYN segment is also an ACK.
     * @param buffer
     *      The byte buffer containing the segment information.
     *      
     * @return
     *      A SYN segment.
     */
    private static SynSegment decodeSyn
            (final boolean ack,
             final ByteBufferExt buffer)
        {
        final F3<UShort,UInt,UInt,SynSegment> f =
                new F3<UShort,UInt,UInt,SynSegment> ()
            {
            public SynSegment run
                    (final UShort dataLength,
                     final UInt seqNum,
                     final UInt ackNum)
                {
                final UShort maxOutstanding = buffer.getUShort ();
                final UShort maxSegmentSize = buffer.getUShort ();
            
                // Ignore the options flag field for now.
                buffer.getUShort ();
            
                return new SynSegmentImpl (seqNum,
                                           ackNum,
                                           ack,
                                           maxOutstanding,
                                           maxSegmentSize);
                }
            };
        
        return decode (buffer, SYN_HEADER_LENGTH, f);
        }
        
    /**
     * Returns the type of segment specified by a given control flags byte.
     * 
     * @param controlByte
     *      The control flags byte.
     *      
     * @return
     *      The type of segment specified by the given control flags byte.
     */
    private static Type getType
            (final byte controlByte)
        {
        final boolean synBit = isBitsSet (controlByte, SYN);
        final boolean ackBit = isBitsSet (controlByte, ACK);
        final boolean eackBit = isBitsSet (controlByte, EACK);
        final boolean rstBit = isBitsSet (controlByte, RST);
        final boolean nulBit = isBitsSet (controlByte, NUL);
        
        // LOG.debug ("Syn bit: " + synBit);
        // LOG.debug ("Ack bit: " + ackBit);
        // LOG.debug ("Eack bit: " + eackBit);
        // LOG.debug ("Rst bit: " + rstBit);
        // LOG.debug ("");
        
        if (synBit && !eackBit && !rstBit)
            {
            if (ackBit)
                {
                return Type.SYN_ACK;
                }
            else
                {
                return Type.SYN;
                }
            }
        else if (eackBit && !synBit && !rstBit)
            {
            return Type.EACK;
            }
        else if (ackBit && !eackBit && !rstBit)
            {
            return Type.ACK;
            }
        else if (rstBit && !synBit && !eackBit)
            {
            return Type.RST;
            }
        else if (nulBit && !synBit && !eackBit)
            {
            return Type.NUL;
            }
        else
            {
            return Type.INVALID;
            }
        }
        
    /**
     * Returns whether all of the bits in a bitfield are set in a given byte.
     * 
     * @param b
     *      The byte.
     * @param bitfield
     *      The bitfield.
     *      
     * @return
     *      True if all of the bits are set, false otherwise.
     */
    private static boolean isBitsSet
            (final byte b,
             final byte bitfield)
        {
        return (b & bitfield) == bitfield;
        }
    
    /**
     * Returns whether the header length given in an EACK segment is valid.
     * 
     * @param headerLength
     *      The header length given in a EACK segment.
     *      
     * @return
     *      True if the header length is valid, false otherwise.
     */
    private static boolean isEackHeaderLengthValid
            (final UByte headerLength)
        {
        if (EACK_HEADER_LENGTH.toInt () <= headerLength.toInt ())
            {
            // Multiply by 2 to get into bytes, since the header lengths are
            // specified in 2-byte units.
            final int seqNumsSize =
                    2 * (headerLength.toInt () - EACK_HEADER_LENGTH.toInt ());
            
            // Each received sequence number takes up 4 bytes.  Therefore, the
            // size, in bytes, of the portion of the header containing sequence
            // numbers must a multiple of 4.
            return (seqNumsSize % 4) == 0;
            }
        else
            {
            return false;
            }
        }
    
    /**
     * Constructs a new RUDP decoder.
     */
    public RudpDecoder ()
        {
        }
        
    /**
     * {@inheritDoc}
     */
    public void decode (final IoSession session, final ByteBuffer buffer,
        final ProtocolDecoderOutput output) throws Exception
        {
        final ByteBufferExt bbExt = new ByteBufferExtImpl (buffer);
        
        if (RudpUtils.isChecksumOk (bbExt))
            {
            final byte controlByte = buffer.get ();
            
            final Type type = getType (controlByte);
            
            try
                {
                output.write (decode (type, bbExt));
                }
            catch (final RuntimeException e)
                {
                LOG.warn("Decoding error", e);
                throw new Exception
                        ("Error trying to decode: " + e.getMessage (), e);
                }
            }
        else
            {
            LOG.error("Bad checksum");
            throw new Exception ("Checksum incorrect");
            }
        }

    /**
     * {@inheritDoc}
     */
    public void dispose (final IoSession session) throws Exception
        {
        // Do nothing.  We have nothing to dispose.
        }

    /**
     * {@inheritDoc}
     */
    public void finishDecode
            (final IoSession session,
             final ProtocolDecoderOutput output) throws Exception
        {
        // Do nothing.
        }

    /**
     * {@inheritDoc}
     */
    public boolean atMessageBoundary()
        {
        return true;
        }
    }
