/*
 * Created on Mar 19, 2008
 */
package org.lastbamboo.common.bencode.composite;

import java.io.IOException;
import java.io.OutputStream;

import org.lastbamboo.common.bencode.Type;

/**
 * @author Daniel Spiewak
 */
public abstract class CompositeType implements Type {
    private final byte prefix;
    
    public CompositeType(byte prefix) {
        this.prefix = prefix;
    }

    public final void write(OutputStream os) throws IOException {
        os.write(prefix);
        writeValue(os);
        os.write('e');
    }
    
    protected abstract void writeValue(OutputStream os) throws IOException;
}
