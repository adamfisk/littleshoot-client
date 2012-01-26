# -*- coding: utf-8 -*-
from ragendja.settings_pre import *

# Increase this when you update your media on the production site, so users
# don't have to refresh their cache. By setting this your MEDIA_URL
# automatically becomes /media/MEDIA_VERSION/
MEDIA_VERSION = 44

# Make this unique, and don't share it with anybody.
SECRET_KEY = '247892euroi79072'

DJANGO_STYLE_MODEL_KIND = False
DEFAULT_FROM_EMAIL = 'afisk@littleshoot.org'
ROOT_URLCONF = 'littleshoot.urls'

ACCOUNT_ACTIVATION_DAYS = 7

#BASE_URI = 'http://www.littleshoot.org'
BASE_URI = 'http://36.latest.littleshootapi.appspot.com'
SHORT_URL_BASE = 'http://f.littleshoot.org'

FACEBOOK_API_KEY = 'FACEBOOK_API_KEY_TOKEN'
FACEBOOK_SECRET_KEY = 'FACEBOOK_SECRET_KEY_TOKEN'

AWS_ACCESS_KEY_ID = 'awsAccessKeyIdToken'
AWS_SECRET_ACCESS_KEY ='awsSecretAccessKeyToken'
AWS_PRODUCT_TOKEN = 'awsProductTokenToken'

#ENABLE_PROFILER = True
#ONLY_FORCED_PROFILE = True
#PROFILE_PERCENTAGE = 25
#SORT_PROFILE_RESULTS_BY = 'cumulative' # default is 'time'
#PROFILE_PATTERN = 'ext.db..+\((?:get|get_by_key_name|fetch|count|put)\)'

# Enable I18N and set default language to 'en'
USE_I18N = True
LANGUAGE_CODE = 'en'

#Restrict supported languages (and JS media generation)
#LANGUAGES = (
#    ('de', 'German'),
#    ('en', 'English'),
#)

TEMPLATE_CONTEXT_PROCESSORS = (
    'django.core.context_processors.auth',
    'django.core.context_processors.media',
    'django.core.context_processors.request',
    'django.core.context_processors.i18n',
)

MIDDLEWARE_CLASSES = (
    # The following LittleShoot middleware is still required for dynamically-generated
    # classes.
    'littleshoot.middleware.FarFutureExpires',
    'django.contrib.sessions.middleware.SessionMiddleware',
    # Django authentication
    #'django.contrib.auth.middleware.AuthenticationMiddleware',
    # Google authentication
    #'ragendja.auth.middleware.GoogleAuthenticationMiddleware',
    # Hybrid Django/Google authentication
    'ragendja.auth.middleware.HybridAuthenticationMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.locale.LocaleMiddleware',
    'ragendja.sites.dynamicsite.DynamicSiteIDMiddleware',
    'django.contrib.flatpages.middleware.FlatpageFallbackMiddleware',
    'django.contrib.redirects.middleware.RedirectFallbackMiddleware',
    'littleshoot.middleware.SubdomainMiddleware',
    
)

# Google authentication
#AUTH_USER_MODULE = 'ragendja.auth.google_models'
#AUTH_ADMIN_MODULE = 'ragendja.auth.google_admin'
# Hybrid Django/Google authentication
#AUTH_USER_MODULE = 'ragendja.auth.hybrid_models'
AUTH_USER_MODULE = 'littleshoot.models'

GLOBALTAGS = (
    'ragendja.templatetags.googletags',
    'ragendja.templatetags.ragendjatags',
    'django.templatetags.i18n',
)

# Django auth uses these paths, which the registration app uses in turn.
#TODO: differentiate these from "accoounts" paths
LOGIN_URL = '/account/login/'
LOGOUT_URL = '/account/logout/'
#LOGIN_REDIRECT_URL = '/accounts/loginRedirect'
LOGIN_REDIRECT_URL = '/devPayUploadForm'

