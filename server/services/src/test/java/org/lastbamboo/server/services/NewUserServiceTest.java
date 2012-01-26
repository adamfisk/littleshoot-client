package org.lastbamboo.server.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;
import org.lastbamboo.server.resource.ResourceRepository;
import org.lastbamboo.server.resource.UserExistsException;
import org.lastbamboo.server.services.command.UserCommand;
import org.lastbamboo.server.services.stubs.EmailServiceStub;
import org.lastbamboo.server.services.stubs.ResourceRepositoryStub;

/**
 * Test the new user service.
 */
public class NewUserServiceTest
    {

    /**
     * Test new user creation. 
     * 
     * @throws Exception If anything goes wrong.
     */
    @Test public void testNewUser() throws Exception
        {
        final ResourceRepository trueRepo = new ResourceRepositoryStub()
            {
            @Override
            public long newWebUser(final String userName, final String password)
                {
                return 74289L;
                }
            };
            
        final ResourceRepository existsRepo = new ResourceRepositoryStub()
            {
            @Override
            public long newWebUser(final String userName, final String password) 
                throws UserExistsException
                {
                throw new UserExistsException("test");
                }
            };
            
        final ResourceRepository exceptionRepo = new ResourceRepositoryStub()
            {
            @Override
            public long newWebUser(final String userName, final String password)
                {
                throw new RuntimeException("just for the test");
                }
            };  
        
        assertTrue( getSuccess(trueRepo));
        assertFalse(getSuccess(existsRepo));
        assertTrue(getExists(existsRepo));
        
        assertFalse(getSuccess(exceptionRepo));
        assertFalse(getExists(exceptionRepo));
        }

    private boolean getSuccess(final ResourceRepository repo) throws Exception
        {
        final EmailService email = new EmailServiceStub(true);
        final NewUserService service = new NewUserService(repo, email);
        
        final UserCommand uc = new UserCommand();
        uc.setEmail("test");
        uc.setPassword("test");
        return new JSONObject(service.getJson(uc)).getBoolean("success");
        }
    
    private boolean getExists(final ResourceRepository repo) throws Exception
        {
        final EmailService email = new EmailServiceStub(true);
        final NewUserService service = new NewUserService(repo,email);
        
        final UserCommand uc = new UserCommand();
        uc.setEmail("test");
        uc.setPassword("test");
        return new JSONObject(service.getJson(uc)).getBoolean("exists");
        }
    }
