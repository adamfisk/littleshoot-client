
# NOTE: Must import *, since Django looks for things here, e.g. handler500.
from django.conf.urls.defaults import *

urlpatterns = patterns('',
    (r'^$', 'django.views.generic.simple.direct_to_template', {'template': 'index.html'}),
    (r'^submit$', 'bugs.views.submit'),
    (r'^submitMacInstallerBug$', 'bugs.views.submitMacInstallerBug'),
    (r'^submitWinInstallerBug$', 'bugs.views.submitWinInstallerBug'),
    (r'^submitJavaScriptError$', 'bugs.views.submitJavaScriptError'),
    
    (r'^bugs$', 'bugs.views.bugs'),
    (r'^bugsForVersion$', 'bugs.views.bugsForVersion'),
    (r'^sipTurnBugs$', 'bugs.views.sipTurnBugs'),
    
    (r'^macInstallerBugs$', 'bugs.views.macInstallerBugs'),
    (r'^winInstallerBugs$', 'bugs.views.winInstallerBugs'),
    (r'^javaScriptErrors$', 'bugs.views.javaScriptErrors'),
    
    
    # Example:
    # (r'^foo/', include('foo.urls')),

    # Uncomment this for admin:
#    (r'^admin/', include('django.contrib.admin.urls')),
)
