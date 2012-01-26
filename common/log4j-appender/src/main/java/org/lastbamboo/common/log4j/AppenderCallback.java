package org.lastbamboo.common.log4j;

import java.util.Collection;

import org.apache.commons.httpclient.NameValuePair;

/**
 * Interface that allows code using the appender to add additional data to
 * post to the server -- custom data that's not added by default.
 */
public interface AppenderCallback
    {

    /**
     * Allows the callback to add custom data to the bug report.
     * 
     * @param dataList The data list to add to.
     */
    void addData(Collection<NameValuePair> dataList);

    }
