
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.utils import simplejson

import datetime

import decorators
import logging
import urllib

def supportsLinks(request):
    logging.info("Checking for link support")
    return littleShootPresent(request, 0.97)
    
def littleShootPresent(request, minimumVersion):
    logging.info('Handling request: %s', request.REQUEST.items())
    logging.info('...with cookies...%s', request.COOKIES)
    
    userAgent = request.META['HTTP_USER_AGENT']
    logging.info("Got user agent: %s", userAgent)
    
    # Disabling the following because the user agent could be, for example:
    # Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-us) AppleWebKit/533.18.1 (KHTML, like Gecko) Version/5.0.2 Safari/533.18.5,gzip(gfe),gzip(gfe),gzip(gfe)
    #if userAgent is not None:
        # If the user agent is LittleShoot, they've clearly got LittleShoot.
    #    logging.info("Returning true due to LittleShoot user agent")
    #    return True
    
    littleShootData = request.COOKIES.get('littleShootData')
    logging.info("Got cookie data")
    if littleShootData is None:
        # If we don't get general data, look for the cookie set by the client
        # check.  The client sets its own cookie, but it doesn't work on IE.
        logging.info("Using client cookie")
        littleShootData = request.COOKIES.get('littleShootClientCookie')
    
    if littleShootData is None:
        logging.info("Still no cookie data...")
        siteListener = request.REQUEST.get('fromSiteListener')
        if siteListener is not None and siteListener == 'true':
            logging.info('App installed, running from SiteListener')
            #return HttpResponse('index.html')
            return True
        else:
            osxLauncher = request.REQUEST.get('fromOsxUrlOpener')
            winLauncher = request.REQUEST.get('fromWinUrlOpener')
            installForced = request.REQUEST.get('installed')
            if osxLauncher is not None and osxLauncher == 'true':
                logging.info("Found LittleShoot from OSX desktop launcher!!")
                return True
            elif winLauncher is not None and winLauncher == 'true':
                logging.info("Found LittleShoot from win desktop launcher!!")
                return True
            elif installForced is not None and installForced == 'true':
                logging.info("Found LittleShoot from install forced!!")
                return True
            else:
                logging.info("Still nothing -- not installed")
                return False
        
            
    elif len(littleShootData) == 0:
        # This is mostly defensive, as early versions of LittleShoot would 
        # set the cookie to empty if they could not find it.
        logging.info('Cookie is empty')
        return False;
    else:
        logging.info('Found cookie: %s', littleShootData)
        
        decoded = urllib.unquote(littleShootData)
        logging.info('Found cookie decoded: %s', decoded)
        data = simplejson.loads(decoded)
        
        logging.info('Data: %s', data)
        
        appPresent = data.get('appPresent')
        appVersion = data.get('appVersion')
        logging.info('app present: %s', appPresent)
        logging.info('app version: %s', appVersion)
        if appPresent:
            if minimumVersion is None:
                logging.info('No minimum version -- returning True')
                return True
            elif appVersion == 0:
                logging.info("Running from main line...");
                return True
            else:
                logging.info("Comparing versions...");
                return appVersion >= minimumVersion
        else:
            logging.info('Returning False')
            return False
        
