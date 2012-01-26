package org.lastbamboo.common.bencode;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.lastbamboo.common.bencode.composite.DictionaryValue;
import org.lastbamboo.common.bencode.composite.EntryValue;
import org.lastbamboo.common.bencode.composite.ListValue;
import org.lastbamboo.common.bencode.primitive.IntegerValue;
import org.lastbamboo.common.bencode.primitive.StringValue;


public class BDecoderTest
    {

    @Test public void testFailedTorrent() throws Exception
        {
        }
    
    @Test public void testBDecoder() throws Exception
        {
        final Parser parser = new Parser();
        final ByteArrayInputStream is = new ByteArrayInputStream(
                "ld5:Helloi5e5:World7:Testing4:Fivei4ee10:Test Valueli123ei456ei7890eee"
                        .getBytes());

        final ListValue root = (ListValue) parser.parse(is);
        for (final Value<?> value : root)
            {
            if (value instanceof DictionaryValue)
                {
                final DictionaryValue dict = (DictionaryValue) value;

                System.out.println('{');
                for (EntryValue pair : dict)
                    {
                    System.out.print("  \""
                            + new String(pair.getKey().resolve()) + "\" -> ");

                    if (pair.getValue() instanceof IntegerValue)
                        {
                        System.out.println(pair.getValue().resolve().toString());
                        }
                    else if (pair.getValue() instanceof StringValue)
                        {
                        System.out.println('"' + new String(
                                        ((StringValue) pair.getValue())
                                                .resolve()) + '"');
                        }
                    else
                        {
                        fail("Unrecognized type");
                        }
                    }
                System.out.println('}');
                }
            else
                if (value instanceof StringValue)
                    {
                    System.out.println(new String(((StringValue) value)
                            .resolve()));
                    }
                else if (value instanceof ListValue)
                    {
                    System.out.println("[");
                    for (Value<?> subValue : (ListValue) value)
                        {
                        System.out.println("  "
                                + subValue.resolve().toString());
                        }
                    System.out.println("]");
                    }
                else
                    {
                    fail("Unrecognized type");
                    }
            }
        }
    }
