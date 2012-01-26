
import urllib
import urllib2
import logging
import random
import sha
import time
import util
import awsUtils

from django.http import HttpResponse, HttpResponseServerError
from django.http import HttpResponseBadRequest
from django.http import HttpResponseRedirect

import models

from google.appengine.ext.db import djangoforms
from google.appengine.ext import db
from django.core.mail import send_mail
from django.template.loader import render_to_string


#PP_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr"
PP_URL = "https://www.paypal.com/cgi-bin/webscr"

class PayPalIpnForm(djangoforms.ModelForm):
    class Meta:
        model = models.PayPalIpn
        exclude = ['pro_key', 'raw_time']
        
def proDownload(request):
    logging.info("Handling pro download request: %s", request.REQUEST.items())
    pro_key = request.REQUEST.get('key')
    query = models.PayPalIpn.all();
    query.filter("pro_key =", pro_key)
    result = query.get()
    if result is not None:
        secsInYear = 60 * 60 * 24 * 365;
        if (util.expired(result.raw_time, secsInYear)):
            return HttpResponse("We're sorry, but your pro version has expired.  If you think this message is in error, please contact us at pro@littleshoot.org.")
        else:
            extension = util.getExtension(request)
            return HttpResponseRedirect(awsUtils.presigned("LittleShootPro."+extension))
    else:
        return HttpResponse("We're sorry, but we could not find a matching pro version in our database.  If you think this message is in error, please contact us at pro@littleshoot.org.")

def ipn(request):
    #logging.info("Handling PayPal IPN request: %s", request.REQUEST.items())
    logging.info("Handling PayPal IPN request...")
    parameters = None

    try:
        logging.info("Getting payment status...")
        payment_status = request.REQUEST.get('payment_status')
        logging.info("Got status...")
        if payment_status is None:
            logging.warn("Payment status is None...")
        else:
            #logging.warn("Status is not none...")
            logging.info("Payment status is: %s", payment_status)
        #if payment_status == 'Completed':
        if request.POST:
            logging.info("Copying post...")
            parameters = request.POST.copy()
            logging.info("Done copying post...")
        else:
            logging.info("Copying get...")
            parameters = request.GET.copy()
            logging.info("Done copying get...")
        #else:
        #    logging.error("The parameter payment_status was not valid")

        if parameters is None:
            logging.info("Parameters is None...")
            logging.info("Payment status is: %s", payment_status)
            
        logging.info("Parsing paramaters...")
        if parameters:
            parameters['cmd']='_notify-validate'

            params = urllib.urlencode(parameters)
            req = urllib2.Request(PP_URL, params)
            req.add_header("Content-type", "application/x-www-form-urlencoded")
            logging.info("Opening URL...")
            response = urllib2.urlopen(req)
            status = response.read()
            if not status == "VERIFIED":
                logging.info("Not verified...")
                logging.warn ("The request could not be verified, check for fraud: " + str(status))
                parameters = None

        if parameters:
            logging.info("Creating form...")
            form = PayPalIpnForm(data=request.REQUEST)
            if not form.is_valid():
                logging.error('Paypal form not valid..')
                return HttpResponse('ERROR: Publish file errors:\n%s' % repr(form.errors),
                                content_type='text/plain', status=400)
            
            logging.info("About to save form")
            payPalIpn = form.save(commit=False)
            
            email = parameters['payer_email']  
            salt = sha.new(str(random.random())).hexdigest()[:5]
            pro_key = sha.new(salt+email).hexdigest()
            
            payPalIpn.pro_key = pro_key
            payPalIpn.raw_time = time.time()
            logging.info("About to put PayPal IPN")
            payPalIpn.put();
            
            logging.info("Saved PayPal data");
            # Now we need to send the user an e-mail with the link to 
            # LittleShoot pro.
                        
            first_name = parameters['first_name'] 
            subject = render_to_string('littleshoot/donate_email_subject.txt',
                                       { 'first_name': first_name })
            # Email subject *must not* contain newlines
            subject = ''.join(subject.splitlines())
            
           
            
            message = render_to_string('littleshoot/donate_email.txt',
                                       { 
                                        'pro_key': pro_key,
                                        'first_name': first_name
                                        })
            
            logging.info("Sending mail...")
            send_mail(subject, message, 'afisk@littleshoot.org', [email], fail_silently=False)

            return HttpResponse("Ok")

    except Exception, e:
        logging.error("An exception was caught: " + str(e))

    logging.error("Returning error!!")
    return HttpResponseServerError("Error")


