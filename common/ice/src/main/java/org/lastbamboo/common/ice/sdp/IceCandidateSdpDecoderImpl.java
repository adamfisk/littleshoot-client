package org.lastbamboo.common.ice.sdp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.ice.IceTransportProtocol;
import org.lastbamboo.common.ice.candidate.IceCandidate;
import org.lastbamboo.common.ice.candidate.IceCandidateType;
import org.lastbamboo.common.ice.candidate.IceTcpActiveCandidate;
import org.lastbamboo.common.ice.candidate.IceTcpHostPassiveCandidate;
import org.lastbamboo.common.ice.candidate.IceTcpRelayPassiveCandidate;
import org.lastbamboo.common.ice.candidate.IceUdpHostCandidate;
import org.lastbamboo.common.ice.candidate.IceUdpServerReflexiveCandidate;
import org.lastbamboo.common.sdp.api.Attribute;
import org.lastbamboo.common.sdp.api.MediaDescription;
import org.lastbamboo.common.sdp.api.SdpException;
import org.lastbamboo.common.sdp.api.SdpFactory;
import org.lastbamboo.common.sdp.api.SdpParseException;
import org.lastbamboo.common.sdp.api.SessionDescription;
import org.littleshoot.util.IoExceptionWithCause;
import org.littleshoot.util.mina.MinaUtils;

/**
 * Factory class for creating ICE candidates from offer/answer data.
 * 
 * TODO: This currently decodes candidates assuming there's only one media
 * stream.  That's incorrect.  It should create collections of candidates for 
 * eacy media stream.
 */
