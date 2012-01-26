/*
 * Created on Mar 23, 2008
 */
package org.lastbamboo.common.bencode.composite;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.lastbamboo.common.bencode.Parser;
import org.lastbamboo.common.bencode.ValueVisitor;
import org.lastbamboo.common.bencode.primitive.IntegerValue;
import org.lastbamboo.common.bencode.primitive.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Spiewak
 */
@org.lastbamboo.common.bencode.ValuePrefix('d')
public class DictionaryValue extends CompositeValue<DictionaryValue, EntryValue> 
    {
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private EntryValue previous;
    
    public DictionaryValue(Parser p, InputStream is) 
        {
        super(p, is);
        previous = null;
        }

    public EntryValue next() 
        {
        try 
            {
            if (previous != null) 
                {
                previous.resolve();
                }
            
            return previous = new EntryValue(this);
            } 
        catch (final IOException e) 
            {
            m_log.warn("Error accessing next..", e);
            throw new RuntimeException(e);
            }
    }

    public String getStringValue(final String keyStr)
        {
        for (final EntryValue pair : this)
            {
            try
                {
                final String key = new String(pair.getKey().resolve(), "UTF-8");
                if (key.equalsIgnoreCase(keyStr))
                    {
                    final StringValue strVal = (StringValue) pair.getValue();
                    return new String(strVal.resolve(), "UTF-8");
                    }
                }
            catch (final UnsupportedEncodingException e)
                {
                m_log.error("Bad encoding??", e);
                return "";
                }
            catch (final IOException e)
                {
                m_log.warn("IO Error resolving value", e);
                return "";
                }
            }
        return "";
        }
    
    public int getIntValue(final String keyStr)
        {
        for (final EntryValue pair : this)
            {
            try
                {
                final String key = new String(pair.getKey().resolve(), "UTF-8");
                if (key.equalsIgnoreCase(keyStr))
                    {
                    final IntegerValue val = (IntegerValue) pair.getValue();
                    return val.resolve().intValue();
                    }
                }
            catch (final UnsupportedEncodingException e)
                {
                m_log.error("Bad encoding??", e);
                return -1;
                }
            catch (final IOException e)
                {
                m_log.warn("IO Error resolving value", e);
                return -1;
                }
            }
        return -1;
        }

    public <T> T accept(final ValueVisitor<T> visitor) throws IOException
        {
        return visitor.visitDictionaryValue(this);
        }
}
