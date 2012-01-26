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
public final class ListTypeStream {
    private final OutputStream os;
    
    ListTypeStream(OutputStream os) {
        this.os = os;
    }
    
    public void add(Type type) throws IOException {
        type.write(os);
    }
}
