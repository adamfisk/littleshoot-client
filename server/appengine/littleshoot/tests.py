
import unittest
import logging
import hmac
import hashlib
import base64
import sha
import jsonpickle
import models
from models import Link
import os
import awsUtils
import util
import time
import amazonDevPayClient
import simplejson

from django.test.client import Client
from django.utils.http import urlencode
from django.conf import settings

from base64 import urlsafe_b64encode
from base64 import b64encode
import httplib
import facebook.djangofb as facebook
from facebook import Facebook

import logging.config
logging.config.fileConfig('logging.conf')

from shortener.baseconv import base62
from google.appengine.ext import db

def newClient():
    return Client(HTTP_USER_AGENT='Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6')


class ShortenerTestCase(unittest.TestCase):
    def testShorten(self):
        url = 'http://www.bop.org/totalboptime'
        new_link = Link(url = url)
        new_link.save()
        
        url2 = 'http://www.bop.org/number222222222'
        new_link2 = Link(url = url2)
        new_link2.save()
        
        q = Link.all()
        #q.filter("url =", url)
        urls = q.fetch(2)
        logging.info("Fetched URLs: %s", urls)
        logging.info("URL is: %s", urls[1].url)
        self.failUnlessEqual(urls[1].url, url2, 'Unexpected URL')
        short = urls[1].short_url()
        logging.info("Short: %s", short)
        
        
        keyId = base62.to_decimal(urls[1].to_base62())
        key = db.Key.from_path("Link", keyId)
        
        #link = get_object_or_404(Link, pk = key)
        #link.usage_count += 1
        #link.save()
        keyQuery = Link.gql('WHERE __key__ = :1', key)
        links = keyQuery.fetch(1)
        self.failUnlessEqual(1, len(links), 'Did not get link')
        logging.info("Link is: %s", links[0].url)
        self.failUnlessEqual(url2, links[0].url, 'Unexpected link: '+links[0].url)
        

        
class FacebookTestCase(unittest.TestCase):
    def testFetch(self):

        facebook = Facebook(settings.FACEBOOK_API_KEY, settings.FACEBOOK_SECRET_KEY)
        facebook.auth.createToken()
        # Show login window
        #facebook.login()
 
        # Login to the window, then press enter
        #print 'After logging in, press enter...'
        #raw_input()
 
        #facebook.auth.getSession()
        #info = facebook.users.getInfo([facebook.uid], ['name', 'birthday', 'affiliations', 'sex'])[0]

        
class AmazonS3TestCase(unittest.TestCase):

    def testHead(self):
        uri = 'littleshoot.s3.amazonaws.com'
        conn = httplib.HTTPConnection(uri)
        conn.request("HEAD", "/LittleShootPlugin.dmg")
        res = conn.getresponse()
        logging.info("Status, Reason: %s, %s", res.status, res.reason)
        logging.info("Headers: %s", res.getheaders())
        logging.info("Content-Length: %s", res.getheader("Content-Length"))
        
        if res.status == 200:
            logging.info("GOT 200")
        
    def testListFiles(self):
        """
        This is just a straight test of S3 and boto -- doesn't really touch 
        our code.
        """
        
        # Note this uses environment variables.
        from boto.s3.connection import S3Connection
        from boto.s3.bucket import Bucket
        conn = S3Connection()
        bucket = Bucket(conn, "littleshoot_test")
        response = bucket.list(prefix='user/adamfisk77/', delimiter='/')
        
        keys = []
        # The results are all Keys.  See:
        # http://boto.s3.amazonaws.com/index.html
        logging.info("LISTING FILES")
        for key in response:
            logging.info("key name: %s, %s", key.name, len(key.name))
            
            digest = hmac.new('7294', key.name, sha).digest()
            logging.info("digest: %s, %s", digest, len(digest))
            path = base64.encodestring(digest).strip()
            logging.info("Path is: %s", path)
            url = "http://f.littleshoot.org/" +path
            
            rawNameArray = key.name.split('/')
            index = len(rawNameArray) - 1;
            rawName = rawNameArray[index]
            logging.info("Raw name: %s", rawName)
            k = {
                 'name' : key.name,
                 'size' : key.size,
                 'last_modified' : key.last_modified}
            keys.append(k)
        
        data = {'bucket' : bucket.name, 'keys' : keys}
        json = simplejson.dumps(data) 
        logging.info('JSON output: \n %s', json)
        
        
class AmazonTestCase(unittest.TestCase):
    def testSigning(self):
        aws_access_key_id = 'awsAccessKeyIdToken'
        aws_secret_access_key = 'awsSecretAccessKeyToken'
        product_token = 'awsProductTokenToken'

        devpay_client = amazonDevPayClient.AmazonDevPayClient(aws_access_key_id,
                                                              aws_secret_access_key,
                                                              product_token)
        
        userToken = 'testingToken'
        policyFile1 = devpay_client.devPayPolicy(userToken)
        
        policyFile2 = devpay_client.policy()
        policyFileSignature = devpay_client.signedPolicy(policyFile1)
        
        
class ProDownloadTestCase(unittest.TestCase):
    def testProDownload(self):
        client = newClient()
        params = {
            'key' : 'this_will_never_work',
        }
        
        # Should not be a matching URI.
        query = urlencode(params, doseq=True)
        logging.info('query: %s', query)
        
        response = client.post('/proDownload', **{'QUERY_STRING': query})
        
        #response = client.post(url)
        failMessage = 'Unexpected response: '+str(response.status_code)+' '+response.content
        
        # We expect a 500-level response here because the callback to PayPal to
        # validate it should fail (since it's not a real request IPN request).
        self.failUnlessEqual(response.status_code, 200, failMessage)
        self.failUnlessEqual(response.content, "We're sorry, but we could not find a matching pro version in our database.  If you think this message is in error, please contact us at pro@littleshoot.org.", failMessage)
        
        logging.info('Response: '+str(response.status_code)+' '+response.content)
        
class UtilTimeTestCase(unittest.TestCase):
    def testExpired(self):
        expired = util.expired(time.time() - 60 * 60, 60*50)
        logging.info("Expired: %s", expired)
        self.failUnless(expired, "Should be expired")
        expired = util.expired(time.time() - 60 * 60, 60*70)
        logging.info("Expired: %s", expired)
        self.failUnless(not expired, "Should be expired")
        
class BotoTestCase(unittest.TestCase):
    def testBoto(self):
        osName = os.uname()[0]
        logging.info(osName)
        if (osName.find('Darwin') != -1):
            ext = "dmg"
            #expectedUrl = "https://littleshoot.s3.amazonaws.com:443/LittleShootPro.dmg?Signature=5hCJI5pkljt6pKF76tEcV9XgXRw%3D&Expires=1238635696&AWSAccessKeyId=04G2SEBTMTS8S59X1SR2"
            #expectedStart = "https://littleshoot.s3.amazonaws.com:443/LittleShootPro.dmg?Signature=5hCJI5pkljt6pKF76tEcV9XgXRw%3D&Expires=1"
        elif (osName.find('CYGWIN') != -1):
            ext = "exe"
            # The signature might be wrong on this -- does the sig include the file name?  Not sure.
            #expectedUrl = "https://littleshoot.s3.amazonaws.com:443/LittleShootPro.exe?Signature=5hCJI5pkljt6pKF76tEcV9XgXRw%3D&Expires=1238635696&AWSAccessKeyId=04G2SEBTMTS8S59X1SR2"
            #expectedStart = "https://littleshoot.s3.amazonaws.com:443/LittleShootPro.exe?Signature=5hCJI5pkljt6pKF76tEcV9XgXRw%3D&Expires=1"
        else:
            ext = "tgz"
            # The signature might be wrong on this -- does the sig include the file name?  Not sure.
            #expectedUrl = "https://littleshoot.s3.amazonaws.com:443/LittleShootPro.tgz?Signature=5hCJI5pkljt6pKF76tEcV9XgXRw%3D&Expires=1238635696&AWSAccessKeyId=04G2SEBTMTS8S59X1SR2"
            #expectedStart = "https://littleshoot.s3.amazonaws.com:443/LittleShootPro.tgz?Signature=5hCJI5pkljt6pKF76tEcV9XgXRw%3D&Expires=1"
        url = awsUtils.presigned(ext)
        #logging.info("uname: %s", )
        #logging.info(os.environ.keys())
        logging.info("Got signed: %s", url)
        
        # The sigs change every time, of course, so this is tough to test.
        #failMessage = "Unxpected query string: " + url
        #self.failUnless(url.find(expectedStart) == 0, failMessage)
        #self.failUnlessEqual(expectedUrl, url, failMessage)
        
