/*********************************************************************************
 * Copyright (c) 2010 Forschungszentrum Juelich GmbH 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * (1) Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the disclaimer at the end. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * (2) Neither the name of Forschungszentrum Juelich GmbH nor the names of its 
 * contributors may be used to endorse or promote products derived from this 
 * software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************************/

package udt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import udt.packets.ConnectionHandshake;
import udt.packets.Destination;
import udt.packets.KeepAlive;
import udt.packets.Shutdown;

/**
 * server side session in client-server mode
 */
public class ServerSession extends UDTSession {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
	private final UDPEndPoint endPoint;

	//last received packet (for testing purposes)
	private UDTPacket lastPacket;

	public ServerSession(DatagramPacket dp,UDPEndPoint endPoint)throws SocketException,UnknownHostException{
		super("ServerSession localPort="+endPoint.getLocalPort()+" peer="+dp.getAddress()+":"+dp.getPort(),new Destination(dp.getAddress(),dp.getPort()));
		this.endPoint=endPoint;
		logger.info("Created "+toString()+" talking to "+getDestination());
	}

	int n_handshake=0;

	@Override
	public void received(UDTPacket packet, Destination peer){
		lastPacket=packet;
		if (getState()<=ready && packet instanceof ConnectionHandshake) {
			logger.info("Received ConnectionHandshake from "+peer);
			ConnectionHandshake connectionHandshake=(ConnectionHandshake)packet;
			destination.setSocketID(connectionHandshake.getSocketID());
			if(getState()<=handshaking){
				setState(handshaking);
			}
			try{
				sendResponseHandShake(connectionHandshake,peer);
				n_handshake++;
				try{
					setState(ready);
					socket=new UDTSocket(endPoint, this);
					cc.init();
				}catch(Exception uhe){
					//session is invalid
					logger.error("Error receiving",uhe);
					setState(invalid);
				}
			}catch(IOException ex){
				//session invalid
				logger.warn("Error processing ConnectionHandshake",ex);
				setState(invalid);
			}
			return;
		}else if(packet instanceof KeepAlive) {
			socket.getReceiver().resetEXPTimer();
			active = true;
			return;
		}

		if(getState()== ready) {
			active = true;

			if (packet instanceof KeepAlive) {
				//nothing to do here
				return;
			}else if (packet instanceof Shutdown) {
				setState(shutdown);
				active = false;
				logger.info("Connection shutdown initiated by the other side.");
				
				// We don't close the socket on this side because all there
				// can be data left in the buffer and we don't want the
				// server to be able to dictate when socket closes happen,
				// since we always know on this side.
				
				// NOTE: Due to the way ICE messages are exchanged, the UDT
				// "server" is actually the HTTP client and the UDT client
				// is actually the HTTP server.
				/*
                try {
                    getSocket().close();
                } catch (IOException e) {
                    logger.warn("Exception closing socket", e);
                }
                */
				return;
			}

			else{
				try{
					if(packet.forSender()){
						socket.getSender().receive(packet);
					}else{
						socket.getReceiver().receive(packet);	
					}
				}catch(Exception ex){
					//session invalid
					logger.error("Error forwarding packet",ex);
					setState(invalid);
				}
			}
			return;

		}


	}

	/**
	 * for testing use only
	 */
	UDTPacket getLastPacket(){
		return lastPacket;
	}

	protected void sendResponseHandShake(ConnectionHandshake handshake,Destination peer)throws IOException{
		ConnectionHandshake responseHandshake = new ConnectionHandshake();
		//compare the packet size and choose minimun
		long clientBufferSize=handshake.getPacketSize();
		long myBufferSize=getDatagramSize();
		long bufferSize=Math.min(clientBufferSize, myBufferSize);
		setDatagramSize((int)bufferSize);
		responseHandshake.setPacketSize(bufferSize);
		responseHandshake.setUdtVersion(4);
		responseHandshake.setInitialSeqNo(getInitialSequenceNumber());
		responseHandshake.setConnectionType(-1);
		//tell peer what the socket ID on this side is 
		responseHandshake.setSocketID(mySocketID);
		responseHandshake.setDestinationID(this.getDestination().getSocketID());
		responseHandshake.setSession(this);
		endPoint.doSend(responseHandshake);
		logger.info("Sent handshake");
	}




}

