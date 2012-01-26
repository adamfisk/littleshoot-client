package org.lastbamboo.common.stun.stack.encoder;

import java.util.Random;

import junit.framework.TestCase;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControlledAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IcePriorityAttribute;

public class StunMessageEncoderTest extends TestCase
    {

    public void testEncode()
        {
        final StunMessageEncoder encoder = new StunMessageEncoder();
        // Now send a BindingRequest with PRIORITY, USE-CANDIDATE,
        // ICE-CONTROLLING etc.
        final long priority = 427972L;

        final IcePriorityAttribute priorityAttribute = new IcePriorityAttribute(
                priority);

        // The agent uses the same tie-breaker throughout the session.
        final Random rand = new Random();
        final byte[] tieBreaker = new byte[16];
        rand.nextBytes(tieBreaker);
        
        final StunAttribute controlling =
            new IceControlledAttribute(tieBreaker);
            

        // TODO: Add CREDENTIALS attribute.
        final BindingRequest request = 
            new BindingRequest(priorityAttribute, controlling);
        
        // Make sure no exceptions are thrown.
        final ByteBuffer encoded = encoder.encode(request);
        
        
        
        }

    }
