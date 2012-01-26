/*
 * Created on Mar 23, 2008
 */
package org.lastbamboo.common.bencode.primitive;

import java.io.IOException;
import java.io.InputStream;

import org.lastbamboo.common.bencode.Value;

/**
 * @author Daniel Spiewak
 */
public interface VariantValue<T> extends Value<T> 
    {
    public long length() throws IOException;
    
    public InputStream getStream();
    }
