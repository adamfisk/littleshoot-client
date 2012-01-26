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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import udt.packets.DataPacket;
import udt.packets.Shutdown;

/**
 * UDTSocket is analogous to a normal java.net.Socket, it provides input and 
 * output streams for the application
 * 
 * TODO is it possible to actually extend java.net.Socket ?
 * 
 * 
 */
public class UDTSocket extends Socket {
	
    //private final Logger logger=Logger.getLogger(getClass().getName());
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    
	//endpoint
	private final UDPEndPoint endpoint;
	
	private volatile boolean active;
	
    //processing received data
	private UDTReceiver receiver;
	private UDTSender sender;
	
	private final UDTSession session;

	private UDTInputStream inputStream;
	private UDTOutputStream outputStream;

	/**
     * @param host
     * @param port
     * @param endpoint
     * @throws SocketException,UnknownHostException
     */
	public UDTSocket(UDPEndPoint endpoint, UDTSession session)throws SocketException {
		this.endpoint=endpoint;
		this.session=session;
		this.receiver=new UDTReceiver(session,endpoint);
		this.sender=new UDTSender(session,endpoint);
	}
	
	public UDTReceiver getReceiver() {
		return receiver;
	}

	public void setReceiver(UDTReceiver receiver) {
		this.receiver = receiver;
	}

	public UDTSender getSender() {
		return sender;
	}

	public void setSender(UDTSender sender) {
		this.sender = sender;
	}

	/*
	public void setActive(boolean active) {
	    logger.info("Setting active to "+active);
		this.active = active;
	}
	*/

	public boolean isActive() {
		return active;
	}

	public UDPEndPoint getEndpoint() {
		return endpoint;
	}
	
	public final UDTSession getSession(){
		return session;
	}
	
	/**
	 * write single block of data without waiting for any acknowledgement
	 * @param data
	 */
	protected void doWrite(byte[]data)throws IOException{
	    logger.info("Called...");
		doWrite(data, 0, data.length);
		
	}
	
