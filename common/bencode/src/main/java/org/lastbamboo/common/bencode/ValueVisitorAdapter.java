package org.lastbamboo.common.bencode;

import java.io.IOException;

import org.lastbamboo.common.bencode.Parser.DelegateComposite;
import org.lastbamboo.common.bencode.composite.DictionaryValue;
import org.lastbamboo.common.bencode.composite.EntryValue;
import org.lastbamboo.common.bencode.composite.ListValue;
import org.lastbamboo.common.bencode.primitive.IntegerValue;
import org.lastbamboo.common.bencode.primitive.StringValue;

public class ValueVisitorAdapter<T> implements ValueVisitor<T>
    {

    public T visitDelegateCompositeValue(
        final DelegateComposite delegateComposite) throws IOException
        {
        return null;
        }

    public T visitDictionaryValue(final DictionaryValue dictionaryValue)
        throws IOException
        {
        // TODO Auto-generated method stub
        return null;
        }

    public T visitEntryValue(final EntryValue entryValue) throws IOException
        {
        return null;
        }

    public T visitIntegerValue(final IntegerValue integerValue)
        throws IOException
        {
        return null;
        }

    public T visitListValue(final ListValue listValue) throws IOException
        {
        return null;
        }

    public T visitStringValue(final StringValue stringValue) throws IOException
        {
        return null;
        }

    }
