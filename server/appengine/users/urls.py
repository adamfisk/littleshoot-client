from django.conf.urls.defaults import *

urlpatterns = patterns('',
    (r'^$', 'django.views.generic.simple.direct_to_template', {'template': 'users.html'}),
    (r'^signup/$', 'users.user_views.create_new_user'),
    
    # Straight 'login' path runs into weird issues with App Engine Path predefined path handlers.
    (r'^normalLogin/$', 'django.contrib.auth.views.login', {'template_name': 'users/user_create_form.html'}),
    (r'^logout/$', 'django.contrib.auth.views.logout_then_login', {'login_url': '/users/'}),

)