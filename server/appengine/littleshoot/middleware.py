from django.utils.http import http_date
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

import time
import logging

#EXPIRES_PREFIXES = ('/timestamped', '/dojo12', '/dijit12', '/dojox12')

# The dojo classes are served via zipserve, not Django, so we don't get them
# here.
EXPIRES_PREFIXES = ('/timestamped')

class FarFutureExpires(object):
    
    def process_request(self, request):
        return None;
    
    def process_response(self, request, response):
        for prefix in EXPIRES_PREFIXES:
            if request.path.startswith(prefix):
                response['Expires'] = http_date(time.time() + 315360000)
                return response
        return response
    
    
class SubdomainMiddleware(object):
    def process_request(self, request):
        # logging.info('Handling request in middleware: %s', request.REQUEST.items())
        domain_parts = request.get_host().split('.')

        if (len(domain_parts) > 2):
            subdomain = domain_parts[0]

            if (subdomain.lower() == 'www'):
                subdomain = ''
        else:
            subdomain = ''

        #logging.info("Got subdomain: %s", subdomain)
        if (subdomain == 'f'):
            logging.info("Modifying request path...")
            #reversed = reverse('file_lookup')
            #logging.info("Got reversed %s", reversed)
            #hostBase = request.get_host()
            #host = hostBase[2: len(hostBase)]
            #logging.info("host: %s", host)
            #return HttpResponseRedirect("http://www."+host+"/file")
            request.path = "/file"+request.path
            return None
        else:
            return None
        """
        if subdomain != '':
            try:
                request.company = Company.objects.get(subdomain=subdomain)
            except Company.DoesNotExist:
                return HttpResponseRedirect(''.join(['http://test.com', reverse('findcompany')]))               
        """ 
