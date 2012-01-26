package org.lastbamboo.common.sdp.parser;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.Hashtable;

import org.lastbamboo.common.sdp.InternalErrorHandler;

/** Factory for creating parsers for the SDP stuff.
*
*@version  JAIN-SIP-1.1
*
*@author M. Ranganathan <mranga@nist.gov>  <br/>
*
*<a href="{@docRoot}/uncopyright.html">This code is in the public domain.</a>
*/
public class ParserFactory {
	private static Hashtable parserTable;
	private static Class[] constructorArgs;

	static {
		constructorArgs = new Class[1];
		constructorArgs[0] = String.class;
		parserTable = new Hashtable();
		parserTable.put("a", AttributeFieldParser.class);
		parserTable.put("b", BandwidthFieldParser.class);
		parserTable.put("c", ConnectionFieldParser.class);
		parserTable.put("e", EmailFieldParser.class);
		parserTable.put("i", InformationFieldParser.class);
		parserTable.put("k", KeyFieldParser.class);
		parserTable.put("m", MediaFieldParser.class);
		parserTable.put("o", OriginFieldParser.class);
		parserTable.put("p", PhoneFieldParser.class);
		parserTable.put("v", ProtoVersionFieldParser.class);
		parserTable.put("r", RepeatFieldParser.class);
		parserTable.put("s", SessionNameFieldParser.class);
		parserTable.put("t", TimeFieldParser.class);
		parserTable.put("u", URIFieldParser.class);
		parserTable.put("z", ZoneFieldParser.class);
	}

	public static SDPParser createParser(final String field) 
        throws ParseException {
		String fieldName = Lexer.getFieldName(field);
		if (fieldName == null)
			return null;
		Class parserClass = (Class) parserTable.get(fieldName.toLowerCase());

		if (parserClass != null) {
			try {

				Constructor cons = parserClass.getConstructor(constructorArgs);
				Object[] args = new Object[1];
				args[0] = field;
				SDPParser retval = (SDPParser) cons.newInstance(args);
				return retval;

			} catch (Exception ex) {
				InternalErrorHandler.handleException(ex);
				return null; // to placate the compiler.
			}
		} else
			throw new ParseException(
				"Could not find parser for " + fieldName,
				0);
	}
}
