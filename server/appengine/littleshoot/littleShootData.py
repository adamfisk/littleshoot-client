
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.http import HttpResponseNotFound
from django.http import HttpResponseRedirect
from django.utils import simplejson

import logging
import appChecker
import jsonpickle
import jsonControllerUtils
import urllib

"""
    Checks if LittleShoot is present on the user's machine using cookies and 
    request paramaters.
"""
def littleShootData(request):
    logging.info('Handling request: %s', request.REQUEST.items())
    
    littleShootPresent = appChecker.littleShootPresent(request, 0.80)
    if littleShootPresent:
        logging.info('Found LittleShoot...returning JSON')
        littleShootData = request.COOKIES.get('littleShootData')
        if littleShootData is None:
            # If we don't get general data, look for the cookie set by the client
            # check.  The client sets its own cookie, but it doesn't work on IE.
            littleShootData = request.COOKIES.get('littleShootClientCookie')
        json = urllib.unquote(littleShootData)
    else:
        # We can't really just return a 404 here because we still need to
        # call the callback function.
        data = {'appPresent' : False}
        json = jsonpickle.dumps(data)
    
    return jsonControllerUtils.writeResponse(request, json)
    
