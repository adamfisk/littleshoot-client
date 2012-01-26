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

package udt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;
import org.littleshoot.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import udt.UDTClient;
import udt.UDTReceiver;

/**
 * helper class for receiving a single file via UDT
 * Intended to be compatible with the C++ version in 
 * the UDT reference implementation
 * 
 * main method USAGE: 
 * java -cp ... udt.util.ReceiveFile <server_ip> <server_port> <remote_filename> <local_filename>
 */
public class TestSocket extends Application{

    private final Logger log = LoggerFactory.getLogger(getClass());
    
	private final int serverPort;
	private final String serverHost;
	private final String remoteFile;
	private final String localFile;
	private final NumberFormat format;
	
	public TestSocket(String serverHost, int serverPort, String remoteFile, String localFile){
		this.serverHost=serverHost;
		this.serverPort=serverPort;
		this.remoteFile=remoteFile;
		this.localFile=localFile;
		format=NumberFormat.getNumberInstance();
	
		format.setMaximumFractionDigits(3);
	}
	

    public void run(){
        configure();
        try{
            UDTReceiver.connectionExpiryDisabled=true;
            InetAddress myHost=localIP!=null?InetAddress.getByName(localIP):InetAddress.getLocalHost();
            UDTClient client=localPort!=-1?new UDTClient(myHost,localPort):new UDTClient(myHost);
            client.connect(serverHost, serverPort);
            
            Socket sock = client.getSocket();
            
            if (false) {
                requestAndResponseOnSocket(sock);
                client.shutdown();
                return;
            }
            InputStream in = sock.getInputStream();
            OutputStream out = sock.getOutputStream();
            byte[]readBuf=new byte[1024];
            ByteBuffer bb=ByteBuffer.wrap(readBuf);
            System.out.println("[ReceiveFile] Requesting file "+remoteFile);
            //send name file info
            byte[]fName=remoteFile.getBytes();
            bb.putInt(fName.length+1);
            
            bb.put(fName);
            bb.put((byte)0);
            
            out.write(readBuf, 0, bb.position());
            //out.flush();
            //out.write("HEAD /uri-res/N2R?urn:sha1:TIV6N2VEORKK4BDY663FBYS6CK7LUWOB HTTP/1.1".getBytes());
            
            
            //pause the sender to save some CPU time
            //out.pauseOutput();
            
            //read size info (an 4-byte int) 
            byte[]sizeInfo=new byte[4];
            
            while(in.read(sizeInfo)==0);
            
            long size=ByteBuffer.wrap(sizeInfo).getInt();
            
            System.out.println("FILE SIZE: "+size);
            final FileOutputStream fos=new FileOutputStream(remoteFile+".downloaded");
            
            System.out.println("[ReceiveFile] Reading <"+size+"> bytes.");
            //long start = System.currentTimeMillis();
            
            //and read the file data
            //Util.copy(in, fos, size, false);
            IoUtils.copy(in, fos, size);
            //long end = System.currentTimeMillis();
            //double rate=1000.0*size/1024/1024/(end-start);
            //System.out.println("[ReceiveFile] Rate: "+format.format(rate)+" MBytes/sec. "
            //        +format.format(8*rate)+" MBit/sec.");
        
            client.shutdown();
            
            if(verbose)System.out.println(client.getStatistics());
            
            
            /*
            in.close();
            out.close();
            sock.close();
            fos.close();
            */
            /*
            IoUtils.copy(in, fos, new WriteListener() {
                private int totalBytes = 0;
                public void onBytesRead(final int numBytes) {
                    System.out.println("Wrote "+numBytes+" bytes...");
                    totalBytes += numBytes;
                    if (totalBytes ==)
                }
            });
            */
        }
        catch (final Exception e) {
            
        }
    }
    
    private void requestAndResponseOnSocket(final Socket sock) 
        throws IOException {
        final InputStream is = sock.getInputStream();
        final OutputStream os = sock.getOutputStream();
        os.write("HEAD /uri-res/N2R?urn:sha1:TIV6N2VEORKK4BDY663FBYS6CK7LUWOB HTTP/1.1\r\n\r\n".getBytes());
        os.flush();
        
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String curLine = reader.readLine();
        while (StringUtils.isNotBlank(curLine)) {
            log.info("curLine: "+curLine);
            curLine = reader.readLine();
        }
        final File file = new File("visualvm.zip.downloaded");
        file.delete();
        final FileOutputStream fos = new FileOutputStream(file);
        //IOUtils.copy(is, fos);
        IoUtils.copy(is, fos, 10393398);
        
        fos.close();
        is.close();
        os.close();
    }
	
