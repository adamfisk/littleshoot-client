
from django.http import HttpResponse
from django.http import HttpResponseNotFound
from django.http import HttpResponseBadRequest

from django.views.decorators.cache import cache_control

import models
import decorators
import logging
import mimetypes
import util

import searchPickler


@decorators.itemsPerPageRequired 
@decorators.startPageRequired
@decorators.keywordsRequired
@decorators.osRequired
#@decorators.appsRequired
#@decorators.audioRequired
#@decorators.docsRequired
#@decorators.imagesRequired
#@decorators.videoRequired 
#@cache_control(no_cache=True)
def search(request):
    logging.info('Handling search request: %s', request.REQUEST.items())
    
    keywords = request.REQUEST.get('keywords')
    startPage = int(request.REQUEST.get('startPage'))
    itemsPerPage = int(request.REQUEST.get('itemsPerPage'))
    
    os = request.REQUEST.get('os')
    applications = util.requestStringToBool(request, 'applications')
    audio = util.requestStringToBool(request, 'audio')
    docs = util.requestStringToBool(request, 'documents')
    images = util.requestStringToBool(request, 'images') 
    video = util.requestStringToBool(request, 'video')

    logging.info('About to query')

    limit = itemsPerPage
    offset = int(startPage) * limit
    
    mediaTypes = toMediaTypes(os, applications, audio, docs, images, video) 
    logging.info('Created media types: %s', mediaTypes)
    query =  'where tags in :tags and numOnlineInstances > 0 and takenDown = false'
    #query =  'where tags in :tags and takenDown = false'
    addTypes = len(mediaTypes) > 0
    if addTypes:
        query += " and mediaType in :mediaTypes"
    
    query += ' order by numOnlineInstances desc'
    
    logging.info('Created query: %s', query)
    
    if addTypes:
        logging.info('Querying with types: %s', mediaTypes)
        gqlQuery = models.MetaFile.gql(query, tags=keywords.split(), mediaTypes=mediaTypes)
    else:
        logging.info('Querying with no types')
        gqlQuery = models.MetaFile.gql(query, tags=keywords.split())

    
    logging.info('Querying for limit, offset: %s, %s', limit, offset)
    # Both count and fetch will return a maximum of 1000. 
    metaFiles = gqlQuery.fetch(limit, offset=offset)
    
    # TODO: There's a bug with count and ListProperty, so we just set the
    # total results to the size of the result set we're returning for now.
    #
    # See: http://code.google.com/p/googleappengine/issues/detail?id=586&sort=-id&colspec=ID%20Type%20Status%20Priority%20Stars%20Owner%20Summary
    totalResults = len(metaFiles)#gqlQuery.count();
    logging.info('Got total results: %s', totalResults)
    
    """
    if totalResults == 0:
        allQuery = models.MetaFile.all();
        allMetaFiles = allQuery.fetch(0, 100);
        logging.info('all into: %s', allMetaFiles)
        allPickled = searchPickler.pickle(allMetaFiles, itemsPerPage, startPage, 
                                       totalResults, keywords)
        
        logging.info('all pickled into: %s', allPickled)
    """
    
    #logging.info('About to pickle')
    if metaFiles is not None:
        pickled = searchPickler.pickle(metaFiles, itemsPerPage, startPage, 
                                       totalResults, keywords)
        
        #pickled = searchPickler.pickle([], 0, 0, 0, keywords)
        #logging.info('pickled into: %s', pickled)
        
        return HttpResponse(pickled)

    pickled = searchPickler.pickle([], 0, 0, 0, keywords)
    
    logging.info('No results...')
    return HttpResponse(pickled)

def toMediaTypes(os, applications, audio, docs, images, video):
    logging.info('Creating resource type')
    if applications and audio and docs and images and video:
        return []
    
    mediaTypes = []
    if applications:
        mediaTypes.append('application')
        mediaTypes.append('archive')
        if len(os) > 0:
            if os.find('mac') > -1:
                mediaTypes.append('application/mac')
            elif os.find('win') > -1:
                mediaTypes.append('application/win')
            else:
                mediaTypes.append('application/linux')
    
    if audio:
        mediaTypes.append('audio')
    if docs:
        mediaTypes.append('document')
        mediaTypes.append('archive')
    if images:
        mediaTypes.append('image')
    if video:
        mediaTypes.append('video')
    
    return mediaTypes