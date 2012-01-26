

from django.http import HttpResponse
from django.http import HttpResponseNotFound
from django.http import HttpResponseBadRequest
from django.shortcuts import render_to_response
from django.http import HttpResponse
 
#from django.views.decorators.cache import cache_control
from google.appengine.ext import db
from google.appengine.ext.db import djangoforms
from google.appengine.api import channel
from google.appengine.api import memcache

import models
import decorators
import logging
import mimetypes
import mediaTypeTranslator
import jsonControllerUtils

import jsonpickle
import filesPickler
import util
import simplejson
from json import toNormalizedDict
import urllib
import uuid
import os

from datetime import datetime, timedelta

mimetypes.add_type('application/json', '.json')
mimetypes.add_type('text/yaml','.yaml')
mimetypes.add_type('application/atom+xml','.atom')
mimetypes.add_type('application/opensearchdescription+xml','.opensearch')
mimetypes.add_type('application/x-bittorrent','.torrent')
mimetypes.add_type('application/x-apple-diskimage', '.dmg')
mimetypes.add_type('application/x-rar', '.rar')

# Not totally clear what this should be.  Ubuntu uses text/plain.  
# Fedora uses application/x-iso9660-image
mimetypes.add_type('application/x-iso9660', '.iso')
mimetypes.add_type('video/x-matroska', '.mkv')
mimetypes.add_type('audio/x-matroska', '.mka')
mimetypes.add_type('video/x-m4v', '.m4v')

typeTranslator = mediaTypeTranslator.MediaTypeTranslator()

class PublishFileForm(djangoforms.ModelForm):
    class Meta:
        model = models.File
        exclude = ['tags', 'mediaType', 'mimeType']
  
class PublishMetaFileForm(djangoforms.ModelForm):
    class Meta:
        model = models.MetaFile
        exclude = ['tags', 'titles', 'mediaType', 'mimeType']

@decorators.uriRequired
def downloadSources(request):
    logging.info('Handling download sources request: %s', request.REQUEST.items())
    
    # We need to query all file instances for the given URI and that are
    # listed as online.
    
    # TODO: We could eventually add optimizations based on IP, time zone, etc
    # here.
    uri = request.REQUEST.get('uri')
    metaFile = models.MetaFile.gql('where uri = :1', uri).get()

    if metaFile is None:
        logging.info('No matching URI found')
        return HttpResponseNotFound('No matching URI found')

    downloads = metaFile.numDownloads + 1
    metaFile.numDownloads = downloads
    metaFile.put()
    
    if metaFile.numOnlineInstances is 0:
        logging.warn("No online instances")
        return HttpResponseNotFound('No online instances')
    
    filesQuery = models.File.all()
    filesQuery.filter('uri = ', uri)
    filesQuery.filter('instanceOnline = ', True)

    # The number of files shouldn't be too huge, but we'd ideally use a 
    # distributed counter here.
    #total = filesQuery.count()
    
    #filesQuery.order('title')
    files = filesQuery.fetch(200)
    
    sha1 = metaFile.sha1
    urls = []
    for file in files:
        #url = 'sip://' + str(file.instanceId) + '/uri-res/N2R?' + sha1
        url = 'sip://' + str(file.instanceId) + '/uri-res/N2R-' + sha1
        urls.append(url)
        
    if len(urls) == 0:
        logging.warn("Setting numOnlineInstances to 0")
        metaFile = models.MetaFile.gql('where uri = :1', uri).get()
        metaFile.numOnlineInstances = 0
        metaFile.put()
        
    logging.info('Added all instance data...pickling JSON...')
    sources = {
        'title' : metaFile.title,
        'downloads' : downloads,
        'urls' : urls,
        'sha1' : sha1
        }
    json = jsonpickle.dumps(sources)
    
    logging.info('Returning JSON: %s', json)
    return HttpResponse(json)


