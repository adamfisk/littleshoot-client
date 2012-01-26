package org.lastbamboo.common.stun.stack.message.attributes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControlledAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControllingAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IcePriorityAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceUseCandidateAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatus;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatusAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.DataAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RelayAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RemoteAddressAttribute;
import org.littleshoot.util.mina.MinaUtils;
import org.littleshoot.mina.common.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for creating STUN attributes.
 */
public class StunAttributesFactoryImpl implements StunAttributesFactory
    {

    private final Logger LOG = LoggerFactory.getLogger(StunAttributesFactoryImpl.class);
    
    public Map<StunAttributeType, StunAttribute> createAttributes(
        final ByteBuffer body)
        {
        final Map<StunAttributeType, StunAttribute> attributes =
            new ConcurrentHashMap<StunAttributeType, StunAttribute>();
        while (body.hasRemaining())
            {
            addAttribute(attributes, body);
            }
        return attributes;
        }

    private void addAttribute(
        final Map<StunAttributeType, StunAttribute> attributes, 
        final ByteBuffer buf)
        {
        final int typeInt = buf.getUnsignedShort();
        final StunAttributeType type = StunAttributeType.toType(typeInt);
        final int length = buf.getUnsignedShort();
        
        if (buf.remaining() < length)
            {
            LOG.error("Error reading attribute of type: "+type+
                "\nExpected length:  "+length+
                "\nActual remaining: "+buf.remaining());
            }
        final byte[] body = new byte[length];
        buf.get(body);
        
        // Handle types we don't recognize, such as types returned from
        // "foreign" STUN servers.
        if (type == null)
            {
            LOG.debug("Did not recognize type: "+typeInt);
            return;
            }
        try
            {
            final StunAttribute attribute = createAttribute(type, body);
            if (attribute != null)
                {
                attributes.put(type, attribute);
                }
            }
        catch (final IOException e)
            {
            LOG.warn("Could not process attribute", e);
            }

        }
    
    private StunAttribute createAttribute(final StunAttributeType type, 
        final byte[] bodyBytes) throws IOException
        {
        final ByteBuffer body = ByteBuffer.wrap(bodyBytes);
        switch (type)
            {
            case MAPPED_ADDRESS:
                {
                final InetSocketAddress address = 
                    AddressAttributeReader.readAddress(body);
                return new MappedAddressAttribute(address);
                }
            case SERVER:
                {
                final String serverText = MinaUtils.toAsciiString(body);
                return new StunServerAttribute(bodyBytes.length, serverText);
                }
                
            case RELAY_ADDRESS:
                {
                final InetSocketAddress address = 
                    AddressAttributeReader.readAddress(body);
                return new RelayAddressAttribute(address);
                }
            case REMOTE_ADDRESS:
                {
                final InetSocketAddress address = 
                    AddressAttributeReader.readAddress(body);
                return new RemoteAddressAttribute(address);
                }
            case DATA:
                {
                return new DataAttribute(bodyBytes);
                }
            case CONNECT_STAT:
                {
                final long statusInt = body.getUnsignedInt();
                final ConnectionStatus status = 
                    ConnectionStatus.valueOf(statusInt);
                return new ConnectionStatusAttribute(status);
                }
                
            case ERROR_CODE:
                {
                LOG.warn("Reading error code attribute.");
                body.skip(2);
                final short errorClass = body.getUnsigned();
                final short errorNumber = body.getUnsigned();
                final String reasonPhrase = MinaUtils.getString(body);
                return new ErrorCodeAttribute(errorClass, errorNumber, reasonPhrase);
                }
                
            case ICE_PRIORITY:
                {
                final long priority = body.getUnsignedInt();
                return new IcePriorityAttribute(priority);
                }
                
            case ICE_USE_CANDIDATE:
                {
                return new IceUseCandidateAttribute();
                }
                
            case ICE_CONTROLLED:
                {
                final byte[] tieBreaker = new byte[8];
                body.get(tieBreaker);
                return new IceControlledAttribute(tieBreaker);
                }
                
            case ICE_CONTROLLING:
                {
                final byte[] tieBreaker = new byte[8];
                body.get(tieBreaker);
                return new IceControllingAttribute(tieBreaker);
                }
           
            default:
                {
                LOG.error("Unrecognized attribute: "+type);
                return null;
                }
            }
        }

    }
