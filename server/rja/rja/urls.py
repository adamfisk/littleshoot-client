# -*- coding: utf-8 -*-
from django.conf.urls.defaults import *
from ragendja.urlsauto import urlpatterns
from ragendja.auth.urls import urlpatterns as auth_patterns
from django.contrib import admin

admin.autodiscover()

handler500 = 'ragendja.views.server_error'

urlpatterns = auth_patterns + patterns('',
    (r'^$', 'django.views.generic.simple.direct_to_template', {'template': 'rja.html'}),
    (r'^toll$', 'django.views.generic.simple.direct_to_template', {'template': 'toll.html'}),
    (r'^fairies$', 'django.views.generic.simple.direct_to_template', {'template': 'fairies.html'}),
    (r'^gorilla$', 'django.views.generic.simple.direct_to_template', {'template': 'gorilla.html'}),
    (r'^resume$', 'django.views.generic.simple.direct_to_template', {'template': 'resume.html'}),
    (r'^test$', 'django.views.generic.simple.direct_to_template', {'template': 'test.html'}),
    
    (r'^resumeTest$', 'django.views.generic.simple.direct_to_template', {'template': 'resumeTest.html'}),
    
    ('^admin/(.*)', admin.site.root),
) + urlpatterns


# NOTE: Must import *, since Django looks for things here, e.g. handler500.
"""
from django.conf.urls.defaults import *

urlpatterns = patterns('',
    (r'^$', 'django.views.generic.simple.direct_to_template', {'template': 'rja.html'}),
    (r'^toll$', 'django.views.generic.simple.direct_to_template', {'template': 'toll.html'}),
    (r'^fairies$', 'django.views.generic.simple.direct_to_template', {'template': 'fairies.html'}),
    (r'^gorilla$', 'django.views.generic.simple.direct_to_template', {'template': 'gorilla.html'}),
    (r'^resume$', 'django.views.generic.simple.direct_to_template', {'template': 'resume.html'}),
    (r'^test$', 'django.views.generic.simple.direct_to_template', {'template': 'test.html'}),
    
    (r'^resumeTest$', 'django.views.generic.simple.direct_to_template', {'template': 'resumeTest.html'}),
    
    # Example:
    # (r'^foo/', include('foo.urls')),

    # Uncomment this for admin:
#    (r'^admin/', include('django.contrib.admin.urls')),
)
"""
