package org.lastbamboo.common.amazon.s3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;

import junit.framework.TestCase;

/**
 * This test needs to be run separately from the command line so we can pass
 * in our keys on the command line instead of checking them in to the 
 * repository.
 */
public class AmazonS3Test extends TestCase
    {

    public void testMimeTypes() throws Exception
        {
        final MimetypesFileTypeMap map = new MimetypesFileTypeMap();
        map.addMimeTypes("application/x-apple-diskimage dmg\n");
        assertEquals("application/x-apple-diskimage", map.getContentType("test.dmg"));
        //System.out.println(map.getContentType("test.exe"));
        //System.out.println(map.getContentType("test.xml"));
        //System.out.println(map.getContentType("test.txt"));
        //System.out.println(map.getContentType("test.html"));
        //final RequestEntity re = new FileRequestEntity(file, mimeType);
        
        }
    
    public static void main(final String[] args) throws Exception
        {
        final String accessKeyId = args[0];
        final String secretAccessKey = args[1];
        
        final AmazonS3 amazon = new AmazonS3Impl();
       
        amazon.createBucket("installers");
        
        final File testFile = new File("test.file");
        testFile.deleteOnExit();
        if (testFile.isFile())
            {
            testFile.delete();
            }
        
        final Writer fw = new FileWriter(testFile);
        final String fileText = "Amazon S3 Test File";
        fw.write(fileText);
        fw.close();
        
        amazon.putPublicFile("installers", testFile);
        
        System.out.println("Downloading file...");
        final File file = new File("s3File.test");
        file.deleteOnExit();
        
        amazon.getPublicFile("installers", testFile.getName(), file);
        assertEquals(testFile.length(), file.length());
        
        final BufferedReader br = new BufferedReader(new FileReader(file));
        final String content = br.readLine(); 
        
        System.out.println("Got file Content: "+content);
        assertEquals(fileText, content);
        }
    }
