# -*- coding: utf-8 -*-
# NOTE: Must import *, since Django looks for things here, e.g. handler500.
from django.conf.urls.defaults import *
from ragendja.urlsauto import urlpatterns
from ragendja.auth.urls import urlpatterns as auth_patterns
#from myapp.forms import UserRegistrationForm
from customRegistration.forms import UserRegistrationForm
from django.contrib import admin
from django.views.generic.simple import direct_to_template
from django.views.generic.simple import redirect_to
from django.http import HttpResponseRedirect

import link
import logging

admin.autodiscover()

#handler500 = 'ragendja.views.server_error'

#urlpatterns = auth_patterns + patterns('',
    #('^admin/(.*)', admin.site.root),
    #(r'^/', direct_to_template,
    #(r'^$', direct_to_template,
    #    {'template': 'main.html'}),
    # Override the default registration form
    #url(r'^account/register/$', 'registration.views.register',
    #    kwargs={'form_class': UserRegistrationForm},
    #    name='registration_register'),
#) + urlpatterns

def redirect_get(request, url, **kwargs):
    logging.info("URL: %s", url)
    logging.info("Request path: %s", request.path);
    url += request.path
    if request.META['QUERY_STRING']:
        url += '?%s' % request.META['QUERY_STRING']
        
    logging.info("FINAL URL: %s", url)
    return HttpResponseRedirect(url)
    #return redirect_to(request, url, **kwargs)

