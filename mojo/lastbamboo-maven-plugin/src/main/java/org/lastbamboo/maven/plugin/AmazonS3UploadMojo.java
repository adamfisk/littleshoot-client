package org.lastbamboo.maven.plugin;

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which uploads files to Amazon S3.
 *
 * @goal s3-upload
 * @phase package
 * @requiresDependencyResolution runtime
 */
public final class AmazonS3UploadMojo extends AbstractMojo
    {
    
    /** 
     * The project itself.  We use this to traverse the dependencies when
     * building the application.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly 
     */ 
    private MavenProject m_project;
    
    /**
     * The list of files to upload.  These are simply string paths.
     * 
     * @parameter 
     * @required
     */
    private List s3Files;
    
    /**
     * The name of the bucket to upload to.
     * 
     * @parameter 
     * @required
     */  
    private String s3BucketName;
    
    /**
     * {@inheritDoc}
     */
    public void execute () throws MojoExecutionException, MojoFailureException
        {
        /*
        final AmazonS3 s3;
        try
            {
            s3 = new AmazonS3Impl();
            }
        catch (final IOException e)
            {
            throw new MojoFailureException("S3 key file not properly configured");
            }
        try
            {
            s3.createBucket(s3BucketName);
            }
        catch (final IOException e)
            {
            System.out.println(
                "Could not create bucket: "+s3BucketName+".  Already exists?");
            }
        
        for (final Iterator iter = s3Files.iterator(); iter.hasNext();)
            {
            final String path = (String) iter.next();
            final File file = new File(path);
            uploadFile(s3, s3BucketName, file);
            }
        */
        }

    /*
    private void uploadFile(final AmazonS3 s3, final String bucketName, 
        final File file) throws MojoFailureException
        {
        if (!file.isFile())
            {
            // File might not be there if we're on a different OS.
            System.out.println("Invalid file: "+file);
            return;
            }
        try
            {
            System.out.println("Adding '"+file+"' to bucket '"+bucketName+"'");
            s3.putPublicFile(bucketName, file);
            }
        catch (final IOException e)
            {
            e.printStackTrace();
            throw new MojoFailureException("Could not access Amazon S3");
            }
        }
        */
    }
