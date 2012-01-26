
import google.appengine.api.datastore_types
import datetime

import logging
import os

import jsonpickle

#import logging.config
#logging.config.fileConfig('logging.conf')


def pickle(metaFiles, itemsPerPage, startIndex, totalResults, keywords):
    
    newFiles = []
    
    for file in metaFiles:
        normalizedEntities = {}
        for k in file.fields().keys():
            
            v = getattr(file, k) 
            #logging.info('pickling k,v: %s, %s', k, v)
            if k == 'onlineInstances':
                # We don't serialize online instances.
                continue
            if v is not None:
                if isinstance(v, str):
                    normalizedEntities[k] = str(v)    
                elif isinstance(v, unicode):
                    #encoded = v.encode('ascii','ignore')
                    #logging.info('Encoded: %s', encoded)
                    normalizedEntities[k] = str(v)
                #elif isinstance(v, unicode):
                 #   normalizedEntities[k] = unicode(v)
                elif isinstance(v, (int, float, list, bool, long, unicode)): 
                    normalizedEntities[k] = v
                elif isinstance(v, datetime.datetime):
                    normalizedEntities[k] = v.strftime('%Y-%m-%d %H:%M:%S')
                elif isinstance(v, datetime.date):
                    normalizedEntities[k] = v.strftime('%Y-%m-%d')
                elif isinstance(v, datetime.time):
                    normalizedEntities[k] = v.strftime('%H:%M:%S')
                else:
                    normalizedEntities[k] = v 
            
        newFiles.append(normalizedEntities)
        
        
    
    """
        appendOpenSearchElement(doc, feed, "totalResults", totalResults);
        appendOpenSearchElement(doc, feed, "startIndex", startIndex);
        appendOpenSearchElement(doc, feed, "itemsPerPage", itemsPerPage);
    """
    formatted = len(newFiles)
    
    # TODO: totalResultsFormatted needs to be:
    # 1) Really the total results
    # 2) Formatted.
    
    # WAIT DO WE REALLY NEED TOTAL RESULTS FORMATTED?  WHERE THE HELL IS IT
    # USED???  MAYBE ONLY FOR DOWNLOADS?  BUT EVEN THERE, WHY?
    filesInstance = {
        'keywords' : keywords,
        'startIndex' : startIndex,
        'itemsPerPage' : itemsPerPage,
        'totalResults': totalResults, 
        'totalResultsFormatted': formatted, 
        'results' : newFiles}
    json = jsonpickle.dumps(filesInstance)
    #json.replace("'", "\\\\'")
    #json.replace("\"", "\\\\\"")
    return json