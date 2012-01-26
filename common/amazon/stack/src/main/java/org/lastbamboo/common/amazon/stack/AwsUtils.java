package org.lastbamboo.common.amazon.stack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for Amazon web services.
 */
public class AwsUtils
    {

    private static final Logger LOG = LoggerFactory.getLogger(AwsUtils.class);

    private AwsUtils()
        {
        
        }
    
    public static String getAccessKey() throws IOException
        {
        return getStringValue("accessKey");
        }

    public static String getAccessKeyId() throws IOException
        {
        return getStringValue("accessKeyId");
        }
    
    public static String getUrlBase() throws IOException 
        {
        return getStringValue("urlBase");
        }
    
    public static String getSecureUrlBase() throws IOException 
        {
        return getStringValue("secureUrlBase");
        }
    
    private static String getStringValue(final String key) throws IOException 
        {
        final Properties props = locatePropsFile();
        final String prop = props.getProperty(key);
        if (StringUtils.isBlank(prop))
            {
            throw new IOException("Could not find value for key: "+key);
            }
        return prop;
        }
    
    private static Properties locatePropsFile() throws IOException
        {
        final File lsDir = new File(SystemUtils.USER_HOME, ".littleshoot");
        final File home = new File(lsDir, "littleshoot.properties");
        if (home.isFile())
            {
            return createPropsFile(home);
            }
        final File etc = new File("/etc/littleshoot/littleshoot.properties");
        if (etc.isFile())
            {
            return createPropsFile(etc);
            }
        throw new IOException(
            "Could not find props file in "+lsDir+" or in "+home);
        }

    private static Properties createPropsFile(final File file) 
        throws IOException
        {
        final Properties props = new Properties();
        final InputStream is = new FileInputStream(file);
        props.load(is);
        return props;
        }

    public static boolean hasPropsFile()
        {
        try
            {
            locatePropsFile();
            return true;
            }
        catch (final IOException e)
            {
            LOG.debug("No props file found", e);
            return false;
            }
        }
    }
