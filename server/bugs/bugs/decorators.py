from django.http import HttpResponseForbidden
from django.http import HttpResponseBadRequest
#from difflib import Differ
import logging
import hmac
import hashlib
import base64

from google.appengine.api import memcache

#import logging.config
#logging.config.fileConfig('logging.conf')


def boolRequired(func, paramName):
    def wrapper(request):
        value = request.REQUEST.get(paramName)
        if not value:
            return HttpResponseBadRequest(
                'Boolean required for '+paramName+' found None')
        else:
            lower = value.lower();
            if not lower in ('false', 'true', 'on', 'off'): 
                return HttpResponseBadRequest(
                    'Boolean required for '+paramName+' found '+value)
            return func(request)
    return wrapper

def intRequired(func, paramName):
    def wrapper(request, *args, **kwds):
        foundInt = request.REQUEST.get(paramName)
        if not foundInt:
            return HttpResponseBadRequest(paramName + ' required')
        else:
            try:
                int (foundInt)
            except TypeError, error:
                logging.info('Error is: %s', error)
                return HttpResponseBadRequest(
                    'Number required for '+paramName+' found '+foundInt)
            
            return func(request, *args, **kwds)
            
    return wrapper

def stringRequired(func, paramName):
    def wrapper(request, *args, **kwds):
        if not request.REQUEST.get(paramName):
            #logging.info('Param not found:  %s in %s', paramName, 
                         #request.REQUEST.items())
            return HttpResponseBadRequest(paramName + ' required')
        else: 
            return func(request, *args, **kwds)
    return wrapper

def postRequired(func):
    def postWrapper(request, *args, **kwds):
        if request.method != 'POST':
            logging.warning('POST is required for: %s', request.get_full_path())
            return HttpResponseForbidden('POST required')
        else: 
            return func(request, *args, **kwds)
    return postWrapper
      
def checkSignature(func):
    def sigWrapper(request, *args, **kwds):
        sig = request.REQUEST.get('signature')
        if not sig:
            #logging.info('no signature -- must be a download')
            return func(request, *args, **kwds)
        else:
            #logging.info('Validating signature')
            return validateSignature(func, request, sig)
    return sigWrapper


def validateSignature(func, request, sig):
    keyId = request.REQUEST.get('keyId')
    if not keyId:
        #logging.info('No key ID')
        return HttpResponseForbidden('Key ID required')
    
    #logging.info('host: %s', request.get_host())
    url = request.get_host() + request.get_full_path()
    #logging.info('Full URL: %s', url)
    sigIndex = url.find('&signature')
    if sigIndex == -1:
        #logging.info('No signature')
        return HttpResponseForbidden('Signature required')
    
    url = 'http://' + url[0:sigIndex]
    #logging.info('extracted URL:   %s', url)
    
    #sessionId = request.COOKIES.get('sessionid')
    

    #if not sessionId:
    #    logging.warning('No session in %s', request.COOKIES)
    #    logging.warning('found: %s', sessionId)
    #    return HttpResponseForbidden('No session ID found')
    
    if not request.session.has_key(keyId):
        logging.warning('No key in session!!')
        return HttpResponseForbidden('No matching key')
    
    key = str(request.session[keyId])
    
    #logging.info('Found session ID: ' + sessionId)
    #key = str(memcache.get(sessionId))
    
    if not key:
        logging.warning('No key for session.  Cookies are: %s', request.COOKIES)
        return HttpResponseForbidden('No key for session')
    #key = str(request.session[keyId])
    
    
    logging.info('Found key, using: %s', key)
    hm = hmac.new(key, url, hashlib.sha1)
    computedSig = base64.b64encode(hm.digest())
    
    if computedSig != sig:
        logging.warning('Signature mismatch!!!, %s %s', sig, computedSig)
        return HttpResponseForbidden('Signature mismatch!!')
    
    #logging.debug('Signatures match!!!')
    return func(request)