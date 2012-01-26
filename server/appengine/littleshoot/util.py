
import itertools
from google.appengine.ext import db
from google.appengine.ext.db import StringProperty

import logging
import urllib
import models
import time

def expired(startTime, period):
    return time.time() - startTime > period
    
def isMac(request):
    return request.META['HTTP_USER_AGENT'].find('Mac') != -1

def isWindows(request):
    return request.META['HTTP_USER_AGENT'].find('Windows') != -1

def getExtension(request):
    ua = request.META['HTTP_USER_AGENT']
    if ua.find('Windows') != -1:
        return "exe"
    elif ua.find("Mac") != -1:
        return "dmg"
    else:
        return "tgz"
    
def requestStringToBool(request, str):
    return dictStringToBool(request.REQUEST, str)

def dictStringToBool(dict, str):
    value = dict.get(str)
    if value is None:
        #logging.warning('No value for %s', str)
        return False
    return stringToBool(value)

def stringToBool(str):
    lower = str.lower()
    if lower in ('false', 'off'):
        #logging.info('Got false')
        return False
    elif lower in ('true', 'on'):
        #logging.info('Got true')
        return True
    
    logging.warning('Boolean was neither true nor false: %s', str)
    return False

def newUrl(uri, title):

    logging.info('title is: %s', title)
    urlTitle = urllib.quote(title)
    urlUri = urllib.quote(uri)
    
    #data = {
    #    'uri' : sha1,
    #}
    #query = urlencode(args, doseq=True)
    
    url = 'http://p2p2o.littleshoot.org:8107/api/client/download/'+urlTitle+'?uri='+urlUri 
    metaFileQuery = models.MetaFile.all()
    metaFileQuery.filter('uri = ', uri)
    metaFile = metaFileQuery.get()
    
    if metaFile is None:
        logging.error('MetaFile does not exist for: %s', uri)
        logging.error('Returning no url')
        return
    
    numInstances = metaFile.numOnlineInstances
    if numInstances == 0:
        logging.warning('no online instances')
    else:
        logging.info('online instances are: %s', numInstances)
    
    return url
    #Starship%20Troopers%203%20French%20Stv%20Dvdrip%20Xvid-Fion%20Tester.avi?uri=urn%3Asha1%3AT7Q5HYB6WCYD656RBC54JDWRRSVGUMEY&name=Starship%20Troopers%203%20French%20Stv%20Dvdrip%20Xvid-Fion%20Tester.avi&urn=urn%3Asha1%3AT7Q5HYB6WCYD656RBC54JDWRRSVGUMEY&size=733304832&mimeType=video%2Fx-msvideo&cancelOnStreamClose=true&noCache=1224807053317&source=limewire'
    """
                    var params = 
                {
                uri: uri, 
                name: result.title,
                urn: result.sha1,
                size: result.size,
                mimeType: result.mimeType,
                cancelOnStreamClose: cancelOnStreamClose,
                noCache: (new Date()).getTime()
                };
            
            // Hack for backwards compatibility with 0.32.
            if (result.source === "limewire")
                {
                params.source = result.source;
                }
            if (CommonUtils.inGroup())
                {
                params.groupName = CommonUtils.getGroupName();
                }
            
            var fileUrl = Constants.DOWNLOAD_URL +
                encodeURIComponent(result.title) +
                "?"+
                dojo.objectToQuery(params);
    """

