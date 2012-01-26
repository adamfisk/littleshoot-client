package org.lastbamboo.common.log4j;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * Utility methods for Log4J.
 */
public class Log4jUtils
    {

    /**
     * Extracts the throwable information from the logging event.
     * 
     * @param le The logging event.
     * @return The string with the stack trace data.
     */
    public static String getThrowableString(final LoggingEvent le)
        {
        final ThrowableInformation ti = le.getThrowableInformation();
        final String throwableString;
        if (ti != null)
            {
            final StringBuilder sb = new StringBuilder();
            final String[] throwableStr = ti.getThrowableStrRep();
            for (final String str : throwableStr)
                {
                sb.append(str);
                sb.append("\n");
                }
            throwableString = sb.toString();
            }
        else
            {
            throwableString = "No throwable.";
            }
        return throwableString;
        }

    }