	/**
	 * write the given data 
	 * @param data - the data array
	 * @param offset - the offset into the array
	 * @param length - the number of bytes to write
	 * @throws IOException
	 */
	protected void doWrite(byte[]data, int offset, int length)throws IOException{
	    logger.info("Called...");
		try{
			doWrite(data, offset, length, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
		}catch(InterruptedException ie){
			IOException io=new IOException();
			io.initCause(ie);
			throw io;
		}
	}
	
	/**
	 * write the given data, waiting at most for the specified time if the queue is full
	 * @param data
	 * @param offset
	 * @param length
	 * @param timeout
	 * @param units
	 * @throws IOException - if data cannot be sent
	 * @throws InterruptedException
	 */
	protected void doWrite(byte[]data, int offset, int length, int timeout, TimeUnit units)throws IOException,InterruptedException{
	    logger.info("Got call to write data!!!: ");//+new String(data));
		int chunksize=session.getDatagramSize()-24;//need some bytes for the header
		ByteBuffer bb=ByteBuffer.wrap(data,offset,length);
		long seqNo=0;
		while(bb.remaining()>0){
			int len=Math.min(bb.remaining(),chunksize);
			byte[]chunk=new byte[len];
			bb.get(chunk);
			DataPacket packet=new DataPacket();
			seqNo=sender.getNextSequenceNumber();
			packet.setPacketSequenceNumber(seqNo);
			packet.setSession(session);
			packet.setDestinationID(session.getDestination().getSocketID());
			packet.setData(chunk);
			//put the packet into the send queue
			if(!sender.sendUdtPacket(packet, timeout, units)){
			    logger.warn("Queue full!!");
				throw new IOException("Queue full");
			}
		}
		if(length>0)active = true;
	}
	/**
	 * will block until the outstanding packets have really been sent out
	 * and acknowledged
	 */
	protected void flush() throws InterruptedException{
	    logger.info("Flushing...");
		if(!active)
		    {
		    logger.info("Not active...returning from flush call");
		    return;
		    }
		final long seqNo=sender.getCurrentSequenceNumber();
		if (seqNo < 0) {
		    logger.info("Sequence number less than zero??!!");
		    throw new IllegalStateException();
		}
		logger.info("Flushing...checking for sent out...");
		while(!sender.isSentOut(seqNo)) {
			Thread.sleep(5);
		}
		if(seqNo>-1){
		    logger.info("Flushing...waiting for ack...");
			//wait until data has been sent out and acknowledged
			while(active && !sender.haveAcknowledgementFor(seqNo)){
				sender.waitForAck(seqNo);
			}
		}
		logger.info("Flushing...pausing");
		sender.pause();
		logger.info("Flushing...returning");
	}
	
	//writes and wait for ack
	protected void doWriteBlocking(byte[]data)throws IOException, InterruptedException{
	    logger.info("Called...");
		doWrite(data);
		flush();
	}
	

    @Override
    public void bind (final SocketAddress address) throws IOException {
        // For now, we do not allow binding of the local address. The ephemeral
        // port will just be chosen.
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public synchronized void close () throws IOException {
        logger.info("Called...");
        this.active = false;
        
        final Shutdown shutdown = new Shutdown();
        shutdown.setDestinationID(this.session.getDestination().getSocketID());
        shutdown.setSession(this.session);
        try{
            this.endpoint.doSend(shutdown);
        }
        catch(final IOException e) {
            logger.error("Exception shutting down", e);
        }
        
        // Take a second to allow the shutdown message to go through.
        try {
            Thread.sleep(300);
        } catch (final InterruptedException e) {
            logger.error("Sleep interrupted?", e);
        }
        
        if (inputStream!=null) inputStream.close();
        if (outputStream!=null) outputStream.close();
        
        this.receiver.stop();
        this.sender.stop();
        
        this.endpoint.stop();
    }

    @Override
    public void connect (final SocketAddress address) throws IOException {
        logger.info("Called");
        connect (address, 60000);
    }

    @Override
    public void connect (final SocketAddress address, final int timeout) 
        throws IOException {
        logger.info("Called");
    }

    @Override
    public SocketChannel getChannel () {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public InetAddress getInetAddress () {
        logger.info("Called");
        return this.endpoint.getSocket().getInetAddress();
    }

    @Override
    public InputStream getInputStream () throws IOException {
        logger.info("Getting input stream");
        /*
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isConnected())
            throw new SocketException("Socket is not connected");
        if (isInputShutdown())
            throw new SocketException("Socket input is shutdown");
        */
        if(inputStream==null){
            inputStream=new UDTInputStream(this);
        }
        return inputStream;
    }
    
    @Override
    public boolean getKeepAlive () throws SocketException {
        logger.info("Called");
        return false;
    }

    @Override
    public InetAddress getLocalAddress () {
        logger.info("Called");
        return this.endpoint.getLocalAddress();
    }

    @Override
    public int getLocalPort () {
        logger.info("Called");
        return this.endpoint.getLocalPort();
    }

    @Override
    public SocketAddress getLocalSocketAddress () {
        logger.info("Called");
        return this.endpoint.getSocket().getLocalSocketAddress();
    }

    @Override
    public boolean getOOBInline () throws SocketException {
        logger.info("Called");
        return false;
    }
    
    @Override
    public OutputStream getOutputStream () throws IOException {
        logger.info("Getting output stream");
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isConnected())
            throw new SocketException("Socket is not connected");
        if (isOutputShutdown())
            throw new SocketException("Socket output is shutdown");
        
        if(outputStream==null){
            outputStream=new UDTOutputStream(this);
        }
        return outputStream;
    }

    @Override
    public int getPort () {
        logger.info("Getting port");
        return this.endpoint.getSocket().getPort();
    }

    @Override
    public synchronized int getReceiveBufferSize () throws SocketException {
        //throw new UnsupportedOperationException ();
        logger.info("Called");
        // This is just the default size we've seen on OSX.
        return 81660;
    }

    @Override
    public SocketAddress getRemoteSocketAddress () {
        logger.info("Called");
        return this.endpoint.getSocket().getRemoteSocketAddress();
    }

    @Override
    public boolean getReuseAddress () throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public synchronized int getSendBufferSize () throws SocketException {
        logger.info("Called");
        //throw new UnsupportedOperationException ();
        // This is just the default size we've seen on OSX.
        return 81660; 
    }

    @Override
    public int getSoLinger () throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public synchronized int getSoTimeout () throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public boolean getTcpNoDelay () throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public int getTrafficClass () throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public boolean isBound () {
        logger.info("Called");
        return this.endpoint.getSocket().isBound();
    }

    @Override
    public boolean isClosed () {
        logger.info("Called -- returning--"+!active);
        //return !active;
        
        // Returning false here since the connection is only seen as active
        // when we've written something.
        return false;
    }

    @Override
    public boolean isConnected () {
        logger.info("Called");
        final int state = this.session.getState();
        return state == 2 || state == 3;
    }

    @Override
    public boolean isInputShutdown () {
        logger.info("Called");
        return isClosed();
    }

    @Override
    public boolean isOutputShutdown () {
        logger.info("Called");
        return isClosed();
    }

    @Override
    public void sendUrgentData (final int data) throws IOException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public void setKeepAlive (final boolean on) throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public void setOOBInline (final boolean on) throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public void setPerformancePreferences (final int connectionTime,
        final int latency, final int bandwidth) {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public synchronized void setReceiveBufferSize (final int size) 
        throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public void setReuseAddress (final boolean on) throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public synchronized void setSendBufferSize (final int size) 
        throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public void setSoLinger (final boolean on, final int linger) 
        throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public synchronized void setSoTimeout (final int timeout) 
        throws SocketException {
        logger.info("Called");
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (timeout < 0)
            throw new IllegalArgumentException("timeout can't be negative");
        
        // TODO: Set this somehow?
    }

    @Override
    public void setTcpNoDelay (final boolean on) throws SocketException {
        logger.info("Called");
        // We have not way of implementing this over RUDP for now.
        //throw new UnsupportedOperationException ();
    }

    @Override
    public void setTrafficClass (final int tc) throws SocketException {
        logger.warn("Not supported!!!");
        throw new UnsupportedOperationException ();
    }

    @Override
    public void shutdownInput () throws IOException {
        logger.info("Called");
    }

    @Override
    public void shutdownOutput () throws IOException {
        logger.info("Called");
    }

}
