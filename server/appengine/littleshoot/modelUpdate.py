
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.http import HttpResponseNotFound
from django.http import HttpResponseBadRequest
 
from google.appengine.ext import db

import models
import logging
import urllib

def updateModelFile(request):
    title = request.REQUEST.get('title')
    #instanceIdString = request.REQUEST.get('instanceId')
    
    if title is None:
        fileQuery = models.File.all()
        fileQuery.order('title')
        file = fileQuery.get();
        if file is not None:
            title = file.title
        else:
            title = 'testing testing'
        #instanceId = file.instanceId
    #else:
     #   instanceId = int(instanceIdString)

    logging.info('Set title to: %s', title)
    q = models.File.gql('WHERE title >= :1 ORDER BY title', title)
    
    logging.info('created query')
    limit = 20
    files = q.fetch(limit)
    
    logging.info('fetched files: %s', str(len(files)))
    
    for curFile in files:
        
    
    #if len(files) > 0:
     #   curFile = files[0]
        
        # Check if the associated instance is online and set the status
        online = models.OnlineInstance.all().filter('instanceId = ', curFile.instanceId).get()
        
        if online:
            logging.info('Setting online to true')
            curFile.instanceOnline = True
        
        # We want to add the new property in any case!!
        logging.info('updating: %s', curFile.title)
        curFile.put()
    
    logging.info('Setting next url...');
    if len(files) == limit:
        # We've got more to update
        #nextFile = files[limit - 1]
        next_name = files[limit - 1].title
        next_url = '/updateModelFile?title=%s' % urllib.quote(next_name)
        moreData = True
    else:
        next_name = 'No more files'
        next_url = '/'  # just in case
        moreData = False
    
    
    context = {'current_name': title,
               'next_name': next_name,
               'next_url': next_url,
               'moreData' : moreData,
               }
    logging.info('rendering...')
    return render_to_response('update.html', context)

def updateModelMetaFile(request):
    uri = request.REQUEST.get('uri')
    title = request.REQUEST.get('title')
    if uri is None:
        metaFileQuery = models.MetaFile.all()
        metaFileQuery.order('uri')
        metaFile = metaFileQuery.get();
        if metaFile is not None:
            uri = metaFile.uri
        else:
            uri = 'testing testing'

    logging.info('Set uri to: %s', uri)
    q = models.MetaFile.gql('WHERE uri >= :1 ORDER BY uri', uri)
    
    logging.info('created query')
    metaFiles = q.fetch(limit=2)
    
    logging.info('fetched meta files: %s', str(len(metaFiles)))
    
    if len(metaFiles) > 0:
        curMetaFile = metaFiles[0]
        title = curMetaFile.title
        oi = hasattr(curMetaFile, 'onlineInstances')
        if oi:
            logging.info('Deleting online instances!!!')
            delattr(curMetaFile, 'onlineInstances')
            logging.info('Deleted attribute...')
        
        fileQuery = models.File.all()
        fileQuery.filter('uri =', curMetaFile.uri)
        fileQuery.filter('instanceOnline', True)
        
        curMetaFile.numOnlineInstances = fileQuery.count()
        
        
        #matchingFiles = fileQuery.fetch(20)
        #for matchingFile in matchingFiles:
            
        curMetaFile.put()
    
    logging.info('Setting next url...');
    if len(metaFiles) == 2:
        # We've got more to update
        nextFile = metaFiles[1]
        next_name = nextFile.title
        next_url = '/updateModelMetaFile?uri=%s' % urllib.quote(nextFile.uri) + '&title=%s' % urllib.quote(nextFile.title)
        moreData = True
    else:
        next_name = 'No more files'
        next_url = '/'  # just in case
        moreData = False
    
    
    context = {'current_name': title,
               'next_name': next_name,
               'next_url': next_url,
               'moreData' : moreData,
               }
    logging.info('rendering...')
    return render_to_response('update.html', context)


def populateModel(request):
    indexStr = request.REQUEST.get('index')
    if indexStr is None:
        index = 0
    else:
        index = int(indexStr)
    
    title = 'test_file_' + str(index)
    index += 1
    next_url = '/populateModel?index=' + str(index)
    
    if index < 20:
        moreData = True
    else:
        moreData = False
    context = {'current_name': title,
               'next_name': 'test_file_' + str(index),
               'next_url': next_url,
               'moreData' : moreData,
               }
    return render_to_response('update.html', context)

      