package org.lastbamboo.client.nativeos;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that proxies calls to perform native configuration, dispatching the
 * job to the appropriate classes for the user's operating system.
 */
public class NativeConfiguratorProxy implements NativeConfigurator
    {

    private final Logger LOG = LoggerFactory.getLogger(NativeConfiguratorProxy.class);
    
    public void configure()
        {
        if (SystemUtils.IS_OS_MAC_OSX)
            {
            configureOsx();
            }
        }

    private void configureOsx()
        {
        try
            {
            final Class clazz = OsxConfigurator.class;
            final NativeConfigurator configurator = 
                (NativeConfigurator) clazz.newInstance();
            configurator.configure();
            }
        catch (final InstantiationException e)
            {
            LOG.error("Could not create dialog", e);
            }
        catch (final IllegalAccessException e)
            {
            LOG.error("Could not create dialog", e);
            }
        }
    }
