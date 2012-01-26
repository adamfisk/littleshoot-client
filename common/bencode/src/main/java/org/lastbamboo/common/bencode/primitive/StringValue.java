/*
 * Created on Mar 23, 2008
 */
package org.lastbamboo.common.bencode.primitive;

import java.io.IOException;
import java.io.InputStream;

import org.lastbamboo.common.bencode.Parser;
import org.lastbamboo.common.bencode.ValueVisitor;

/**
 * @author Daniel Spiewak
 */
public class StringValue implements VariantValue<byte[]> {
    private final InputStream is;
    private final long length;
    
    private boolean resolved = false;
    
    public StringValue(final Parser p, final InputStream is) throws IOException {
        this.is = is;
        this.length = is.available();
    }
    
    public InputStream getStream() {
        return is;
    }

    public byte[] resolve() throws IOException {
        if (resolved || is.available() == 0) {
            throw new IOException("Value already resolved");
        }
        resolved = true;
        
        final byte[] bytes = new byte[is.available()];
        is.read(bytes);
        
        return bytes;
    }
    
    public boolean isResolved() {
        return resolved;
    }

    public long length() throws IOException {
        return length;
    }

    public <T> T accept(ValueVisitor<T> visitor) throws IOException
        {
        return visitor.visitStringValue(this);
        }
}
