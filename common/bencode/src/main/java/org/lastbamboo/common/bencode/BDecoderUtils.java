package org.lastbamboo.common.bencode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lastbamboo.common.bencode.Parser.DelegateComposite;
import org.lastbamboo.common.bencode.composite.DictionaryValue;
import org.lastbamboo.common.bencode.composite.EntryValue;
import org.lastbamboo.common.bencode.composite.ListValue;
import org.lastbamboo.common.bencode.primitive.IntegerValue;
import org.lastbamboo.common.bencode.primitive.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for bdecoding.
 */
public class BDecoderUtils
    {

    private static final Logger LOG = LoggerFactory.getLogger(BDecoderUtils.class);
    
    private static ValueVisitor<Value<?>> newListVisitor(final List<Object> list) 
        {
        return new ValueVisitor<Value<?>>()
            {
            public Value<?> visitDelegateCompositeValue(
                final DelegateComposite delegateComposite) throws IOException
                {
                LOG.debug("Visiting delegate composite");
                delegateComposite.resolve();
                return null;
                }
    
            public Value<?> visitEntryValue(final EntryValue entryValue) throws IOException
                {
                entryValue.resolve();
                return null;
                }
    
            public Value<?> visitIntegerValue(final IntegerValue integerValue) throws IOException
                {
                final Long intVal = integerValue.resolve();
                LOG.debug("Visited int: {}", intVal);
                list.add(intVal);
                return null;
                }
            
            public Value<?> visitStringValue(final StringValue stringValue) throws IOException 
                {
                final byte[] strVal = stringValue.resolve();
                final String str = new String(strVal, "UTF-8");
                LOG.debug("Visited string: {}", str);
                list.add(str);
                return null;
                }
    
            public Value<?> visitListValue(final ListValue listValue) throws IOException
                {
                //return findInList(listValue, this);
                final List<Object> newList = new LinkedList<Object>();
                list.add(newList);
                return traverseList(listValue, newList);
                }
            
            public Value<?> visitDictionaryValue(
                final DictionaryValue dictionaryValue) throws IOException
                {
                final Map<String, Object> subMap = new HashMap<String, Object>();
                list.add(subMap);
                return mapDictionary(dictionaryValue, subMap);
                }
            };
        }

    private static ValueVisitor<Value<?>> newMapVisitor(final String key, 
        final Map<String, Object> map) 
        {
        return new ValueVisitor<Value<?>>()
            {
            public Value<?> visitDelegateCompositeValue(
                final DelegateComposite delegateComposite) throws IOException
                {
                LOG.debug("Visiting delegate composite");
                delegateComposite.resolve();
                return null;
                }
    
            public Value<?> visitEntryValue(final EntryValue entryValue) throws IOException
                {
                entryValue.resolve();
                return null;
                }
    
            public Value<?> visitIntegerValue(final IntegerValue integerValue) throws IOException
                {
                final Long intVal = integerValue.resolve();
                LOG.debug("Visited int: {}", intVal);
                map.put(key, intVal);
                return null;
                }
            
            public Value<?> visitStringValue(final StringValue stringValue) throws IOException 
                {
                final byte[] strVal = stringValue.resolve();
                final String str = new String(strVal, "UTF-8");
                map.put(key, str);
                return null;
                }
    
            public Value<?> visitListValue(final ListValue listValue) throws IOException
                {
                //return findInList(listValue, this);
                final List<Object> list = new LinkedList<Object>();
                map.put(key, list);
                return traverseList(listValue, list);
                }
            
            public Value<?> visitDictionaryValue(
                final DictionaryValue dictionaryValue) throws IOException
                {
                //if (!key.equals("root"))
                //    {
                //    }
                final Map<String, Object> subMap = new HashMap<String, Object>();
                map.put(key, subMap);
                return mapDictionary(dictionaryValue, subMap);
                }
            };
        }
        
    private static ValueVisitor<Value<?>> newVisitor(final String str) 
        {
        return new ValueVisitor<Value<?>>()
            {
            public Value<?> visitDelegateCompositeValue(
                final DelegateComposite delegateComposite) throws IOException
                {
                LOG.debug("Visiting delegate composite");
                delegateComposite.resolve();
                return null;
                }
    
            public Value<?> visitEntryValue(final EntryValue entryValue) throws IOException
                {
                entryValue.resolve();
                return null;
                }
    
            public Value<?> visitIntegerValue(final IntegerValue integerValue) throws IOException
                {
                final Long intVal = integerValue.resolve();
                LOG.debug("Visited int: {}", intVal);
                return null;
                }
            
            public Value<?> visitStringValue(final StringValue stringValue) throws IOException 
                {
                final byte[] strVal = stringValue.resolve();
                LOG.debug("Visited string: {}", new String(strVal, "UTF-8"));
                return null;
                }
    
            public Value<?> visitListValue(final ListValue listValue) throws IOException
                {
                return findInList(listValue, this);
                }
            
            public Value<?> visitDictionaryValue(
                final DictionaryValue dictionaryValue) throws IOException
                {
                return findInDictionary(dictionaryValue, str, this);
                }
            };
        }
    
    private static Value<?> find(final Value<?> root, final String str) throws IOException
        {
        final ValueVisitor<Value<?>> visitor = newVisitor(str);
        return root.accept(visitor);
        }

    private static Value<?> findInList(final ListValue root, 
        final ValueVisitor<Value<?>> visitor) throws IOException 
        {
        LOG.debug("Visiting list");
        for (final Value<?> value : root)
            {
            LOG.debug("Traversing "+value);
            //value.resolve();
            final Value<?> visited = value.accept(visitor);
            if (visited != null)
                {
                return visited;
                }
            }
        LOG.debug("Returning null");
        return null;
        }
    
    private static Value<?> traverseList(final ListValue root, 
        final List<Object> list) throws IOException 
        {
        LOG.debug("Visiting list");
        final ValueVisitor<Value<?>> listVisitor = newListVisitor(list);
        for (final Value<?> value : root)
            {
            LOG.debug("Traversing "+value);
            //value.resolve();
            final Value<?> visited = value.accept(listVisitor);
            }
        LOG.debug("Returning null");
        return null;
        }
    
    private static Value<?> mapDictionary(final DictionaryValue root,
        final Map<String, Object> map) throws IOException
        {
        LOG.debug("Visiting dict: {}", root);
        
        for (final EntryValue pair : root)
            {
            final String key = safeStr(pair);

            final Value<?> val = pair.getValue();
            //LOG.debug("Visiting value: {}", val);
            final ValueVisitor<Value<?>> mapVisitor = newMapVisitor(key, map);
            final Value<?> found = val.accept(mapVisitor);
            }
        return null;
        }

    private static Value<?> findInDictionary(final DictionaryValue root,
        final String str, final ValueVisitor<Value<?>> visitor) throws IOException
        {
        LOG.debug("Visiting dict: {}", root);
        for (final EntryValue pair : root)
            {
            final String key = safeStr(pair);
            LOG.debug("Visiting key: {}", key);
            if (key.equals(str))
                {
                LOG.debug("Found!!");
                return pair.getValue();
                }
            final Value<?> val = pair.getValue();
            //LOG.debug("Visiting value: {}", val);
            final Value<?> found = val.accept(visitor);
            if (found != null)
                {
                LOG.debug("Found in sub-dictionary");
                return found;
                }
            LOG.debug("Still searching");
            }
        return null;
        }
    
    private static Value<?> traverseDictionary(final DictionaryValue root,
        final ValueVisitor<Value<?>> visitor) throws IOException
        {
        LOG.debug("Visiting dict: {}", root);
        for (final EntryValue pair : root)
            {
            final String key = safeStr(pair);
            LOG.debug("Visiting key: {}", key);
            final Value<?> val = pair.getValue();
            //LOG.debug("Visiting value: {}", val);
            val.accept(visitor);
            }
        return null;
        }

    private static String safeStr(final EntryValue pair) throws IOException
        {
        try
            {
            return new String(pair.getKey().resolve(), "UTF-8");
            }
        catch (final UnsupportedEncodingException e)
            {
            // Should never happen.
            LOG.error("No UTF-8?", e);
            return "";
            }
        }

    public static String findString(final DictionaryValue root, 
        final String str) throws IOException
        {
        final Value<?> val = find(root, str);
        final StringValue strVal = (StringValue) val;

        return new String(strVal.resolve(), "UTF-8");
        }

    public static int findInt(final DictionaryValue root, final String str) 
        throws IOException
        {
        final Value<?> val = find(root, str);
        final IntegerValue intVal = (IntegerValue) val;

        return intVal.resolve().intValue();
        }

    public static Map<String, Object> infoStrings(final InputStream is) 
        throws IOException
        {
        final Map<String, Object> map = new HashMap<String, Object>();
        final Parser parser = new Parser();
        final DictionaryValue root = (DictionaryValue) parser.parse(is);
        //final ValueVisitor<Value<?>> visitor = newMapVisitor("root", map);
        //root.accept(visitor);
        
        mapDictionary(root, map);
        //final Value<?> val = find(root, "info");
        //final DictionaryValue dictVal = (DictionaryValue) val;
        //return dictToMap(dictVal);
        return map;
        }
    
    public static DictionaryValue dict(final InputStream is, final String str) 
        throws IOException
        {
        final Parser parser = new Parser();
        final DictionaryValue root = (DictionaryValue) parser.parse(is);
        final Value<?> val = find(root, "info");
        final DictionaryValue dictVal = (DictionaryValue) val;
        return dictVal;
        }

    private static Map<String, String> dictToMap(final DictionaryValue dict) 
        throws IOException
        {
        final Map<String, String> map = new HashMap<String, String>();
        LOG.debug("Visiting dict: {}", dict);
        for (final EntryValue pair : dict)
            {
            final String key = safeStr(pair);
            LOG.debug("Visiting key: {}", key);
            final Value<?> val = pair.getValue();
            final ValueVisitor<Value<?>> visitor = new ValueVisitorAdapter<Value<?>>()
                {
                @Override
                public Value<?> visitIntegerValue(final IntegerValue integerValue)
                    throws IOException
                    {
                    final IntegerValue intVal = (IntegerValue) val;
                    map.put(key, intVal.resolve().toString());
                    return null;
                    }
    
                @Override
                public Value<?> visitStringValue(final StringValue stringValue) 
                    throws IOException
                    {
                    final StringValue strVal = (StringValue) val;
                    map.put(key, new String(strVal.resolve(), "UTF-8"));
                    return null;
                    }
                
                public Value<?> visitListValue(final ListValue listValue) throws IOException
                    {
                    findInList(listValue, this);
                    return null;
                    }
                
                public Value<?> visitDictionaryValue(
                    final DictionaryValue dictionaryValue) throws IOException
                    {
                    //findInDictionary(dictionaryValue, null, this);
                    traverseDictionary(dictionaryValue, this);
                    return null;
                    }
                };
            val.accept(visitor);
            }
        return map;
        }

    public static Map<String, Object> map(final File streamFile) 
        throws IOException
        {
        final InputStream is = new FileInputStream(streamFile);
        return infoStrings(is);
        }
    
    public static void print(File streamFile) throws IOException
        {
        final InputStream is = new FileInputStream(streamFile);
        final Parser parser = new Parser();
        final DictionaryValue root = (DictionaryValue) parser.parse(is);
        printDict(root, 0);
        }

    private static void printDict(final DictionaryValue root, final int numIndents) 
        throws IOException
        {
        print("{", numIndents, true, true);
        for (final EntryValue pair : root)
            {
            print("\"" + new String(pair.getKey().resolve()) + "\" -> ", numIndents, false);

            if (pair.getValue() instanceof IntegerValue)
                {
                print(pair.getValue().resolve().toString(), 0);
                }
            else if (pair.getValue() instanceof StringValue)
                {
                print('"' + new String(
                                ((StringValue) pair.getValue())
                                        .resolve()) + '"', 0);
                }
            else if (pair.getValue() instanceof DictionaryValue)
                {
                printDict((DictionaryValue) pair.getValue(), numIndents + 1);
                }
            else if (pair.getValue() instanceof ListValue)
                {
                printList((ListValue) pair.getValue(), numIndents);
                }
            else
                {
                print("Unrecognized type", 0, false);
                }
            }
        print("}", numIndents);
        }


    private static void print(String str, int numIndents, boolean newLine)
        {
        print(str, numIndents, false, newLine);
        }
    
    private static void print(String str, int numIndents, boolean newLinePrefix, boolean newLineSuffix)
        {
        final StringBuilder sb = new StringBuilder();
        if (newLinePrefix)
            {
            sb.append("\n");
            }
        for (int i =0; i< numIndents*4; i++)
            {
            sb.append(' ');
            }
        sb.append(str);
        if (newLineSuffix)
            {
            sb.append("\n");
            }
        System.out.print(sb);
        }

    private static void print(final String str, int numIndents)
        {
        print(str, numIndents, true);
        }

    private static void printList(final ListValue root, final int numIndents) 
        throws IOException
        {
        print("(", 0, false);
        for (final Value<?> value : root)
            {
            if (value instanceof DictionaryValue)
                {
                final DictionaryValue dict = (DictionaryValue) value;
                //System.out.print("from list");
                printDict(dict, numIndents+1);
                }
            else if (value instanceof IntegerValue)
                {
                print(value.resolve().toString(), 0, false);
                }
            else if (value instanceof StringValue)
                {
                print(new String(((StringValue) value).resolve()), 0, false);
                }
            else if (value instanceof ListValue)
                {
                printList((ListValue) value, numIndents);
                /*
                print("[", numIndents+1);
                for (Value<?> subValue : (ListValue) value)
                    {
                    print("  " + subValue.resolve().toString(), numIndents+1);
                    }
                print("]", numIndents+1);
                */
                }
            else
                {
                print("Unrecognized type", 0, false);
                }
            }
        print(")", 0, true);
        }

    public static int numFiles(final Map<String, Object> torrentMap)
        {
        final Map<String, Object> infoMap = 
            (Map<String, Object>) torrentMap.get("info");
        if (infoMap.containsKey("files"))
            {
            final List<Object> files = (List<Object>) infoMap.get("files");
            return files.size();
            }
        return 1;
        }

    public static String name(final Map<String, Object> torrentMap)
        {
        final Map<String, Object> infoMap = 
            (Map<String, Object>) torrentMap.get("info");
        
        final Object name = infoMap.get("name");
        if (name instanceof String)
            {
            return (String) name;
            }
        else if (name instanceof byte[])
            {
            final byte[] data = (byte[]) name;
            try
                {
                return new String(data, "UTF-8");
                }
            catch (final UnsupportedEncodingException e)
                {
                LOG.error("No decoding?", e);
                };
            }

        LOG.error("Name not a string, but rather: "+name.getClass().getSimpleName());
        return "Could Not Determine Torrent Name-"+new Random().nextInt();
        }

    public static long getLength(final Map<String, Object> torrentMap)
        {
        final Map<String, Object> infoMap = 
            (Map<String, Object>) torrentMap.get("info");
        
        if (infoMap.containsKey("length"))
            {
            final Object length = infoMap.get("length");
            if (length instanceof Long)
                {
                return ((Long) infoMap.get("length")).longValue();
                }
            else
                {
                LOG.error("Length not a long, but rather: "+length);
                return -1L;
                }
            }
        else
            {
            // This means it's a multi-file torrent.  We don't know the full
            // length yet for multi-file torrents.
            return -1L;
            }
        }
    }
