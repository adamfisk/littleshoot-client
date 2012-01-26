/*
 * Created on Mar 23, 2008
 */
package org.lastbamboo.common.bencode;

import java.io.IOException;

/**
 * @author Daniel Spiewak
 */
public interface Value<T>
    {
    public T resolve() throws IOException;
    public boolean isResolved();
    
    /**
     * Accepts the specified visitor class.
     * 
     * @param <T> The type the visitor will return.
     * @param visitor The visitor to accept.
     * @return The return value of the visitor. 
     * @throws IOException If the value cannot be resolved.
     */
    <T> T accept(ValueVisitor<T> visitor) throws IOException;
    }
