from django.contrib.auth.decorators import login_required
from django.shortcuts import render_to_response
from django.utils.decorators import decorator_from_middleware
from facebook.djangofb import FacebookMiddleware
import facebook.djangofb as facebook

from django.http import HttpResponse
from django.http import HttpResponseBadRequest
from django.http import HttpResponseRedirect
from django.template import RequestContext

import decorators
import logging
import os
import util
import models
import files
import uuid
import amazonDevPayClient

from google.appengine.ext.db import djangoforms
from google.appengine.ext import db
from registration.forms import RegistrationForm
from django.contrib.auth.forms import AuthenticationForm
from django.contrib.auth.forms import PasswordResetForm, SetPasswordForm, PasswordChangeForm
from django.conf import settings
from django.http import HttpResponseRedirect

import httplib

devpay_client = amazonDevPayClient.AmazonDevPayClient(settings.AWS_ACCESS_KEY_ID,
                                                      settings.AWS_SECRET_ACCESS_KEY,
                                                      settings.AWS_PRODUCT_TOKEN)

@decorator_from_middleware(FacebookMiddleware)
#@facebook.require_login()
@facebook.require_login(next="http://www.littleshoot.org/publish")
def freeForm(request):
    fbId = str(request.facebook.uid)
    logging.info("Facebook ID: %s", fbId)
        
    policyFile = devpay_client.policy(settings.BASE_URI)
    policyFileSignature = devpay_client.signedPolicy(policyFile)
    #if userToken is None:
    return render_to_response('freeUploadForm.html',
                              {'base64_policy_file' : policyFile, 
                               'policy_file_signature' : policyFileSignature,
                               'aws_access_key_id' : settings.AWS_ACCESS_KEY_ID,
                               'fbId' : fbId, 
                               'baseUrl' : settings.BASE_URI},
                              context_instance=RequestContext(request))
    
#@decorator_from_middleware(FacebookMiddleware)
#@facebook.require_login()
def uploadSuccess(request):
    logging.info('Handling upload success: %s', request.REQUEST.items())

    bucket = request.REQUEST.get('bucket')
    key = request.REQUEST.get('key')
    etag = request.REQUEST.get('etag')
    
    baseUri = bucket + '.s3.amazonaws.com'
    conn = httplib.HTTPConnection(baseUri)
    conn.request("HEAD", "/" + key)
    res = conn.getresponse()
    
    if res.status != 200:
        # We're responding to a callback from Amazon here, so we don't need 
        # to write anything intelligible.
        logging.info("Unexpected response from S3: %s", res.status)
        return HttpResponse()
    
    logging.info("Status, Reason: %s, %s", res.status, res.reason)
    logging.info("Headers: %s", res.getheaders())
    
    size = res.getheader("Content-Length")
    logging.info("Content-Length: %s", size)
    
    requestCopy = request.GET.copy()
    requestCopy.update({'size': size})
    
    # See: http://developer.amazonwebservices.com/connect/entry.jspa?externalID=1963&categoryID=117
    uri = "urn:etag:" + etag
    requestCopy.update({'uri': uri})
    requestCopy.update({'etag': etag})
    
    # Here's the response:
    # Got redirect from Amazon with [(u'etag', u'"2b63da5eb9f0e5d5a76ce4c34315843d"'), (u'fbId', u'1014879'), (u'bucket', u'littleshoot_test'), (u'key', u'user/1014879/files/build.bash'), (u'title', u'build.bash')]
    return files.publishFileBase(requestCopy, False)
    #return HttpResponse('Got redirect from Amazon with %s' % (request.REQUEST.items()))
    
    #return HttpResponseRedirect('uploadMapped.html')
    
    
