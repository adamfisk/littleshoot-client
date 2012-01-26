
import google.appengine.api.datastore_types
import datetime

import logging
import os

import jsonpickle
from json import toNormalizedDict


def pickle(obj, total):
    fp = FilesPickler()
    return fp.pickle(obj, total)
    
class FilesPickler():
    
                        
    def pickle(self, files, total):
        
        newFiles = []
        
        for file in files:
            #logging.info('file dired: %s', dir(file))
            normalizedEntities = toNormalizedDict(file)
            newFiles.append(normalizedEntities)
            
        
        #TODO TAGS!!!!!
        
        #logging.info(newFiles)
        #JsonUtils.put(json, "totalResults", totalResults);
        #JsonUtils.put(json, "totalResultsFormatted", formatted);
        
        #totalResults = len(newFiles)
        #formatted = len(newFiles)
        
        # TODO: totalResultsFormatted needs to be:
        # 1) Really the total results
        # 2) Formatted.
        filesInstance = {
            'totalResults': total, 
            'totalResultsFormatted': total, 
            'results' : newFiles}
        json = jsonpickle.dumps(filesInstance)
        return json