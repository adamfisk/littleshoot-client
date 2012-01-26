# Create your views here.

from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.http import HttpResponseRedirect

import logging

def download(request):
    
    logging.info('Processing download request: %s', request.GET)
    userAgent = request.META['HTTP_USER_AGENT']
    
    if 'Windows' in userAgent:
        logging.info('Returning Windows...')
        #return render_to_response('downloadWindows.html')
        return render_to_response('aboutTab.html', 
                                  {'tabId' : 'forthTab', 
                                   'tabJavaScriptClass' : 'AboutTab', 
                                   'downloadSelected' : True,
                                   'isWindows' : True})
    elif 'Mac' in userAgent:
        #return render_to_response('downloadWindows.html')
        #return render_to_response('downloadOther.html')
        #return render_to_response('downloadMac.html')
        logging.info('Returning mac...')
        return render_to_response('aboutTab.html', 
                          {'tabId' : 'forthTab', 
                           'tabJavaScriptClass' : 'AboutTab', 
                           'downloadSelected' : True,
                           'isMac' : True})
    else:
        #return render_to_response('downloadOther.html')
        logging.info('Returning other...')
        return render_to_response('aboutTab.html', 
                      {'tabId' : 'forthTab', 
                       'tabJavaScriptClass' : 'AboutTab', 
                       'downloadSelected' : True,
                        'isOther' : True})
    

def fileNotFound(request):
    logging.error("File Not Found for request path: \n %s \n args:\n %s \n and headers:\n %s", 
                  request.path,
                  request.REQUEST.items(),
                  request.META)
    return render_to_response('404.html')


def internalServerError(request):
    logging.error("Internal Server Error for request path: \n %s \n args:\n %s \n and headers:\n %s", 
                  request.path,
                  request.REQUEST.items(),
                  request.META)
    return render_to_response('500.html')
