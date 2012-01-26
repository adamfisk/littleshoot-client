/*
 * Copyright 2006 The asyncWeb Team.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lastbamboo.common.util.mina;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;


/**
* A decoder which writes all read bytes in to a known <code>Bytes</code>
* context until a <code>CRLF</code> has been encountered.
*/
public abstract class ConsumeToCrlfDecodingState implements DecodingState 
    {

  /**
     * Carriage return character
     */
    private static final byte CR = 13;

    /**
     * Line feed character
     */
    private static final byte LF = 10;

    private boolean m_lastIsCr;

    private ByteBuffer m_buffer;

    public DecodingState decode(final ByteBuffer in, 
        final ProtocolDecoderOutput out) throws Exception
        {
        final int beginPos = in.position();
        final int limit = in.limit();
        int terminatorPos = -1;

        for (int i = beginPos; i < limit; i++)
            {
            byte b = in.get(i);
            if (b == CR)
                {
                m_lastIsCr = true;
                }
            else
                {
                if (b == LF && m_lastIsCr)
                    {
                    terminatorPos = i;
                    break;
                    }
                m_lastIsCr = false;
                }
            }

        if (terminatorPos >= 0)
            {
            ByteBuffer product;

            int endPos = terminatorPos - 1;

            if (beginPos < endPos)
                {
                in.limit(endPos);

                if (m_buffer == null)
                    {
                    product = in.slice();
                    }
                else
                    {
                    m_buffer.put(in);
                    product = m_buffer.flip();
                    m_buffer = null;
                    }

                in.limit(limit);
                }
            else
                {
                // When input contained only CR or LF rather than actual data...
                if (m_buffer == null)
                    {
                    product = ByteBuffer.allocate(1);
                    product.limit(0);
                    }
                else
                    {
                    product = m_buffer.flip();
                    m_buffer = null;
                    }
                }
            in.position(terminatorPos + 1);
            return finishDecode(product, out);
            }
        else
            {
            in.position(beginPos);
            if (m_buffer == null)
                {
                m_buffer = ByteBuffer.allocate(in.remaining());
                m_buffer.setAutoExpand(true);
                }

            m_buffer.put(in);
            if (m_lastIsCr)
                {
                m_buffer.position(m_buffer.position() - 1);
                }
            return this;
            }
        }

    protected abstract DecodingState finishDecode(ByteBuffer product,
            ProtocolDecoderOutput out) throws Exception;
    }
