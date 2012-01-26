/*
 * Created on Mar 23, 2008
 */
package org.lastbamboo.common.bencode.composite;

import java.io.IOException;
import java.io.InputStream;

import org.lastbamboo.common.bencode.Parser;
import org.lastbamboo.common.bencode.Value;
import org.lastbamboo.common.bencode.ValuePrefix;
import org.lastbamboo.common.bencode.ValueVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Spiewak
 */
@ValuePrefix('l')
public class ListValue extends CompositeValue<ListValue, Value<?>> 
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public ListValue(final Parser parser, final InputStream is) 
        {
        super(parser, is);
        }

    public Value<?> next() 
        {
        try 
            {
            return parse();
            } 
        catch (final IOException e) 
            {
            m_log.warn("Could not parse", e);
            throw new RuntimeException("Could not parse", e);
            }
    }

    public <T> T accept(final ValueVisitor<T> visitor) throws IOException
        {
        return visitor.visitListValue(this);
        }
}
