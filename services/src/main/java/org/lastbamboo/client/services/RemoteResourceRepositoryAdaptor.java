package org.lastbamboo.client.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.lastbamboo.common.http.client.ForbiddenException;

public class RemoteResourceRepositoryAdaptor 
    implements RemoteResourceRepository {

    public String insertResource(File file, URI uri, URI sha1)
            throws IOException, ForbiddenException {
        // TODO Auto-generated method stub
        return null;
    }

    public String insertResource(File file, boolean downloaded, URI uri,
            URI sha1) throws IOException, ForbiddenException {
        // TODO Auto-generated method stub
        return null;
    }

    public String deleteResource(File file, URI sha1) throws IOException,
            ForbiddenException {
        // TODO Auto-generated method stub
        return null;
    }

    public String deleteResource(Map<String, String> paramMap,
            Map<String, String> cookieMap) throws IOException,
            ForbiddenException {
        // TODO Auto-generated method stub
        return null;
    }

    public void insertResource(File file, Map<String, String> map,
            Map<String, String> cookieMap,
            PublishedFilesTracker pendingFilesTracker, FileMapper fileMapper,
            PublishedCallback callback, Map<String, String> headerMap)
            throws IOException, ForbiddenException {
        // TODO Auto-generated method stub
        
    }

    public void insertResource(File file, Map<String, String> map,
            Map<String, String> cookieMap,
            PublishedFilesTracker pendingFilesTracker, FileMapper fileMapper,
            PublishedCallback callback, Map<String, String> headerMap,
            String namespace) throws IOException, ForbiddenException {
        // TODO Auto-generated method stub
        
    }
}
