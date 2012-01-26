package org.lastbamboo.client.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.lastbamboo.common.http.client.ForbiddenException;

/**
 * Interface for a remote repository of resources.
 */
public interface RemoteResourceRepository
    {

    /**
     * Inserts the specified resource.
     * 
     * @param file The resource to insert.
     * @param uri The URI for the resource.
     * @param sha1 The SHA-1 for the file we're publishing.
     * @throws IOException If there's an IO error accessing the server.
     * @throws ForbiddenException If the user does not have permission to 
     * make this call.
     */
    String insertResource(File file, URI uri, URI sha1) throws IOException,
        ForbiddenException;
    
    /**
     * Inserts the specified resource.
     * 
     * @param file The resource to insert.
     * @param downloaded Whether or not the file was downloaded.
     * @param uri The URI for the resource.
     * @param sha1 The SHA-1 for the file we're publishing.
     * @throws IOException If there's an IO error accessing the server.
     * @throws ForbiddenException If the user does not have permission to 
     * make this call.
     * @return The JSON response from the server.
     */
    String insertResource(File file, boolean downloaded, URI uri, URI sha1) 
        throws IOException, ForbiddenException;
    
    
    String deleteResource(File file, URI sha1) throws IOException, 
        ForbiddenException;
    
    /**
     * Deletes the specified resource.
     * 
     * @param paramMap The parameters to send.
     * @param cookieMap {@link Map} of cookies included with the original 
     * request.
     * @throws IOException If there's an IO error accessing the server.
     * @throws ForbiddenException If the user does not have permission to 
     * make this call.
     */
    String deleteResource(Map<String, String> paramMap, 
        Map<String, String> cookieMap) throws IOException,
        ForbiddenException;

    /**
     * Inserts a file resource with tags.
     * 
     * @param file The file.
     * @param map The parameters to send.
     * @param cookieMap {@link Map} of cookies included with the original 
     * request.
     * @param fileMapper The class that keeps track of what files we've mapped.
     * @param headerMap The map of HTTP request headers.
     * @param namespace The namespace to store the file under.
     * @throws IOException If there's an IO error accessing the server. 
     * @throws ForbiddenException If the user does not have permission to 
     * make this call.
     */
    void insertResource(File file, Map<String, String> map, 
        Map<String, String> cookieMap, PublishedFilesTracker pendingFilesTracker, 
        FileMapper fileMapper, PublishedCallback callback, 
        Map<String, String> headerMap, final String namespace) 
        throws IOException, ForbiddenException;
    
    /**
     * Inserts a file resource with tags.
     * 
     * @param file The file.
     * @param map The parameters to send.
     * @param cookieMap {@link Map} of cookies included with the original 
     * request.
     * @param fileMapper The class that keeps track of what files we've mapped.
     * @param headerMap The map of HTTP request headers.
     * @throws IOException If there's an IO error accessing the server. 
     * @throws ForbiddenException If the user does not have permission to 
     * make this call.
     */
    void insertResource(File file, Map<String, String> map, 
        Map<String, String> cookieMap, PublishedFilesTracker pendingFilesTracker, 
        FileMapper fileMapper, PublishedCallback callback, 
        Map<String, String> headerMap) 
        throws IOException, ForbiddenException;

    }
