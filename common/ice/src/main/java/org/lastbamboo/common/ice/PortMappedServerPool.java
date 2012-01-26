package org.lastbamboo.common.ice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.lastbamboo.common.portmapping.NatPmpService;
import org.lastbamboo.common.portmapping.PortMapListener;
import org.lastbamboo.common.portmapping.PortMappingProtocol;
import org.lastbamboo.common.portmapping.UpnpService;
import org.littleshoot.util.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A pool of servers that have had their ports mapped using either UPnP or
 * NAT PMP. The idea here is to pre-map ports on the router. This may not make
 * sense for all circumstances, however, as some routers might timeout the 
 * mappings. Those timeouts would force this code to carefully maintain and
 * re-allocate all mappings.
 */
public class PortMappedServerPool 
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private static final int NUM_SERVERS = 4;
    
    private final PriorityBlockingQueue<PortMappedServerSocket> m_mappedServers = 
        new PriorityBlockingQueue<PortMappedServerSocket>();
    
    private final Map<Integer, PortMappedServerSocket> m_upnpCodesToServers =
        new ConcurrentHashMap<Integer, PortMappedServerSocket>();
    
    private final Map<Integer, PortMappedServerSocket> m_natPmpCodesToServers =
        new ConcurrentHashMap<Integer, PortMappedServerSocket>();

    private final NatPmpService natPmpService;

    private final UpnpService upnpService;
    
    public PortMappedServerPool(final NatPmpService natPmpService, 
        final UpnpService upnpService)
        {
        this.natPmpService = natPmpService;
        this.upnpService = upnpService;
        for (int i=0; i<NUM_SERVERS;i++)
            {
            try 
                {
                addServer();
                } 
            catch (final IOException e) 
                {
                m_log.warn("Could not add server", e);
                }
            }
        }

    public PortMappedServerSocket getServer() throws IOException 
        {
        try 
            {
            final PortMappedServerSocket serverSocket = 
                this.m_mappedServers.take();
            addServer();
            return serverSocket;
            } 
        catch (final InterruptedException e) 
            {
            } 
        throw new IOException("Could not get a server??");
        }
    
    private void addServer() throws IOException 
        {
        final ServerSocket serverSocket = new ServerSocket();
        
        // We don't use the "any" address here (0.0.0.0) because we need to
        // tell the other ICE agent the actual address to connect to.
        serverSocket.bind(new InetSocketAddress(NetworkUtils.getLocalHost(), 0));
        final InetSocketAddress socketAddress =
            (InetSocketAddress) serverSocket.getLocalSocketAddress();
        final int port = socketAddress.getPort();
        final PortMapListener upnpPortMapListener = new PortMapListener() {
            
            public void onPortMapError() {
            }
            
            public void onPortMap(int externalPort) {
            }
        }; 
        
        final PortMapListener natPmpPortMapListener = new PortMapListener() {
            
            public void onPortMapError() {
            }
            
            public void onPortMap(int externalPort) {
            }
        };
        final int upnp = 
            this.upnpService.addUpnpMapping(PortMappingProtocol.TCP, port, 
                port, upnpPortMapListener);
        final int natPmp = 
            this.natPmpService.addNatPmpMapping(PortMappingProtocol.TCP, port, 
                port, natPmpPortMapListener);
        final PortMappedServerSocket server = 
            new PortMappedServerSocket(serverSocket);
        this.m_mappedServers.add(server);
        
        if (upnp != -1)
            {
            this.m_upnpCodesToServers.put(upnp, server);
            }
        if (natPmp != -1)
            {
            this.m_natPmpCodesToServers.put(natPmp, server);
            }
        }
    }
