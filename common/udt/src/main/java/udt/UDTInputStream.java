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
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import udt.util.UDTStatistics;

/**
 * The UDTInputStream receives data blocks from the {@link UDTSocket}
 * as they become available, and places them into an ordered, 
 * bounded queue (the flow window) for reading by the application
 * 
 * 
 */
public class UDTInputStream extends InputStream {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
	//the socket owning this inputstream
	private final UDTSocket socket;

	//inbound application data, in-order, and ready for reading
	//by the application
	private final PriorityBlockingQueue<AppData> appData;

	private final UDTStatistics statistics;

	//the highest sequence number read by the application
	private volatile long highestSequenceNumber=0;

	//set to 'false' by the receiver when it gets a shutdown signal from the peer
	//see the noMoreData() method
	private final AtomicBoolean expectMoreData=new AtomicBoolean(true);

	private volatile boolean closed=false;

	private volatile boolean blocking=true;

	/**
	 * create a new {@link UDTInputStream} connected to the given socket
	 * @param socket - the {@link UDTSocket}
	 * @param statistics - the {@link UDTStatistics}
	 * @throws IOException
	 */
	public UDTInputStream(UDTSocket socket, UDTStatistics statistics)throws IOException{
		this.socket=socket;
		this.statistics=statistics;
		int capacity=socket!=null? 4*socket.getSession().getFlowWindowSize() : 64 ;
		appData = new PriorityBlockingQueue<AppData>(capacity);
	}

	/**
	 * create a new {@link UDTInputStream} connected to the given socket
	 * @param socket - the {@link UDTSocket}
	 * @throws IOException
	 */
	public UDTInputStream(UDTSocket socket)throws IOException{
		this(socket, socket.getSession().getStatistics());
	}

	private final byte[]single=new byte[1];

	@Override
	public int read()throws IOException {
	    log.info("Reading single byte");
		int b=0;
		while(b==0)
			b=read(single);

		if(b>0){
			return single[0];
		}
		else {
			return b;
		}
	}

	private AppData currentChunk=null;
	//offset into currentChunk
	int offset=0;

	/*
    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;

        int i = 1;
        try {
            for (; i < len ; i++) {
            c = read();
            if (c == -1) {
                break;
            }
            b[off + i] = (byte)c;
            }
        } catch (IOException ee) {
        }
        return i;
        }
        */
    
    @Override
    public int read(final byte[] target, final int off, final int len) 
        throws IOException {
        log.info("Reading data with offset '"+off+"' and len '"+len+"'");
        return read(target, off, len, 1);
    }
    
