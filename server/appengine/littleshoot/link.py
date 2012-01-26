
from django.shortcuts import render_to_response
from django.http import HttpResponse, HttpResponseForbidden
from django.http import HttpResponseRedirect
from google.appengine.ext import db

from models import Link

import appChecker
import logging
import util
import decorators
from google.appengine.api import oauth
from shortener.baseconv import base62

def oauthTest(request):
    try:
        # Get the db.User that represents the user on whose behalf the
        # consumer is making this request.
        user = oauth.get_current_user()
        return HttpResponse('Got OAuth for: '+ str(user))

    except oauth.OAuthRequestError, e:
        # The request was not a valid OAuth request.
        # ...
        return HttpResponseForbidden('OAuth failed')
    
def shorten(url, title, size):
    q = Link.all()
    q.filter("url =", url)
    urls = q.fetch(1)
    if (len(urls) > 0):
        return urls[0].short_url()
    newLink = Link(url = url, title = title, size = size)
    newLink.save()
    return newLink.short_url()
    
def getFile(request, fileId):
    """
    Called when we get a request for a LittleShoot-shortened link.  This
    resolves to the "real" url and checks if the caller has LittleShoot.  If
    they do, this just redirects the caller to the file.  Otherwise, it
    redirects them to a page prompting them to install LittleShoot.
    """
    logging.info('Handling request to lookup a file: %s', request.REQUEST.items())
    logging.info("File ID: %s", fileId)
    
    keyId = base62.to_decimal(fileId)
    key = db.Key.from_path("Link", keyId)
    
    keyQuery = Link.gql('WHERE __key__ = :1', key)
    links = keyQuery.fetch(1)
    if (len(links) == 0):
        logging.warn("No match for file ID: %s", fileId)
        return HttpResponseNotFound("Could not find a matching file")

    link = links[0]
    link.usageCount += 1
    link.save()
    url = link.url
    logging.info("Link is: %s", url)
        
    littleShootPresent = appChecker.supportsLinks(request)
    if littleShootPresent:
        logging.info('Found LittleShoot')
        return HttpResponseRedirect(url)
    
    else:
        logging.info('Sending to link not installed page...')
        uri = link.url
        title = link.title
        
        return render_to_response('aboutTab.html', 
                                  {'link' : uri, 
                                   'title' : title,
                                   'tabId' : 'forthTab', 'tabJavaScriptClass' : 'AboutTab', 'homeSelected' : True, 'showLinkNotInstalled': True})

@decorators.uriRequired
@decorators.titleRequired
def link(request):
    logging.info('Link handling request: %s', request.REQUEST.items())
    
    uri = request.REQUEST.get('uri')
    #sender = request.REQUEST.get('sender')
    title = request.REQUEST.get('title')

    #link = util.newUrl(uri, title)
    #if link is None:
    #    logging.error('Could not generate link from request: %s', request.REQUEST.items())
    #    return render_to_response('linkNotFound.html', 
    #                              {'sender' : sender, 
    #                               'title' : title})
    
    littleShootPresent = appChecker.supportsLinks(request)
    if littleShootPresent:
        logging.info('Found LittleShoot')
        
        # We just always redirect here because we should only be getting these
        # requests over HTTP, and we need to serve the page over HTTPS to 
        # avoid the referrer header. We need HTTP because only HTTP requests
        # have all the cookies required for LittleShoot detection. Then, though,
        # we need to serve the meta refresh page via HTTPS because that will
        # redirect without sending the referrer header that sites might 
        # otherwise use to kick us out to some external page.
        query_string = request.META['QUERY_STRING']
        fullUrl = "https://littleshootapi.appspot.com/metaRefresh?"+query_string
        #fullUrl = "https://36.latest.littleshootapi.appspot.com/metaRefresh?"+query_string
        logging.info("Redirecting to: %s", fullUrl)
        return HttpResponseRedirect(fullUrl)
        #if request.is_secure():
        #    logging.info('Secure connection, redirecting to: %s', uri)
        #    return HttpResponseRedirect(uri)
        #else:
        #    logging.info('Connection not secure, redirecting to https')
        #    return HttpResponseRedirect("https://littleshootapi.appspot.com"+request.get_full_path())
        # We use meta-refresh here to get around the Referer header that some
        # sites use to avoid linking directly to content.
        #return render_to_response("metaRefresh.html", {'link' : uri })
    else:
        logging.info('Sending to link not installed page...')
        return render_to_response('aboutTab.html', 
                                  {'link' : uri, 
                                   'title' : title,
                                   'tabId' : 'forthTab', 'tabJavaScriptClass' : 'AboutTab', 'homeSelected' : True, 'showLinkNotInstalled': True})
    