@decorators.postRequired       
@decorators.instanceIdRequired
@decorators.titleRequired
@decorators.sizeRequired
@decorators.uriRequired
@decorators.sha1Required
#@cache_control(no_cache=True)
@decorators.checkSignature
def publishFile(request):
    logging.info('Handling publishFile request: %s', request.REQUEST.items())
    return publishFileBase(request, True);

def publishFileBase(request, useInstanceId):
    requestDict = request.REQUEST
    logging.info('Handling publishFile request: %s', requestDict.items())
    title = requestDict.get('title')
    #logging.info('Getting SHA-1')
    sha1 = requestDict.get('sha1')
    #etag = requestDict.get('etag')
    uri = requestDict.get('uri')
    
    size = requestDict.get('size')
    
    #logging.info('Got params')
    if title[0] == '.':
        return HttpResponseBadRequest('Cannot publish dot files')
    
    #logging.info('About to guess MIME')
    mimeType, encoding = mimetypes.guess_type(title)
    #logging.info('full mime: %s', mimeType)
    if mimeType is None:
        mimeType = 'application/octet-stream'
    mediaType = typeTranslator.getType(title);
    #logging.info('mediaType: %s', mediaType)
    
    if useInstanceId:
        #logging.info('getting instance ID')
        instanceId = int(requestDict.get('instanceId'))
        file = models.File.gql('where sha1 = :1 and instanceId = :2', sha1, instanceId).get()
    else:
        file = models.File.gql('where uri = :1 and size = :2', uri, size).get()
        
    logging.info('Existing file is: %s', file)
    
    requestTags = requestDict.get('tags')
    tagsArray = toTags(requestTags)
    
    alreadyPublished = False
    if file is None:
        form = PublishFileForm(data=requestDict)
    else:
        logging.info('File is not None!!')
        form = PublishFileForm(data=requestDict, instance=file)
        alreadyPublished = True
    if not form.is_valid():
        logging.info('Form not valid!!')
        return HttpResponse('ERROR: Publish file errors:\n%s' % repr(form.errors),
                        content_type='text/plain', status=400)
        
    file = form.save(commit=False)
    file.downloaded = util.dictStringToBool(requestDict, 'downloaded')
    file.mimeType = mimeType
    file.mediaType = mediaType
    file.tags = tagsArray
    
    metaFile = models.MetaFile.gql('where uri = :1', form.cleaned_data['uri']).get()
    if metaFile is None:
        #logging.info('Creating new meta file')
        metaForm = PublishMetaFileForm(data=requestDict)
        if not metaForm.is_valid():
            logging.info('ERROR: Publish meta file errors:\n%s' % repr(metaForm.errors))
            return HttpResponse('ERROR: Publish meta file errors:\n%s' % repr(metaForm.errors),
                        content_type='text/plain', status=400)
        metaFile = metaForm.save(commit=False)
        metaTags = title.split()
        metaTags.extend(tagsArray)
        metaFile.tags = metaTags
        metaFile.titles = [metaFile.title]
        #metaFile.onlineInstances = []
        metaFile.mimeType = mimeType
        metaFile.mediaType = mediaType
        
    else:
        #logging.info('Updating meta file')
        if title not in metaFile.titles:
            metaFile.titles.append(title)
        
        metaTags = []
        for tag in tagsArray:
            if tag not in metaFile.tags:
                logging.info('Appending tag: %s', tag)
                metaTags.append(tag)
        metaFile.tags.extend(metaTags) 

    #logging.info('Saving metaFile: %s', metaFile)
    if useInstanceId:
        instance = models.OnlineInstance.gql('where instanceId = :1', instanceId).get()
        if instance is None:
            logging.error('We know nothing about the instance')
            file.instanceOnline = False
        else:
            file.instanceOnline = True
            logging.debug('Appending LittleShoot instance')
            #if instance.key() not in metaFile.onlineInstances:
            #logging.info('Adding instance...')
            #metaFile.onlineInstances.append(instance.key())
            
            # This could be a publish request that's updating an existing file,
            # in which case it shouldn't increase the online count.
            if not alreadyPublished:
                metaFile.numOnlineInstances += 1
            
    file.put()
    metaFile.put()
    
    logging.info("Encoding file title: %s", file.title)
    encodedName = urllib.quote(file.title)
    downloadParams = {'uri': metaFile.uri,
        'name': file.title,
        'size': metaFile.size,
        
        # Careful here. In the future the URI passed to this method might 
        # not be a URN.
        'urn': metaFile.uri
        }
    encodedDownloadParams = urllib.urlencode(downloadParams)
    fullUri = 'http://www.littleshoot.org/api/client/download/'+encodedName+'?' + encodedDownloadParams
    logging.info("Encoded URL: %s", fullUri)
    
    # The URI here needs to be the full
    params = {'uri': fullUri,
        'title': file.title,
        }
    #query = urllib.urlencode(params)
    #linkUrl = "http://www.littleshoot.org/link?"+ query
    
    allProps = toNormalizedDict(metaFile)
    #allProps['link'] = linkUrl
    allProps['link'] = fullUri
    
    logging.info("Props: %s", allProps)
    
    json = simplejson.dumps(allProps)
    
    channels = simplejson.loads(memcache.get('channels') or '{}')
    
    files = fetchRecentFiles("0", "10")
    encoded_message = filesPickler.pickle(files, 10)
    
    for channel_id in channels.iterkeys():
        # encoded_message = simplejson.dumps(message)
        logging.info("Sending message on channel: %s", channel_id)
        channel.send_message(channel_id, encoded_message)
        
    return jsonControllerUtils.writeResponse(request, json)