ABOUT_TAB = 'forthTab'
EXE_NAME = 'LittleBeta096.exe'
urlpatterns = auth_patterns + patterns('',
    ('^admin/(.*)', admin.site.root),
    #url(r'^accounts/register/$', 'registration.views.register',
        #kwargs={'form_class': UserRegistrationForm, 'template_name':'customRegistration/registration_form.html'},
    #    kwargs={'template_name':'customRegistration/registration_form.html'},
    #    name='registration_register'),
        
    (r'^accounts/', include('customRegistration.urls')),

    #(r'^users/', include('users.urls')),
    #(r'^timestamped/base-combined.js$', direct_to_template, {'template': 'baseJavaScriptCombined.txt', 'mimetype' : 'text/javascript'}),
    #(r'^timestamped/littleshoot-combined.js$', direct_to_template, {'template': 'javaScriptCombined.txt', 'mimetype' : 'text/javascript'}),
    (r'^timestamped/extra-combined.js$', direct_to_template, {'template': 'javaScriptExtraCombined.txt', 'mimetype' : 'text/javascript'}),
    (r'^$', 'littleshoot.index.index'),

    # These are just to append the trailing slash
    (r'^littleproxy$', 'django.views.generic.simple.redirect_to', {'url': 'littleproxy/'}),
    (r'^maven-docs$', 'django.views.generic.simple.redirect_to', {'url': 'maven-docs/'}),
    (r'^embedded-demo$', 'django.views.generic.simple.redirect_to', {'url': 'embedded-demo/'}),
    
    
    #(r'^api/client/(?P<path>.*)$', 'django.views.generic.simple.redirect_to', {'url': 'http://client.littleshoot.org/%(path)s'}),
    url(r'^api/client/(?P<path>.*)$', redirect_get, {'url':'http://client.littleshoot.org:8107'}),    
    #url(r'^api/client/(?P<path>.*)$', redirect_get, {'url':'http://127.0.0.1:8107'}),
    
    url(r'^gnutellaDotNet$', redirect_get, {'url':'http://dl.frostwire.com/frostwire/gnutella.net'}),

    (r'^lastbamboo*$', 'django.views.generic.simple.redirect_to', {'url': None}),
    (r'^lastbamboo-common-bug-server/bugs$', 'django.views.generic.simple.redirect_to', {'url': None}),
    #(r'^index$', direct_to_template, {'template': 'index.html'}),
    
    (r'^index$', 'django.views.generic.simple.redirect_to', {'url': 'home'}),
    (r'^index.html*$', 'django.views.generic.simple.redirect_to', {'url': 'home'}),
    #(r'^index.html*$', direct_to_template, {'template': 'index.html'}),
    
    # This is called from the special installer for IE.  We can do something differently here if we like.
    # Search and IE installer should stay in sync for now.
    (r'^search$',      direct_to_template, {'template': 'searchTab.html', 'extra_context': {'tabId' : 'firstTab', 'tabJavaScriptClass' : 'Search'}}),
    (r'^shortcut$',    direct_to_template, {'template': 'searchTab.html', 'extra_context': {'tabId' : 'firstTab', 'tabJavaScriptClass' : 'Search'}}),
    (r'^welcome$',     direct_to_template, {'template': 'searchTab.html', 'extra_context': {'tabId' : 'firstTab', 'tabJavaScriptClass' : 'Search', 'showWelcome': True}}),
    (r'^ieInstaller$', direct_to_template, {'template': 'searchTab.html', 'extra_context': {'tabId' : 'firstTab', 'tabJavaScriptClass' : 'Search', 'showActiveX': True}}),
    #(r'^ieInstaller$', direct_to_template, {'template': 'searchTab.html', 'extra_context': {'tabId' : 'firstTab', 'tabJavaScriptClass' : 'Search', 'activexTest': 'filler'}}),
    (r'^downloads$', direct_to_template, {'template': 'downloadsTab.html', 'extra_context': {'tabId' : 'secondTab', 'tabJavaScriptClass' : 'DownloadsTab'}}),
    (r'^publish$', direct_to_template, {'template': 'publishTab.html', 'extra_context': {'tabId' : 'thirdTab', 'tabJavaScriptClass' : 'Publisher'}}),
    (r'^about$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'homeSelected' : True}}),
    
    # These are all the sub-navigation elements under the about tab
    (r'^home$',  direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'homeSelected' : True}}),
    (r'^whatIs$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'whatIsSelected' : True}}),
    (r'^whatis*$', 'django.views.generic.simple.redirect_to', {'url': 'whatIs'}),
    
    
    #(r'^devPayUploadForm$', 'littleshoot.amazonDevPay.uploadForm'), 
    (r'^freeUploadForm$', 'littleshoot.amazonDevPay.freeForm'), 
    #(r'^loginControl$', direct_to_template, {'template': 'loginControl.html' }),
    #(r'^uploadSuccess$', direct_to_template, {'template': 'uploadSuccess.html' }),
    (r'^uploadSuccess$', 'littleshoot.amazonDevPay.uploadSuccess'),
    
    # This is a redirect for the IE installer so we don't need to constantly update and re-sign the installer itself.
    (r'^nsisInetLoad*$', 'django.views.generic.simple.redirect_to', {'url': 'http://cloudfront.littleshoot.org/'+EXE_NAME}),
    
    
    (r'^help$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'helpSelected' : True}}),
    (r'^technology$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'technologySelected' : True}}),
    (r'^team$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'teamSelected' : True}}),
    (r'^contact$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'contactSelected' : True}}),
    (r'^developers$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'developersSelected' : True}}),
    (r'^code$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'codeSelected' : True}}),
    
    #(r'^beta$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'betaSelected' : True}}),
    (r'^beta*$', 'django.views.generic.simple.redirect_to', {'url': 'home'}),
    
    #(r'^download$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'downloadSelected' : True}}),
    
    (r'^emailThanks$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'emailThanksSelected' : True}}),
    (r'^buyPro$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'buyProSelected' : True}}),
    
    
    # Google somehow decided this was a base-level nav link.  We just go to the team page.
    (r'^aboutUs$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'teamSelected' : True}}),
    
    #(r'^amazonDevPayCallback$', direct_to_template, {'template': 'amazonDevPay.html'}),
    (r'^amazonDevPayCallback$', 'littleshoot.amazonDevPay.activate'),
    (r'^amazonDevPayPurchase$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'homeSelected' : True, 'showProSuccess': True}}),
    (r'^amazonDevPayError$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'homeSelected' : True, 'showProFailed': True}}),
    (r'^payPalIpn$', 'littleshoot.paypal.ipn'),
    #(r'^proCancelled$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'homeSelected' : True, 'showProCancelled': True}}),
    #(r'^proSuccess$', direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'homeSelected' : True, 'showProSuccess': True}}),

    (r'^donateSuccess$',  direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'homeSelected' : True, 'showDonateSuccess': True}}),
    (r'^donateCancelled$',  direct_to_template, {'template': 'aboutTab.html', 'extra_context': {'tabId' : ABOUT_TAB, 'tabJavaScriptClass' : 'AboutTab', 'homeSelected' : True}}),
    
    (r'^proDownload$', 'littleshoot.paypal.proDownload'),
    
    (r'^download$', 'littleshoot.views.download'), 
    (r'^site.html*$', 'littleshoot.views.download'), 
    
    (r'^homeContent$', direct_to_template, {'template': 'homeContentBody.html'}),
    (r'^whatIsContent$', direct_to_template, {'template': 'whatIsContentBodyWrapper.html'}),
    (r'^helpContent$', direct_to_template, {'template': 'helpContentBodyWrapper.html'}),
    (r'^technologyContent$', direct_to_template, {'template': 'technologyContentBodyWrapper.html'}),
    (r'^teamContent$', direct_to_template, {'template': 'teamContentBodyWrapper.html'}),
    (r'^contactContent$', direct_to_template, {'template': 'contactContentBodyWrapper.html'}),
    (r'^developersContent$', direct_to_template, {'template': 'developersContentBodyWrapper.html'}),
    
    (r'^downloadContent$', direct_to_template, {'template': 'downloadContentBodyWrapper.html'}),
    (r'^downloadWinContent$', direct_to_template, {'template': 'downloadWindowsContentBodyWrapper.html'}),
    (r'^downloadMacContent$', direct_to_template, {'template': 'downloadMacContentBodyWrapper.html'}),
    (r'^downloadOtherContent$', direct_to_template, {'template': 'downloadOtherContentBodyWrapper.html'}),
    
    
    (r'^downloadsWindow$',  direct_to_template, {'template': 'downloadsWindow.html'}),
    
    (r'^downloadsDemo$',  direct_to_template, {'template': 'downloadsDemo.html'}),
    
    # This gets called when we download a torrent file from a site that doesn't
    # use x-application/bittorrent or that uses Content-Disposition: attachment.
    (r'^torrentFiles$',  direct_to_template, {'template': 'downloadsWindow.html'}),
    #(r'^aboutUs$', direct_to_template, {'template': 'aboutUs.html'}),
    #(r'^help$', direct_to_template, {'template': 'help.html'}),
    #(r'^thanks$', direct_to_template, {'template': 'thanks.html'}),
    #(r'^technology$', direct_to_template, {'template': 'technology.html'}),
    #(r'^whatIs$', direct_to_template, {'template': 'whatIs.html'}),
    #(r'^contact$', direct_to_template, {'template': 'contact.html'}),
    
    
    (r'^link$', 'littleshoot.link.link'),
    
    (r'^metaRefresh', direct_to_template, {'template': 'metaRefresh.html'}),
    
    
    url(r'^api/oauthTest/$', link.oauthTest),
    
    # These are all the API elements.    
    (r'^api/instanceOnline$', 'littleshoot.instance.instanceOnline'),
    (r'^api/key/$', 'littleshoot.key.generateKey'),

    (r'^api/publishFile$', 'littleshoot.files.publishFile'),
    (r'^api/publishRawFile$', 'littleshoot.files.publishFile'),
    (r'^api/fileListing$', 'littleshoot.files.listFiles'),
    
    (r'^api/recentFiles$', 'littleshoot.files.recentFiles'),
    (r'^smartFiles$', 'littleshoot.files.smartFiles'),
    
    (r'^api/listS3Files$', 'littleshoot.amazonDevPay.listS3Files'),
    (r'^api/listS3FilesForId$', 'littleshoot.amazonDevPay.listS3FilesForId'),
    
    url(r'^file/(?P<fileId>[^/])/$', link.getFile, name="file_lookup"),
    
    (r'^api/editFile$', 'littleshoot.files.editFile'),
    (r'^api/deleteFile$', 'littleshoot.files.deleteFile'),
    (r'^api/downloadSources$', 'littleshoot.files.downloadSources'),

    (r'^api/search$',  'littleshoot.search.search'),
    
    # We also support the old URL so old clients don't keep hammering us!!
    #(r'^lastbamboo-server-site/api/sipServer$', 'littleshoot.servers.sipServer'),
    
    #(r'^api/sipServer$', 'littleshoot.servers.sipServer'),
    (r'^api/turnServer$', 'littleshoot.servers.turnServer'),
    (r'^api/serverOnline$', 'littleshoot.servers.serverOnline'),
    
    (r'^api/instancesForServer$', 'littleshoot.servers.instancesForServer'),
    
    (r'^api/littleShootData$', 'littleshoot.littleShootData.littleShootData'),
    
    
    #(r'^updateModelFile$', 'littleshoot.modelUpdate.updateModelFile'),
    #(r'^updateModelMetaFile$', 'littleshoot.modelUpdate.updateModelMetaFile'),
    #(r'^populateModel$', 'littleshoot.modelUpdate.populateModel'),
    
    # This is just for testing the template -- can be hard to get to otherwise.
    #(r'^linkFound$', direct_to_template, {'template': 'linkNotInstalled.html', 
    #                                                                    'extra_context': {'link' : 'test', 
    #                                                                                      'sender' : 'adam', 
    #                                                                                      'title' : 'bop'}}),
    
    #(r'^test$',  direct_to_template, {'template': 'test.html'}),
    #(r'^accordionTest$',  direct_to_template, {'template': 'accordionTestTemplate.html'}),
    #(r'^downloadsTest$',  direct_to_template, {'template': 'downloadsTest.html'}),
    #(r'^amazonFpsTest$', direct_to_template, {'template': 'amazonFpsTest.html'}),
    
    # This will generate the URL for the token API and will redirect the browser to that URL, 
    # storing appropriate values in the database along the way.
    #(r'^amazonFpsBuy$', 'littleshoot.amazonfps.buy'),
    #(r'^amazonFpsProcessPayment$', 'littleshoot.amazonfps.processCoBrandedServiceApiCallback'),
    #(r'^amazonFpsIpn$', 'littleshoot.amazonfps.ipn'),
    #(r'^facebook$', direct_to_template, {'template': 'facebook.html'}),
    #(r'^facebook$', 'littleshoot.fb.fbTest'),
    
    # Example:
    # (r'^foo/', include('foo.urls')),

    # Uncomment this for admin:
#    (r'^admin/', include('django.contrib.admin.urls')),
)  + urlpatterns

handler404 = 'littleshoot.views.fileNotFound'
handler500 = 'littleshoot.views.internalServerError'