class PayPalIpnTestCase(unittest.TestCase):

    def testUrl(self):
        client = newClient()
        params = {
            'first_name' : 'paypal test',
            'last_name' : 'paypal test',
            'address_country' : 'paypal test',
            'txn_id' : '62S53799EN327331K',
            'invoice' : 'paypal test',
            'mc_currency' : 'paypal test',
            'mc_fee' : '1.31',
            'payer_email' : 'billy@cookies.com',
            'payer_id' : 'paypal test',
            'payment_status' : 'Completed',
            'pending_reason' : 'paypal test'
        }
        
        # Should not be a matching URI.
        query = urlencode(params, doseq=True)
        logging.info('query: %s', query)
        
        response = client.post('/payPalIpn', **{'QUERY_STRING': query})
        
        #response = client.post(url)
        failMessage = 'Unexpected response: '+str(response.status_code)+' '+response.content
        
        # We expect a 500-level response here because the callback to PayPal to
        # validate it should fail (since it's not a real request IPN request).
        self.failUnlessEqual(response.status_code, 500, failMessage)
        
        logging.info('Response: '+str(response.status_code)+' '+response.content)
        
class AmazonDevPayTestCase(unittest.TestCase):

    def testUrl(self):
        client = newClient()
        params = {
            'ActivationKey' : 'ABIKEOQQQPHR3XZKE3MCPDBF3CIA',
            'ProductCode' : 'a49320afjajndf;an'
        }
        
        # Should not be a matching URI.
        query = urlencode(params, doseq=True)
        logging.info('query: %s', query)
        
        response = client.post('/amazonDevPayCallback', **{'QUERY_STRING': query})
        
        #response = client.post(url)
        failMessage = 'Unexpected response: '+str(response.status_code)+' '+response.content
        self.failUnlessEqual(response.status_code, 200, failMessage)
        
        logging.info('Response: '+str(response.status_code)+' '+response.content)
        

class AmazonFpsTestCase(unittest.TestCase):

    def testUrl(self):
        client = newClient()
        response = client.post('/amazonFpsBuy')
        
        #response = client.post(url)
        failMessage = 'Unexpected response: '+str(response.status_code)+' '+response.content
        self.failUnlessEqual(response.status_code, 200, failMessage)
        
        logging.info('Response: '+str(response.status_code)+' '+response.content)
        
class UrlParsingTestCase(unittest.TestCase):

    def testUrl(self):
        client = newClient()
        #url = '/link?title=flag.jpg&uri=urn%3Asha1%3A2XZL5S2PB2VRWZAPEWIHGN3Q2F3CO7BP&sender=adamfisk?url=http://www.littleshoot.org/link?title=flag.jpg&uri=urn%3Asha1%3A2XZL5S2PB2VRWZAPEWIHGN3Q2F3CO7BP&sender=adamfisk'
        #url = '/link?uri=urn%3Asha1%3A2XZL5S2PB2VRWZAPEWIHGN3Q2F3CO7BP'
        
        sha1='urn:sha1:uRUEIUREPOEREE'
        data = {
            'uri' : sha1,
            'title' : 'jfkadjfkal;'
        }
        
        # Should not be a matching URI.
        query = urlencode(data, doseq=True)
        logging.info('query: %s', query)
        response = client.post('/link', **{'QUERY_STRING': query})
        
        #response = client.post(url)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
class UpdateModelTestCase(unittest.TestCase):
    
    def testUpdate(self):
        client = newClient()
        fileName = 'aaaaaesterfajka'
        uri ='fiuaqfioaqfjiaqfjiaqjf'
        instanceId = 4782947
        response = publishFile(fileName, uri, instanceId)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        fileName = 'fafiwjfioe'
        uri ='afuoq7u498qofj'
        instanceId = 423415
        response = publishFile(fileName, uri, instanceId)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        response = client.post('/updateModelMetaFile')
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        
class RootRedirectTestCast(unittest.TestCase):
    
    def testRedirect(self):
        client = newClient();
        response = client.get('/')
        #logging.info(response.content)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        self.assertTrue('BigIdea' in response.content, 'Unexpected content!!')
        
class AppCheckTestCase(unittest.TestCase):
    
    def testCheck(self):
        client = newClient()
        
        data = '%7B%22appVersion%22%3A0%2C%22appPresent%22%3Atrue%2C%22searchers%22%3A%5B%22YouTube%22%2C%22Yahoo%22%2C%22Flickr%22%2C%22LimeWire%22%2C%22LittleShoot%22%5D%2C%22instanceId%22%3A1183733614%7D'
        client.cookies['littleShootData'] = data
    
        #logging.info('client dir: %s', dir(client))
        response = client.post('/api/littleShootData')
        
        content = response.content
        logging.info('Response: '+content)
        data = jsonpickle.loads(response.content)
        logging.info('instanceId: '+str(data['instanceId']))
        self.failUnlessEqual(data['instanceId'], 1183733614, 
                             'Unexpected instance ID in: '+response.content)
        
    
