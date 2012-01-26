package org.littleshoot.p2p;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.junit.Test;
import org.lastbamboo.common.ice.IceMediaStreamDesc;
import org.littleshoot.commom.xmpp.XmppP2PClient;
import org.littleshoot.commom.xmpp.XmppUtils;


public class XmppTest {

    @Test public void testXmpp() throws Exception {
        InetSocketAddress serverAddress = new InetSocketAddress(7777);
        
        final IceMediaStreamDesc streamDesc = 
            new IceMediaStreamDesc(true, false, "message", "http", 1, false);
        final XmppP2PClient p2p = 
            P2P.newXmppP2PClient(streamDesc, serverAddress);
        
        //final Properties props = new Properties();
        //final File file = 
        //    new File(LanternUtils.configDir(), "lantern.properties");
        //props.load(new FileInputStream(file));
        //final String user = props.getProperty("google.user");
        //final String pwd = props.getProperty("google.pwd");
        final String user = "adamfisk@gmail.com";
        final String pwd = "1745t77q";
        p2p.login(user, pwd);
        p2p.getXmppConnection().addPacketListener(new PacketListener() {
            
            public void processPacket(Packet pack) {
                //System.out.println("Got packet!!");
                //System.out.println(pack.toXML());
            }
        }, new PacketFilter() {
            
            public boolean accept(Packet arg0) {
                return true;
            }
        });
        
        
        Thread.sleep(1000);
    }
}
