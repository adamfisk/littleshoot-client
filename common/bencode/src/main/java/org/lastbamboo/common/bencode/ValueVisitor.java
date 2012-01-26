package org.lastbamboo.common.bencode;

import java.io.IOException;

import org.lastbamboo.common.bencode.Parser.DelegateComposite;
import org.lastbamboo.common.bencode.composite.DictionaryValue;
import org.lastbamboo.common.bencode.composite.EntryValue;
import org.lastbamboo.common.bencode.composite.ListValue;
import org.lastbamboo.common.bencode.primitive.IntegerValue;
import org.lastbamboo.common.bencode.primitive.StringValue;

/**
 * Visitor for bencoded values.
 * 
 * @param <T> The return type of the visitor.
 */
public interface ValueVisitor<T>
    {

    T visitEntryValue(EntryValue entryValue) throws IOException;

    T visitListValue(ListValue listValue) throws IOException;

    T visitDictionaryValue(DictionaryValue dictionaryValue) throws IOException;

    T visitIntegerValue(IntegerValue integerValue) throws IOException;

    T visitStringValue(StringValue stringValue) throws IOException;

    T visitDelegateCompositeValue(DelegateComposite delegateComposite) throws IOException;

    
    }
