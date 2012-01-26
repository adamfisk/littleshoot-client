
from django.http import HttpResponse
from django.http import HttpResponseBadRequest
from django.http import HttpResponseRedirect

import decorators
import logging
import os
from base64 import urlsafe_b64encode
from base64 import b64encode

from google.appengine.api import memcache

def generateKey(request):
    logging.info('Handling request: %s', request.REQUEST.items())
    
    keyId = request.REQUEST.get('keyId')
    if not keyId:
        logging.warning('No Key ID!!')
        return HttpResponseBadRequest('Key ID required')
                
    clientKey = os.urandom(32)
    siteKey = os.urandom(32)
    #logging.info('random number: %s', clientKey);
    
    # We substitute '=' because the cookie class calls str() on the value and
    # encloses it in quotes when encountering an '='.  This leads to java
    # interpreting the cookie differently from the browser.
    base64ClientKey = urlsafe_b64encode(clientKey).replace('=', 'F')
    base64SiteKey = urlsafe_b64encode(siteKey).replace('=', 'F')
    #logging.info('key: %s',  base64ClientKey)
    #logging.info('site key: %s', base64SiteKey)
    
    #logging.info('regular: %s', b64encode(siteKey))
    
    
    domain = '.littleshoot.org'
    #domain = '.littleshootapi.appspot.com'
    
    # We need to store the key in the database temporarily.
    response = HttpResponse()
    
    # we set no max_age here so the client can use the
    # key for the length of the browser session.
    
    # Would be max_age=40 or something like that.
    response.set_cookie('key', value=base64ClientKey, 
                        domain=domain, 
                        path='/api/client')
    
    # max_age is in seconds.
    response.set_cookie('siteKey', value=base64SiteKey, 
                        domain=domain,  
                        path='/')
    
    logging.info('using key id: %s', keyId);
    request.session[keyId] = base64ClientKey
    
    # Expiration times are in seconds.
    request.session.set_expiry(80)
    
    sessionId = request.session._get_session_key()
    
    logging.info('Session id: %s', sessionId)
    response.set_cookie('sessionid', value=sessionId, 
                        domain=domain,
                        path='/api/client')
    
    return response