class DownloadSourcesTestCase(unittest.TestCase):
    
    def testDownloadSources(self):
        client = newClient()
        response = client.post('/api/downloadSources')
        self.failUnlessEqual(response.status_code, 400, 
                             'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        sha1 = 'urn:sha1:1289JOEUO2982'
        data = {
            'uri' : sha1,
        }
        
        # Should not be a matching URI.
        query = urlencode(data, doseq=True)
        logging.info('query: %s', query)
        response = client.post('/api/downloadSources', **{'QUERY_STRING': query})
        
        #logging.info("Headers: %s", response.getheaders())
        logging.info('response: %s', response._headers.keys())
        logging.info(response._headers.values())
        self.failUnlessEqual(response.status_code, 404, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        # Now, publish the file and make sure there's still no one there 
        # because the online instances should be empty.
        instanceId = 24024
        response = publishFile('tester.txt', sha1, instanceId)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        response = client.post('/api/downloadSources', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        sources = jsonpickle.loads(response.content)
        urls = sources['urls']
        self.failUnlessEqual(0, len(urls), 'Should be 1 URL')
        
        
        # Finally, put the instance online, and we should get a file!!
        putOnline(instanceId)
        response = client.post('/api/downloadSources', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        sources = jsonpickle.loads(response.content)
        logging.info('Got json: %s', sources)
        urls = sources['urls']
        self.failUnlessEqual(1, len(urls), 'Should be 1 URL')
        
        url = urls[0]
        self.failUnlessEqual('sip://'+str(instanceId)+'/uri-res/N2R?'+sha1, url, 'Unexpected URL: '+url)
        
        
        # Now get 2 instances.
        instanceId2 = 24024324
        response = putOnline(instanceId2)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        response = publishFile('tester.txt', sha1, instanceId2)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        response = client.post('/api/downloadSources', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        sources = jsonpickle.loads(response.content)
        logging.info('Got json: %s', sources)
        urls = sources['urls']
        self.failUnlessEqual(2, len(urls), 'Should be 2 URLs but found '+str(len(urls)))
        
        url = urls[0]
        self.failUnlessEqual('sip://'+str(instanceId)+'/uri-res/N2R?'+sha1, url, 'Unexpected URL: '+url)
        url2 = urls[1]
        self.failUnlessEqual('sip://'+str(instanceId2)+'/uri-res/N2R?'+sha1, url2, 'Unexpected URL: '+url2)
        
class ServerTestCase(unittest.TestCase):
    
    def testSipServer(self):
        client = newClient()
        response = client.post('/api/sipServer')
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response code: '+str(response.status_code))
        
        servers = jsonpickle.loads(response.content)
        
        self.failUnless(len(servers)==1, 'Bad server data')
        
        serverArray = servers['servers'];
        self.failIf(serverArray is None, 'should be an array of servers')
        
        address = serverArray[0].get('address')
        port = serverArray[0].get('port')
        
        self.failUnlessEqual('sip2.littleshoot.org', address, 'bad address')
        self.failUnlessEqual(5061, port, 'bad port')
   
    def testTurnServer(self):
        client = newClient()
        response = client.post('/api/turnServer')
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response code: '+str(response.status_code))
        
        servers = jsonpickle.loads(response.content)
        
        self.failUnless(len(servers)==1, 'Bad server data')
        
        serverArray = servers['servers'];
        self.failIf(serverArray is None, 'should be an array of servers')
        
        address = serverArray[0].get('address')
        port = serverArray[0].get('port')
        
        self.failUnlessEqual('turn2.littleshoot.org', address, 'bad address')
        self.failUnlessEqual(3478, port, 'bad port')     
        

class SearchTestCase(unittest.TestCase):
    
    def searchWeirdNames(self):
        client = newClient()

        tags = 'test\'this240out'
        searchData = {
            'keywords': tags,
            'itemsPerPage': 20,
            'startPage': 0, 
            'os': 'mac',
            'applications':'false',
            'audio':'true',
            'documents':'true',
            'images':'true',
            'video':'on',
        }
        
        instanceId = 429478
        uri = 'urn:sha1:1jio1jr91u93184hy87018'
        title = 'irou\'hiohqofrq'
        publishData = {
                'title' : 'test.txt',
                'size' : 42090,
                'uri' : uri,
                'instanceId' : instanceId,
                'tags': tags,
                'sha1': uri,
                }
        
        putOnline(instanceId)
        response = publishFileRaw(publishData)
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        query = urlencode(searchData, doseq=True)
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, response.status_code)
        
        files = jsonpickle.loads(response.content).get('results')
        logging.info('found files: %s', files)
        
        if len(files) < 1:
            publishedFiles = models.MetaFile.gql('where uri = :1', uri).get()
            logging.info('published files for uri: %s', publishedFiles)
        self.failUnlessEqual(1, len(files), response.content)

    def insertFiles(self, client, tags, numFilesAndInstances):
        uri = 'urn:sha1:afjka897432208120dljo23'
        instanceId = 781741
        publishData = {
            'title' : 'test.txt',
            'size' : 42090,
            'uri' : uri,
            'instanceId' : instanceId,
            'tags': tags,
            'sha1': uri,
            }
        response = putOnline(instanceId)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response code: '+str(response.status_code)+' '+response.content)
        baseId = 49204
        
        instances = []
        sha1s = []
        files = []

        numInstances = numFilesAndInstances
        for i in xrange(numFilesAndInstances):
            sha1 = str(os.urandom(32))
            sha1 = 'urn:sha1:'+urlsafe_b64encode(sha1)
            sha1s.append(sha1)
        
        for i in xrange(numInstances):
            id = baseId + i
            putOnline(id)
            for x in xrange(i):
                
                
                #logging.info('\n\n\n\n\nsha1*************:%s\n\n\n\n\n ', sha1)
                sha1 = sha1s[x]
                publishData.update({'instanceId' : id, 'sha1': sha1, 
                    'uri': sha1, 'title' : 'test'+str(x)+'.txt',})
                
                response = publishFileRaw(publishData)
                self.failUnlessEqual(response.status_code, 200, 
                    'Unexpected response code: '+str(response.status_code)+' '+response.content)
                files.append(publishData)
            
        return files
            
        
    def testSearchPaging(self):
        client = newClient()
        
        tags = 'ajfaldkjkla'
        numFilesAndInstances = 12
        files = self.insertFiles(client, tags, numFilesAndInstances)
        itemsPerPage = 6;
        data = {
            'keywords': tags,
            'itemsPerPage': itemsPerPage,
            'startPage': 0, 
            'os': 'mac',
            'applications':'off',
            'audio':'true',
            'documents':'true',
            'images':'true',
            'video':'on',
        }
        
        query = urlencode(data, doseq=True)
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+response.content)
        files = jsonpickle.loads(response.content).get('results')
        
        expectedResults = itemsPerPage
        self.failUnlessEqual(expectedResults, len(files), 
            'Should be '+str(expectedResults)+' files, but response was: '+str(len(files)))
        
        for i in xrange(expectedResults):
            logging.info('Accessing index %s', str(i))
            title = files[i].get('title')
            expectedInstances = numFilesAndInstances - i -1
            expectedTitle = 'test'+str(i)+'.txt'
            self.failUnlessEqual(expectedTitle, title, 
                'Unexpected title: '+title+' expected: '+expectedTitle)
            
            numOnlineInstances = files[i].get('numOnlineInstances')
            self.failUnlessEqual(expectedInstances, numOnlineInstances, 
                'Unexpected number of online instances.  Expected ' +str(expectedInstances) + ' but was: ' + str(numOnlineInstances))
        
        data.update({'startPage': 1})
        query = urlencode(data, doseq=True)
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+response.content)
        files = jsonpickle.loads(response.content).get('results')
        
        # There will be one less because the last file has no instances online.
        expectedResults = expectedResults - 1
        self.failUnlessEqual(expectedResults, len(files), 
            'Should be '+str(expectedResults)+' files, but response was: '+str(len(files)))
        
        baseIndex = numFilesAndInstances - itemsPerPage
        for i in xrange(expectedResults):
            logging.info('Accessing index %s', str(i))
            title = files[i].get('title')
            expectedInstances = baseIndex - i - 1
            expectedTitle = 'test'+str(baseIndex + i)+'.txt'
            self.failUnlessEqual(expectedTitle, title, 
                'Unexpected title: '+title+' expected: '+expectedTitle)
            
            numOnlineInstances = files[i].get('numOnlineInstances')
            self.failUnlessEqual(expectedInstances, numOnlineInstances, 
                'Unexpected number of online instances.  Expected ' +str(expectedInstances) + ' but was: ' + str(numOnlineInstances))
            
                 
    def testSearch(self):
        client = newClient()
        data = {'keywords': 'test'}
        
        query = urlencode(data, doseq=True)
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 400, 
            'Unexpected response code: '+str(response.status_code))

        data = {
            'keywords': 'testthisout',
            'itemsPerPage': 20,
            'startPage': 0, 
            'os': 'mac',
            'applications':'false',
            'audio':'true',
            'documents':'true',
            'images':'true',
            'video':'on',
        }
        
        query = urlencode(data, doseq=True)
        logging.info('query: %s', query)
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+response.content)
        
        files = jsonpickle.loads(response.content).get('results')
        self.failUnlessEqual(0, len(files), 
                             'Should be no data, but found: '+response.content)
        
        instanceId = 429478
        uri = 'urn:sha1:1jio1jr91u93184hy87018'
        publishData = {
                'title' : 'test.txt',
                'size' : 42090,
                'uri' : uri,
                'instanceId' : instanceId,
                'tags': 'testthisout, blah',
                'sha1': uri,
                }
        
        putOnline(instanceId)
        response = publishFileRaw(publishData)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+response.content)
        
        query = urlencode(data, doseq=True)
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response code: '+str(response.status_code))
        
        files = jsonpickle.loads(response.content).get('results')
        logging.info('found files: %s', files)
        
        if len(files) < 1:
            publishedFiles = models.MetaFile.gql('where uri = :1', uri).get()
            logging.info('published files for uri: %s', publishedFiles)
        self.failUnlessEqual(1, len(files), 
                             'Should be 1 file '+response.content)
        
        # Make sure it obeys the media type.
        data.update({'documents': False})
        query = urlencode(data, doseq=True)
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response code: '+str(response.status_code))
        
        files = jsonpickle.loads(response.content).get('results')
        logging.info('found files: %s', files)
        self.failUnlessEqual(0, len(files), 
                             'Should be no files '+response.content)    
        
        # Test video
        uri = 'urn:sha1:EUYUI';
        instanceId = 197196
        publishData = {
            'title' : 'test.mov',
            'size' : 42090,
            'uri' : uri,
            'instanceId' : instanceId,
            'tags': 'testthisout, blah',
            'sha1': uri,
            }
        response = publishFileRaw(publishData)
        
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+response.content)
        putOnline(instanceId)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+response.content)
        
        queryData = {
            'keywords': 'testthisout',
            'itemsPerPage': 20,
            'startPage': 0, 
            'os': 'mac',
            'applications':'false',
            'audio':'true',
            'documents':'false',
            'images':'true',
            'video':'true',
        }
        query = urlencode(queryData, doseq=True)
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response code: '+str(response.status_code))
        
        files = jsonpickle.loads(response.content).get('results')
        logging.info('found files: %s', files)
        self.failUnlessEqual(1, len(files), 
                             'Should be one file '+response.content)  
        file = files[0]
        numOnlineInstances = file.get('numOnlineInstances')
        self.failUnlessEqual(1, numOnlineInstances, 'Expected 1 online instance')  

        publishData.update({'tags' : 'different, completely'})
        response = publishFileRaw(publishData)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+response.content)
        
        queryData.update({'keywords': 'completely'})
        query = urlencode(queryData, doseq=True)
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response code: '+str(response.status_code))
        
        json = jsonpickle.loads(response.content)
        
        logging.info('Full JSON response:\n %s\n', json)
        files = json.get('results')
        
        logging.info('found files: %s', files)
        self.failUnlessEqual(1, len(files), 
                             'Should be one file '+response.content) 
        file = files[0]
        self.failUnlessEqual('video/quicktime', file['mimeType'], 
                             'unexpected mimeType: '+file['mimeType'])
        numOnlineInstances = file.get('numOnlineInstances')
        self.failUnlessEqual(1, numOnlineInstances, 'Expected 1 online instance but was '+str(numOnlineInstances))
        
        # Now put the user offline and make sure we don't get the result anymore
        response = putOffline(instanceId)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response code: '+str(response.status_code)+' '+response.content)
        
        
