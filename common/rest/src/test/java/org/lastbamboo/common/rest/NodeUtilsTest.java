package org.lastbamboo.common.rest;

import java.util.Date;

import org.lastbamboo.common.rest.NodeUtils;

import junit.framework.TestCase;

/**
 * Tests for the xml utilities class.
 */
public class NodeUtilsTest extends TestCase
    {

    /**
     * Tests the method for parsing the date.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testParseDate() throws Exception
        {
        final String dateString = "2006-12-20T18:41:01.000Z";
        final Date date = NodeUtils.parseDateString(dateString);
        }
    }
