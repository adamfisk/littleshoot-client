/*
 * Created on Jul 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.lastbamboo.client.util;

/**
 * Utility class for performing time conversions.
 */
public final class TimeConverter {

    /**
     * Converts a value in seconds to:
     *     "d:hh:mm:ss" where d=days, hh=hours, mm=minutes, ss=seconds, or
     *     "h:mm:ss" where h=hours<24, mm=minutes, ss=seconds, or
     *     "m:ss" where m=minutes<60, ss=seconds
     * 
     * @param seconds the seconds to convert
     * @return the converted string
     */
    public static String seconds2time(int seconds) {
        int minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        int hours = minutes / 60;
        minutes = minutes - hours * 60;
        int days = hours / 24;
        hours = hours - days * 24;
        // build the numbers into a string
        StringBuffer time = new StringBuffer();
        if (days != 0) {
            time.append(Integer.toString(days));
            time.append(":");
            if (hours < 10) time.append("0");
        }
        if (days != 0 || hours != 0) {
            time.append(Integer.toString(hours));
            time.append(":");
            if (minutes < 10) time.append("0");
        }
        time.append(Integer.toString(minutes));
        time.append(":");
        if (seconds < 10) time.append("0");
        time.append(Integer.toString(seconds));
        return time.toString();
    }
}
