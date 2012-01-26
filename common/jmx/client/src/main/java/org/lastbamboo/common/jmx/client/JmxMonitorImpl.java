package org.lastbamboo.common.jmx.client; 

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.StringUtils;
import org.littleshoot.util.BeanUtils;
import org.littleshoot.util.CandidateProvider;
import org.littleshoot.util.JmxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that uses JMX to monitor servers.  This class records all platform
 * JMX data, but it can also easily be customized through external classes
 * implementing {@link JmxDomainHandler} and adding themselves as domain
 * handlers to this class.  Whenever we see beens from that domain, we
 * pass them to the handler class for processing.
 */
public class JmxMonitorImpl implements JmxMonitor
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private static final int JMX_PORT = 8190;
    
    private static final long SLEEP_TIME = 4000;
    private final CandidateProvider<InetAddress> m_candidateProvider;
    
    private final Set<InetAddress> m_servers = new HashSet<InetAddress>();
    
    private final Collection<ServerStatusListener> m_serverListeners =
        new LinkedList<ServerStatusListener>();
    
    private final Map<InetSocketAddress, Map<String, String>> m_allServerData = 
        new ConcurrentHashMap<InetSocketAddress, Map<String,String>>();
    
    private final Map<InetSocketAddress, MBeanServerConnection> 
        m_addressesToConnections = 
            new ConcurrentHashMap<InetSocketAddress, MBeanServerConnection>();

    private final Map<String, Collection<JmxDomainHandler>> m_domainHandlers =
        new ConcurrentHashMap<String, Collection<JmxDomainHandler>>();

    /**
     * Creates a new SIP/TURN monitoring service.
     * 
     * @param candidateProvider The class providing addresses of servers to
     * connect to.
     */
    public JmxMonitorImpl(
        final CandidateProvider<InetAddress> candidateProvider)
        {
        this.m_candidateProvider = candidateProvider;
        }
    
    public void addListener(final ServerStatusListener listener)
        {
        m_serverListeners.add(listener);
        }
    
    public void addJmxDomainHandler(final JmxDomainHandler handler)
        {
        final String domain = handler.getDomain();
        final Collection<JmxDomainHandler> handlers;
        if (this.m_domainHandlers.containsKey(domain))
            {
            handlers = this.m_domainHandlers.get(domain);
            }
        else
            {
            handlers = new LinkedList<JmxDomainHandler>();
            this.m_domainHandlers.put(domain, handlers);
            }
        handlers.add(handler);
        m_log.debug("Added handler: {} for domain: "+domain, handler);
        }
    
    public void monitor()
        {
        final Timer timer = new Timer();
        final TimerTask task = createTimerTask(timer);
        timer.schedule(task, 0L);
        }
    
    private TimerTask createTimerTask(final Timer timer)
        {
        final TimerTask task = new TimerTask()
            {
            @Override
            public void run()
                {
                final Collection<InetAddress> servers = 
                    m_candidateProvider.getCandidates();
                clearServers(servers);
                for (final InetAddress server : servers)
                    {
                    final InetSocketAddress jmxAddress =
                        new InetSocketAddress(server, JMX_PORT);
                    final InetSocketAddress sipAddress =
                        new InetSocketAddress(server, 5061);
                    try
                        {
                        final MBeanServerConnection connection = 
                            getConnection(jmxAddress);
                        addHostData(jmxAddress, connection);
                        m_servers.add(jmxAddress.getAddress());
                        notifyListeners(server, true);
                        }
                    catch (final Exception e)
                        {
                        m_log.error("JMX error accessing server at: "+
                            jmxAddress, e);
                        m_addressesToConnections.remove(jmxAddress);
                        m_servers.remove(jmxAddress.getAddress());
                        m_allServerData.remove(jmxAddress);
                        notifyListeners(server, false);
                        }
                    }
                
                timer.schedule(createTimerTask(timer), SLEEP_TIME);
                }

            };
        return task;  
        }

    /**
     * Clears servers that are not listed in the set of servers to check but
     * that we have data for (servers we think are online but aren't).
     * 
     * @param servers The new list of servers to check against the old.
     */
    private void clearServers(final Collection<InetAddress> servers)
        {
        final Set<InetAddress> coveredServers = new HashSet<InetAddress>();
        for (final InetAddress serverAddress : this.m_servers)
            {
            // If we don't have information about a server we think is online,
            // notify listeners it's offline.
            if (!servers.contains(serverAddress))
                {
                // Make sure we haven't already sent a notification.
                if (!coveredServers.contains(serverAddress))
                    {
                    notifyListeners(serverAddress, false);
                    coveredServers.add(serverAddress);
                    }
                }
            }
        }
    
    private Map<String, String> getServerData(
        final InetSocketAddress socketAddress)
        {
        if (!this.m_allServerData.containsKey(socketAddress))
            {
            final Map<String, String> serverData = 
                new ConcurrentHashMap<String, String>();
            this.m_allServerData.put(socketAddress, serverData);
            return serverData;
            }
        else
            {
            return this.m_allServerData.get(socketAddress);
            }
        }

    /**
     * Adds data for the machine.
     * 
     * @param socketAddress The address of the machine.
     * @param connection The connection to the machine.
     * @throws IOException If there's an error reading data from the servers.
     */
    private void addHostData(final InetSocketAddress socketAddress, 
        final MBeanServerConnection connection) throws Exception
        {
        m_log.debug("Domains: ");
        final List<String> domains = Arrays.asList(connection.getDomains());
        for (final String domain : domains)
            {
            m_log.debug("Domain = " + domain);
            }

        m_log.debug("MBeanServer default domain = {}", connection.getDefaultDomain());
        m_log.debug("MBean count = " + connection.getMBeanCount());
        m_log.debug("Query MBeanServer MBeans: "+connection.queryNames(null, null));

        final Map<String, String> serverData = getServerData(socketAddress);
        if (serverData.isEmpty())
            {
            // Add static data.  We don't need to request this every time.
            serverData.put("socketAddress", socketAddress.toString());
            
            map(connection, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
                OperatingSystemMXBean.class, serverData);
            map(connection, ManagementFactory.COMPILATION_MXBEAN_NAME,
                CompilationMXBean.class, serverData);
            map(connection, ManagementFactory.CLASS_LOADING_MXBEAN_NAME,
                ClassLoadingMXBean.class, serverData);
            
            // This has the uptime, but we'll get that later,
            map(connection, ManagementFactory.RUNTIME_MXBEAN_NAME,
                RuntimeMXBean.class, serverData);
            }
        
        final RuntimeMXBean bean = 
            ManagementFactory.newPlatformMXBeanProxy(connection, 
                ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
        
        serverData.put("uptime", String.valueOf(bean.getUptime()));

        // We exclude getAllThreadIds because the return values are in an
        // array we'd have to custom parse.
        map(connection, ManagementFactory.THREAD_MXBEAN_NAME, 
            ThreadMXBean.class, serverData, "getAllThreadIds");
        
        map(connection, ManagementFactory.MEMORY_MXBEAN_NAME,
            MemoryMXBean.class, serverData);

        mapCustomData(connection, serverData, domains);
        }

    private void mapCustomData(final MBeanServerConnection connection,
        final Map<String, String> serverData, final List<String> domains)
        {
        for (final String domain : domains)
            {
            final Collection<JmxDomainHandler> handlers = 
                this.m_domainHandlers.get(domain);
            if (handlers != null)
                {
                m_log.debug("Found handlers: "+handlers+" for domain: {}", 
                    domain);
                for (final JmxDomainHandler handler : handlers)
                    {
                    handleData(connection, serverData, handler.getMonitoredClass(),
                        handler.getMBeanInterface());
                    }
                }
            else
                {
                m_log.debug("No handler for domain "+domain+" in: {}", 
                    this.m_domainHandlers);
                }
            }
        }

    private <T, Z> void handleData(final MBeanServerConnection connection,
        final Map<String, String> data, final Class<T> proxiedClass, 
        final Class<Z> interfaceClass)
        {
        final ObjectName mBeanName = JmxUtils.getObjectName(proxiedClass);
        final Object mBean = 
            MBeanServerInvocationHandler.newProxyInstance(connection, 
                mBeanName, interfaceClass, true);
        mapBean(mBean, data);
        }

    private <T> void map(final MBeanServerConnection connection, 
        final String beanName, final Class<T> clazz, 
        final Map<String, String> serverData) throws IOException
        {
        map(connection, beanName, clazz, serverData, "");
        }
    
    private <T> void map(final MBeanServerConnection connection, 
        final String beanName, final Class<T> clazz, 
        final Map<String, String> serverData, final String... exclude) 
        throws IOException
        {
        final T bean = 
            ManagementFactory.newPlatformMXBeanProxy(connection, beanName, 
                clazz);
        mapBean(bean, serverData, exclude);
        }
    
    private void mapBean(final Object mBean, final Map<String, String> data)
        {
        mapBean(mBean, data, "");
        }
    
    private void mapBean(final Object mBean, final Map<String, String> data,
        final String... exclude)
        {
        m_log.debug("Mapping bean: {}", mBean.getClass().getSimpleName());
        final Set<String> excludes = new HashSet<String>();
        for (final String str : exclude)
            {
            if (!StringUtils.isBlank(str))
                {
                excludes.add(str);
                }
            }
        excludes.add("getNotificationInfo");
        data.putAll(BeanUtils.mapBean(mBean, excludes));
        }

    private MBeanServerConnection getConnection(
        final InetSocketAddress socketAddress) throws IOException
        {
        final MBeanServerConnection connection = 
            this.m_addressesToConnections.get(socketAddress);
        if (connection != null)
            {
            return connection;
            }
        
        final MBeanServerConnection newConnection =
            newConnection(socketAddress);
        this.m_addressesToConnections.put(socketAddress, newConnection);
        return newConnection;
        }

    private MBeanServerConnection newConnection(
        final InetSocketAddress socketAddress) throws IOException
        {
        m_log.debug("Create an RMI connector client and "
                + "connect it to the RMI connector server");
        final String urlString = 
            "service:jmx:rmi:///jndi/rmi://"+
            socketAddress.getAddress().getHostAddress() +
            ":" +
            socketAddress.getPort() +
            "/jmxrmi";
        m_log.debug("Connecting using URL: {}", urlString);
        final JMXServiceURL url = new JMXServiceURL(urlString);
	//final Map env = new HashMap();
	//final String[] credentials = 
	  //  new String[] {this.m_userName, this.m_password};
        //env.put("jmx.remote.credentials", credentials);

        final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        m_log.debug("Getting an MBeanServerConnection");
        return jmxc.getMBeanServerConnection();
        }

    private void notifyListeners(final InetAddress server, final boolean online)
        {
        m_log.debug("Notifying "+m_serverListeners.size()+" server listeners.  " +
            "Online status of "+server+" is "+online);
        final Runnable runner = new Runnable()
            {
            public void run()
                {
                synchronized (m_serverListeners)
                    {
                    for (final ServerStatusListener listener : m_serverListeners)
                        {
                        listener.onOnline(server, online);
                        }
                    }
                }
            };

        final Thread thread = new Thread(runner, "Server-Status-Updater");
        thread.setDaemon(true);
        thread.start();
        }
    
    /**
     * Inner class that will handle the notifications.
     */
    public class ClientListener implements NotificationListener
        {
        public void handleNotification(Notification notification,
                Object handback)
            {
            m_log.debug("Received notification:");
            m_log.debug("ClassName: " + notification.getClass().getName());
            m_log.debug("Source: " + notification.getSource());
            m_log.debug("Type: " + notification.getType());
            m_log.debug("Message: " + notification.getMessage());
            if (notification instanceof AttributeChangeNotification)
                {
                final AttributeChangeNotification acn = 
                    (AttributeChangeNotification) notification;
                m_log.debug("AttributeName: " + acn.getAttributeName());
                m_log.debug("AttributeType: " + acn.getAttributeType());
                m_log.debug("NewValue: " + acn.getNewValue());
                m_log.debug("OldValue: " + acn.getOldValue());
                }
            }
        }

    /**
     * Gets the data for the servers being monitored.
     * 
     * @return The data for the servers being monitored.
     */
    public Map<InetSocketAddress, Map<String, String>> getServerData()
        {
        final Map<InetSocketAddress, Map<String, String>> serverData = 
            new ConcurrentHashMap<InetSocketAddress, Map<String,String>>();
        
        synchronized (this.m_allServerData)
            {
            serverData.putAll(this.m_allServerData);
            }
        return serverData;
        }

    }
