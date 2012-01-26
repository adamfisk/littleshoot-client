
# NOTE: Must import *, since Django looks for things here, e.g. handler500.
from django.conf.urls.defaults import *

urlpatterns = patterns('',
    (r'^$', 'django.views.generic.simple.direct_to_template', {'template': 'lakmm.html'}),
    (r'^test$', 'django.views.generic.simple.direct_to_template', {'template': 'test.html'}),
    
    # Example:
    # (r'^foo/', include('foo.urls')),

    # Uncomment this for admin:
#    (r'^admin/', include('django.contrib.admin.urls')),
)
