/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.lastbamboo.common.util.mina;

import java.io.IOException;
import java.io.InputStream;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link InputStream} that buffers data read from
 * {@link IoHandler#messageReceived(IoSession, Object)} events.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (금, 13  7월 2007) $
 */
public class IoSessionInputStream extends InputStream
    {
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final Object m_mutex = new Object();

    private final ByteBuffer m_buf;

    private volatile boolean m_closed;

    private volatile boolean m_released;

    private IOException m_exception;
    private final IoSession m_ioSession;
    private final int m_readTimeout;
    private volatile int m_rawBytesReceived = 0;
    private volatile int m_totalReadBytes;

    public IoSessionInputStream(final IoSession ioSession, 
        final int readTimeout)
        {
        m_ioSession = ioSession;
        m_readTimeout = readTimeout;
        m_buf = ByteBuffer.allocate(16);
        m_buf.setAutoExpand(true);
        m_buf.limit(0);
        }

    public int available()
        {
        if (m_released)
            {
            return 0;
            }
        else
            {
            synchronized (m_mutex)
                {
                return m_buf.remaining();
                }
            }
        }

    public void close()
        {
        m_log.debug("Closing input stream...");
        if (m_closed)
            {
            return;
            }

        synchronized (m_mutex)
            {
            m_closed = true;
            releaseBuffer();

            m_mutex.notifyAll();
            }
        }

    public int read() throws IOException
        {
        synchronized (m_mutex)
            {
            if (!waitForData())
                {
                return -1;
                }

            return m_buf.get() & 0xff;
            }
        }

    public int read(final byte[] b, final int off, final int len) 
        throws IOException
        {
        //Thread.dumpStack();
        m_log.debug("Reading data...");
        synchronized (m_mutex)
            {
            if (!waitForData())
                {
                m_log.debug("Not waiting for data...");
                return -1;
                }
            m_log.debug("Continuing with read...");
            int readBytes;

            if (len > m_buf.remaining())
                {
                readBytes = m_buf.remaining();
                }
            else
                {
                readBytes = len;
                }

            m_log.debug("Copying bytes...");
            m_buf.get(b, off, readBytes);

            m_totalReadBytes += readBytes;
            m_log.debug("Total read bytes: {}", m_totalReadBytes);
            return readBytes;
            }
        }

    private boolean waitForData() throws IOException
        {
        if (m_released)
            {
            m_log.debug("Released...");
            return false;
            }

        synchronized (m_mutex)
            {
            while (!m_released && m_buf.remaining() == 0 && m_exception == null)
                {
                try
                    {
                    m_log.debug("Waiting for data for: "+this.m_readTimeout);
                    m_mutex.wait(this.m_readTimeout);
                    }
                catch (final InterruptedException e)
                    {
                    IOException ioe = new IOException(
                            "Interrupted while waiting for more data");
                    ioe.initCause(e);
                    throw ioe;
                    }
                }
            }

        if (m_exception != null)
            {
            releaseBuffer();
            throw m_exception;
            }

        if (m_closed && m_buf.remaining() == 0)
            {
            releaseBuffer();

            return false;
            }

        return true;
        }

    private void releaseBuffer()
        {
        if (m_released)
            {
            return;
            }

        m_released = true;
        m_buf.release();
        }

    public void write(final ByteBuffer src)
        {
        m_log.debug("Writing data to input stream...");
        m_rawBytesReceived += src.remaining();
        m_log.debug("Received raw bytes: {}", m_rawBytesReceived);
        synchronized (m_mutex)
            {
            if (m_closed)
                {
                m_log.debug("InputStream closed...");
                return;
                }

            if (m_buf.hasRemaining())
                {
                m_log.debug("Copying buffer data...");
                this.m_buf.compact();
                this.m_buf.put(src);
                this.m_buf.flip();
                m_mutex.notifyAll();
                }
            else
                {
                m_log.debug("Nothing remaining in buffer...");
                this.m_buf.clear();
                this.m_buf.put(src);
                this.m_buf.flip();
                m_mutex.notifyAll();
                }
            }
        }

    public void throwException(IOException e)
        {
        synchronized (m_mutex)
            {
            if (m_exception == null)
                {
                m_exception = e;

                m_mutex.notifyAll();
                }
            }
        }
    }