class EditFileTestCase(unittest.TestCase):
    def testEdit(self):
        client = newClient()
        fileName = 'testFileEditing.txt'
        uri ='urn:sha1:FudIOr341431FDF'
        instanceId = 49272
        
        onlineResponse = putOnline(instanceId)
        self.failUnlessEqual(onlineResponse.status_code, 200, 
                             'Unexpected response code: '+str(onlineResponse.status_code))
        
        files = listFiles(instanceId)
        self.failUnlessEqual(0, len(files))

        data = {'title': fileName, 
            'uri': uri,
            'size': 1313,
            'instanceId': instanceId,
            'sha1': uri,
            'tags' : 'original, tags'}
        publishFileRaw(data)
        
        files = listFiles(instanceId)
        self.failUnlessEqual(1, len(files))
        data = {
            'instanceId': instanceId,
            'sha1' : uri,
            'tags' : 'new tags, for test'
        }
        
        query = urlencode(data, doseq=True)

        # Make sure tag editing works
        response = sendSigned('/api/editFile', data)
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+response.content)
        
        files = listFiles(instanceId)
        fileTags = files[0].get('tags')
        self.failUnlessEqual(['new tags', 'for test'], fileTags, 'Unexpected tags: '+str(fileTags))
        
class DeleteFileTestCase(unittest.TestCase):
    def testDelete(self):
        client = newClient()

        fileName = 'testThisBabyOut.txt'
        uri ='urn:sha1:jcj289y292ud0j2020'
        instanceId = 429478
        
        onlineResponse = putOnline(instanceId)
        self.failUnlessEqual(onlineResponse.status_code, 200, 
                             'Unexpected response code: '+str(onlineResponse.status_code))
        
        files = listFiles(instanceId)
        self.failUnlessEqual(0, len(files))

        data = {'title': fileName, 
            'uri': uri,
            'size': 1313,
            'instanceId': instanceId,
            'sha1': uri}
        publishFileRaw(data)
        
        files = listFiles(instanceId)
        self.failUnlessEqual(1, len(files))
        data = {
            'instanceId': instanceId,
            'uri' : uri
        }
        
        query = urlencode(data, doseq=True)
        
        # Make sure we can't delete using an unsigned request.
        response = client.post('/api/deleteFile', **{'QUERY_STRING': query})
        
        # TODO: This should really check for 403!!
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response code: '+str(response.status_code))
        
        # Now make sure incorrect signing doesn't work
        data.update({'signature' : 'blah'})
        query = urlencode(data, doseq=True)
        
        # Make sure we can't delete using an unsigned request.
        response = client.post('/api/deleteFile', **{'QUERY_STRING': query})
        # TODO: This should really check for 403!!
        self.failUnlessEqual(response.status_code, 404, 
                             'Unexpected response code: '+str(response.status_code))
        
        
        files = listFiles(instanceId)
        
        ## TODO: This should really check for 1 file!!
        self.failUnlessEqual(0, len(files), 'Should be one file')
        
        # Now make sure signed requests work!
        data = {
            'instanceId': instanceId,
            'sha1' : uri
        }
        
        logging.info('Deleting file')
        sendSigned('/api/deleteFile', data)

        files = listFiles(instanceId)
        
        logging.info('loaded json: %s', files)
        self.failUnlessEqual(0, len(files), 'Should be no files after delete')
        
        
