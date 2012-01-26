package org.lastbamboo.common.bug.server;

import java.util.Date;

/**
 * Interface for individual bug reports.
 */
public interface Bug
    {

    String getClassName();

    void setClassName(String className);

    String getCountry();

    void setCountry(String country);

    String getJavaVersion();

    void setJavaVersion(String javaVersion);

    String getLanguage();

    void setLanguage(String language);

    int getLineNumber();

    void setLineNumber(int lineNumber);

    String getLogLevel();

    void setLogLevel(String logLevel);

    String getMessage();

    void setMessage(String message);

    String getMethodName();

    void setMethodName(String methodName);

    String getOsArch();

    void setOsArch(String osArch);

    String getOsName();

    void setOsName(String osName);

    String getOsVersion();

    void setOsVersion(String osVersion);

    Date getStartTime();

    void setStartTime(Date startTime);

    String getThreadName();

    void setThreadName(String threadName);

    String getThrowable();

    void setThrowable(String throwable);

    Date getTimeStamp();

    void setTimeStamp(Date timeStamp);

    String getTimeZone();

    void setTimeZone(String timeZone);

    String getVersion();

    void setVersion(String version);
    
    String getRemoteAddress();

    void setRemoteAddress(String remoteAddress);

    long getInstanceId();

    void setInstanceId(long instanceId);

    String getUserName();

    void setUserName(String userName);
    
    void setId(Long id);
    
    Long getId();

    }