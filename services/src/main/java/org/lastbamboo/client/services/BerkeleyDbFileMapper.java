package org.lastbamboo.client.services;

import java.io.File;
import java.net.URI;
import java.util.Collection;

/**
 * Stores data in Berkeley DB.
 */
public class BerkeleyDbFileMapper implements FileMapper
    {
    /*
    private static final Logger LOG = 
        LoggerFactory.getLogger(BerkeleyDbFileMapper.class);

    private final Environment m_dbEnv;

    private final StoredClassCatalog m_catalog;

    private final Map<URI, File> m_uriToFileMap;

    private final Database m_filesDb;

    private volatile boolean m_closed = false;
    */
    
    /**
     * Creates a new Berkeley DB class for mapping files.
     */
    public BerkeleyDbFileMapper()
        {
        //this(getDbDir());
        }
        
    /**
     * Creates a new Berkeley DB class for mapping files.
     * 
     * @param configDir The configuration directory to use.
     */
    public BerkeleyDbFileMapper(final File configDir)
        {
        /*
        final EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);

        final DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(true);
        
        final File dbDir = configDir;
        try
            {
            m_dbEnv = new Environment(dbDir, envConfig);
            
            final Database catalogueDb = 
                m_dbEnv.openDatabase(null, "java_catalogue", dbConfig);
            m_catalog = new StoredClassCatalog(catalogueDb);
            
            final EntryBinding filesKeyBinding = 
                new SerialBinding(m_catalog, URI.class);
            final EntryBinding filesValueBinding = 
                new SerialBinding(m_catalog, File.class);
            
            // Passing null for the transaction makes it default to auto-commit.
            this.m_filesDb = 
                m_dbEnv.openDatabase(null, "shoot_files", dbConfig);
            
            this.m_uriToFileMap = 
                new StoredMap(this.m_filesDb, filesKeyBinding, 
                    filesValueBinding, true);
            }
        catch (final DatabaseException e)
            {
            LOG.error("Could not create database", e);
            throw new RuntimeException("Could not create database", e);
            }
        
        final Runnable closeDb = new Runnable()
            {
            public void run()
                {
                System.out.println("Closing the database...");
                try
                    {
                    m_closed = true;
                    // This also closes the database.
                    synchronized (m_uriToFileMap)
                        {
                        m_filesDb.close();
                        m_catalog.close();
                        m_dbEnv.close();
                        }
                    }
                catch (final DatabaseException e)
                    {
                    LOG.error("Could not close database", e);
                    }
                System.out.println("Finished closing the database...");
                }
            };
        final Thread closeDbThread = 
            new Thread(closeDb, "Close-Berkeley-DB-Thread");
        Runtime.getRuntime().addShutdownHook(closeDbThread);
        */
        }

    public void clear()
        {
        // TODO Auto-generated method stub
        
        }

    public Collection<File> getAllFiles()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public File getFile(URI sha1)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public URI getUri(File file)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public boolean hasFile(File file)
        {
        // TODO Auto-generated method stub
        return false;
        }

    public void map(URI uri, File file)
        {
        // TODO Auto-generated method stub
        
        }

    public void map(File file)
        {
        // TODO Auto-generated method stub
        
        }

    public void removeFile(File onDisk)
        {
        // TODO Auto-generated method stub
        
        }

    public void removeFile(URI sha1)
        {
        // TODO Auto-generated method stub
        
        }

    public boolean updateDirectoryFile(File file)
        {
        return false;
        }

    public boolean hasFile(URI uri) 
        {
        return false;
        }

    /*
    private static File getDbDir()
        {
        final long id = Prefs.getId();
        final File configDir = new File(SystemUtils.USER_HOME, ".littleshoot");
        
        if (!configDir.isDirectory())
            {
            if (!configDir.mkdirs())
                {
                LOG.warn("Could not create directory: {}", configDir);
                }
            }
        final File userSpecificConfigDir = 
            new File(configDir, String.valueOf(id));
        
        LOG.debug("Setting database directory to: "+userSpecificConfigDir);
        
        if (!userSpecificConfigDir.isDirectory())
            {
            if (!userSpecificConfigDir.mkdirs())
                {
                LOG.warn("Could not create directory: {}", 
                    userSpecificConfigDir);
                }
            }
        return userSpecificConfigDir;
        }

    public void map(final URI uri, final File file)
        {
        LOG.debug("Adding file: {}", file);
        if (this.m_closed)
            {
            LOG.debug("Database closed...");
            return;
            }
        synchronized (this.m_uriToFileMap)
            {
            this.m_uriToFileMap.put(uri, file);
            }
        }

    public File getFile(final URI uri)
        {
        if (this.m_closed)
            {
            LOG.debug("Database closed...");
            return null;
            }
        if (uri == null)
            {
            LOG.error("Null sha1");
            throw new IllegalArgumentException("Null SHA-1");
            }
        final File file = this.m_uriToFileMap.get(uri);
        if (file == null)
            {
            LOG.warn("No file for SHA-1 "+uri+" in: {}", 
                this.m_uriToFileMap.keySet());
            }
        return file;
        }
    
    public URI getUri(final File file)
        {
        // Clearly not efficient, but luckily we don't need it often.
        if (this.m_closed)
            {
            LOG.debug("Database closed...");
            return null;
            }
        synchronized (this.m_uriToFileMap)
            {
            final Set<Entry<URI, File>> entries = 
                this.m_uriToFileMap.entrySet();
            for (final Entry<URI, File> entry : entries)
                {
                if (entry.getValue().equals(file))
                    {
                    return entry.getKey();
                    }
                }
            }
        return null;
        }

    public void map(final File file)
        {
        try
            {
            final URI sha1 = Sha1Hasher.createSha1Urn(file);
            map(sha1, file);
            }
        catch (final IOException e)
            {
            LOG.error("Could not create hash", e);
            }
        }

    public Collection<File> getAllFiles()
        {
        if (this.m_closed)
            {
            LOG.debug("Database closed...");
            return Collections.emptySet();
            }
        final Collection<File> copy = new LinkedList<File>();
        try
            {
            synchronized (this.m_uriToFileMap)
                {
                // We don't use addAll here because Berkeley DB has funky 
                // semantics for it.  We explicitly make a copy to ensure 
                // external code uses a true collection.
                final Collection<File> files = this.m_uriToFileMap.values();
                for (final File file : files)
                    {
                    copy.add(file);
                    }
                return copy;
                }
            }
        catch (final RuntimeExceptionWrapper e)
            {
            LOG.error("Could not get files: {}", e);
            return Collections.emptySet();
            }
        }

    public void removeFile(final File file)
        {
        if (!file.exists())
            {
            LOG.warn("No file at: "+file);
            return;
            }
        if (this.m_closed)
            {
            LOG.debug("Database closed...");
            return;
            }
        try
            {
            final URI sha1 = Sha1Hasher.createSha1Urn(file);
            synchronized (this.m_uriToFileMap)
                {
                this.m_uriToFileMap.remove(sha1);
                }
            }
        catch (final IOException e)
            {
            LOG.error("Could not create hash", e);
            }
        }

    public void removeFile(final URI sha1)
        {
        if (this.m_closed)
            {
            LOG.debug("Database closed...");
            return;
            }
        synchronized (this.m_uriToFileMap)
            {
            this.m_uriToFileMap.remove(sha1);
            }
        }

    public boolean updateDirectoryFile(final File file)
        {
        if (this.m_closed)
            {
            LOG.debug("Database closed...");
            return false;
            }
        final URI sha1;
        try
            {
            sha1 = Sha1Hasher.createSha1Urn(file);
            }
        catch (final IOException e)
            {
            LOG.error("Could not create hash", e);
            return false;
            }
        synchronized (this.m_uriToFileMap)
            {
            if (this.m_uriToFileMap.containsKey(sha1))
                {
                LOG.debug("File already published, returning...");
                return false;
                }
            }
        map(sha1, file);
        return true;
        }

    public boolean hasFile(final File file)
        {
        synchronized (this.m_uriToFileMap)
            {
            final Collection<File> values = this.m_uriToFileMap.values();
            return values.contains(file);
            }
        }

    public void clear()
        {
        this.m_uriToFileMap.clear();
        }
        */
    }