class FileListingTestCase(unittest.TestCase):
    
    def testFilePaging(self):
        client = newClient()
        
        numFiles = 12
        uri = 'urn:sha1:afjka897432208120dljo23'
        instanceId = 781741
        publishData = {
            'title' : 'test.txt',
            'size' : 42090,
            'uri' : uri,
            'instanceId' : instanceId,
            'tags': 'test',
            'sha1': uri,
            }

        alphabet = ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',]
        for i in xrange(numFiles):
            sha1 = str(os.urandom(32))
            uniqueSha1 = 'urn:sha1:'+urlsafe_b64encode(sha1)
            #sha1s.append(sha1)
            publishData.update({'title' : alphabet[i]+'.mov', 'uri' : uniqueSha1, 'sha1' : uniqueSha1})
            response = publishFileRaw(publishData)
            self.failUnlessEqual(response.status_code, 200, 
                'Unexpected response code: '+str(response.status_code)+' '+response.content)
        
        resultsPerPage = 4
        data = {
            'instanceId': instanceId,
            'resultsPerPage': resultsPerPage
        }
        
        for i in xrange(3):
            data.update({'pageIndex': i})
            query = urlencode(data, doseq=True)
            response = client.post('/api/fileListing', **{'QUERY_STRING': query})
            self.failUnlessEqual(response.status_code, 200, 
                'Unexpected response code: '+str(response.status_code)+' '+response.content)
            
            loadedJson = jsonpickle.loads(response.content)
            files = loadedJson.get('results')
            logging.info('Got files: %s', files)
            self.failUnlessEqual(resultsPerPage, len(files), 'Unexpected number of files')
            
            totalResults = loadedJson.get('totalResults')
            logging.info('Got total results: %s', totalResults)
            self.failUnlessEqual(numFiles, totalResults, 'Unexpected total results: '+ str(totalResults))
            
            offsetIndex = resultsPerPage * i
            for z in xrange(resultsPerPage):
                #logging.info('')
                fileSetIndex = offsetIndex + z
                expectedTitle = alphabet[fileSetIndex]+'.mov'
                logging.info('Expecting: %s', expectedTitle)
                file = files[z]
                readTitle = file.get('title')
                self.failUnlessEqual(expectedTitle, readTitle, '')
                
        
        
    def testFileListing(self):
        client = newClient()
        
        fileName = 'testThisBabyOut.txt'
        uri ='urn:sha1:FDJJFLKSLS42432fadfa'
        instanceId = 429478
        pageIndex = 0
        resultsPerPage = 20
        
        # Just make sure there are no files to start with.
        files = listFiles(instanceId)
        self.failUnlessEqual(0, len(files))
        
        publishFile(fileName, uri, instanceId)
        
        data = {
            'instanceId': instanceId,
            #'pageIndex': pageIndex,
            #'resultsPerPage': resultsPerPage
        }
        
        query = urlencode(data, doseq=True)
        response = client.post('/api/fileListing', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 400, 'Unexpected response: '+response.content)
        
        # Still should fail with just pageIndex
        data.update({'pageIndex': pageIndex})
        query = urlencode(data, doseq=True)
        response = client.post('/api/fileListing', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 400, 'Unexpected response: '+response.content)
        
        # Now should succeed and get a listing with the above file.
        data.update({'resultsPerPage': resultsPerPage})
        query = urlencode(data, doseq=True)
        response = client.post('/api/fileListing', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 'Unexpected response: '+response.content)
        
        files = jsonpickle.loads(response.content).get('results')
        logging.info('Got files: %s', files)
        self.failUnlessEqual(1, len(files), 'Should be 1 file')
        
   
             
