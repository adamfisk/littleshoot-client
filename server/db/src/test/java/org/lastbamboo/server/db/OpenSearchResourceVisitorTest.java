package org.lastbamboo.server.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.lastbamboo.server.db.MetaFileResourceImpl;
import org.lastbamboo.server.db.OpenSearchResourceVisitor;
import org.lastbamboo.server.db.OpenSearchResourceVisitorFactory;
import org.lastbamboo.server.db.OpenSearchResourceVisitorFactoryImpl;
import org.lastbamboo.server.db.stubs.FileResourceStub;
import org.lastbamboo.server.resource.MetaFileResource;

/**
 * Tests the class for outputing Amazon Open Search results.
 */
public class OpenSearchResourceVisitorTest extends TestCase
    {

    /**
     * Tests for general Open Search result creation.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testVisit() throws Exception
        {
        final Collection<MetaFileResource> resources = createFileResources();
        final OpenSearchResourceVisitorFactory factory = 
            new OpenSearchResourceVisitorFactoryImpl();
        final OpenSearchResourceVisitor visitor = 
            factory.createVisitor("test", 1, 10, resources, resources.size());
        
        // Write to the console for visual debugging.
        //visitor.write(System.out);
        
        //writeToFile(visitor);
        }

    private void writeToFile(OpenSearchResourceVisitor visitor) 
        throws IOException
        {
        final File testFile = new File("test.xml");
        
        final OutputStream os = new FileOutputStream(testFile);
        visitor.write(os);
        os.close();
        }

    private Collection<MetaFileResource> createFileResources()
        {
        final Collection<MetaFileResource> resources = 
            new LinkedList<MetaFileResource>();
        for (int i = 0; i < 10; i++)
            {
            final MetaFileResource fr = 
                new MetaFileResourceImpl(new FileResourceStub());
            fr.setSize(i);
            resources.add(fr);
            }
        return resources;
        }
    }
