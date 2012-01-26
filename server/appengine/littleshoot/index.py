
from django.shortcuts import render_to_response

import appChecker
import logging

def index(request):
    logging.info('Index handling request: %s', request.REQUEST.items())
    
    #littleShootPresent = appChecker.littleShootPresent(request)
    littleShootPresent = False
    if littleShootPresent:
        logging.info('Returning straight index page...')
        #return render_to_response('index.html')
        return render_to_response('aboutTab.html', 
                              {'tabId' : 'thirdTab', 
                               'tabJavaScriptClass' : 'AboutTab', 
                               'homeSelected' : True});
    else:
        logging.info('No LittleShoot -- going to About Home')
        
        # This needs to match the value for home in urls.py.
        return render_to_response('aboutTab.html', 
                                  {'tabId' : 'thirdTab', 
                                   'tabJavaScriptClass' : 'AboutTab', 
                                   'homeSelected' : True});
    
