package org.lastbamboo.common.amazon.s3;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpMethod;

/**
 * Interface for interacting with an Amazon S3 repository.
 */
public interface AmazonS3
    {

    /**
     * Creates a new bucket.
     * 
     * @param name The name of the bucket.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void createBucket(String name) throws IOException;
    
    /**
     * Deletes the bucket with the specified name.
     * 
     * @param bucketName The name of the bucket to delete.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void deleteBucket(String bucketName) throws IOException;

    /**
     * Uploads a file to S3.
     *
     * @param bucketName The name of the bucket.
     * @param file The file to upload.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void putPrivateFile(String bucketName, File file) throws IOException;

    /**
     * Uploads a file to S3 that will be publicly available.
     *
     * @param bucketName The name of the bucket.
     * @param file The file to upload.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void putPublicFile(String bucketName, File file) throws IOException;
    
    /**
     * Downloads a file from the specified bucket and file name to the 
     * specified local path.
     * 
     * @param bucketName The name of the Amazon S3 bucket.
     * @param fileName The name of the file within the bucket.
     * @param target The name of the local file to download to.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void getPrivateFile(String bucketName, String fileName, File target)
        throws IOException;
    
    /**
     * Downloads a file from the specified bucket and file name to the 
     * specified local path.  The specified file must be publicly available
     * in S3.
     * 
     * @param bucketName The name of the Amazon S3 bucket.
     * @param fileName The name of the file within the bucket.
     * @param target The name of the local file to download to.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void getPublicFile(String bucketName, String fileName, File target)
        throws IOException;

    /**
     * Normalizes the HTTP request headers with things like the authentication
     * token, the date, etc.
     * 
     * @param method The HTTP method.
     * @param methodString The HTTP method string, such as "PUT" or "GET".
     * @param fullPath The full path for the resource.
     * @param addPublicHeader Whether or not to add the header to make a 
     * resource publicly accessible, as in:<p>
     * 
     * x-amz-acl: public-read
     * <p>
     * 
     * @param useAuth Whether or not to add the authentication header.
     */
    void normalizeRequest(final HttpMethod method, 
        final String methodString, final String fullPath, 
        final boolean addPublicHeader, final boolean useAuth);

    /**
     * Deletes the specified file.
     * 
     * @param bucketName The bucket.
     * @param fileName The file name.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void delete(String bucketName, String fileName) throws IOException;

    /**
     * Deletes all of the files in the specified bucket matching the specified pattern.  Far
     * from allowing full regular expressions, this just simply allows the star expander
     * at the beginning or end of the file name, or both, such as "*.txt*, "file*" or *ile*".
     * 
     * @param bucketName The bucket.
     * @param file The file(s) to delete.  This can be of the form "*.txt*, "file*" or *ile*", 
     * for example.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    public void deleteStar(String bucketName, String file) throws IOException;

    /**
     * List all the files in the bucket.
     * 
     * @param bucketName The name of the bucket to list.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void listBucket(String bucketName) throws IOException;

    /**
     * Adds all the files in the specified directory to the specified bucket.
     * 
     * @param bucketName The name of the bucket to list.
     * @param dir The directory containing files to add.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void putPrivateDir(String bucketName, File dir) throws IOException;

    /**
     * Adds all the files in the specified directory to the specified bucket.
     * 
     * @param bucketName The name of the bucket to list.
     * @param dir The directory containing files to add.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void putPublicDir(String bucketName, File dir) throws IOException;

    /**
     * Lists all the user's buckets.
     * @throws IOException If we could either could not make a network 
     * connection to S3 or could not understand the HTTP exchange.
     */
    void listBuckets() throws IOException;
    }