class OnlineTestCase(unittest.TestCase):

    def clearInstances(self, serverAddress):
        client = newClient()
        postData = {'serverAddress': serverAddress, 'limit': 1000}
        response = client.post('/api/instancesForServer', postData)
        json = jsonpickle.loads(response.content)
        nextInstanceId = json.get('nextInstanceId')
        complete = json.get('complete')
        jsonIds = json.get('instanceIds')
        for instanceId in jsonIds:
            response = putOffline(instanceId)
            self.failUnlessEqual(response.status_code, 200, 'Unexpected response: '+response.content)
        
    def testInstancesForServer(self):
        self.clearInstances('43.32.56.76')
        client = newClient()
        
        # We'll put a bunch of users online and just make sure we get back
        # the correct number of results, next instance IDs, etc.
        instanceIds = [144444, 244444, 344444, 444444, 
                       544444, 644444, 744444, 844444] 
        for instanceId in instanceIds:
            putOnline(instanceId)
            
        postData = {'serverAddress': '43.32.56.76', 'limit': 4}
        response = client.post('/api/instancesForServer', postData)
        logging.info('Got response to instancesForServer: %s', response.content)
        json = jsonpickle.loads(response.content)
        nextInstanceId = json.get('nextInstanceId')
        complete = json.get('complete')
        jsonIds = json.get('instanceIds')
        self.failUnlessEqual(False, complete, 'Unexpected JSON:'+str(json))
        self.failUnlessEqual([144444, 244444, 344444], jsonIds, 'Unexpected JSON:'+str(json))
        self.failUnlessEqual(444444, nextInstanceId, 'Unexpected JSON:'+str(json))
        
        
        postData['instanceId'] = nextInstanceId
        response = client.post('/api/instancesForServer', postData)
        logging.info('Got response to instancesForServer: %s', response.content)
        json = jsonpickle.loads(response.content)
        nextInstanceId = json.get('nextInstanceId')
        complete = json.get('complete')
        jsonIds = json.get('instanceIds')
        
        #self.failUnless(nextInstanceId is None, 'Got an instance id in: '+str(json))
        self.failUnlessEqual(False, complete, 'Unexpected JSON:'+str(json))
        self.failUnlessEqual([444444, 544444, 644444], jsonIds, 'Unexpected JSON:'+str(json))
        self.failUnlessEqual(744444, nextInstanceId, 'Unexpected JSON:'+str(json))
        
        
        postData['instanceId'] = nextInstanceId
        response = client.post('/api/instancesForServer', postData)
        logging.info('Got response to instancesForServer: %s', response.content)
        json = jsonpickle.loads(response.content)
        nextInstanceId = json.get('nextInstanceId')
        complete = json.get('complete')
        jsonIds = json.get('instanceIds')
        self.failUnless(nextInstanceId is None, 'Got an instance id in: '+str(json))
        self.failUnlessEqual(True, complete, 'Unexpected JSON:'+str(json))
        self.failUnlessEqual([744444, 844444], jsonIds, 'Unexpected JSON:'+str(json))
        
    # This tests the code that doesn't put all files online or offline for 
    # a given user at once because it will time out for large data sets.
    # This code instead pages it.
    def testThrottledOnlineOffline(self):
        client = newClient()
        
        # We'll basically just put a bunch of files online for a given user,
        # set that user to offline, make sure we get back the correct JSON
        # at each pass, and progressively set each one to offline.
        instanceId = 74289711
        putOnline(instanceId)
        
        numFiles = 16
        for i in range(0, numFiles):
            # We need to normalize the names for lexical ordering (same # digits)
            if i < 10:
                suffix = '0' + str(i)
            else:
                suffix = str(i)
            fileName = 'test_file_'+suffix
            uri = 'urn:sha1:'+suffix
            publishFile(fileName, uri, instanceId)
        
        files = listFiles(instanceId)
        self.failUnlessEqual(numFiles, len(files), 'Unexpected number of files: '+str(len(files)))

        # The JSON should tell us which SHA-1 to update next
        response = putOffline(instanceId)
        json = jsonpickle.loads(response.content)
        logging.info('Got json: %s', json)
        startSha1 = json.get('nextSha1')
        complete = json.get('complete')
        self.failUnlessEqual('urn:sha1:05', startSha1, 'Unexpected JSON:'+str(json))
        self.failUnlessEqual(False, complete, 'Unexpected JSON:'+str(json))
        
        logging.info('Setting offline with start SHA-1: %s', startSha1)
        response = putOfflineWithStart(instanceId, startSha1)
        json = jsonpickle.loads(response.content)
        logging.info('Got json: %s', json)
        startSha1 = json.get('nextSha1')
        complete = json.get('complete')
        self.failUnlessEqual('urn:sha1:10', startSha1, 'Unexpected JSON:'+str(json))
        self.failUnlessEqual(False, complete, 'Unexpected JSON:'+str(json))
        
        response = putOfflineWithStart(instanceId, startSha1)
        json = jsonpickle.loads(response.content)
        logging.info('Got json: %s', json)
        startSha1 = json.get('nextSha1')
        complete = json.get('complete')
        self.failUnlessEqual('urn:sha1:15', startSha1, 'Unexpected JSON:'+str(json))
        self.failUnlessEqual(False, complete, 'Unexpected JSON:'+str(json))
        
        response = putOfflineWithStart(instanceId, startSha1)
        json = jsonpickle.loads(response.content)
        logging.info('Got json: %s', json)
        startSha1 = json.get('nextSha1')
        complete = json.get('complete')
        self.assertTrue(startSha1 is None)
        #self.failUnlessEqual('urn:sha1:10', startSha1, 'Unexpected JSON:'+str(json))
        self.failUnlessEqual(True, complete, 'Unexpected JSON:'+str(json))
        
    def testServerOnlineOffline(self):
        client = newClient()
        
        instanceId = 47434729
        instanceId2 = 424155
        instanceId3 = 12876
        serverAddress = '22.12.43.4'
        serverOnlineData = {'serverAddress': serverAddress, 'online': 'false'}
        response = client.post('/api/serverOnline', serverOnlineData)
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        # Should succeed
        postData = {'instanceId' : instanceId, 'online' : 'true', 
                    'serverAddress': serverAddress, 'baseUri' : 'sip://31312'}
        response = client.post('/api/instanceOnline', postData)
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        postData = {'instanceId' : instanceId2, 'online' : 'true', 
                    'serverAddress': serverAddress, 'baseUri' : 'sip://31312'}
        response = client.post('/api/instanceOnline', postData)
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        postData = {'instanceId' : instanceId3, 'online' : 'true', 
                    'serverAddress': serverAddress, 'baseUri' : 'sip://31312'}
        response = client.post('/api/instanceOnline', postData)
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        query = models.OnlineInstance.all()
        query.filter('serverAddress = ', serverAddress)
        onlineInstances = query.fetch(limit=1000)
        self.failUnlessEqual(3, len(onlineInstances), 'unexpected number of online instances: '+str(len(onlineInstances)))
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        # Now put a file up for the user and search for it.  Then, we'll put
        # the server offline and make sure we can no longer find the file.
        uri = 'urn:sha1:ruioqur893941ihjehjw'
        tags = 'ryqrueqhuiqwofnnalf'
        publishData = {
                'title' : 'test.txt',
                'size' : 42090,
                'uri' : uri,
                'instanceId' : instanceId,
                'tags': tags,
                'sha1': uri,
                }
        
        response = publishFileRaw(publishData)
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        searchData = {
            'keywords': tags,
            'itemsPerPage': 20,
            'startPage': 0, 
            'os': 'mac',
            'applications':'false',
            'audio':'true',
            'documents':'true',
            'images':'true',
            'video':'on',
        }
        
        query = urlencode(searchData, doseq=True)
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+response.content)
        
        files = jsonpickle.loads(response.content).get('results')
        self.failUnlessEqual(1, len(files), 
                             'Should be one file, but found: '+response.content)

        # Set the server offline!!
        logging.info('About to set server offline...')
        serverOnlineData = {'serverAddress': serverAddress, 'online': 'false'}
        response = client.post('/api/serverOnline', serverOnlineData)
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        onlineQuery = models.OnlineInstance.all()
        onlineQuery.filter('serverAddress = ', serverAddress)
        onlines = onlineQuery.fetch(10)
        self.failUnlessEqual(0, len(onlines), 'Unexpected number of online instances: '+str(len(onlines)))
        
        # Now should be gone *in search too* after setting the server offline.
        response = client.post('/api/search', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response: '+response.content)
        
        files = jsonpickle.loads(response.content).get('results')
        self.failUnlessEqual(0, len(files), 
                             'Should be no data, but found: '+response.content)
        
        
    
    # This test just makes sure putting a non-existent user offline works.
    def testInstanceOfflineBeforeExists(self):
        client = newClient()
        instanceId = 4743829742
        #postData = {'instanceId' : instanceId, 'online' : False}
        postData = {'instanceId' : instanceId, 'online' : 'false', 
                    'serverAddress':'37.12.43.1', 'baseUri' : 'sip://31312'}
        response = client.post('/api/instanceOnline', postData)
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        
    def testInstanceOnline(self):
        client = newClient()
        
        # Should fail with no online arg
        instanceId = 4743829742
        postData = {'instanceId' : instanceId}
        response = client.post('/api/instanceOnline', postData)
        self.failUnlessEqual(response.status_code, 400, response.content)
        
        # Should fail with no instance ID arg
        postData = {'online' : True}
        response = client.post('/api/instanceOnline', postData)
        self.failUnlessEqual(response.status_code, 400, response.content)
        
        # Should succeed
        postData = {'instanceId' : instanceId, 'online' : 'true', 
                    'serverAddress':'37.12.43.1', 'baseUri' : 'sip://31312'}
        response = client.post('/api/instanceOnline', postData)
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        instance = models.OnlineInstance.gql('where instanceId = :1', instanceId).get()
        self.failIf(instance is None, 'Should be an online instance')
        
        # Now set it to false and make sure it disappears
        logging.info('About to remove instance')
        postData = {'instanceId' : instanceId, 'online' : 'false', 
                    'serverAddress':'37.12.43.1', 'baseUri' : 'sip://31312'}
        response = client.post('/api/instanceOnline', postData)
        instance = models.OnlineInstance.gql('where instanceId = :1', instanceId).get()
        self.failUnless(instance is None, 'Should be NO online instance')
        response = putOffline(instanceId)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        # The key here is also to test MetaFile to make sure it has or does 
        # not have corresponding instance data!!
        putOnline(instanceId)
        uri = "urn:sha1:testestesteefajfkaj209teststest"
        title = 'testFiletetingiatirj32jr92u'
        response = publishFile(title, uri, instanceId)
        
        files = listFiles(instanceId)
        self.failUnlessEqual(1, len(files), 'Should be exactly one file, but was: '+str(len(files)))
        
        file = models.MetaFile.all().filter('sha1 =', uri).get()
        self.failUnlessEqual(title, file.title, 'Unexpected title: '+file.title)
        self.failUnlessEqual(1, file.numOnlineInstances, 'Unexpected num instance: '+str(file.numOnlineInstances))
        
        putOffline(instanceId)
        file = models.MetaFile.all().filter('sha1 =', uri).get()
        self.failUnlessEqual(0, file.numOnlineInstances, 'Unexpected num instance: '+str(file.numOnlineInstances))
        
    def testDuplicateInstances(self):
        client = newClient()
        
class PublishDotFilesTestCase(unittest.TestCase):
    
    def testPublishDotFiles(self):
        # Should not be able to publish dot files.
        response =  publishFile('.testDotFile', 'urn:sha1:qefq', 4729472)
        self.failUnlessEqual(response.status_code, 400, response.content)

class PublishTestCase(unittest.TestCase):

    def testDownloaded(self):
        client = newClient()
        instanceId = 729740014
        uri = 'urn:sha1:JDFJKDOURUEUUIEOHFUIHFJF10iur894u1'
        publishData = {
            'title' : 'test.txt',
            'size' : 42090,
            'uri' : uri,
            'instanceId' : instanceId,
            'tags': 'test, blah',
            'sha1': uri,
            'downloaded': 'false',
            }
        
        putOnline(instanceId)
        response = publishFileRaw(publishData)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        files = listFiles(instanceId)
        self.failUnlessEqual(1, len(files), 'Should be exactly one file, but was: '+str(len(files)))
        
        file = models.File.all().filter('sha1 =', uri).get()
        self.assertFalse(file.downloaded, 'Should not have been downloaded, but was '+str(file.downloaded))
        
        # Now just publish again to make sure the second publish doesn't have
        # any impact
        response = publishFileRaw(publishData)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        files = listFiles(instanceId)
        self.failUnlessEqual(1, len(files), 'Should be exactly one file, but was: '+str(len(files)))
        
        file = models.File.all().filter('sha1 =', uri).get()
        self.assertFalse(file.downloaded, 'Should not have been downloaded, but was '+str(file.downloaded))
        
        
    def testDuplicateFilePublishing(self):
        client = newClient()
        instanceId = 78335
        uri = 'urn:sha1:JDODNDuiqour18473105710iur894u1'
        publishData = {
            'title' : 'test.txt',
            'size' : 42090,
            'uri' : uri,
            'instanceId' : instanceId,
            'tags': 'test, blah',
            'sha1': uri,
            }
        
        #putOnline(instanceId)
        response = publishFileRaw(publishData)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        response = publishFileRaw(publishData)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        files = listFiles(instanceId)
        self.failUnlessEqual(1, len(files), 'Should be exactly one file, but was: '+str(len(files)))
        
        #response = putOffline(instanceId)
        #self.failUnlessEqual(response.status_code, 200, 
        #    'Unexpected response: '+str(response.status_code)+' '+response.content)
        
                    
    def testNumOnlineInstancesForFiles(self):
        client = newClient()
        instanceId1 = 4809347831
        instanceId2 = 81748099
        
        response = putOnline(instanceId1)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        response = putOnline(instanceId2)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        uri1 = 'urn:sha1:afjk208120dljo23'
        uri2 = 'urn:sha1:uriofu9u93qru8197915133'
        title = 'testering18430.txt'
        publishData = {
            'title' : title,
            'size' : 42090,
            'uri' : uri1,
            'instanceId' : instanceId1,
            'tags': 'test',
            'sha1': uri1,
            }
        
        #logging.info('\n\n\n\n\n\n\n11111')
        response = publishFileRaw(publishData)
        #logging.info('\n\n\n\n\n\n\n')
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        # Now give instance ID 2 both files.
        publishData.update({'instanceId' : instanceId2})
        #logging.info('\n\n\n\n\n\n\n22222')
        response = publishFileRaw(publishData)
        #logging.info('\n\n\n\n\n\n\n')
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        publishData.update({'instanceId' : instanceId2, 'uri' : uri2, 'sha1' : uri2})
        
        #logging.info('\n\n\n\n\n\n\n33333')
        response = publishFileRaw(publishData)
        #logging.info('\n\n\n\n\n\n\n')
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        
        metaFiles = models.MetaFile.gql('where numOnlineInstances > 0 and title = :1 order by numOnlineInstances', title).fetch(10)
        #metaFiles = models.MetaFile.gql('where numOnlineInstances > 0 order by numOnlineInstances').fetch(10)
        self.failUnlessEqual(2, len(metaFiles), 'Unexpected number of files: ' +str(len(metaFiles)))
        metaFile1 = metaFiles[0]
        metaFile2 = metaFiles[1]
        
        onlineInstances1 = metaFile1.numOnlineInstances
        onlineInstances2 = metaFile2.numOnlineInstances

        self.failUnlessEqual(1, onlineInstances1, '')
        self.failUnlessEqual(2, onlineInstances2, '')
        
        # Now take them offline and make sure we don't get results.
        response = putOffline(instanceId1)
        response = putOffline(instanceId2)
        
        #metaFiles = models.MetaFile.gql('where numOnlineInstances > 0 order by numOnlineInstances and title = :1', title).fetch(10)
        metaFiles = models.MetaFile.gql('where numOnlineInstances > 0 and title = :1 order by numOnlineInstances', title).fetch(10)
        self.failUnlessEqual(0, len(metaFiles), 'Unexpected number of files: ' +str(len(metaFiles)))
        
        
        response = putOffline(instanceId1)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
        response = putOffline(instanceId2)
        self.failUnlessEqual(response.status_code, 200, 
            'Unexpected response: '+str(response.status_code)+' '+response.content)
            
            
        
    def testPublishFileSignature(self):
        # No key ID
        client = newClient()
        sha1 = 'urn:sha1:djeiwoq8971EO37'
        postData = {'title': 'testFile.txt', 
                    'uri': 'http://www.test.com/testFile.txt1',
                    'size': 1313,
                    'instanceId': 434241,
                    'signature' : 'uoeuqiouroq',
                    'sha1': sha1,}
        response = client.post('/api/publishFile', postData)
        self.failUnlessEqual(response.status_code, 403, response.content)
        
        # No matching key ID for session
        postData.update({'keyId' : 27840987})
        query = urlencode(postData, doseq=True)
        #logging.info('Using query: %s'+ query)
        response = client.post('/api/publishFile', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 403, response.content)
        
        # Now add the key ID to the session, first fetching a key.
        keyId = 4298741
        postParams = {'keyId' : keyId}
        query = urlencode(postParams, doseq=True)
        logging.info('Using query: %s', query)
        response = client.post('/api/key/', **{'QUERY_STRING': query})
        
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response code: '+
                             str(response.status_code))
        self.failUnless(response.cookies.has_key('key'), 
            'no key in response!')
        self.failUnless(response.cookies.has_key('siteKey'), 
            'no site key in response!')
        clientKey = response.cookies.get('key').value
        client.session[keyId] = clientKey
        
        # Wrong sig
        postData.update({'keyId' : keyId,
            'signature' : 'totallyWrongSignature'})
        query = urlencode(postData, doseq=True)
        #logging.info('Using query: %s', query)
        #logging.info('Publishing file with key and signature!!')
        response = client.post('/api/publishFile', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 403, response.content)

        # Correct sig
        sha1 = 'urn:sha1:REOUOU7943E'
        size = 724892
        instanceId = 434241
        uri = 'http://www.test.com/testFile.txt1'
        tags = 'test, tag with spaces, moretags'
        preSigPostData = {'title': 'testFile.txt', 
            'uri': uri,
            'size': size,
            'instanceId': instanceId,
            'keyId' : keyId,
            'sha1' : sha1,
            'tags' : tags}
        preSigQuery = urlencode(preSigPostData, doseq=True)
        
        # We have to cheat here a little bit and manually add the testserver
        # path
        fullUrl = 'http://testserver/api/publishFile?' + preSigQuery
        
        logging.debug('Using key: %s', clientKey)
        logging.info('Computing signature on: %s', fullUrl)
        
        
        hm = hmac.new(clientKey, fullUrl, hashlib.sha1)
        sig = base64.b64encode(hm.digest())
        query = urlencode(preSigPostData, doseq=True) + '&' + urlencode({'signature' : sig})
        #logging.info('Using query: %s', query)
        #logging.info('Publishing file with key and signature!!')
        response = client.post('/api/publishFile', **{'QUERY_STRING': query})
        self.failUnlessEqual(response.status_code, 200, response.content)
        
        
        insertedMeta = models.MetaFile.gql('where sha1 = :1', sha1).get()
        insertedFile = models.File.gql('where sha1 = :1', sha1).get()

        self.failIf(insertedMeta is None, 'Should be a meta file')
        self.failIf(insertedFile is None, 'Should be a file')
        
        self.failUnlessEqual(724892, insertedMeta.size, 'Unexpected size')
        self.failUnlessEqual(size, insertedFile.size, 'Unexpected size')

        self.failUnlessEqual(uri, insertedMeta.uri, 'Unexpected uri')
        self.failUnlessEqual(uri, insertedFile.uri, 'Unexpected uri')
        
        self.failUnlessEqual(instanceId, insertedFile.instanceId, 'Unexpected id')
        
        self.failUnlessEqual('document', insertedMeta.mediaType, 'Unexpected mediaType')
        self.failUnlessEqual('document', insertedFile.mediaType, 'Unexpected mediaType')
        
        self.failUnlessEqual('text/plain', insertedMeta.mimeType, 'Unexpected mimeType')
        self.failUnlessEqual('text/plain', insertedFile.mimeType, 'Unexpected mimeType')
        
        files = listFiles(instanceId)
        fileTags = files[0].get('tags')
        self.failUnlessEqual(['test', 'tag with spaces', 'moretags'], fileTags, 'Unexpected tags: '+str(fileTags))
        
        
