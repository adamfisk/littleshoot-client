package org.lastbamboo.common.sip.proxy;

import java.io.IOException;

import org.lastbamboo.common.sip.stack.message.SipResponse;

/**
 * Interface for classes that can forward SIP requests and responses.
 */
public interface SipRequestAndResponseForwarder extends SipRequestForwarder
    {
    
    /**
     * Forwards the SIP response to the appropriate target.
     * 
     * @param response The response to forward.
     * @throws IOException If the response could not be forwarded as expected.
     */
    void forwardSipResponse(SipResponse response) throws IOException;
    }
