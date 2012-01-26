dojo.provide("littleshoot.Group");

var Group =
    {

    showGroupWizard : function()
        {
        dijit.byId("groupWizardDialog").show();
        },
        
    newGroup : function()
        {
        var dialog = dijit.byId("groupWizardDialog"); 
        CommonUtils.showSpinner("groupWizardDialog");
        //CommonUtils.setMessage("groupNameStatus","Creating your group.");
        
        var params =
            {
            };
        
        var frm = document.forms.groupName;
        var groupName = frm.groupName.value;
        console.info("Setting name to: "+groupName);
        params.name = groupName;
        

        var descForm = document.forms.groupDescription;
        var groupDesc = descForm.groupDescription.value;
        console.info("Setting group description to: "+groupDesc);
        params.description = groupDesc;
        
        var publicPrivate = document.forms.publicPrivate;
        var publicChecked = publicPrivate.permission[0].checked;
        var privateChecked = publicPrivate.permission[1].checked;
        console.info("Public checked: "+publicChecked);
        console.info("Private checked: "+privateChecked);
        
        if (publicChecked)
            {
            params.permission = "public";
            }
        else
            {
            params.permission = "group";
            }
        
        params.userId = User.getUserId();

        console.info("Submitting data: "+dojo.toJson(params));
        var httpsUrl = CommonUtils.createHttpsUrl("newGroup");

        var newGroupDeferred =  dojo.io.script.get(
            {
            url: httpsUrl,
            timeout: 20000,
            content: params,
            handleAs: "json",
            callbackParamName: "callback",
            load: function(response, ioArgs)
                {
                console.info("New group got the response: " + 
                    dojo.toJson(response));
                if (response.success)
                    {
                    console.info("Created group.");
                    dialog.hide();
                    CommonUtils.showMessage("Group '"+groupName+"' Created",
                        "Congratulations!  Your group '"+groupName+"' has been successfully created.");
                    }
                else if (response.exists)
                    {
                    CommonUtils.setMessage("newGroupStatus", 
                        "Sorry, but the group name you requested already exists.");
                    } 
                else 
                    {
                    
                    console.warn("Group error: "+dojo.toJson(response));
                    dialog.hide();
                    CommonUtils.showMessage("Server Error",
                        "We apologize, but there was an error creating your group.  Please try again.");
                    }
                return response;
                },
            error: function(response, ioArgs)
                {
                console.warn("Error on login: "+response);
                dialog.hide();
                CommonUtils.showMessage("Server Error", 
                    "Sorry, but there was an error accessing the LittleShoot servers.  Please try again later.");
                return response;
                }
            });
            
        
        var stopSpinner = function()
            {
            CommonUtils.hideSpinner("groupWizardDialog");
            };
        newGroupDeferred.addBoth(stopSpinner);
        
        return newGroupDeferred;
        },

    checkName : function()
        {
        var frm = document.forms.groupName;
        
        var groupName = frm.groupName.value;
        if (CommonUtils.isBlank(groupName))
            {
            //CommonUtils.setMessage("groupNameStatus", "Checking to see if your name is available.");
            CommonUtils.setMessage("groupNameStatus","You must enter a group name.");
            return false;
            }
        else
            {
            return true;
            }
        },
        
    clearStatus : function()
        {
        console.info("Clearing status");
        CommonUtils.clearMessage("newGroupStatus");
        }
    };