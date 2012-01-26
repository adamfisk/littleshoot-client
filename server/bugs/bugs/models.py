

import logging
from google.appengine.ext import db

class Bug(db.Model):

    # Messages can get really long, especially in logs that don't include
    # a throwable where we tend to send more data.
    message = db.TextProperty(required=True)
    logLevel = db.TextProperty()
    className = db.StringProperty()
    methodName = db.StringProperty()
    lineNumber = db.IntegerProperty(required=True)
    threadName = db.StringProperty()
    date = db.DateTimeProperty(auto_now_add=True)
    javaVersion = db.StringProperty()
    osName = db.StringProperty()
    osArch = db.StringProperty()
    osVersion = db.StringProperty()
    language = db.StringProperty()
    country = db.StringProperty()
    timeZone = db.StringProperty()
    throwable = db.TextProperty()
    version = db.FloatProperty(required=True)
    remoteAddress = db.StringProperty()
    userName = db.StringProperty()
    freeSpace = db.IntegerProperty()
    
    # Not required due to SIP servers and such.
    instanceId = db.IntegerProperty()
    count = db.IntegerProperty(required=True, default=0)
    
    
    
class Version(db.Model):
    version = db.FloatProperty(required=True)
    date = db.DateTimeProperty(auto_now_add=True)
    
    
class MacInstallerBug(db.Model):
    message = db.TextProperty(required=True)
    host = db.StringProperty()
    disk = db.StringProperty()
    group = db.StringProperty()
    user = db.StringProperty()
    version = db.FloatProperty()
    lineNumber = db.IntegerProperty(required=True)
    date = db.DateTimeProperty(auto_now_add=True)
    
class WinInstallerBug(db.Model):
    message = db.TextProperty(required=True)
    version = db.FloatProperty()
    #lineNumber = db.IntegerProperty(required=True)
    date = db.DateTimeProperty(auto_now_add=True)
    
class JavaScriptError(db.Model):
    message = db.TextProperty(required=True)
    url = db.StringProperty(required=True)
    lineNumber = db.IntegerProperty(required=True)
    date = db.DateTimeProperty(auto_now_add=True)
