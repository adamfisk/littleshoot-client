package org.lastbamboo.server.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lastbamboo.common.services.BooleanService;
import org.lastbamboo.common.util.Pair;
import org.lastbamboo.common.util.UriUtils;
import org.lastbamboo.server.services.ConfirmNewUserService;
import org.lastbamboo.server.services.command.ConfirmNewUserCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for verifying user e-mails.
 */
public class ConfirmNewUserController extends AbstractCommandController 
    {

    /**
     * Logger for this class.
     */
    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final BooleanService m_confirmNewUserService;

    /**
     * Creates a new controller for verifying new accounts.
     * 
     * @param confirmNewUser Service for verifying an e-mail address.
     */
    public ConfirmNewUserController(final ConfirmNewUserService confirmNewUser)
        {
        this.m_confirmNewUserService = confirmNewUser;
        }

    @Override
    protected ModelAndView handle(final HttpServletRequest request,
        final HttpServletResponse response, final Object obj, 
        final BindException errors) throws ServletException, IOException 
        {        
        m_log.debug("Received login request: {}", request.getQueryString());
        
        final ConfirmNewUserCommand command = (ConfirmNewUserCommand) obj;
        
        final Collection<Pair<String, String>> params =
            new LinkedList<Pair<String,String>>();
        params.add(UriUtils.pair("ol", "showMessage"));
        
        if (this.m_confirmNewUserService.service(command))
            {
            params.add(UriUtils.pair("title", "Verification Successful"));
            params.add(UriUtils.pair("msg", 
                "Congratulations.  You've successfully verified your account.  Please enjoy LittleShoot."));
            }
        else
            {
            params.add(UriUtils.pair("title", "Verification Error"));
            params.add(UriUtils.pair("msg", 
                "We're sorry, but there was an error verifying your account.  Please try again later."));
            }
        
        final String baseUrl = "http://www.littleshoot.org/index.html";
        final String url = UriUtils.newWwwUrlEncodedUrl(baseUrl, params);
        return new ModelAndView(new RedirectView(url));
        }
    }
