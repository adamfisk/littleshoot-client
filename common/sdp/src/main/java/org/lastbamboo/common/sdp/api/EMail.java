/*
 * EMail.java
 *
 * Created on December 18, 2001, 4:56 PM
 */

package org.lastbamboo.common.sdp.api;

/** An EMail represents an e= field contained within a SessionDescription. 
 *
 *   Please refer to IETF RFC 2327 for a description of SDP. 
 *
 * @author  deruelle
 * @version 1.0
 */
public interface EMail extends Field {

    /** Returns the value.
     */
    public String getValue() throws SdpParseException;
    
    /** Set the value.
     */
    public void setValue(String value) throws SdpException;

}

