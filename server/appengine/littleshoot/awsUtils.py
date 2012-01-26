
import base64
import hmac
import sha
import urllib, urllib2

from boto.s3.connection import S3Connection
from django.conf import settings
#import boto

#aws_access_key_id = 'awsAccessKeyIdToken'
#aws_secret_access_key = 'awsSecretAccessKeyToken'

def presigned(fileKey):
    conn = S3Connection(settings.AWS_ACCESS_KEY_ID, settings.AWS_SECRET_ACCESS_KEY)
    url = conn.generate_url(1000*60*10, "GET", bucket="littleshoot", key=fileKey);
    return url