def toTags(requestTags):
    tagsArray = []
    if requestTags is not None:
        logging.info('Tags are: %s', requestTags)
        splitTags = requestTags.split(',')
        for tag in splitTags:
            tag = tag.strip().lower()
            tagsArray.append(tag)
    return tagsArray

@decorators.signatureRequired
@decorators.postRequired       
@decorators.instanceIdRequired
@decorators.sha1Required
@decorators.tagsRequired
#@cache_control(no_cache=True)
@decorators.checkSignature
def editFile(request):
    logging.info('Handling editFile request: %s', request.REQUEST.items())
    
    instanceId = request.REQUEST.get('instanceId')
    sha1 = request.REQUEST.get('sha1')
    
    file = models.File.gql('where instanceId = :1 and sha1 = :2', 
                            int(instanceId), sha1).get()
    
    if file is not None:
        requestTags = request.REQUEST.get('tags')
        
        newTags = toTags(requestTags)
        
        file.tags = newTags
        #url = request.REQUEST.get('url')
        file.put();
        #return HttpResponse()
        filesInstance = {'tags' : newTags,
                         'success' : True,
                         'message' : 'Michael Corleone says hello'}
        json = jsonpickle.dumps(filesInstance)
        return jsonControllerUtils.writeResponse(request, json)
            
    else:
        logging.warn('No matching file')
        return HttpResponseNotFound()

#@decorators.signatureRequired
@decorators.postRequired       
@decorators.instanceIdRequired
@decorators.uriRequired
#@cache_control(no_cache=True)
#@decorators.checkSignature
def deleteFile(request):
    logging.debug('Handling deleteFile request: %s', request.REQUEST.items())
    
    instanceId = request.REQUEST.get('instanceId')
    uri = request.REQUEST.get('uri')
    
    metaFile = models.MetaFile.gql("where uri = :1", uri).get()
    instance = models.OnlineInstance.gql('where instanceId = :1', 
                                         int(instanceId)).get()
    
    file = models.File.gql('where instanceId = :1 and uri = :2', 
                            int(instanceId), uri).get()
                            
    def trans(metaFile, instance, file):
        if file is not None:
            db.delete(file)
        else:
            logging.warning('No matching file for %s and %s', instanceId, uri)
            return HttpResponseNotFound()
        
        if metaFile is None:
            logging.warning('No matching meta file for %s and %s', instanceId, uri)
            return HttpResponseNotFound()
        
        if instance is not None:
            logging.info('Removing instance!!!')
            #metaFile.removeInstance(instance)
            metaFile.numOnlineInstances = metaFile.numOnlineInstances - 1
            metaFile.put()
        else:
            logging.warning('No online instance!')
        
        return HttpResponse()

    return trans(metaFile, instance, file)
    #return db.run_in_transaction(trans, metaFile, instance, file)

