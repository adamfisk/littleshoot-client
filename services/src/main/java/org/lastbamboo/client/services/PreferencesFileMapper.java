package org.lastbamboo.client.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.littleshoot.util.IoUtils;
import org.littleshoot.util.Sha1Hasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * File mapping that uses a {@link Preferences} instance as the backing store
 * for file paths and {@link URI}s.  This also compresses paths to save space.
 */
public class PreferencesFileMapper implements FileMapper
    {
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final Preferences m_prefs;
    
    /**
     * Creates a new instance with the default {@link Class} root.
     */
    public PreferencesFileMapper()
        {
        this(PreferencesFileMapper.class);
        }

    /**
     * Creates a new instance with the specified class as the root class to
     * store data by.  This is mostly used in testing where we don't want to
     * overwrite real data.
     * 
     * @param clazz The class to use as the root.
     */
    public PreferencesFileMapper(final Class<?> clazz)
        {
        m_prefs = Preferences.userNodeForPackage(clazz);
        }

    public Collection<File> getAllFiles()
        {
        final Collection<File> files = new HashSet<File>();
        final String[] keys;
        try
            {
            keys = m_prefs.keys();
            }
        catch (final BackingStoreException e)
            {
            m_log.error("Could not access keys!!", e);
            return Collections.emptyList();
            }
        for (final String keyString : keys)
            {
            final URI key = URI.create(keyString);
            final File curFile = getFile(key, false);
            files.add(curFile);
            }
        return files;
        }

    public File getFile(final URI uri)
        {
        return getFile(uri, true);
        }
    
    private File getFile(final URI uri, final boolean deleteKey)
        {
        final String key = uri.toASCIIString();
        final byte[] compressedPath = m_prefs.getByteArray(key, new byte[0]);
        if (compressedPath.length == 0)
            {
            try
                {
                m_log.warn("Could not find file for URI " + uri +
                    " in pref keys: "+Arrays.asList(m_prefs.keys()));
                }
            catch (final BackingStoreException e)
                {
                m_log.error("Could not print error for URI: "+uri, e);
                }
            return null;
            }
        final String inflated = IoUtils.inflateString(compressedPath);
        final File file = new File(inflated);
        if (!file.isFile())
            {
            // This can happen if the user manually removes a file, for example.
            m_log.info("File no longer exists at path: "+inflated);
            // We'll just remove it.
            if (deleteKey) 
                {
                m_prefs.remove(key);
                }
            }
        return file;
        }

    public URI getUri(final File file)
        {
        final String[] keys;
        try
            {
            keys = m_prefs.keys();
            }
        catch (final BackingStoreException e)
            {
            m_log.error("Could not access keys!!", e);
            return null;
            }
        for (final String keyString : keys)
            {
            final URI key = URI.create(keyString);
            final File curFile = getFile(key);
            if (curFile.equals(file))
                {
                return key;
                }
            }
        m_log.info("Could not find URI for file: "+file);
        return null;
        }

    public boolean hasFile(final File file)
        {
        return getUri(file) != null;
        }
    
    public boolean hasFile(final URI uri)
        {
        final String key = uri.toASCIIString();
        final byte[] compressedPath = m_prefs.getByteArray(key, new byte[0]);
        if (compressedPath.length == 0) 
            {
            return false;
            }
        
        final String inflated = IoUtils.inflateString(compressedPath);
        final File file = new File(inflated);
        return file.isFile();
        }

    public void map(final URI uri, final File file)
        {
        if (!file.isFile())
            {
            throw new IllegalArgumentException("Not a file: "+file);
            }
        final String key = uri.toASCIIString();
        
        final String filePath = file.getAbsolutePath();
        final byte[] compressedPath = IoUtils.deflate(filePath);
        m_prefs.putByteArray(key, compressedPath);
        }

    public void map(final File file)
        {
        if (!file.isFile())
            {
            throw new IllegalArgumentException("Not a file: "+file);
            }
        try
            {
            final URI sha1 = Sha1Hasher.createSha1Urn(file);
            map(sha1, file);
            }
        catch (final IOException e)
            {
            m_log.error("Could not create hash", e);
            }
        }

    public void removeFile(final File file)
        {
        final URI key = getUri(file);
        if (key != null) 
            {
            removeFile(key);
            }
        }

    public void removeFile(final URI uri)
        {
        final String key = uri.toASCIIString();
        m_prefs.remove(key);
        }

    public boolean updateDirectoryFile(final File file)
        {
        if (!hasFile(file))
            {
            map(file);
            return true;
            }
        return false;
        }

    public void clear()
        {
        final String[] keys;
        try
            {
            keys = m_prefs.keys();
            }
        catch (final BackingStoreException e)
            {
            m_log.error("Could not access keys!!", e);
            return;
            }

        for (final String keyString : keys)
            {
            m_prefs.remove(keyString);
            }
        }

    }