    public void run15(){
        configure();
        try{
            UDTReceiver.connectionExpiryDisabled=true;
            InetAddress myHost=localIP!=null?InetAddress.getByName(localIP):InetAddress.getLocalHost();
            UDTClient client=localPort!=-1?new UDTClient(myHost,localPort):new UDTClient(myHost);
            client.connect(serverHost, serverPort);
            
            Socket sock = client.getSocket();
            InputStream in = sock.getInputStream();
            OutputStream out = sock.getOutputStream();
            
            byte[]readBuf=new byte[1024];
            ByteBuffer bb=ByteBuffer.wrap(readBuf);
            System.out.println("[ReceiveFile] Requesting file "+remoteFile);
            //send name file info
            byte[]fName=remoteFile.getBytes();
            bb.putInt(fName.length+1);
            
            bb.put(fName);
            bb.put((byte)0);
            
            out.write(readBuf, 0, bb.position());
            out.flush();
            
            
            //pause the sender to save some CPU time
            //out.pauseOutput();
            
            //read size info (an 4-byte int) 
            byte[]sizeInfo=new byte[4];
            
            while(in.read(sizeInfo)==0);
            
            long size=ByteBuffer.wrap(sizeInfo).getInt();
            
            System.out.println("FILE SIZE: "+size);
            final FileOutputStream fos=new FileOutputStream(remoteFile+".downloaded");
            
            System.out.println("[ReceiveFile] Reading <"+size+"> bytes.");
            //long start = System.currentTimeMillis();
            
            //and read the file data
            //Util.copy(in, fos, size, false);
            IoUtils.copy(in, fos, size);
            //long end = System.currentTimeMillis();
            //double rate=1000.0*size/1024/1024/(end-start);
            //System.out.println("[ReceiveFile] Rate: "+format.format(rate)+" MBytes/sec. "
            //        +format.format(8*rate)+" MBit/sec.");
        
            client.shutdown();
            
            if(verbose)System.out.println(client.getStatistics());
            
            
            /*
            in.close();
            out.close();
            sock.close();
            fos.close();
            */
            /*
            IoUtils.copy(in, fos, new WriteListener() {
                private int totalBytes = 0;
                public void onBytesRead(final int numBytes) {
                    System.out.println("Wrote "+numBytes+" bytes...");
                    totalBytes += numBytes;
                    if (totalBytes ==)
                }
            });
            */
        }
        catch (final Exception e) {
            
        }
    }
	
	public void run2(){
		configure();
		try{
			UDTReceiver.connectionExpiryDisabled=true;
			InetAddress myHost=localIP!=null?InetAddress.getByName(localIP):InetAddress.getLocalHost();
			UDTClient client=localPort!=-1?new UDTClient(myHost,localPort):new UDTClient(myHost);
			client.connect(serverHost, serverPort);
			
			Socket sock = client.getSocket();
			InputStream in = sock.getInputStream();
			OutputStream out = sock.getOutputStream();
			//InputStream in=client.getInputStream();
			//UDTOutputStream out=(UDTOutputStream) client.getOutputStream();
			
			byte[]readBuf=new byte[1024];
			ByteBuffer bb=ByteBuffer.wrap(readBuf);
			System.out.println("[ReceiveFile] Requesting file "+remoteFile);
			//send name file info
			byte[]fName=remoteFile.getBytes();
			bb.putInt(fName.length+1);
			
			bb.put(fName);
			bb.put((byte)0);
			
			out.write(readBuf, 0, bb.position());
			out.flush();
			
			//pause the sender to save some CPU time
			//out.pauseOutput();
			
			//read size info (an 4-byte int) 
			byte[]sizeInfo=new byte[4];
			
			while(in.read(sizeInfo)==0);
			
			long size=ByteBuffer.wrap(sizeInfo).getInt();
			
			File file=new File(new String(localFile));
			System.out.println("[ReceiveFile] Write to local file <"+file.getAbsolutePath()+">");
			FileOutputStream fos=new FileOutputStream(file);
			try{
				System.out.println("[ReceiveFile] Reading <"+size+"> bytes.");
				long start = System.currentTimeMillis();
			    //and read the file data
				Util.copy(in, fos, size, false);
				long end = System.currentTimeMillis();
				double rate=1000.0*size/1024/1024/(end-start);
				System.out.println("[ReceiveFile] Rate: "+format.format(rate)+" MBytes/sec. "
						+format.format(8*rate)+" MBit/sec.");
			
				client.shutdown();
				
				if(verbose)System.out.println(client.getStatistics());
				
			}finally{
				fos.close();
			}		
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	
	public static void main(String[] fullArgs) throws Exception{
		int serverPort=65321;
		String serverHost="localhost";
		String remoteFile="";
		String localFile="";
		
		String[] args=parseOptions(fullArgs);
		
		try{
			serverHost=args[0];
			serverPort=Integer.parseInt(args[1]);
			remoteFile=args[2];
			localFile=args[3];
		}catch(Exception ex){
			usage();
			System.exit(1);
		}
		
		TestSocket rf=new TestSocket(serverHost,serverPort,remoteFile, localFile);
		rf.run();
	}
	
	public static void usage(){
		System.out.println("Usage: java -cp .. udt.util.ReceiveFile " +
				"<server_ip> <server_port> <remote_filename> <local_filename> " +
				"[--verbose] [--localPort=<port>] [--localIP=<ip>]");
	}
	
}