@decorators.instanceIdRequired
#@cache_control(no_cache=True)
def listFiles(request):
    logging.info('Handling listFiles request: %s', request.REQUEST.items())
    
    instanceId = request.REQUEST.get('instanceId')
    pageIndex = request.REQUEST.get('pageIndex')
    resultsPerPage = request.REQUEST.get('resultsPerPage')
    
    if not pageIndex:
        logging.warning('No pageIndex in: %s ', request.REQUEST.items())
        return HttpResponseBadRequest('pageIndex required')
    
    if not resultsPerPage:
        logging.warning('No resultsPerPage!!')
        return HttpResponseBadRequest('resultsPerPage required')
    
    
    limit = int(resultsPerPage)
    offset = int(pageIndex) * limit
    
    filesQuery = models.File.all()
    filesQuery.filter('instanceId = ', int(instanceId))
    filesQuery.filter('downloaded = ', False)

    # The number of files shouldn't be too huge, but we'd ideally use a 
    # distributed counter here.
    total = filesQuery.count()
    
    filesQuery.order('title')
    files = filesQuery.fetch(limit, offset)
    
    if files is not None:
        pickled = filesPickler.pickle(files, total)
        #logging.info('Sending response: %s', pickled)
        return HttpResponse(pickled, mimetype='application/json; charset=utf-8')

    else:
        return HttpResponse()

def smartFiles(request):
    channel_id = uuid.uuid4().hex
    token = channel.create_channel(channel_id)
    channels = simplejson.loads(memcache.get('channels') or '{}')
    logging.info("Got existing channels: %s", channels)
    channels[channel_id] = str(datetime.now())
    memcache.set('channels', simplejson.dumps(channels))

    template_values = {'token': token}

    path = os.path.join(os.path.dirname(__file__), "smartfiles.html")
    return render_to_response('smartfiles.html', template_values)


def recentFiles(request):
    logging.info('Handling listFiles request: %s', request.REQUEST.items())
    
    pageIndex = request.REQUEST.get('pageIndex')
    resultsPerPage = request.REQUEST.get('resultsPerPage')
    
    files = fetchRecentFiles(pageIndex, resultsPerPage)
    
    if files is not None:
        pickled = filesPickler.pickle(files, resultsPerPage)
        #logging.info('Sending response: %s', pickled)
        return HttpResponse(pickled, mimetype='application/json; charset=utf-8')

    else:
        return HttpResponse()

def fetchRecentFiles(pageIndex, resultsPerPage):
    if not pageIndex:
        logging.warning('No pageIndex!!')
        return HttpResponseBadRequest('pageIndex required')
    
    if not resultsPerPage:
        logging.warning('No resultsPerPage!!')
        return HttpResponseBadRequest('resultsPerPage required')

    limit = int(resultsPerPage)
    offset = int(pageIndex) * limit
    
    filesQuery = models.MetaFile.all()
    # filesQuery.filter('instanceId = ', int(instanceId))
    # filesQuery.filter('downloaded = ', False)
    filesQuery.order('-publishTime')

    # The number of files shouldn't be too huge, but we'd ideally use a 
    # distributed counter here.
    total = filesQuery.count()
    
    filesQuery.order('title')
    files = filesQuery.fetch(limit, offset)    
    return files

