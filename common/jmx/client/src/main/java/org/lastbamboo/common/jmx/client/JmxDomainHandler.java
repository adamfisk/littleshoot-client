package org.lastbamboo.common.jmx.client;

/**
 * A class that can process JMX beans for a particular domain.  This is just
 * a marker interface.  Since JMX uses a lot of reflection underneath, this
 * is all we need. 
 */
public interface JmxDomainHandler<Z, T>
    {

    /**
     * Get the class that's being monitored.
     * 
     * @return The class that's being monitored.
     */
    Class<T> getMBeanInterface();

    /**
     * The domain for the handler, typically the java package of the monitored
     * class.
     * 
     * @return The domain string.
     */
    String getDomain();

    Class<Z> getMonitoredClass();
    
    }
