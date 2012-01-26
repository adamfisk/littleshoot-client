package org.lastbamboo.common.stun.stack;

import java.net.InetSocketAddress;

/**
 * Constants for STUN, such as the default STUN port.
 */
public class StunConstants {

    /**
     * The default port for STUN.
     */
    public static final int STUN_PORT = 3478;
    
    public static InetSocketAddress[] SERVERS = {
        new InetSocketAddress("stun.ideasip.com", StunConstants.STUN_PORT),
        new InetSocketAddress("stun01.sipphone.com", StunConstants.STUN_PORT),
        new InetSocketAddress("stun.softjoys.com", StunConstants.STUN_PORT),
        new InetSocketAddress("stun.voipbuster.com", StunConstants.STUN_PORT),
        new InetSocketAddress("stun.voxgratia.org", StunConstants.STUN_PORT),
        new InetSocketAddress("stun.xten.com", StunConstants.STUN_PORT),
        new InetSocketAddress("stun.sipgate.net", 10000),
        new InetSocketAddress("numb.viagenie.ca", StunConstants.STUN_PORT) 
    };
}