INSTALLED_APPS = (
    'django.contrib.auth',
    'django.contrib.sessions',
    'django.contrib.admin',
    'django.contrib.webdesign',
    'django.contrib.flatpages',
    'django.contrib.redirects',
    'django.contrib.sites',
    'appenginepatcher',
    'littleshoot',
    'images',
    'mediautils',
    'customRegistration',
    'registration',
    'boto',
    'facebook',
    'shortener',
)

# List apps which should be left out from app settings and urlsauto loading
IGNORE_APP_SETTINGS = IGNORE_APP_URLSAUTO = (
    # Example:
    # 'django.contrib.admin',
    # 'django.contrib.auth',
    # 'yetanotherapp',
    'facebook',
    'boto',
)

# Set default language without country code
# (LANGUAGE_CODE must be contained in LANGUAGES)
LANGUAGE_CODE = 'en'

# Only generate media files for English and German
LANGUAGES = (
    #('de', 'German'),
    ('en', 'English'),
)

# Combine media files
COMBINE_MEDIA = {
    # Create a combined JS file which is called "combined-en.js" for English,
    # "combined-de.js" for German, and so on
    'combined-%(LANGUAGE_CODE)s.js': (
        # Integrate bla.js from "myapp/media" folder
        # You don't write "media" because that folder is used automatically
        #littleshoot/downloads.js,
        # Integrate morecode.js from "media" under project root folder
        
        'global/dojoLoader.js',
        'global/swfobject.js',
        'global/ymp.js',
        'global/littleshootlib.js.uncompressed.js',
        
        'global/CommonUtils.js',
        'global/Button.js',
        'global/pageNav.js',
        'global/Constants.js',
        'global/AppMonitor.js',
        'global/LittleShootLoader.js',
        'global/LittleShootUtils.js',
        'global/SiteBase.js',
        'global/FlickrResult.js',
        'global/IsoHuntResult.js',
        'global/YouTubeResult.js',
        'global/YahooBossResult.js',
        'global/YahooVideoResult.js',
        
        'global/SearchResult.js',
        'global/SearchResults.js',
        'global/Search.js',
        'global/SearchResultUtils.js',
        'global/AboutTab.js',
        
        'global/appDetectionBase.js',
        'global/appDetectionFlash.js',
        
        'global/index.js',
        'global/publisher.js',
        
        'global/jquery-ui-1.7.custom.js',
        
        #'global/purePacked-1.21.js',
        'global/pure-1.34.js',
        'global/download.js',
        'global/downloads.js',
        'global/setup.js',
        'global/accordionNav.js',
        'global/tabbedContent.js',
        'global/buyPro.js',
        'global/beta.js',
        'global/DownloadsTab.js',
        'global/PublishTab.js',
        'global/easyTooltip.js',
        'global/accordion.js',
        'global/jquery.block-ui-2.15.js',
        'global/home.js',
        'global/pro.js',
        'global/Welcome.js',
        'global/Overlay.js',
        'global/BitlyUtils.js',
        'global/Link.js',
        'global/common.js',
        'global/facebook.js',
    ),

    # Create a combined CSS file which is called "combined-ltr.css" for
    # left-to-right text direction
    'combined-%(LANGUAGE_DIR)s.css': (
                                      
        'global/common.css',
        'global/tabLayoutSite.css',
        'global/button.css',
        'global/horizontal.css',
        'global/vertical.css',
        'global/sprite.css',
        'global/jquery.lightbox-0.5.css',
        
        # Load layout for the correct text direction
        #'global/layout-%(LANGUAGE_DIR)s.css',
        'global/ui.core.css',
        'global/ui.theme.css',
        'global/ui.progressbar.css',
        'global/accordionNav.css',
        'global/download.css',
        'global/downloadsTab.css',
        'global/easyTooltip.css',
        'global/accordion.css',
    ),
}


from ragendja.settings_post import *
