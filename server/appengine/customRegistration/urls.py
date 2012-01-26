"""
URLConf for Django user registration and authentication.

If the default behavior of the registration views is acceptable to
you, simply use a line like this in your root URLConf to set up the
default URLs for registration::

    (r'^accounts/', include('registration.urls')),

This will also automatically set up the views in
``django.contrib.auth`` at sensible default locations.

But if you'd like to customize the behavior (e.g., by passing extra
arguments to the various views) or split up the URLs, feel free to set
up your own URL patterns for these views instead. If you do, it's a
good idea to use the names ``registration_activate``,
``registration_complete`` and ``registration_register`` for the
various steps of the user-signup process.

"""


from django.conf.urls.defaults import *
from django.views.generic.simple import direct_to_template
from django.contrib.auth import views as auth_views

from littleshoot import authViews as littleshoot_auth_views

from registration.views import activate
from registration.views import register


urlpatterns = patterns('',
                       # Activation keys get matched by \w+ instead of the more specific
                       # [a-fA-F0-9]{40} because a bad activation key should still get to the view;
                       # that way it can return a sensible "invalid key" message instead of a
                       # confusing 404.
                       url(r'^activate/(?P<activation_key>\w+)/$',
                           activate,
                           kwargs={'template_name':'customRegistration/activate.html'},
                           name='custom_registration_activate'),
                       url(r'^login/$',
                           littleshoot_auth_views.login,
                           {'template_name': 'customRegistration/login.json'},
                           name='auth_login'),
                       url(r'^loginOrRegisterLogin/$',
                           littleshoot_auth_views.login,
                           {'template_name': 'customRegistration/loginOrRegister.html'},
                           name='auth_login_or_register_login'),
                       url(r'^loginOrRegisterRegister/$',
                           register,
                           {'template_name': 'customRegistration/loginOrRegister.html'},
                           name='auth_login_or_register_register'),
                       url(r'^loginSubmit/$',
                           littleshoot_auth_views.loginSubmit,
                           {'template_name': 'customRegistration/login.json'},
                           name='auth_login_submit'),
                       url(r'^loginSuccess/$',
                           littleshoot_auth_views.login,
                           {'template_name': 'customRegistration/loginSuccess.json'},
                           name='auth_login_success'),
                       url(r'^loginRedirect/$',
                           littleshoot_auth_views.login,
                           {'template_name': 'customRegistration/loginSuccess.html'},
                           name='auth_login_success'),
                       url(r'^logout/$',
                           littleshoot_auth_views.logout,
                           {'next_page' : '/devPayUploadForm',
                            'template_name': 'customRegistration/logged_out.html'},
                           name='auth_logout'),
                       url(r'^password/change/$',
                           auth_views.password_change,
                           name='auth_password_change'),
                       url(r'^password/change/done/$',
                           auth_views.password_change_done,
                           name='auth_password_change_done'),
                       url(r'^password/reset/$',
                           auth_views.password_reset,
                           name='auth_password_reset'),
                       url(r'^password/reset/confirm/(?P<uidb36>.+)/(?P<token>.+)/$',
                           auth_views.password_reset_confirm,
                           name='auth_password_reset_confirm'),
                       url(r'^password/reset/complete/$',
                           auth_views.password_reset_complete,
                           name='auth_password_reset_complete'),
                       url(r'^password/reset/done/$',
                           auth_views.password_reset_done,
                           name='auth_password_reset_done'),
                       url(r'^register/$',
                           register,
                           kwargs={'template_name':'customRegistration/registration_form.html'},
                           name='registration_register'),
                       url(r'^register/complete/$',
                           direct_to_template,
                           {'template': 'customRegistration/registration_complete.html'},
                           name='registration_complete'),
                       )
