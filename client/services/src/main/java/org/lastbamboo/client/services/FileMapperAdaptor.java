package org.lastbamboo.client.services;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;

public class FileMapperAdaptor implements FileMapper
    {

    public Collection<File> getAllFiles()
        {
        return Collections.emptyList();
        }

    public File getFile(URI sha1)
        {
        return null;
        }

    public URI getUri(File file)
        {
        return null;
        }

    public boolean hasFile(File file)
        {
        return false;
        }

    public void map(URI uri, File file)
        {
        }

    public void map(File file)
        {
        }

    public void removeFile(File onDisk)
        {
        }

    public void removeFile(URI sha1)
        {
        }

    public boolean updateDirectoryFile(File file)
        {
        return false;
        }

    public void clear()
        {
        }

    public boolean hasFile(URI uri) 
        {
        return false;
        }

    }
