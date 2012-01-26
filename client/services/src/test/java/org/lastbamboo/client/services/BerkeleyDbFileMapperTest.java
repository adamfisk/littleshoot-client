package org.lastbamboo.client.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.littleshoot.util.Sha1Hasher;


public class BerkeleyDbFileMapperTest
    {

    @Test public void testDb() throws Exception
        {
        
        final String tempDir = System.getProperty("java.io.tmpdir");
        final File testDir = new File(tempDir, getClass().getSimpleName());
        if (testDir.exists())
            {
            FileUtils.forceDelete(testDir);
            assertFalse(testDir.exists());
            }
        
        //final File testDir = File.createTempFile(getClass().getName(), null);
        testDir.deleteOnExit();
        testDir.mkdir();
        final BerkeleyDbFileMapper mapper = new BerkeleyDbFileMapper(testDir);
        
        Collection<File> files = mapper.getAllFiles();
        for (final File curFile : files)
            {
            System.out.println("First wave file is:" + curFile);
            }
        
        final File file = new File("pom.xml").getAbsoluteFile();
        assertTrue(file.isFile());
        mapper.map(file);
        
        final File mapped = mapper.getFile(Sha1Hasher.createSha1Urn(file));
        assertEquals(file, mapped);
        files = mapper.getAllFiles();
        for (final File curFile : files)
            {
            assertEquals("File not expected", file, curFile);
            }
        assertEquals(1, files.size());
        }
    }
