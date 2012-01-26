package org.lastbamboo.client.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.littleshoot.util.Sha1Hasher;

public class PreferencesFileMapperTest
    {

    @Test
    public void testAdd() throws Exception 
        {
        final FileMapper mapper = new PreferencesFileMapper(getClass());
        
        final URI uri = new URI("http://www.files.net.file");
        final File path = 
            new File(getClass().getSimpleName()+"Test.tmp").getAbsoluteFile();
        FileUtils.touch(path);
        path.deleteOnExit();
        mapper.map(uri, path);
        
        final URI returnedUri = mapper.getUri(path);
        assertEquals("Unexpected URI", uri, returnedUri);
        
        final File returned = mapper.getFile(uri);
        assertEquals("Unexpected file", path, returned);
        
        assertTrue(mapper.hasFile(path));
        
        mapper.removeFile(uri);
        
        assertFalse("Should have removed the file!!", mapper.hasFile(path));
        
        final URI sha1 = Sha1Hasher.createSha1Urn(path);
        mapper.map(path);
        
        assertTrue(mapper.hasFile(path));
        final File sha1File = mapper.getFile(sha1);
        assertEquals(path, sha1File);
        
        final Collection<File> files = mapper.getAllFiles();
        assertEquals(1, files.size());
        final File collectionFile = files.iterator().next();
        assertEquals(path, collectionFile);
        
        mapper.clear();
        }
    
    @Test
    public void testUpdateDirectoryFile() throws Exception
        {
        final FileMapper mapper = new PreferencesFileMapper(getClass());
        
        final URI uri = new URI("http://www.files.net.file");
        final File path = 
            new File(getClass().getSimpleName()+"Test.tmp").getAbsoluteFile();
        FileUtils.touch(path);
        path.deleteOnExit();
        mapper.map(uri, path);
        
        assertFalse(mapper.updateDirectoryFile(path));
        
        mapper.removeFile(path);
        
        assertTrue(mapper.updateDirectoryFile(path));
        mapper.clear();
        }
    }