class PublishTestNoSigCase(unittest.TestCase):
    def testPublishFileNoSignature(self):
        logging.info("Running test!!")
        #client = newClient()
        client = Client(HTTP_USER_AGENT='Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6')
        postData = {'title': 'testFile.txt', 
                    'uri': 'http://www.test.com/testFile.txt',
                    'size': 1313,
                    'instanceId': 434241,
                    'sha1': 'urn:sha1:UEO433IEOREO'}
        response = client.post('/api/publishFile', postData)
        self.failUnlessEqual(response.status_code, 200, response.content)
        logging.info("RESPONSE: %s", response.content)
        
        # Now test to make sure it fails without a title
        postData = {'uri': 'http://www.test.com/testFile.txt1',
                    'size': 1313,
                    'instanceId': 434241}
        response = client.post('/api/publishFile', postData)
        self.failUnlessEqual(response.status_code, 400, response.content)
        
        # And uri.
        postData = {'title': 'testFile.txt', 
                    'size': 1313,
                    'instanceId': 434241}
        response = client.post('/api/publishFile', postData)
        self.failUnlessEqual(response.status_code, 400, response.content)

        # size
        postData = {'title': 'testFile.txt', 
                    'uri': 'http://www.test.com/testFile.txt2',
                    'instanceId': 434241}
        response = client.post('/api/publishFile', postData)
        self.failUnlessEqual(response.status_code, 400, response.content)
        
        # instance ID
        postData = {'title': 'testFile.txt', 
                    'uri': 'http://www.test.com/testFile.txt3',
                    'size': 1313}
        response = client.post('/api/publishFile', postData)
        self.failUnlessEqual(response.status_code, 400, response.content)
        
        # Make sure a GET request fails.
        postData = {'title': 'testFile.txt', 
                    'uri': 'http://www.test.com/testFile.txt4',
                    'size': 1313,
                    'instanceId': 434241}
        response = client.get('/api/publishFile', postData)
        self.failUnlessEqual(response.status_code, 403, 
                             str(response.status_code)+'\n'+response.content)
        
        
