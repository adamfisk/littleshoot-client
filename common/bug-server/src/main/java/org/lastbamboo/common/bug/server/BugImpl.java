package org.lastbamboo.common.bug.server;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of the bug bean.
 */
public class BugImpl implements Bug 
    {

    private static final Logger LOG = LoggerFactory.getLogger(BugImpl.class);
    
    /**
     * The Hibernate ID of this bug.
     */
    private Long m_id;
    
    private String m_message;
    private String m_logLevel;
    private String m_className;
    private String m_methodName;
    private int m_lineNumber;
    private String m_threadName;
    private Date m_startTime;
    private Date m_timeStamp;
    private String m_javaVersion;
    private String m_osName;
    private String m_osArch;
    private String m_osVersion;
    private String m_language;
    private String m_country;
    private String m_timeZone;
    private String m_throwable;
    private String m_version;
    private String m_remoteAddress;

    private String m_userName;

    private long m_instanceId;
    
    
    /**
     * Required no-argument constructor Hibernate uses to generate new 
     * instances.
     */
    public BugImpl() 
        {
        // No argument constructor for hibernate to use.
        }
    

    /**
     * Creates a new bug from the map data.
     * 
     * @param data The map of bug data names to bug data values.
     */
    public BugImpl(final Map<String, String> data)
        {
        m_message = extractString(data, "message");
        m_logLevel = extractString(data, "logLevel");
        m_className = extractString(data, "className");
        m_methodName = extractString(data, "methodName");
        m_lineNumber = extractInt(data, "lineNumber");
        m_threadName = extractString(data, "threadName");
        m_javaVersion = extractString(data, "javaVersion");
        m_osName = extractString(data, "osName");
        m_osArch = extractString(data, "osArch");
        m_osVersion = extractString(data, "osVersion");
        m_language = extractString(data, "language");
        m_country = extractString(data, "country");
        m_timeZone = extractString(data, "timeZone");
        m_throwable = extractText(data, "throwable");
        m_version = extractString(data, "version");
        m_remoteAddress = extractString(data, "remoteAddress");
        m_userName = extractString(data, "userName");
        m_instanceId = extractLong(data, "instanceId");
        
        m_startTime = extractDate(data, "startTime");
        m_timeStamp = extractDate(data, "timeStamp");
        }

    private Date extractDate(final Map<String, String> data, final String name)
        {
        try
            {
            final String value = extractString(data, name);
            if (StringUtils.isBlank(value) || value.equals("none"))
                {
                return new Date(0);
                }
            final DateFormat df = 
                DateFormat.getDateTimeInstance(DateFormat.DEFAULT, 
                    DateFormat.DEFAULT, Locale.US);
            return df.parse(value);
            //final Date dateValue = DateUtils.iso8601ToDate(value);
            //return dateValue;
            }
        catch (final ParseException e)
            {
            LOG.warn("Could not parse date in bug report: "+this, e);
            return new Date(0);
            }
        }


    private int extractInt(final Map<String, String> data, final String name)
        {
        final String value = data.get(name);
        if (StringUtils.isBlank(value))
            {
            return -1;
            }
        if (!NumberUtils.isNumber(value))
            {
            LOG.warn("Not a number: "+value);
            return -1;
            }
        return Integer.parseInt(value);
        }
    
    private long extractLong(final Map<String, String> data, final String name)
        {
        final String value = data.get(name);
        if (StringUtils.isBlank(value))
            {
            return -1L;
            }
        if (!NumberUtils.isNumber(value))
            {
            LOG.warn("Not a number: "+value);
            return -1L;
            }
        return Long.parseLong(value);
        }

    private String extractText(final Map<String, String> data, 
        final String name)
        {
        final String value = data.get(name);
        if (StringUtils.isBlank(value))
            {
            return "none";
            }
        return value;
        }
    
    private String extractString(final Map<String, String> data, 
        final String name)
        {
        final String value = data.get(name);
        if (StringUtils.isBlank(value))
            {
            return "none";
            }
        if (value.length() >= 255)
            {
            return value.substring(0, 255);
            }
        return value;
        }

    public String getClassName()
        {
        return m_className;
        }

    public void setClassName(String className)
        {
        m_className = className;
        }

    public String getCountry()
        {
        return m_country;
        }

    public void setCountry(String country)
        {
        m_country = country;
        }

    public String getJavaVersion()
        {
        return m_javaVersion;
        }

    public void setJavaVersion(String javaVersion)
        {
        m_javaVersion = javaVersion;
        }

    public String getLanguage()
        {
        return m_language;
        }

    public void setLanguage(String language)
        {
        m_language = language;
        }

    public int getLineNumber()
        {
        return m_lineNumber;
        }

    public void setLineNumber(int lineNumber)
        {
        m_lineNumber = lineNumber;
        }

    public String getLogLevel()
        {
        return m_logLevel;
        }

    public void setLogLevel(String logLevel)
        {
        m_logLevel = logLevel;
        }

    public String getMessage()
        {
        return m_message;
        }

    public void setMessage(String message)
        {
        m_message = message;
        }

    public String getMethodName()
        {
        return m_methodName;
        }

    public void setMethodName(String methodName)
        {
        m_methodName = methodName;
        }

    public String getOsArch()
        {
        return m_osArch;
        }

    public void setOsArch(String osArch)
        {
        m_osArch = osArch;
        }

    public String getOsName()
        {
        return m_osName;
        }

    public void setOsName(String osName)
        {
        m_osName = osName;
        }

    public String getOsVersion()
        {
        return m_osVersion;
        }

    public void setOsVersion(String osVersion)
        {
        m_osVersion = osVersion;
        }

    public Date getStartTime()
        {
        return m_startTime;
        }

    public void setStartTime(Date startTime)
        {
        m_startTime = startTime;
        }

    public String getThreadName()
        {
        return m_threadName;
        }

    public void setThreadName(String threadName)
        {
        m_threadName = threadName;
        }

    public String getThrowable()
        {
        return m_throwable;
        }

    public void setThrowable(String throwable)
        {
        m_throwable = throwable;
        }

    public Date getTimeStamp()
        {
        return m_timeStamp;
        }

    public void setTimeStamp(Date timeStamp)
        {
        m_timeStamp = timeStamp;
        }

    public String getTimeZone()
        {
        return m_timeZone;
        }

    public void setTimeZone(String timeZone)
        {
        m_timeZone = timeZone;
        }

    public String getVersion()
        {
        return m_version;
        }

    public void setVersion(String version)
        {
        m_version = version;
        }

    public String getRemoteAddress()
        {
        return m_remoteAddress;
        }

    public void setRemoteAddress(String remoteAddress)
        {
        m_remoteAddress = remoteAddress;
        }

    public long getInstanceId()
        {
        return m_instanceId;
        }

    public void setInstanceId(long instanceId)
        {
        m_instanceId = instanceId;
        }

    public String getUserName()
        {
        return m_userName;
        }

    public void setUserName(final String userName)
        {
        m_userName = userName;
        }
    
    public void setId(final Long id)
        {
        m_id = id;
        }

    public Long getId()
        {
        return m_id;
        }

    @Override 
    public String toString()
        {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(getClass().getSimpleName());
        sb.append("  #class: ");
        sb.append(getClassName());
        sb.append("  #method: ");
        sb.append(getMethodName());
        sb.append("  #line number: ");
        sb.append(getLineNumber());
        sb.append("  #thread: ");
        sb.append(getThreadName());
        sb.append("  #throwable: ");
        sb.append(getThrowable());
        sb.append("  #timestamp: ");
        sb.append(getTimeStamp());
        sb.append("  #verson: ");
        sb.append(getVersion());
        return sb.toString();
        }

    }
