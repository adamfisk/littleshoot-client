
import unittest
import logging
import hmac
import hashlib
import base64
#import jsonpickle
import models
import os

from google.appengine.ext.db import GqlQuery
from django.test.client import Client
from django.utils.http import urlencode
from google.appengine.ext import db

from base64 import urlsafe_b64encode
from base64 import b64encode

#import logging.config
#logging.config.fileConfig('logging.conf')

class BugsTestCast(unittest.TestCase):
    
    def testMacInstallerBug(self):
        client = Client()
        self.submitMacInstallerBug()
        response = client.post('/installerBugs')
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        #versionQuery = db.Query(models.Version)
        #version = versionQuery.get()
        #self.failIf(version is None, 'Did not submit version!!')
        #self.failUnlessEqual(0.50, version.version, 'Unexpected version')
        
        logging.info(response.content)
        
        
        
        
    def testTopTen(self):
        client = Client()
        self.submitBug()
        response = client.post('/bugs')
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        versionQuery = db.Query(models.Version)
        version = versionQuery.get()
        self.failIf(version is None, 'Did not submit version!!')
        self.failUnlessEqual(0.50, version.version, 'Unexpected version')
        
        logging.info(response.content)
        
        
    def testRedirect(self):
        client = Client()
        bugData = {
            'message' : 'test message',
            'logLevel' : 'warn',
            'className' : 'test',
            'methodName' : 'test',
            'lineNumber' : '10',
            'threadName' : 'testThread',
            'javaVersion' : '1.5.3',
            'osName' : 'Test OS',
            'osArch' : 'test arch',
            'osVersion' : 'os version',
            'language' : 'test language',
            'country' : 'test country',
            'timeZone' : 'test time zone',
            'userName' : 'test user name',
            'throwable' : 'test throwable',
            'count' : '1',
            'version' : '0.50',
            'instanceId' : '472897429'
            }
        response = client.post('/submit', bugData)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        query = models.Bug.all()
        results = query.fetch(limit=5)
        self.failUnlessEqual(len(results), 1, 'Expected 1 result')
          
        versionQuery = models.Bug.gql("where threadName = 'testThread'")
        bug = versionQuery.get()
        
        self.failIf(bug is None, 'Could not get bug')
        logging.info('Bug is: %s', bug)
        
        response = client.post('/submit', bugData)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        results = query.fetch(limit=5)
        self.failUnlessEqual(len(results), 1, 'Expected 1 result but was '+str(len(results)))
        
        bug = results[0]
        self.failUnlessEqual(2, bug.count, 'Expected 1 result but was '+str(len(results)))
        

    def submitBug(self):
        client = Client()
        bugData = {
            'message' : 'test message',
            'logLevel' : 'warn',
            'className' : 'test',
            'methodName' : 'test',
            'lineNumber' : '10',
            'threadName' : 'testThread',
            'javaVersion' : '1.5.3',
            'osName' : 'Test OS',
            'osArch' : 'test arch',
            'osVersion' : 'os version',
            'language' : 'test language',
            'country' : 'test country',
            'timeZone' : 'test time zone',
            'userName' : 'test user name',
            'throwable' : 'test throwable',
            'count' : '1',
            'version' : '0.50',
            'instanceId' : '472897429'
            }
        response = client.post('/submit', bugData)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
    def submitMacInstallerBug(self):
        client = Client()
        bugData = {
            'message' : 'test message',
            'lineNumber' : '10',
            'host' : 'Darwin afisk-macbook.local 9.3.0 Darwin Kernel Version 9.3.0: Fri May 23 00:49:16 PDT 2008; root:xnu-1228.5.18~1/RELEASE_I386 i386',
            'disk' : '1Filesystem      Size   Used  Avail Capacity  Mounted on',
            }
        response = client.post('/submitMacInstallerBug', bugData)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)