package org.lastbamboo.common.ice.stubs;

import org.littleshoot.mina.common.IoHandlerAdapter;
import org.lastbamboo.common.ice.transport.IceUdpConnector;

public class IceUdpConnectorStub extends IceUdpConnector
    {

    public IceUdpConnectorStub()
        {
        super(new ProtocolCodecFactoryStub(), new IoHandlerAdapter(), true);
        }

    }
