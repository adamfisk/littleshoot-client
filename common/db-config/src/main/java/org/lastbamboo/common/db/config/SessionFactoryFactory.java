package org.lastbamboo.common.db.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Factory class for creating Hibernate session factories.
 */
public class SessionFactoryFactory
    {
    
    private final Logger LOG = LoggerFactory.getLogger(SessionFactoryFactory.class);
    
    private final Configuration m_config;

    private final boolean m_createTables;

    /**
     * Creates a new factory for creating Hibernate {@link SessionFactory}s.
     * 
     * @param config The Hibernate configuration class.
     * @param strategy The naming strategy to use.
     * @param createTables Specifies whether or not to automatically create tables from
     * scratch.  If <code>true</code>, this will completely overwrite existing tables.
     */
    public SessionFactoryFactory(final Configuration config,
        final NamingStrategy strategy, final boolean createTables)
        {
        this.m_config = config;
        this.m_createTables = createTables;
        this.m_config.setNamingStrategy(strategy);
        this.m_config.configure();
        }

    /**
     * Creates a new Hibernate session factory.
     * 
     * @return A new Hibernate session factory.
     */
    public SessionFactory newSessionFactory()
        {
        LOG.debug("Building session factory...");
        // Not clear why the below is necessary.  Hibernate seemed to not be
        // picking up on our "auto" setting from the XML config file, but the
        // below does the trick!
        final SchemaExport export = new SchemaExport(this.m_config);
        if (this.m_createTables)
            {
            export.create(false, true);
            }
        return this.m_config.buildSessionFactory();
        }
    }
