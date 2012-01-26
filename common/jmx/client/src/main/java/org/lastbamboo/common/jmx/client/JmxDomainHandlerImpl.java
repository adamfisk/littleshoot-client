package org.lastbamboo.common.jmx.client;



/**
 * Simple domain handler implementation.
 * @param <Z> The type of the monitored class.
 * @param <T> The type of the MBean interface.
 */
public class JmxDomainHandlerImpl<Z, T> implements JmxDomainHandler<Z, T>
    {

    private final Class<T> m_interfaceClass;
    private final Class<Z> m_monitoredClass;

    /**
     * Creates a new domain handler for the specified classes.
     * 
     * @param monitoredClass The class that's being monitored.
     * @param interfaceClass The MBean interface class.
     */
    public JmxDomainHandlerImpl(final Class<Z> monitoredClass,
        final Class<T> interfaceClass)
        {
        this.m_interfaceClass = interfaceClass;
        this.m_monitoredClass = monitoredClass;
        }

    public String getDomain()
        {
        return this.m_monitoredClass.getPackage().getName();
        }

    public Class<T> getMBeanInterface()
        {
        return this.m_interfaceClass;
        }

    public Class<Z> getMonitoredClass()
        {
        return this.m_monitoredClass;
        }
    
    @Override
    public String toString()
        {
        return getClass().getSimpleName() +" for " + this.m_monitoredClass;
        }
    }
