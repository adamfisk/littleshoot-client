import base64
import hmac
import sha
#import urllib, urllib2
import urllib
import logging
import time
import link
import simplejson
import mimetypes
import mediaTypeTranslator
from datetime import datetime, tzinfo

from google.appengine.api import urlfetch
from django.conf import settings

import xml.etree.ElementTree as ET

typeTranslator = mediaTypeTranslator.MediaTypeTranslator()

def upcase_compare(left, right):
    left = left.upper()
    right = right.upper()
    if(left < right):
        return -1
    elif(left > right):
        return 1
    return 0

def attr_name_from_tag(tag_name):
    # some tag names have an XML namespace that we
    # aren't really concerned with.  This strips them:
    tag_name = tag_name[tag_name.find("}")+1:]
    # Then we lowercase the first letter
    return tag_name[0].lower() + tag_name[1:]

class FPSResponse(object):
    def __init__(self, element=None):
        if element is not None:
            if isinstance(element, str):
                element = ET.fromstring(element)
        self.element = element

        for child in element.getchildren():
            if len(child.getchildren()) ==0:
                value = child.text
                if child.tag.find("Date") >= 0:
                    # TODO this is a little less than ideal
                    # we truncate the milliseconds and time zone info
                    value = value[0:value.find(".")]
                    value = datetime.strptime(value,
                                             "%Y-%m-%dT%H:%M:%S")
                if child.tag == "Amount":
                    value = float(child.text)
                if child.tag.find("Size") >= 0:
                    value = int(child.text)
                setattr(self, attr_name_from_tag(child.tag), value)
            else:
                if child.tag == "Errors" and child.getchildren()[0].tag == "Errors":
                    self.errors = []
                    for e in child.getchildren():
                        self.errors.append(FPSResponse(e))
                elif child.tag =="Transactions":
                    if not hasattr(self, "transactions"):
                        self.transactions = []
                    self.transactions.append(FPSResponse(child))
                else:
                    setattr(self, attr_name_from_tag(child.tag), FPSResponse(child))

        if hasattr(self, "status"):
            self.success = (self.status == "Success")
        if hasattr(self, "transactionResponse"):
            setattr(self, "transaction", self.transactionResponse)
            delattr(self, "transactionResponse")
    
