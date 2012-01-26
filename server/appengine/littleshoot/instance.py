#from django.db import models

# Create your models here.

from django.shortcuts import render_to_response
from django.http import HttpResponseBadRequest

from django.http import HttpResponse, HttpResponseRedirect
from django.http import HttpResponseForbidden, HttpResponseNotFound

from django.utils import simplejson
from google.appengine.ext.db import djangoforms
from google.appengine.runtime import DeadlineExceededError

from google.appengine.ext import db

import decorators
import models
import logging
import JsonEncoder
import util
import hashlib
json_encode = JsonEncoder.JSONEncoder().encode

class InstanceForm(djangoforms.ModelForm):

    class Meta:
        model = models.Instance

class OnlineInstanceForm(djangoforms.ModelForm):
    
    class Meta:
        model = models.OnlineInstance

@decorators.onlineRequired        
@decorators.serverAddressRequired
@decorators.baseUriRequired
@decorators.instanceIdRequired
def instanceOnline(request):
    #logging.info("Handling request: %s", request.REQUEST.items())
    try:
        idArg = request.REQUEST.get('instanceId')
        if isinstance(idArg, int):
            instanceId = int(idArg)
        else:
            instanceId = int(hashlib.md5(idArg).hexdigest(), 16) % 0x7fffffffffffffff
        online = util.requestStringToBool(request, 'online')
        baseUri = request.REQUEST.get('baseUri')
        serverAddress = request.REQUEST.get('serverAddress')
        
        instance = models.Instance.gql('where instanceId = :1', instanceId).get()
        
        if instance is None:
            form = InstanceForm(request.REQUEST)
            if not form.is_valid():
                logging.info('ERROR: Form invalid for online instances:\n%s' 
                         % repr(form.errors))
                return HttpResponseBadRequest('ERROR: Form invalid for instances:\n%s' 
                         % repr(form.errors))
            instance = form.save(commit=False)
                
        else:
            instance.baseUri = baseUri
            instance.online = online
            instance.serverAddress = serverAddress
            
        
        # We get all the models to update so we can do a bulk put
        pendingData = []
        pendingData.append(instance)
        
        jsonResponse = {'message': 'Update succeeded'}
        updateOnlineInstance(request, instanceId, online, 
                            serverAddress, pendingData, jsonResponse);
        db.put(pendingData)

        
        return HttpResponse(json_encode(jsonResponse))
    except DeadlineExceededError:
        logging.error('Deadline exceeded!!');
        logging.exception('DeadlineExceededError')
        return HttpResponse('DeadlineExceededError')
    except MemoryError:
        logging.error('MemoryError!!');
        logging.exception('MemoryError')
        return HttpResponse('MemoryError')
    except AssertionError:
        logging.error('AssertionError!!');
        logging.exception('AssertionError')
        return HttpResponse('AssertionError')

def updateOnlineInstance(request, instanceId, online, serverAddress, 
                         pendingData, jsonResponse):
    #logging.debug('Updating online instance to online: %s', online)

    # We separate instances and online instances because we want to know all
    # the instances that have ever been online.
    onlineInstance = models.OnlineInstance.gql('where instanceId = :1', instanceId).get()
    if onlineInstance is None:
        if online:
            onlineInstance = insertOnlineInstance(request, pendingData)
            if onlineInstance is not None:
                updateFiles(request, onlineInstance, online, pendingData, jsonResponse)
        else:
            logging.error('Setting user we do not know about to offline')
    elif online is False:
        logging.info('Found onlineInstance...deleting')
        # This also updates the files associated with the user!!
        updateFiles(request, onlineInstance, False, pendingData, jsonResponse)
        
        # If we've updated all the files, delete the online instance.  Otherwise
        # we need it to keep updating the rest of the files.
        if jsonResponse['complete'] == True:
            onlineInstance.delete()
    else:
        logging.info('Instance is already online: %s, updating files', instanceId)
        updateFiles(request, onlineInstance, online, pendingData, jsonResponse)


def insertOnlineInstance(request, pendingData):
    #logging.debug('Inserting online instance')
    form = OnlineInstanceForm(data=request.REQUEST)
    if form.is_valid():
        instance = form.save(commit=False)
        pendingData.append(instance)
        #instance.put()
        return instance
    else:
        logging.error('ERROR: Form invalid for online instances:\n%s' 
                     % repr(form.errors))
        return None
    
          
def updateFiles(request, instance, online, pendingData, jsonResponse):
    logging.debug('Updating files')
    
    startSha1 = request.REQUEST.get('startSha1')
    if startSha1 is None:
        filesQuery = models.File.all()
        filesQuery.filter('instanceId =', instance.instanceId)
        filesQuery.order('sha1')
        startFile = filesQuery.get()
        if startFile is None:
            # This just means the user isn't sharing any files.
            logging.debug('User has no shared files')
            jsonResponse['complete'] = True
            return
        startSha1 = startFile.sha1
    
    
    filesQuery = models.File.all()
    filesQuery.filter('instanceId =', instance.instanceId)
    filesQuery.filter('sha1 >= ', startSha1)
    filesQuery.order('sha1')
    
    limit = 6
    
    files = filesQuery.fetch(limit)
    #files = models.File.gql('where instanceId = :1', instance.instanceId)
    #logging.debug('Got files: %s', files)
    
    filesLength = len(files)
    
    if filesLength < limit:
        maxIndex = filesLength
    else:
        maxIndex = limit - 1
     #= min(filesLength, limit) - 1 
    logging.debug('Max is: %s', maxIndex)
    for i in range(0, maxIndex):
    #for file in files:
        #logging.info('\n\n\n')
        file = files[i]
        logging.info('Updating file: %s', file.uri)
        uri = file.uri
        # First set the file's online status
        file.instanceOnline = online
        pendingData.append(file)
        
        # Then handle the meta file.
        metaFile = models.MetaFile.gql('where uri = :1', uri).get()
        if metaFile is not None:
            logging.debug('Updating meta file for sha1: %s', uri)
            numInstances = metaFile.numOnlineInstances
            if online:
                logging.debug('Adding instance for SHA-1: %s', uri)
                metaFile.numOnlineInstances = numInstances + 1
                #metaFile.addInstance(instance)
            else:
                logging.debug('Removing instance for SHA-1: %s', uri)
                if numInstances > 0:
                    metaFile.numOnlineInstances = numInstances - 1
                    
            if numInstances < 0:
                logging.error('num instances is negative')
                # reset corrupt data
                metaFile.numOnlineInstances = 0
                #metaFile.removeInstance(instance)
            pendingData.append(metaFile)
            #metaFile.put();
        else:
            logging.error('No matching meta file for file with SHA1 %s and URI %s', 
                            file.sha1, uri)
            # There somehow is not a meta file for this file??  That's odd.
            # We should create one.
    
    if filesLength == limit:
        jsonResponse['complete'] = False
        jsonResponse['nextSha1'] = files[limit - 1].sha1
    else:
        jsonResponse['complete'] = True
        # We only delete the online instance if we've updated everything.
        #logging.info('\n\n\n')
        