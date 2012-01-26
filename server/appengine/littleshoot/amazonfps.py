
from django.shortcuts import render_to_response

from django.http import HttpResponse
from django.http import HttpResponseBadRequest
from django.http import HttpResponseRedirect

import decorators
import logging
import os
import util
import models
import uuid
import fpsClient

from google.appengine.ext.db import djangoforms
from google.appengine.ext import db

aws_access_key_id = 'awsAccessKeyIdToken'
aws_secret_access_key = 'awsSecretAccessKeyToken'

class AmazonFpsForm(djangoforms.ModelForm):
    class Meta:
        model = models.AmazonFps

fps_client = fpsClient.FlexiblePaymentClient(aws_access_key_id,
                                             aws_secret_access_key)

price='4.95'
def buy(request):
    logging.info("Handling FPS buy request: %s", request.REQUEST.items())
    logging.info("Host is: %s", request.get_host());
    
    returnUrl = 'http://' + request.get_host() + '/amazonFpsProcessPayment'
    callerReference = 'LittleShootFpsRef-'+uuid.uuid1().hex
    
    fpsData = {'callerReference' : callerReference}
    form = AmazonFpsForm(data=fpsData)
    if form.is_valid():
        fpsInstance = form.save(commit=False)
        fpsInstance.put()
    else:
        logging.error('ERROR: Form invalid for online instances:\n%s' 
                     % repr(form.errors))
    
    """
    url = fps_client.getMultiUsePipelineUrl(caller_reference=callerReference,
                                            payment_reason='LittleShoot Pro Purchase',
                                            transaction_amount=price,
                                            return_url=returnUrl,
                                            )
    """
    
    url = fps_client.getPipelineUrl(caller_reference=callerReference,
                                    payment_reason='LittleShoot Pro Purchase',
                                    transaction_amount=price,
                                    return_url=returnUrl
                                    )
    
    logging.info("Redirecting to: %s", url);
    return HttpResponseRedirect(url)


def processCoBrandedServiceApiCallback(request):
    """
    Processes a payment response from Amazon FPS.
    """
    logging.info("Handling FPS Co-Branded Callback request: %s", request.REQUEST.items())
    
    tokenId = request.REQUEST.get('tokenID')
    expiry = request.REQUEST.get('expiry')
    callerReference = request.REQUEST.get('callerReference')
    status = request.REQUEST.get('status')
    signature = request.REQUEST.get('signature')
    
    #if not fps_client.validate_pipeline_signature(signature, request.path, request.REQUEST):
    #    return HttpResponse('Invalid signature??')
    
    if status == 'SA':
        logging.info('Got SA status response from co-branded callback')
        return pay (tokenId, callerReference)
    elif status == 'SB':
        logging.info('Got SB status response from co-branded callback')
        return pay (tokenId, callerReference)   
    elif status == 'SC':
        logging.info('Got SC status response from co-branded callback')
        return pay (tokenId, callerReference)
    elif status == 'SE':
        # FPS system error
        logging.warn('System error.')
        return HttpResponseRedirect('home')
    elif status == 'A':
        # Abandoned
        logging.warn('Buyer abandoned the pipeline.')
        return HttpResponseRedirect('home')
    elif status == 'CE':
        # Caller exception
        logging.warn('Specifies a caller exception.')
        return HttpResponseRedirect('home')
    elif status == 'PE':
        # Payment method mismatch
        logging.warn('Payment Method Mismatch Error: Specifies that the buyer does not have the payment method you requested.')
        return HttpResponseRedirect('home')
    elif status == 'NP':
        # This account type does not support the specified payment method.
        logging.warn('This account type does not support the specified payment method.')
        return HttpResponseRedirect('home')
    elif status == 'NM':
        # Third party not registered
        logging.warn('Third party not registered')
        return HttpResponseRedirect('home')
    else:
        logging.error('Unrecognized Amazon FPS response status: %s', status)

    return HttpResponseRedirect('home')

def pay(tokenId, callerReference):
    logging.info('Sending PAY request...')
    response = fps_client.pay(sender_token=tokenId,
                              amount=price,
                              caller_reference=callerReference)
    
    
    logging.info('response: %s', dir(response))
    
    transactionId = response.payResult.transactionId
    transactionStatus = response.payResult.transactionStatus
    requestId = response.responseMetadata.requestId
    logging.info('Got transaction ID: %s', transactionId)
    logging.info('Got transaction status: %s', transactionStatus)
    logging.info('Got request ID: %s', requestId)
    
    query = models.AmazonFps.all()
    query.filter('callerReference =', callerReference)
    entry = query.get()
    if entry is not None:
        entry.transactionId = transactionId
        entry.requestId = requestId
        entry.put()
        logging.info('Updated Amazon FPS payment data')
    else:
        logging.error('No matching caller reference in the database for: %s', callerReference)
    
    context = {'tabId' : 'thirdTab', 'tabJavaScriptClass' : 'AboutTab'}
    
    if transactionStatus == 'Success':
        context.update({'amazonFpsSuccessSelected': True})
        return render_to_response('aboutTab.html', context)
    elif transactionStatus == 'Pending':
        context.update({'amazonFpsPendingSelected': True})
        return render_to_response('aboutTab.html', context)
    else:
        context.update({'amazonFpsFailedSelected': True})
        return render_to_response('aboutTab.html', context)

def ipn(request):
    """
    Processes an Instant Payment Notification from Amazon FPS.
    """
    logging.info("Handling FPS IPN request: %s", request.REQUEST.items())

    return HttpResponseRedirect('home')