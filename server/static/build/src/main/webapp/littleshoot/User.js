dojo.provide("littleshoot.User");

var User =
    {

    /**
     * Logs a user in.
     */    
    login : function(dialogId)
        {
        console.info("Logging in...");
        
        var dialog = dijit.byId(dialogId);
        var email = dijit.byId("loginEmail").getValue();
        var pwd = dijit.byId("loginPassword").getValue();
        
        if (!dojox.validate.isEmailAddress(email))
            {
            console.warn("Not an email address: "+email);
            User.addStatusMessage("loginStatusDiv", 
                "Invalid e-mail address.  Please try again.");
            return;
            }
        else
            {
            console.info("Got an email address: "+email);
            }
        
        if (CommonUtils.isBlank(pwd))
            {
            User.addStatusMessage("loginStatusDiv", "Please enter a password.");
            return;
            }
            
        var params =
            {
            email : email,
            password: pwd
            };
        
        var httpsUrl = CommonUtils.createHttpsUrl("login");
        console.info("Logging in with url: "+httpsUrl);
        
        var loginDeferred =  dojo.io.script.get(
            {
            url: httpsUrl,
            timeout: 20000,
            content: params,
            handleAs: "json",
            callbackParamName: "callback",
            load: function(response, ioArgs)
                {
                console.info("Login got the response: " + dojo.toJson(response));
                console.info("All cookies: "+document.cookie);
                
                if (response.success)
                    {
                    dialog.hide();
                    User.onLogin();
                    CommonUtils.showMessage("Signed In", 
                        "OK, you're all set.  Thanks for signing in and welcome back to LittleShoot.");
                    }
                else if (response.notVerified)
                    {
                    console.info("Login error: "+dojo.toJson(response));
                    CommonUtils.showMessage("Login Error",
                        "Sorry, your account still needs to be verified.  Did you respond to our sign up e-mail?");
                    }
                else if (response.notFound)
                    {
                    CommonUtils.showMessage("Login Error",
                        "Sorry, but we could not find a matching account.  " +
                        "Are you sure you used the correct user name and password?");
                    }
                else 
                    {
                    console.info("Login error: "+dojo.toJson(response));
                    CommonUtils.showMessage("Login Error", "Could not login.  " +
                        "Are you sure you have permission to login to this group?");
                    }
                    
                return response;
                },
            error: function(response, ioArgs)
                {
                console.error("Error on login: "+response);
                dialog.hide();
                CommonUtils.showMessage("Error", "There was an error accessing our servers.  Please try again later.");
                return response;
                }
            });
        return loginDeferred;
        },
    
    /**
     * Processes a login event, setting things like cookie expiration timers.
     */
    onLogin : function ()
        {
        Publisher.activatePublish();
        
        // This is the expiry time for sessions on the server, minus a little
        // bit.
        var timeoutTime = (1000 * 60 * 60 * 8) - 20000;
        this.timeoutId = setTimeout(User.onLoginTimeout, timeoutTime);
        },
        
    onLoginTimeout : function()
        {
        console.info("Received login timeout -- changing publish buttons.");
        Publisher.loginToPublish();
        },
        
    newUser : function(dialogId)
        {
        CommonUtils.showSpinner(dialogId);
        var dialog = dijit.byId(dialogId);
        var email = dijit.byId("newUserEmail").getValue();
        var pwd = dijit.byId("newUserPassword").getValue();
        var confirm = dijit.byId("newUserConfirm").getValue();
        
        var statusDivName = "newUserStatusDiv";
        if (!dojox.validate.isEmailAddress(email))
            {
            console.warn("Not an email address: "+email);
            User.addStatusMessage(statusDivName, "Invalid e-mail address.  Please try again.");
            return;
            }
        else
            {
            console.info("Got an email address: "+email);
            }
        
        if (pwd.length < 6)
            {
            console.warn("Too short");
            User.addStatusMessage(statusDivName, "Your password must be at least 6 characters.");
            return;    
            }
        if (pwd != confirm)
            {
            console.warn("Passwords don't match: "+pwd+" and "+confirm);
            User.addStatusMessage(statusDivName, "Your passwords don't match.  Please try again.");
            return;    
            }
            
        var params =
            {
            email : email,
            password: pwd
            };

        var httpsUrl = CommonUtils.createHttpsUrl("newUser");
        
        //var newUserDeferred =  dojo.xhrPost(
        var newUserDeferred =  dojo.io.script.get(
            {
            url: httpsUrl,
            timeout: 20000,
            content: params,
            handleAs: "json",
            callbackParamName: "callback",
            load: function(response, ioArgs)
                {
                console.info("New user got the response: " + response);
                if (response.success)
                    {
                    dialog.hide();
                    CommonUtils.showMessage("E-Mail Sent", 
                        "You will receive a confirmation e-mail shortly.  Please follow the instructions in that e-mail to complete the registration process.");
                    }
                else if (response.exists)
                    {
                    // The specified user name already exists.
                    User.addStatusMessage(statusDivName, 
                        "Sorry, but a user with that e-mail address already exists.");
                    }
                else 
                    {
                    console.warn("Login error: "+dojo.toJson(response));
                    dialog.hide();
                    CommonUtils.showMessage("Login Error", 
                        "We're sorry, but there was an error with our system.  Please try again later.");
                    }
                return response;
                },
            error: function(response, ioArgs)
                {
                console.warn("Error on login: "+response);
                dialog.hide();
                CommonUtils.showMessage("Login Error", 
                    "We're sorry, but there was an error contacting our servers.  Please try again later.  The server said: " + response);
                return response;
                }
            });
        
        newUserDeferred.addBoth(function(){CommonUtils.hideSpinner(dialogId);});
        return newUserDeferred;
        },
        
    resetPassword : function(dialogId)
        {
        var dialog = dijit.byId(dialogId);
        var pwd = dijit.byId("resetPasswordPassword").getValue();
        var confirm = dijit.byId("resetPasswordConfirm").getValue();
        
        var resetId = document.resetPasswordForm.param1.value;
        var email = document.resetPasswordForm.param2.value;
        
        console.info("Got reset ID: "+resetId);
        console.info("Got email: "+email);
        
        var statusDivName = "resetPasswordStatusDiv";

        if (!dojox.validate.isEmailAddress(email))
            {
            console.warn("Not an email address: "+email);
            throw new Error("The e-mail address is not valid!!");
            }
        else
            {
            console.info("Got an email address: "+email);
            }
        
        
        if (pwd.length < 6)
            {
            console.warn("Too short");
            User.addStatusMessage(statusDivName, "Your password must be at least 6 characters.");
            return;    
            }
        if (pwd != confirm)
            {
            console.warn("Passwords don't match: "+pwd+" and "+confirm);
            User.addStatusMessage(statusDivName, "Your passwords don't match.  Please try again.");
            return;    
            }
            
        var params =
            {
            resetId : resetId,
            email : email,
            password: pwd
            };

        var httpsUrl = CommonUtils.createHttpsUrl("resetPassword");
        
        var resetPasswordDeferred =  dojo.io.script.get(
            {
            url: httpsUrl,
            timeout: 20000,
            content: params,
            handleAs: "json",
            callbackParamName: "callback",
            load: function(response, ioArgs)
                {
                console.info("New user got the response: " + response);
                if (response.success)
                    {
                    dialog.hide();
                    CommonUtils.showMessage("Reset Succeeded", 
                        "Your password has been reset.");
                    }
                else if (!response.exists)
                    {
                    console.info("User not found.");
                    User.addStatusMessage(statusDivName, 
                        "Sorry, we could not find a user with the given e-mail address.");
                    }
                else 
                    {
                    console.warn("Login error: "+dojo.toJson(response));
                    dialog.hide();
                    CommonUtils.showMessage("Reset Password Error", 
                        "We're sorry, but there was an error with our system.  Please try again later.");
                    }
                return response;
                },
            error: function(response, ioArgs)
                {
                console.warn("Error on login: "+response);
                dialog.hide();
                CommonUtils.showMessage("Login Error", 
                    "We're sorry, but there was an error contacting our servers.  Please try again later.  The server said: " + response);
                return response;
                }
            });
        return resetPasswordDeferred;
        },
        
    sendPasswordReminder : function (dialogId)
        {
        var dialog = dijit.byId(dialogId);
        var email = dijit.byId("forgotPasswordEmail").getValue();
        
        if (!dojox.validate.isEmailAddress(email))
            {
            console.warn("Not an email address: "+email);
            this.User.addStatusMessage("forgotPasswordStatusDiv", "Invalid e-mail address.  Please try again.");
            return;
            }
        else
            {
            console.info("Got an email address: "+email);
            }
        
        var params =
            {
            email : email
            };
        
        var httpsUrl = CommonUtils.createHttpsUrl("forgotPassword");
        
        var forgotPasswordDeferred =  dojo.io.script.get(
            {
            url: httpsUrl,
            timeout: 20000,
            content: params,
            handleAs: "json",
            callbackParamName: "callback",
            load: function(response, ioArgs)
                {
                console.info("Forgot password got the response: " + response);
                if (response.success)
                    {
                    dialog.hide();
                    CommonUtils.showMessage("E-Mail Sent", 
                        "You will receive an e-mail shortly with a link to reset your password.");
                    }
                else if (!response.exists)
                    {
                    // The specified user name already exists.
                    User.addStatusMessage("forgotPasswordStatusDiv", 
                        "Sorry, but we could not find a user with that e-mail address.  Is there another e-mail address you can can try?");
                    console.log("Added status message");
                    }
                else 
                    {
                    console.warn("Login error: "+dojo.toJson(response));
                    dialog.hide();
                    CommonUtils.showMessage("Reset Error", 
                        "We're sorry, but there was an error with our system.  Please try again later.");
                    }
                console.info("About to return response...");
                return response;
                },
            error: function(response, ioArgs)
                {
                console.warn("Error on login: "+response);
                dialog.hide();
                CommonUtils.showMessage("Login Error", 
                    "We're sorry, but there was an error contacting our servers.  Please try again later.  The server said: " + response);
                return response;
                }
            });
        console.info("Finished sending password reminder...");
        return forgotPasswordDeferred;
        },
        
    showNewUserDialog : function(oldDialogId)
        {
        console.info("Showing new user dialog");
        User.clearStatusMessage("newUserStatusDiv");
        dijit.byId(oldDialogId).hide();
        dijit.byId("newUserDialog").show();
        },
 
    showResetPasswordDialog : function ()
        {
        console.info("Showing forgot password dialog.");
        User.clearStatusMessage("resetPasswordStatusDiv");
        dijit.byId("resetPasswordDialog").show();
        console.info("Finished resetting password..");
        }, 
        
    forgotPassword : function (oldDialogId)
        {
        console.info("Showing forgot password dialog.");
        User.clearStatusMessage("forgotPasswordStatusDiv");
        dijit.byId(oldDialogId).hide();    
        dijit.byId("forgotPasswordDialog").show();
        console.info("Finished forgot password..");
        }, 
        
    showLoginPublishDialog : function ()
        {
        User.clearStatusMessage("loginStatusDiv");
        dijit.byId("loginPublishDialog").show();
        },
        
    showLoginDialog : function (msg)
        {
        CommonUtils.setMessage("loginMessageDiv", msg);
        User.clearStatusMessage("loginStatusDiv");
        
        dijit.byId("loginPublishDialog").show();
        },

    /**
     * Clear the old status message. 
     * 
     * @param {Object} baseId The base ID for the element containing the status message.
     */        
    clearStatusMessage : function (baseId)
        {
        var statusDiv = dojo.byId(baseId);
        if (!statusDiv)
            {
            console.error("Could not find status div: "+baseId);
            return;
            }
        statusDiv.innerHTML = ""; 
        },
        
    addStatusMessage : function (baseId, msg)
        {
        var statusDiv = dojo.byId(baseId);
        if (!statusDiv)
            {
            console.error("Could not find status div: "+baseId);
            return;
            }
        statusDiv.innerHTML = msg;
        console.info("Added status!!"); 
        },
    
    getSessionId : function ()
        {
        var sessionId = dojo.cookie("sessionId");
        console.info("Returning session ID: "+sessionId);
        return sessionId;    
        },
        
    getKey : function()
        {
        console.info("Accessing key");
        return dojo.cookie("key");
        },
        
    getUserId : function()
        {
        console.info("Accessing user ID");
        return dojo.cookie("userId");
        },
        
    /**
     * Determines if the user is logged in or not.
     */    
    isLoggedIn : function()
        {
        if (dojo.cookie("siteKey"))
            {
            return true;
            }
        return false;
        },
    
    createGroup : function() 
        {
        /*
        if (!User.isLoggedIn())
            {
            User.showLoginDialog("You need to be logged in to create a group.");
            }
        else
        */
            //{
            console.info("Showing group wizard");
            /*
            var fo = dojo.fadeOut(
                {
                node: dojo.byId("contentDiv"),
                duration: 2000
                });
            fo.play();
            */
            
            Group.showGroupWizard();
            //}
        }
    };