    private int read(final byte[] target, final int off, final int len, 
        final int numCalls) throws IOException {
        log.info("Reading data with offset '"+off+"' and len '"+len+"'");
        if (target == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > target.length - off) {
            log.error("Throwing index out of bounds!");
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        try{
            int read=0;
            log.debug("About to update chunk");
            updateCurrentChunk(false);
            log.debug("Updated chunk...starting while");
            while(currentChunk!=null){
                final byte[] data = currentChunk.data;
                final int targetMax = target.length - read - off;
                final int sourceMax = data.length - offset;
                int length = Math.min(targetMax, sourceMax);
                length = Math.min(length, len);
                System.arraycopy(data, offset, target, read+off, length);
                read+=length;
                offset+=length;
                //check if chunk has been fully read
                if(offset>=data.length){
                    currentChunk=null;
                    offset=0;
                }

                //if no more space left in target, exit now
                if(read == target.length || read == len){
                    log.info("Returning read of: "+read);
                    return read;
                }

                updateCurrentChunk(blocking && read==0);
            }

            if (read > 0) {
                log.info("Returning positive read");
                return read;
            }
            if(closed) {
                log.info("Closed, returning -1");
                return -1;
            }
            if(expectMoreData.get() || !appData.isEmpty()) {
                log.info("Waiting for more data");
                Thread.sleep(100 * (numCalls*2));
                return read(target, off, len, numCalls + 1);
            }
            log.info("Reached end -- no more data!!");
            //no more data
            return -1;

        } catch (final Exception ex){
            final IOException e= new IOException("Exception during read!!");
            e.initCause(ex);
            throw e;
        }
    }
    
	@Override
	public int read(final byte[]target) throws IOException {
	    log.info("Reading with straight byte array");
	    return read(target, 1);
	}
	
    private int read(final byte[]target, final int numCalls) throws IOException {
        log.info("Reading with straight byte array");
        try{
            int read=0;
            updateCurrentChunk(false);
            while(currentChunk!=null){
                byte[]data=currentChunk.data;
                int length=Math.min(target.length-read,data.length-offset);
                System.arraycopy(data, offset, target, read, length);
                read+=length;
                offset+=length;
                //check if chunk has been fully read
                if(offset>=data.length){
                    currentChunk=null;
                    offset=0;
                }
    
                //if no more space left in target, exit now
                if(read==target.length){
                    log.info("Returning amount read: "+read);
                    return read;
                }
    
                updateCurrentChunk(blocking && read==0);
            }
    
            if(read>0) {
                log.info("Returning positive read");
                return read;
            }
            if(closed) {
                log.info("Closed, returning -1");
                return -1;
            }
            if(expectMoreData.get() || !appData.isEmpty()) {
                log.info("Waiting for more data");
                Thread.sleep(100 * (numCalls*2));
                return read(target, numCalls + 1);
                //log.info("Returning 0");
                //return 0;
            }
            log.info("Reached end -- no more data!!");
            //no more data
            return -1;
    
        }catch(Exception ex){
            IOException e= new IOException();
            e.initCause(ex);
            throw e;
        }
    }

	/**
	 * Reads the next valid chunk of application data from the queue<br/>
	 * 
	 * In blocking mode,this method will block until data is available or the socket is closed, 
	 * otherwise it will wait for at most 10 milliseconds.
	 * 
	 * @throws InterruptedException
	 */
	private void updateCurrentChunk(boolean block)throws IOException{
		if(currentChunk!=null)return;

		while(true){
			try{
				if(block){
					currentChunk=appData.poll(1, TimeUnit.MILLISECONDS);
					while (!closed && currentChunk==null){
						currentChunk=appData.poll(1000, TimeUnit.MILLISECONDS);
					}
				}
				else currentChunk=appData.poll(10, TimeUnit.MILLISECONDS);
				
			}catch(InterruptedException ie){
				IOException ex=new IOException();
				ex.initCause(ie);
				throw ex;
			}
			if(currentChunk!=null){
				//check if the data is in-order
				if(currentChunk.sequenceNumber==highestSequenceNumber+1){
					highestSequenceNumber++;
					return;
				}
				else if(currentChunk.sequenceNumber<=highestSequenceNumber){
					//duplicate, drop it
					currentChunk=null;
					statistics.incNumberOfDuplicateDataPackets();
				}
				else{
					//out of order data, put back into queue and exit
					appData.offer(currentChunk);
					currentChunk=null;
					return;
				}
			}
			else return;
		}
	}

	/**
	 * new application data
	 * @param data
	 * 
	 */
	protected boolean haveNewData(long sequenceNumber,byte[]data)throws IOException{
		if(sequenceNumber<=highestSequenceNumber)return true;
		return appData.offer(new AppData(sequenceNumber,data));
	}

	@Override
	public void close()throws IOException {
	    log.info("Closing input stream.");
		if(closed) return;
		closed=true;
		noMoreData();
	}

	public UDTSocket getSocket(){
		return socket;
	}

	/**
	 * sets the blocking mode
	 * @param block
	 */
	public void setBlocking(boolean block){
		this.blocking=block;
	}

	/**
	 * notify the input stream that there is no more data
	 * @throws IOException
	 */
	protected void noMoreData()throws IOException{
		expectMoreData.set(false);
	}

	/**
	 * used for storing application data and the associated
	 * sequence number in the queue in ascending order
	 */
	public static class AppData implements Comparable<AppData>{
		final long sequenceNumber;
		final byte[] data;
		AppData(long sequenceNumber, byte[]data){
			this.sequenceNumber=sequenceNumber;
			this.data=data;
		}

		public int compareTo(AppData o) {
			return (int)(sequenceNumber-o.sequenceNumber);
		}

		public String toString(){
			return sequenceNumber+"["+data.length+"]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
			+ (int) (sequenceNumber ^ (sequenceNumber >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AppData other = (AppData) obj;
			if (sequenceNumber != other.sequenceNumber)
				return false;
			return true;
		}


	}

}
