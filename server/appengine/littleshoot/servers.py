
from django.http import HttpResponse
from django.http import HttpResponseBadRequest
from django.http import HttpResponseRedirect

import decorators
import logging
import os
import util
import models
import jsonpickle

import JsonEncoder
json_encode = JsonEncoder.JSONEncoder().encode

from instance import updateFiles
from google.appengine.ext import db


sip = json_encode({'servers' : [{'address': 'sip2.littleshoot.org', 'port' : 5061}]})
turn = json_encode({'servers' : [{'address': 'turn2.littleshoot.org', 'port' : 3478}]})
def sipServer(request):
    #logging.info('Handling SIP request...')

    #servers = {'servers' : [{'address': 'sip2.littleshoot.org', 'port' : 5061}]}
    #logging.info('Encoding servers: %s', servers)
    #return HttpResponse(sip)
    return HttpResponse(sip)


def turnServer(request):
    #logging.info('Handling TURN request...')

    #servers = {'servers' : [{'address': 'turn2.littleshoot.org', 'port' : 3478}]}
    #return HttpResponse(turn)
    return HttpResponse(turn)

@decorators.serverAddressRequired
def instancesForServer(request):
    """
    The caller may send an instance ID to specify where to start.  This is
    because we can only fetch 1000 items at a time, so we'll need to page
    if there are more than that (which there likely will be).
    """
    logging.info("Handling instancesForServer request: %s", request.REQUEST.items())
    serverAddress = request.REQUEST.get('serverAddress')
    instanceIdParam = request.REQUEST.get('instanceId')
    limit = int(request.REQUEST.get('limit'))

    logging.debug('Got params...');
    rawJson = {
        'serverAddress' : serverAddress,
        }
    instancesQuery = models.OnlineInstance.all()
    instancesQuery.filter('serverAddress =', serverAddress)
    instancesQuery.order('instanceId')
    
    logging.debug('about to query for for first instance')
    if instanceIdParam is None:
        firstInstance = instancesQuery.get()
        if firstInstance is None:
            logging.info('No files for server!!')
            rawJson['complete'] = True
            json = jsonpickle.dumps(rawJson)
            return HttpResponse(json, mimetype='application/json')
        else:
            instanceId = firstInstance.instanceId
    else:
        instanceId = int(instanceIdParam)
    
    logging.debug('Starting with instance ID: %s', str(instanceId))
    
    instancesQuery.filter('instanceId >=', instanceId)
    
    #limit = 2
    logging.debug('Fetching with limit: %s', str(limit))
    instances = instancesQuery.fetch(limit=limit)
    
    logging.debug('Got responses: %s', str(len(instances)))
    
    if len(instances) == limit:
        arraySize = limit - 1
        
    else:
        arraySize = len(instances)
    
    instanceIds = []
    logging.debug('Created array of length: %s', len(instanceIds))
    
    for i in range(0, arraySize):
        instanceIds.append(instances[i].instanceId)
    #for instance in instances:
    #    instanceIds.append(instance.instanceId)
        #instanceJson = {}
        #instanceJson['instanceId'] = instance.instanceId
    
    rawJson['instanceIds'] = instanceIds;
    
    if len(instances) == limit:
        # We just give the last one in the list.  This means we'll overlap
        # by one each time, but that's OK
        rawJson['nextInstanceId'] = instances[limit - 1].instanceId
        rawJson['complete'] = False
    else:
        rawJson['complete'] = True
    
    json = jsonpickle.dumps(rawJson)
    return HttpResponse(json, mimetype='application/json')


@decorators.postRequired
@decorators.onlineRequired
@decorators.serverAddressRequired
def serverOnline(request):
    logging.info('Handling request: %s', request.REQUEST.items())
    online = util.requestStringToBool(request, 'online')
    
    if online:
        # We don't care about online notifications -- it should already be
        # online
        logging.info('Ignoring online notification')
        return HttpResponse()
    serverAddress = request.REQUEST.get('serverAddress')
    
    jsonResponse = {'message': 'Update succeeded'}
    setOffline(request, serverAddress, jsonResponse)
        
    return HttpResponse(json_encode(jsonResponse))

def setOffline(request, serverAddress, jsonResponse):
    query = models.OnlineInstance.all()
    query.filter('serverAddress = ', serverAddress)
    
    batchSize = 1000
    onlineInstances = query.fetch(limit=batchSize)
    
    count = 0
    for inst in onlineInstances:
        logging.info('Updating instance')
        pendingData = []
        updateFiles(request, inst, False, pendingData, jsonResponse)
        db.put(pendingData)
        inst.delete()
        count = count + 1
        
    if count == batchSize:
        # Call the function recursively until all of them are gone.
        logging.info('Count %s less than %s', count, batchSize)
        setOffline(serverAddress)
        