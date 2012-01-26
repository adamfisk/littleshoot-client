from google.appengine.ext import db
from django.core.serializers import serialize
from django.db.models.query import QuerySet
from django.http import HttpResponse
from django.utils import simplejson
import datetime
from google.appengine.api import users

class JsonResponse(HttpResponse):
    def __init__(self, object):
        if isinstance(object, QuerySet):
            content = serialize('json', object)
        else:
            content = simplejson.dumps(object)
        super(JsonResponse, self).__init__(content, mimetype='application/json')
        
'''
Not currently used, but interesting.

class GaeJsonEncoder(simplejson.JSONEncoder):
  def default(self, obj):
    isa=lambda *xs: any(isinstance(obj, x) for x in xs) # shortcut
    return obj.isoformat() if isa(datetime.datetime) else \
      dict((p, getattr(obj, p)) for p in obj.properties()) if isa(db.Model) else \
      obj.email() if isa(users.User) else \
      simplejson.JSONEncoder.default(self, obj)
'''
      
def toNormalizedDict(obj):
    normalizedEntities = {}
    for k in obj.fields().keys():
        
        v = getattr(obj, k) 
        #logging.info('pickling k,v: %s, %s', k, v)
        if v is not None:
            if isinstance(v, str):
                normalizedEntities[k] = str(v)    
            elif isinstance(v, unicode):
                #encoded = v.encode('ascii','ignore')
                #logging.info('Encoded: %s', encoded)
                normalizedEntities[k] = str(v)
            #elif isinstance(v, unicode):
             #   normalizedEntities[k] = unicode(v)
            elif isinstance(v, datetime.datetime):
                normalizedEntities[k] = v.strftime('%Y-%m-%d %H:%M:%S')
            elif isinstance(v, datetime.date):
                normalizedEntities[k] = v.strftime('%Y-%m-%d')
            elif isinstance(v, datetime.time):
                normalizedEntities[k] = v.strftime('%H:%M:%S')
            elif isinstance(v, (int, float, list, bool, long, unicode)): 
                normalizedEntities[k] = v
            else:
                normalizedEntities[k] = v 
    return normalizedEntities