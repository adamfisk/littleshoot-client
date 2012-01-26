/*
 * Created on Mar 23, 2008
 */
package org.lastbamboo.common.bencode.composite;

import java.io.IOException;

import org.lastbamboo.common.bencode.Value;
import org.lastbamboo.common.bencode.ValueVisitor;
import org.lastbamboo.common.bencode.primitive.StringValue;

/**
 * @author Daniel Spiewak
 */
public class EntryValue implements Value<EntryValue> 
    {
    private final DictionaryValue parent;
    
    private StringValue key;
    private Value<?> value;
    
    private boolean resolved = false;
    
    EntryValue(DictionaryValue parent) 
        {
        this.parent = parent;
        }

    public EntryValue resolve() throws IOException 
        {
        if (resolved) 
            {
            throw new IOException("Value already resolved");
            }
        resolved = true;
        
        if (!getKey().isResolved()) 
            {
            getKey().resolve();
            }
        
        if (!getValue().isResolved()) 
            {
            getValue().resolve();
            }
        
        return this;
        }
    
    public boolean isResolved() 
        {
        return resolved;
        }
    
    public StringValue getKey() throws IOException 
        {
        if (key == null) {
            Value<?> parse = parent.parse();
            
            if (parse instanceof StringValue) 
                {
                key = (StringValue) parse;
                } 
            else 
                {
                throw new IOException("Unexpected key type: " + key.getClass());
                }
            }
        
        return key;
        }
    
    public Value<?> getValue() throws IOException 
        {
        if (value == null) 
            {
            if (!getKey().isResolved()) 
                {
                getKey().resolve();        // reposition stream
                }
            
            value = parent.parse();
            }
        
        return value;
        }

    public <T> T accept(final ValueVisitor<T> visitor) throws IOException
        {
        return visitor.visitEntryValue(this);
        }
    }
