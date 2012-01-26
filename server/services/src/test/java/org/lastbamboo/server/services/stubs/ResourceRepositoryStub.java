package org.lastbamboo.server.services.stubs;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Random;

import org.lastbamboo.server.resource.BadPasswordResetIdException;
import org.lastbamboo.server.resource.FileAndInstances;
import org.lastbamboo.server.resource.FileResource;
import org.lastbamboo.server.resource.FileResourceResult;
import org.lastbamboo.server.resource.GroupExistsException;
import org.lastbamboo.server.resource.MetaFileResourceResult;
import org.lastbamboo.server.resource.OnlineInstance;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.resource.UserExistsException;
import org.lastbamboo.server.resource.UserNotFoundException;

public class ResourceRepositoryStub implements ResourceRepository
    {

    public long authenticateWebUser(String userName, String password)
        {
        return 832908L;
        }

    public void deleteResource(long userId, String sha1) throws IOException
        {
        }

    public boolean deleteWebUser(String userName)
        {
        return false;
        }

    public FileAndInstances getFileAndInstances(URI sha1)
        {
        return null;
        }

    public FileResourceResult getFileResources(int pageIndex,
            int resultsPerPage, long userId)
        {
        return null;
        }

    public Collection<OnlineInstance> getOnlineInstances()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void insertResource(FileResource fr) throws IOException
        {
        // TODO Auto-generated method stub

        }

    public long newWebUser(String userName, String password) throws UserExistsException
        {
        // TODO Auto-generated method stub
        return 4242L;
        }

    public MetaFileResourceResult search(String keywords, int startIndex,
            int itemsPerPage, String os, long userId, boolean applications,
            boolean audio, boolean docs, boolean images, boolean videos)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void setInstanceOnline(long userId, String baseUri, boolean online,
            String serverAddress)
        {
        // TODO Auto-generated method stub

        }

    public void setServerOnline(boolean online, String serverAddress)
        {
        // TODO Auto-generated method stub

        }

    public void takeDown(String sha1Urn, boolean takeDown)
        {
        // TODO Auto-generated method stub

        }

    public boolean confirmNewUser(long token)
        {
        // TODO Auto-generated method stub
        return false;
        }

    public long generatePasswordResetId(String email)
            throws UserNotFoundException
        {
        return new Random().nextLong();
        }

    public void resetPassword(String email, String password, long resetId)
            throws UserNotFoundException, BadPasswordResetIdException
        {
        // TODO Auto-generated method stub
        
        }

    public FileResource getFileResource(long userId, String sha1)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public String newGroup(long userId, String name, String description,
            String permission) throws GroupExistsException, IOException
        {
        // TODO Auto-generated method stub
        return null;
        }

    public FileResourceResult getFileResources(int pageIndex,
            int resultsPerPage, long instanceId, String groupName)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public MetaFileResourceResult search(String keywords, int startIndex,
            int itemsPerPage, String os, long userId, String groupName,
            boolean applications, boolean audio, boolean docs, boolean images,
            boolean videos)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public boolean hasGroupPermission(long userId, String groupName)
        {
        // TODO Auto-generated method stub
        return false;
        }

    public void editResource(long instanceId, String sha1, String tags,
            String url) throws IOException
        {
        // TODO Auto-generated method stub
        
        }

    }