class KeyTestCase(unittest.TestCase):
    def setUp(self):
        # Every test needs a client.
        self.client = Client()

    def testKey(self):
        
        client = Client();
        logging.info('Sending test')
        postParams = {'keyId' : 248920}
        query = urlencode(postParams, doseq=True)
        logging.info('Using query: %s', query)
        response = client.post('/api/key/', **{'QUERY_STRING': query})
        
        self.failUnlessEqual(response.status_code, 200, 
                             'Unexpected response code: '+
                             str(response.status_code))
        self.failUnless(response.cookies.has_key('key'), 
            'no key in response!')
        self.failUnless(response.cookies.has_key('siteKey'), 
            'no site key in response!')
        #logging.info('response: %s', dir(response.cookies))
        
    def testKeyMissing(self):
        postParams = {'signature': 'ieqopurq'}
        
        # Note we just use POST body params here because the server can 
        # handle both in the body and in the request query.
        response = self.client.post('/api/key/', postParams)
        logging.info('response: %s', response._headers.keys())
        logging.info(response._headers.values())
        self.failUnlessEqual(response.status_code, 400, 
                             'Unexpected response code: '+str(response.status_code))
  
def listFiles(instanceId):
    client = Client()
    data = {
        'instanceId': instanceId,
        'pageIndex': 0,
        'resultsPerPage': 200
    }
    
    query = urlencode(data, doseq=True)
    response = client.post('/api/fileListing', **{'QUERY_STRING': query})
    
    pickled = jsonpickle.loads(response.content)
    #logging.info('Got pickled: %s', pickled) 
    return pickled.get('results')

def putOffline(instanceId):
    client = Client()
    postData = {'instanceId' : instanceId, 'online' : 'false', 
                'serverAddress': '43.32.56.76', 
                'baseUri': 'sip://'+str(instanceId)}
    
    response = client.post('/api/instanceOnline', postData)
    return response

def putOfflineWithStart(instanceId, startSha1):
    client = Client()
    postData = {'instanceId' : instanceId, 'online' : 'false', 
                'serverAddress': '43.32.56.76', 
                'baseUri': 'sip://'+str(instanceId),
                'startSha1' : startSha1}
    
    response = client.post('/api/instanceOnline', postData)
    return response

def putOnline(instanceId):
    client = Client()
    postData = {'instanceId' : instanceId, 'online' : 'true', 
                'serverAddress': '43.32.56.76', 
                'baseUri': 'sip://'+str(instanceId)}
    response = client.post('/api/instanceOnline', postData)
    logging.info('Got response to putOnline: %s', response.content)
    return response
        
def publishFile(fileName, uri, instanceId):
    data = {'title': fileName, 
        'uri': uri,
        'size': 1313,
        'instanceId': instanceId,
        'sha1': uri}
    return publishFileRaw(data)
    
  
def publishFileRaw(data):
    return sendSigned('/api/publishFile', data)
      
def sendSigned(path, nonSigned):
    
    client = Client()
    # Now add the key ID to the session, first fetching a key.
    keyId = 4298741
    postParams = {'keyId' : keyId}
    query = urlencode(postParams, doseq=True)
    #logging.info('Using query: %s', query)
    response = client.post('/api/key/', **{'QUERY_STRING': query})
    
    clientKey = response.cookies.get('key').value
    client.session[keyId] = clientKey
    
    sessionId = response.cookies.get('sessionid').value
    
    nonSigned.update({'keyId' : keyId})
    preSigQuery = urlencode(nonSigned, doseq=True)
    
    # We have to cheat here a little bit and manually add the testserver
    # path
    fullUrl = 'http://testserver'+path+'?' + preSigQuery
    
    #logging.debug('Using key: %s', clientKey)
    #logging.info('Computing signature: %s', fullUrl)
    
    hm = hmac.new(clientKey, fullUrl, hashlib.sha1)
    sig = base64.b64encode(hm.digest())
    
    #postData = {'signature' : sig}
    #postData.update(preSigPostData)
    
    query = preSigQuery + '&' + urlencode({'signature' : sig})
    #logging.info('Using query: %s', query)
    #logging.info('Publishing file with key and signature!!')
    
    # We need to set the session ID here!!!
    
    client.cookies['sessionid'] = sessionId
    
    #logging.info('client dir: %s', dir(client))
    response = client.post(path, **{'QUERY_STRING': query})
    return response

