
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.http import HttpResponseRedirect
from django.http import HttpResponseBadRequest

from google.appengine.ext import db
from google.appengine.ext.db import djangoforms

import models
import logging
import decorators


def lineNumberRequired(func):
    return decorators.intRequired(func, 'lineNumber')
    
class BugForm(djangoforms.ModelForm):
    class Meta:
        model = models.Bug
        exclude = []
        
class MacInstallerBugForm(djangoforms.ModelForm):
    class Meta:
        model = models.MacInstallerBug
        exclude = []
        
class WinInstallerBugForm(djangoforms.ModelForm):
    class Meta:
        model = models.WinInstallerBug
        exclude = []
        
class JavaScriptErrorForm(djangoforms.ModelForm):
    class Meta:
        model = models.JavaScriptError
        exclude = []

@lineNumberRequired
def submit(request):
    logging.info("Got request: %s", request.POST);
    
    version = request.REQUEST.get('version')
    lineNumber = request.REQUEST.get('lineNumber')
    className = request.REQUEST.get('className')
    osName = request.REQUEST.get('osName')
    message = request.REQUEST.get('message')
    
    logging.info('Version: %s', version)
    logging.info('LineNumber: %s', lineNumber)
    logging.info('className: %s', className)
    logging.info('osName: %s', osName)

    # We don't include the message property because it's a TextProperty and
    # therefore cannot be searched.
    query = models.Bug.gql('WHERE version = :version AND '+
                           'lineNumber = :lineNumber AND '+
                           'className = :className AND ' +
                           'osName = :osName', 
                           version=float(version), 
                           lineNumber=int(lineNumber), 
                           className=className,
                           osName=osName)

    bug = query.get()

    logging.info('Bug: %s', bug)
    
    if bug is not None:
        logging.info('Found existing bug')
        bug.count = bug.count + 1;
        
        logging.info('About to call put on old bug')
        bug.put()
        
        addVersion(bug.version)
        return HttpResponse()
    
    logging.info('Creating new bug')
    form = BugForm(data=request.REQUEST)
    if form.is_valid():
        bug = form.save(commit=False)
        logging.info('About to call put on new bug')
        bug.put()
        
        addVersion(bug.version)
        return HttpResponse()
    else:
        logging.warn('The bug submission had errors:\n%s' % repr(form.errors))
        return HttpResponseBadRequest('ERROR submitting bug:\n%s' % repr(form.errors))

    
def addVersion (version):
    # Add the version if we don't know about it.
    versionQuery = db.Query(models.Version)
    versionQuery.filter('version =', version)
    existingVersion = versionQuery.get()
    if existingVersion is None:
        newVersion = models.Version(version=version)
        newVersion.put()

def bugsForVersion (request):
    logging.info('Received bugs for version request: %s', request.POST)
    
    version = request.POST.get('version')
    return renderBugsForVersion(float(version))
    
    
def bugs(request):
    logging.info('Received bugs request...')
    
    query = db.Query(models.Bug)
    query.order('-version')
    
    versionBug = query.get()
    logging.info('Got version: %s', versionBug)
    
    if versionBug is None:
        return render_to_response('bugs.html')
    
    version = versionBug.version
    logging.info('Got top version: %s', version)
    
    return renderBugsForVersion(version)
    
def renderBugsForVersion(version):
    versionQuery = db.Query(models.Version)
    versionQuery.order('-version')
    lastFiveVersions = versionQuery.fetch(5)
    if lastFiveVersions is None:
        logging.error('No existing versions??')
    
    numVersions = len(lastFiveVersions)
    if numVersions == 0:
        logging.error('Zero versions?')
    
    logging.info('Rendering bugs for version: %s', version)
    
    topBugsQuery = db.Query(models.Bug)
    topBugsQuery.filter('version = ', version)
    topBugsQuery.order('-count')
    
    topBugs = topBugsQuery.fetch(40)
    
    return render_to_response('bugs.html', {'bugs' : topBugs, 'bugsTitle' : 'LittleShoot', 'version' : version, 'title' : 'LittleShoot Bugs', 'versions' : lastFiveVersions})


def sipTurnBugs(request):
    logging.info('Received SIP/TURN bugs request...')
    
    version = 0.11114
    topBugsQuery = db.Query(models.Bug)
    topBugsQuery.filter('version = ', version)
    topBugsQuery.order('-count')
    topBugs = topBugsQuery.fetch(20)
    
    return render_to_response('bugs.html', {'bugs' : topBugs, 'bugsTitle' : 'SIP/TURN Servers', 'version' : version, 'title' : 'LittleShoot SIP/TURN Bugs'})
   

def submitMacInstallerBug (request):
    logging.info("Got installer bug!!: %s", request.POST);
    
    form = MacInstallerBugForm(data=request.POST)
    if form.is_valid():
        bug = form.save(commit=False)
        bug.put()
        
        return HttpResponse()
    else:
        logging.warn('The installer bug submission had errors:\n%s' % repr(form.errors))
        return HttpResponseBadRequest('ERROR submitting bug:\n%s' % repr(form.errors))  
    
def submitWinInstallerBug (request):
    logging.info("Submitting windows installer bug!!: %s", request.REQUEST);
    
    form = WinInstallerBugForm(data=request.REQUEST)
    if form.is_valid():
        bug = form.save(commit=False)
        bug.put()
        
        return HttpResponse()
    else:
        logging.warn('The installer bug submission had errors:\n%s' % repr(form.errors))
        return HttpResponseBadRequest('ERROR submitting bug:\n%s' % repr(form.errors))   
    
def macInstallerBugs (request):
    logging.info('Rendering installer bugs')
    query = db.Query(models.MacInstallerBug)
    query.order('-date')
    bugs = query.fetch(20)
    
    return render_to_response('macInstallerBugs.html', {'bugs' : bugs})

def winInstallerBugs (request):
    logging.info('Rendering installer bugs')
    query = db.Query(models.WinInstallerBug)
    query.order('-version')
    bugs = query.fetch(20)
    
    return render_to_response('winInstallerBugs.html', {'bugs' : bugs})

def javaScriptErrors (request):
    logging.info('Rendering JavaScript bugs')
    query = db.Query(models.JavaScriptError)
    query.order('-date')
    bugs = query.fetch(20)
    
    return render_to_response('javaScriptErrors.html', {'bugs' : bugs})