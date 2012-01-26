from google.appengine.ext import db
#from ragendja.auth.models import User
from ragendja.auth.hybrid_models import User

from shortener.baseconv import base62
from django import forms
from django.conf import settings

class Link(db.Model):
    """
    Model that represents a shortened URL
    """
    #url = models.URLField(verify_exists=True, unique=True)
    url = db.LinkProperty(required=True)
    title = db.StringProperty(required=True)
    size = db.IntegerProperty(required=True)
    dateSubmitted = db.DateTimeProperty(auto_now_add=True)
    usageCount = db.IntegerProperty(default=0)
    
    # This is updated every time the link is saves, such as when the usage count
    # is incremented
    lastAccessTime = db.DateTimeProperty(auto_now=True)

    def to_base62(self):
        return base62.from_decimal(self.key().id())

    def short_url(self):
        return settings.SHORT_URL_BASE + "/" + self.to_base62()
    
    def __unicode__(self):
        return self.to_base62() + ' : ' + self.url
    
class LinkSubmitForm(forms.Form):
    u = forms.URLField(verify_exists=True,
                       label='URL to be shortened:',
                       )
    
# This this also includes all of the Django user fields, described at:
# http://docs.djangoproject.com/en/dev/topics/auth/
class User(User):
    amazonDevPayUserToken = db.StringProperty()
    amazonDevPayPersistentIdentifier = db.StringProperty()
    uploads = db.IntegerProperty(default=0)
    downloads = db.IntegerProperty(default=0)
    creationTime = db.DateTimeProperty(auto_now_add=True)
    facebookId = db.IntegerProperty()
    twitterId = db.IntegerProperty()

class PayPalIpn(db.Model):
    transaction_time = db.DateTimeProperty(auto_now_add=True)
    raw_time = db.FloatProperty()
    first_name = db.StringProperty()
    last_name = db.StringProperty()
    address_country = db.StringProperty()
    txn_id = db.StringProperty(required=True)
    invoice = db.StringProperty()
    mc_currency = db.StringProperty()
    mc_fee = db.FloatProperty(required=True, default=0.0)
    payer_email = db.StringProperty(required=True)
    payer_id = db.StringProperty()
    payment_status = db.StringProperty()
    pending_reason = db.StringProperty()
    pro_key = db.StringProperty()
    
class OnlineInstance(db.Model):
    instanceId = db.IntegerProperty(required=True)
    serverAddress = db.StringProperty()
    onlineTime = db.DateTimeProperty(auto_now_add=True)
    #user = db.UserProperty()
    fbUserName = db.TextProperty()
    
    # Override toString for debugging
    def __str__(self):
        return "models.OnlineInstance with id "+str(self.instanceId)+" online "+str(self.online) 
    
class Instance(db.Model):
    instanceId = db.IntegerProperty(required=True)
    baseUri = db.TextProperty(required=True)
    online = db.BooleanProperty(required=True, default=False)
    repeatInfringer = db.BooleanProperty(default=False)
    onlineTime = db.DateTimeProperty(auto_now_add=True)
    #user = db.UserProperty()
    fbUserName = db.TextProperty()
    
    # Override toString for debugging
    def __str__(self):
        return "models.Instance with id "+str(self.instanceId)


mediaTypeChoices = ('application/mac', 'application/linux', 'application/win', 
                 'audio', 'video', 'image', 'document', 'application', 
                 'archive', 'unknown')

class MetaFile(db.Model):
    title = db.StringProperty(required=True)
    uri = db.StringProperty(required=True)
    size = db.IntegerProperty(required=True)
    sha1 = db.StringProperty()
    
    # This is the ID of the instance that first published the file.
    instanceId = db.IntegerProperty()
    
    tags = db.StringListProperty() # always required
    numDownloads = db.IntegerProperty(default=0)
    mimeType = db.TextProperty()
    mediaType = db.StringProperty(choices=mediaTypeChoices)
    takenDown = db.BooleanProperty(default=False)
    publishTime = db.DateTimeProperty(auto_now_add=True)
    permission = db.IntegerProperty(choices=(0, 1, 2), default=0)
    groupName = db.StringProperty(default='world',)
    numRatings = db.IntegerProperty(default=0)
    averageRating = db.FloatProperty(default=0.0)
    description = db.StringProperty(multiline=True) 
    link = db.LinkProperty()
    
    titles = db.StringListProperty() # always required
    #onlineInstances = db.ListProperty(db.Key) 
    numOnlineInstances = db.IntegerProperty(default=0)
    
    #def addInstance(self, instance):
    #    key = instance.key()
    #    if key not in self.onlineInstances:
    #        self.onlineInstances.append(key)
    #        self.numOnlineInstances = len(self.onlineInstances)
        
    #def removeInstance(self, instance):
    #    key = instance.key()
    #    if key in self.onlineInstances:
    #        logging.info('Removing instance...')
    #        self.onlineInstances.remove(key)
    #        self.numOnlineInstances = len(self.onlineInstances)
    #    else:
    #        logging.error('Instance not found in list! %s', instance.instanceId)
    #  

class File(db.Model):
    title = db.StringProperty(required=True)
    uri = db.StringProperty()
    size = db.IntegerProperty(required=True)
    instanceId = db.IntegerProperty()
    instanceOnline = db.BooleanProperty(default=False)
    sha1 = db.StringProperty()
    etag = db.StringProperty()
    tags = db.StringListProperty() # always required
    numDownloads = db.IntegerProperty(default=0)
    mimeType = db.TextProperty()
    mediaType = db.TextProperty()#choices=mediaTypeChoices)
    takenDown = db.BooleanProperty(default=False)
    
    # making publish time required fouls things up...
    publishTime = db.DateTimeProperty(auto_now_add=True)
    permission = db.IntegerProperty(choices=(0, 1, 2), default=0)
    groupName = db.StringProperty(default='world')
    numRatings = db.IntegerProperty(default=0)
    averageRating = db.FloatProperty(default=0.0)
    description = db.StringProperty(multiline=True) 
    link = db.LinkProperty()
    
    twitterUrl = db.LinkProperty()
    fbId = db.IntegerProperty()
    
    # This is the published state of the file.  
    # 0) published but unhashed,
    # 1) published and hashed
    # 2) published and backed up
    # 3) to be used later
    state = db.IntegerProperty(choices=(0, 1, 2, 3))
    creator = db.TextProperty()
    userId = db.IntegerProperty()
    downloaded = db.BooleanProperty(default=False)
    language = db.TextProperty()
    timeZone = db.TextProperty()
    country = db.TextProperty()
    ip = db.TextProperty()
    
    #instance = db.ReferenceProperty(Instance, collection_name='files')
    

class AmazonFps(db.Model):
    callerReference = db.StringProperty(required=True)
    transactionId = db.StringProperty()
    requestId = db.StringProperty()
    