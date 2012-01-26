from google.appengine.api import oauth

from os import environ
from traceback import format_exc
from urllib import unquote
from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.runtime import DeadlineExceededError
import logging

DEVELOPMENT = environ.get('SERVER_SOFTWARE', '').startswith('Dev')
#DEBUG = DEVELOPMENT
DEBUG = True
if DEBUG:
    logging.getLogger().setLevel(logging.DEBUG)

class MyHandler(webapp.RequestHandler):
    def get(self):
        user = users.get_current_user()
        if user:
            greeting = ("Welcome, %s! (<a href=\"%s\">sign out</a>)" %
                        (user.nickname(), users.create_logout_url("/")))
        else:
            greeting = ("<a href=\"%s\">Sign in or register</a>." %
                        users.create_login_url("/"))

        self.response.out.write("<html><body>%s</body></html>" % greeting)
        
        #try:
            # Get the db.User that represents the user on whose behalf the
            # consumer is making this request.
        #    user = oauth.get_current_user()

        #except oauth.OAuthRequestError, e:
            # The request was not a valid OAuth request.
            # ...

app = webapp.WSGIApplication((
    (r'/.*', MyHandler),
    ), debug=DEBUG)

def main():
    if DEVELOPMENT:
        from wsgiref.handlers import CGIHandler
        CGIHandler().run(app)
    else:
        from google.appengine.ext.webapp.util import run_wsgi_app
        run_wsgi_app(app)

if __name__ == "__main__":
    main()