@decorator_from_middleware(FacebookMiddleware)
@facebook.require_login()
def uploadForm(request):
    #if request.user.is_authenticated():
    #logging.info('User is authenticated!')
    #logging.info('User is %s', request.user.username)
    fbId = str(request.facebook.uid)
    logging.info("Facebook ID: ", fbId)
    try:
        userToken = request.user.amazonDevPayUserToken
    except AttributeError, e:
        logging.error("An exception was caught: " + str(e))
        
    policyFile = devpay_client.policy()
    policyFileSignature = devpay_client.signedPolicy(policyFile)
    #if userToken is None:
    return render_to_response('purchaseDevPay.html',
                              {'base64_policy_file' : policyFile, 
                               'policy_file_signature' : policyFileSignature,
                               'aws_access_key_id' : settings.AWS_ACCESS_KEY_ID,
                               'fbId' : fbId,
                               'baseUri' : settings.BASE_URI},
                              context_instance=RequestContext(request))
    """
        else:
            devPayPolicyFile = devpay_client.devPayPolicy(userToken)
            devPayPolicyFileSignature = devpay_client.signedPolicy(devPayPolicyFile)
            logging.info('Straight sig: %s', policyFileSignature)
            logging.info('DevPay sig: %s', devPayPolicyFileSignature)
            return render_to_response('publisherUploadForm.html', 
                                      {'base64_policy_file' : policyFile, 
                                       'policy_file_signature' : policyFileSignature, 
                                       'devpay_base64_policy_file' : devPayPolicyFile, 
                                       'devpay_policy_file_signature' : devPayPolicyFileSignature,
                                       'aws_access_key_id' : settings.AWS_ACCESS_KEY_ID},
                                       context_instance=RequestContext(request))
        
    else:
        logging.info("User is not authenticated!!")
        loginForm = AuthenticationForm(request)
        registrationForm = RegistrationForm()
        logging.info("Rendering loginOrRegister")
        return render_to_response('customRegistration/loginOrRegister.html', 
                                {'loginForm' : loginForm, 
                                 'registrationForm': registrationForm },
                                context_instance=RequestContext(request))
    """

"""
    Documentation from Amazon: 
    
    Once the application has the activation key and product code, it looks 
    up the product token associated with the product code. The application 
    then makes a signed request to the License Service action 
    ActivateHostedProduct. The request must include the product token for 
    the customer and the customer's activation key. The response includes 
    the user token for the customer.
"""
@decorators.activationKeyRequired
@decorators.productCodeRequired
def activate(request):    
    logging.info("Handling DevPay activate request: %s", request.REQUEST.items())
    logging.info("Host is: %s", request.get_host())
    logging.info('Cookies on DevPay callback: %s', request.COOKIES)
    facebookId = request.COOKIES.get('facebookId')
    logging.info('Facebook ID: %s', facebookId)
    
    #logging.info(request.META['SERVER_NAME'])
    
    activationKey = request.REQUEST.get('ActivationKey')
    
    # We only have a single product for now, so we don't need to look anything
    # up based on the product code.
    
    #productCode = request.REQUEST.get('ProductCode')
    
    response = devpay_client.activateHostedProduct(activationKey)
    
    #logging.info("Activated hosted product response: %s", dir(response))
    
    result = response.activateHostedProductResult
    
    #logging.info("Activated hosted product result: %s", dir(result))
    userToken = result.userToken
    persistentIdentifier = result.persistentIdentifier
    
    logging.info('User token: %s', userToken)
    logging.info('Persistent Identifier: %s', persistentIdentifier)
    
    
    urlBase = "http://" + request.get_host() + "/amazonDevPay"
    if request.user.is_authenticated():
        logging.info('User is authenticated!')
        logging.info('User is %s', request.user.username)
        request.user.amazonDevPayUserToken = userToken
        request.user.amazonDevPayPersistentIdentifier = persistentIdentifier
        
        # We also need to create a bucket for the user.
        request.user.put()
        # We redirect to a page that will get rid of the separate Amazon frame.
        #return HttpResponse('Activation Successful!')
        
        # We just use the server name in case we're running from a staging 
        # server, for example.
        finalUrl = urlBase + "Purchase";
        return render_to_response('frameBuster.html', 
                                  {'frameUrl' : finalUrl})
    else:
        finalUrl = urlBase + "Error";
        return render_to_response('frameBuster.html', 
                                  {'frameUrl' : finalUrl})


@decorator_from_middleware(FacebookMiddleware)
@facebook.require_login()
#@facebook.require_login(next="http://www.littleshoot.org/publish")
def listS3Files(request):
    logging.info("Handling listS3Files request: %s", request.REQUEST.items())
    logging.info('Cookies on list files: %s', request.COOKIES)
    fbId = str(request.facebook.uid)
    
    json = devpay_client.listS3FilesForId(fbId)
    return HttpResponse(json, mimetype='application/json; charset=utf-8')

@decorator_from_middleware(FacebookMiddleware)
@facebook.require_login()
#@facebook.require_login(next="http://www.littleshoot.org/publish")
def listS3FilesForId(request):
    userId = request.REQUEST.get('userId')
    json = devpay_client.listS3FilesForId(userId)
    return HttpResponse(json, mimetype='application/json; charset=utf-8')