class AmazonDevPayClient(object):
    def __init__(self, aws_access_key_id, aws_secret_access_key, product_token,
                 devpay_url="https://ls.amazonaws.com"):
        self.access_key_id = aws_access_key_id
        self.aws_secret_access_key = aws_secret_access_key
        self.product_token = product_token
        self.devpay_url = devpay_url

    def sign_string(self, string):
        """
        Strings going to and from the Amazon FPS service must be cryptographically
        signed to validate the identity of the caller.

        Sign the given string with the aws_secret_access_key using the SHA1 algorithm,
        Base64 encode the result and strip whitespace.
        """
        #logging.debug("to sign: %s" % string)
        sig = base64.encodestring(hmac.new(self.aws_secret_access_key, 
                                           string, 
                                           sha).digest()).strip()
        #logging.debug(sig)
        return(sig)

    def get_signed_query(self, parameters, signature_name='Signature'):
        """
        Returns a signed query string ready for use against the FPS REST
        interface.  Encodes the given parameters and adds a signature 
        parameter.
        """
        keys = parameters.keys()
        keys.sort(upcase_compare)
        message = ''
        for k in keys:
            message += "%s%s" % (k, parameters[k])
        sig = self.sign_string(message)
        #logging.debug("signature = %s" % sig)
        
        parameters[signature_name]  = sig
        return urllib.urlencode(parameters)
    

    def execute(self, parameters):
        """
        A generic call to the FPS service.  The parameters dictionary
        is sorted, signed, and turned into a valid FPS REST call.  
        The response is read via urllib2 and parsed into an FPSResponse object
        """
        #print "executing"
        parameters['AWSAccessKeyId'] = self.access_key_id
        parameters['ProductToken'] = self.product_token
        parameters['SignatureVersion'] = 1
        parameters['Timestamp'] = time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())
        parameters['Version'] = '2008-04-28'
        

        query_str = self.get_signed_query(parameters)
        logging.debug("request_url == %s/?%s" % (self.devpay_url, query_str))

        result = urlfetch.fetch("%s/?%s" % (self.devpay_url, query_str))
        if result.status_code == 200:
            #print result.content
            logging.info('Response: %s', result.content)
            return FPSResponse(ET.fromstring(result.content))
            
        else:
            #print result.content
            # Raise an exception here?
            logging.info('Error Response: %s', result.content)
            return None
        '''
        try:
            #response = urllib2.urlopen("%s/?%s" % (self.devpay_url, query_str))
            response = urlfetch.fetch("%s/?%s" % (self.devpay_url, query_str))

            print response
            data = response.read()
            print data
            response.close()
        except urllib2.HTTPError, httperror:
            print httperror
            data = httperror.read()
            print data
            httperror.close()
        '''

    def activateHostedProduct(self, activationKey):
        params = {'Action': 'ActivateHostedProduct',
                  'ActivationKey': activationKey,
                  }
        return self.execute(params)
    
    def devPayPolicy(self, userToken):
        policy_document = '{"expiration": "2012-01-01T00:00:00Z","conditions": [{"bucket": "files.littleshoot.org"},{"x-amz-security-token": "'+userToken+'"},["starts-with", "$key", "user"],{"acl": "public-read"}]}'
        logging.info("Policy document is: %s", policy_document)
        policy = base64.b64encode(policy_document)
        return policy

    def policy(self, baseUri):
        policy_document = '{"expiration": "2012-01-01T00:00:00Z","conditions": [{"bucket": "files.littleshoot.org"},["starts-with", "$success_action_redirect", "'+baseUri+'/uploadSuccess"],["starts-with", "$key", "user"],{"acl": "public-read"}]}'
        logging.info("Policy document is: %s", policy_document)
        policy = base64.b64encode(policy_document)
        return policy
    
    def signedPolicy(self, policy):
        sig = base64.encodestring(hmac.new(self.aws_secret_access_key, 
                                  policy, 
                                  sha).digest()).strip()
                                  
        logging.debug(sig)
        return(sig)
    
    def listS3FilesForId(self, id):
        from boto.s3.connection import S3Connection
        from boto.s3.bucket import Bucket
        
        logging.info("AWS keys: %s %s: ", self.access_key_id, self.aws_secret_access_key)
        conn = S3Connection(self.access_key_id, self.aws_secret_access_key)
        bucketName = "files.littleshoot.org"
        bucket = Bucket(conn, bucketName)
        
        prefix = 'user/'+id+'/files/'
        response = bucket.list(prefix=prefix, delimiter='/')
        baseUrl = "http://" + bucketName + "/"
        
        files = []
        # The results are all Keys.  See:
        # http://boto.s3.amazonaws.com/index.html
        for key in response:
            logging.info("Got full key: %s", key)
            #fullPath = "http://" + bucketName + "/" + key.name
            rawNameArray = key.name.split('/')
            index = len(rawNameArray) - 1
            title = rawNameArray[index]
            #digest = hmac.new(self.aws_secret_access_key, key.name, sha).digest()
            #path = base64.encodestring(digest).strip()
            
            ##logging.info("Path is: %s", path)
            
            #url = baseUrl + key.name
            url = baseUrl + key.name + "?torrent"
            short = link.shorten(url, title, key.size)
            
            mimeType, encoding = mimetypes.guess_type(title)
            if mimeType is None:
                mimeType = 'application/octet-stream'
            mediaType = typeTranslator.getType(title);
            
            file = {
                    'title' : title,
                    'size' : key.size,
                    'lastModified' : key.last_modified,
                    'uri' : short,
                    'mimeType' : mimeType,
                    'mediaType' : mediaType
                    }
            files.append(file)
        
        data = {'files' : files}
        json = simplejson.dumps(data) 
        logging.info('JSON output: \n %s', json)
        return json
        
