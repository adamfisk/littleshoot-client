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
import java.io.OutputStream;

import org.littleshoot.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UDTOutputStream provides a UDT version of {@link OutputStream}
 */
public class UDTOutputStream extends OutputStream{

    private final Logger log = LoggerFactory.getLogger(getClass());
    
	private final UDTSocket socket;
	
	private volatile boolean closed;
	
	public UDTOutputStream(UDTSocket socket){
		this.socket=socket;	
	}
	
	@Override
	public void write(int args)throws IOException {
	    log.info("Writing single byte");
		checkClosed();
		socket.doWrite(new byte[]{(byte)args});
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
	    log.info("Writing data with offset '"+off+"' and len '"+len+"'");
		checkClosed();
		socket.doWrite(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
	    log.info("Writing straight byte array");
		write(b,0,b.length);
	}
	
	@Override
	public void flush()throws IOException {
	    log.info("Flushing -- IGNORING!!");
	    /*
		try{
			checkClosed();
			socket.flush();
		}catch(InterruptedException ie){
			IOException io=new IOException();
			io.initCause(ie);
			throw io;
		}
		*/
	}
	
	/**
	 * This method signals the UDT sender that it can pause the 
	 * sending thread. The UDT sender will resume when the next 
	 * write() call is executed.<br/>
	 * For example, one can use this method on the receiving end 
	 * of a file transfer, to save some CPU time which would otherwise
	 * be consumed by the sender thread.
	 */
	public void pauseOutput()throws IOException {
	    log.info("Pausing output");
		socket.getSender().pause();
	}
	
	
	/**
	 * close this output stream
	 */
	@Override
	public void close()throws IOException{
	    log.info("Closing output stream");
		closed = true;
	}
	
	private void checkClosed() throws IOException {
	    log.info("Checking closed");
		if (closed) {
		    log.info("OutputStream is closed!!");
		    throw new IOException("Stream has been closed");
		}
	}
}