public final class IceCandidateSdpDecoderImpl implements IceCandidateSdpDecoder
    {
    
    /**
     * Logger for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger(IceCandidateSdpDecoderImpl.class);
    
    private static final String CANDIDATE_KEY = "candidate";

    private final SdpFactory m_sdpFactory;

    /**
     * Creates a new decoder.
     */
    public IceCandidateSdpDecoderImpl()
        {
        m_sdpFactory = new SdpFactory();
        }
    
    public Collection<IceCandidate> decode(final ByteBuffer buf,
        final boolean controlling) throws IOException
        {
        final String responseBodyString = MinaUtils.toAsciiString(buf);
        
        try
            {
            final SessionDescription sdp = 
                this.m_sdpFactory.createSessionDescription(responseBodyString);
            final Collection mediaDescriptions = sdp.getMediaDescriptions(true);
            LOG.debug("Creating candidates from media descs:\n" + 
                mediaDescriptions);
            return createCandidates(mediaDescriptions, controlling);
            }
        catch (final SdpException e)
            {
            LOG.warn("Could not parse SDP: "+MinaUtils.toAsciiString(buf));
            throw new IoExceptionWithCause("Could not parse SDP", e);
            }
        

        }
    
    /**
     * Sends TURN "Send Request"s to the collection of candidate connections 
     * from the peer's SDP.
     * 
     * @param remoteMediaDescriptions The <code>Collection</code> of media
     * description's from the peer's SDP.  Each media description will contain
     * some number of candidates for exchanging media.
     * @param controlling Whether or not to create controlling candidates.
     */
    private Collection<IceCandidate> createCandidates(
        final Collection remoteMediaDescriptions, final boolean controlling)
        {
        final Collection<IceCandidate> candidates = 
            new LinkedList<IceCandidate>();
        for (final Iterator iter = remoteMediaDescriptions.iterator(); 
            iter.hasNext();)
            {
            final MediaDescription mediaDesc = (MediaDescription) iter.next();
            final Collection attributes = mediaDesc.getAttributes(true);
            for (final Iterator iterator = attributes.iterator(); 
                iterator.hasNext();)
                {
                final Attribute attribute = (Attribute) iterator.next();
                try
                    {
                    if (attribute.getName().equals(CANDIDATE_KEY))
                        {
                        final String attributeValue = attribute.getValue(); 
                        final Collection<IceCandidate> newCandidates = 
                            createCandidates(attributeValue, controlling);
                        candidates.addAll(newCandidates);
                        }
                    }
                catch (final UnknownHostException e)
                    {
                    LOG.warn("Could not parse SDP", e);
                    // Go to the next candidate.
                    continue;
                    }
                catch (final SdpParseException e)
                    {
                    LOG.warn("Could not parse SDP", e);
                    // Go to the next candidate.
                    continue;
                    }
                }
            }
        return candidates;
        }


    /**
     * Sends a TURN send request to a TURN server to enable incoming data
     * for a specific candidate for a specific media.
     * 
     * @param tracker The tracker for the status of the available ICE 
     * connection candidates.
     * @param attribute The attribute string to parse in order to send a
     * TURN send request to the server in the candidate.
     * @throws UnknownHostException If we could not parse the host 
     */
    private Collection<IceCandidate> createCandidates(final String attribute,
        final boolean controlling) throws UnknownHostException
        {
        LOG.trace("Parsing attribute: "+attribute);
        final List<IceCandidate> candidates = new LinkedList<IceCandidate>();
        final Scanner scanner = new Scanner(attribute);
        scanner.useDelimiter(" ");
        while (scanner.hasNext())
            {
            final IceCandidate candidate = 
                createIceCandidate(scanner, controlling);
            candidates.add(candidate);
            }
        return candidates;
        }
    
    private IceCandidate createIceCandidate(final Scanner scanner, 
        final boolean controlling) throws UnknownHostException 
        {
        final String foundation = scanner.next();
        final int componentId = Integer.parseInt(scanner.next());
        final String transportString = scanner.next();  
        final IceTransportProtocol transportProtocol = 
            IceTransportProtocol.toTransport(transportString);
        if (transportProtocol == null)
            {
            LOG.warn("Null transport");
            throw new NullPointerException("Null transport");
            }
        
        final int priority = Integer.parseInt(scanner.next());
        final InetAddress address = InetAddress.getByName(scanner.next());
        final int port = Integer.parseInt(scanner.next());
        final InetSocketAddress socketAddress = 
            new InetSocketAddress(address, port);
        
        final String typeToken = scanner.next();
        if (!typeToken.equals("typ"))
            {
            LOG.error("Unexpected type token: "+typeToken);
            throw new IllegalArgumentException(
                "Unexpected type token: "+typeToken);
            }
        
        final String typeString = scanner.next();
        final IceCandidateType type = IceCandidateType.toType(typeString);
        if (type == null)
            {
            LOG.warn("Unrecognized type: "+typeString);
            throw new IllegalArgumentException(
                "Unrecognized type: "+typeString);
            }
        
        switch (transportProtocol)
            {
            case UDP:
                switch (type)
                    {
                    case HOST:
                        return new IceUdpHostCandidate(socketAddress, 
                            foundation, priority, controlling, componentId);
                    case RELAYED:
                        break;
                    case PEER_REFLEXIVE:
                        break;
                    case SERVER_REFLEXIVE:
                        final InetSocketAddress related = parseRelated(scanner);
                        return new IceUdpServerReflexiveCandidate(socketAddress, 
                            foundation, related.getAddress(), related.getPort(),
                            controlling, priority, componentId);
                    }
                break;
            case TCP_PASS:
                switch (type)
                    {
                    case HOST:
                        return new IceTcpHostPassiveCandidate(socketAddress,
                            foundation, controlling, priority, componentId);
                    case RELAYED:
                        LOG.debug("Received a TCP relay passive candidate");
                        final InetSocketAddress related = parseRelated(scanner);
                        return new IceTcpRelayPassiveCandidate(socketAddress, 
                            foundation, related.getAddress(), related.getPort(),
                            controlling, priority, componentId);
                    case PEER_REFLEXIVE:
                        break;
                    case SERVER_REFLEXIVE:
                        break;
                    }
                break;
            case TCP_SO:
                // Not currently used.  Awaiting progress on the ICE TCP
                // draft before implementing things that could change.
                switch (type)
                    {
                    case HOST:
                        break;
                    case RELAYED:
                        break;
                    case PEER_REFLEXIVE:
                        break;
                    case SERVER_REFLEXIVE:
                        break;
                    }
                break;
            case TCP_ACT:
                // Not currently used.  Awaiting progress on the ICE TCP
                // draft before implementing things that could change.
                switch (type)
                    {
                    case HOST:
                        return new IceTcpActiveCandidate(socketAddress, 
                            controlling);
                    case RELAYED:
                        break;
                    case PEER_REFLEXIVE:
                        break;
                    case SERVER_REFLEXIVE:
                        break;
                    }
                break;
            }
        
        LOG.warn("Returning null candidate for type: "+type+
            " and protocol "+transportProtocol);
        return null;
        }

    private InetSocketAddress parseRelated(final Scanner scanner) 
        throws UnknownHostException
        {
        final String raddr = scanner.next();
        if (!raddr.equals("raddr"))
            {
            LOG.error("Bad related address: "+raddr);
            }
        final InetAddress relatedAddress = 
            InetAddress.getByName(scanner.next());
        
        final String rport = scanner.next();
        if (!rport.equals("rport"))
            {
            LOG.error("Bad related port: "+rport);
            }
        final int relatedPort = 
            Integer.parseInt(scanner.next());
        
        return new InetSocketAddress(relatedAddress, relatedPort);
        }
    }
