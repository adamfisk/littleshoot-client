#!/usr/bin/env bash

source constants.bash
function die
{   
  echo $*
  exit 1
}

baseDir=../$DOJO_VERSION
echo "Using base dir: $baseDir"
buildDir=$baseDir/util/buildscripts
tmpBase=`basename $0`

# We don't use this because Cygwin gets all bent out of shape
#tmpDir=`mktemp -d /tmp/${tmpBase}.XXXXXX` || die "Could not create temp dir"
tmpDir=../buildTemp
rm -rf $tmpDir
mkdir $tmpDir || die "Could not make temp dir"

rd=`pwd`/../../../../../../appengine/tmp
mkdir $rd

echo "Release dir is: $rd"
test -d $rd || die "Release dir does not exist!!!"

cd $buildDir || die "Could not change directories"
#./build.sh loader=xdomain xdDojoPath=http://ajax.googleapis.com/ajax/libs/dojo/1.1.1/ copyTests=false profile=shoot cssOptimize=comments.keepLines cssImportIgnore=../dijit.css action=release releaseDir=$tmpDir/ releaseName=js || die "Could not build"
./build.sh loader=xdomain stripConsole=normal copyTests=false profileFile=../../../util/shoot.profile.js cssOptimize=comments.keepLines action=clean,release releaseDir=$tmpDir/ releaseName=js || die "Could not build"
#./build.sh stripConsole=normal copyTests=false profileFile=../../../util/shoot.profile.js cssOptimize=comments.keepLines cssImportIgnore=../dijit.css action=clean,release releaseDir=$tmpDir/ releaseName=js || die "Could not build"
#./build.sh localeList="en-us" copyTests=false profile=shoot cssOptimize=comments.keepLines cssImportIgnore=../dijit.css action=release releaseDir=$tmpDir/ releaseName=js || die "Could not build"

echo "Finished build.  Syncing."
cd $tmpDir || die "Could not move to temp dir!!"

echo `pwd`
echo `ls -la` || die "Could not print directory contents"

function regularRsync
{
  rsync -avz $tmpDir/js $rd || die "Could not sync"
}

function excludeRsync
{

        #--exclude *.uncompressed.js \
  rsync --exclude .svn/ \
        --exclude test/ \
        --exclude tests/ \
        --exclude demo/ \
        --exclude demos/ \
        --exclude soria/ \
        --exclude nihilo/ \
        --exclude grid/ \
        --exclude charting/ \
        --exclude util/ \
        --exclude collections/ \
        --exclude README* \
        --exclude *.psd \
        --exclude *.commented.css \
        --exclude dijit/templates \
        --exclude dijit/form/templates \
        --exclude dijit/layout/templates \
        --exclude *silverlight* \
        --exclude gfx3d/ \
        --exclude dojo/_base/ \
        --exclude dojo/_base.js \
        --exclude dojo/build.txt \
        --exclude functional/ \
        --exclude off/ \
        --exclude presentation/ \
        --exclude sketch/ \
        --exclude storage/ \
        --exclude wire/ \
        --exclude data/ \
        --exclude dtl/ \
        --exclude dojox/xmpp/ \
        --exclude dojox/widget/ \
        --exclude dojox/layout \
        --exclude dojox/image \
        --exclude dojox/editor \
        --exclude dojox/gfx \
        --exclude dojox/form \
        -avz $tmpDir/js $rd || die "Could not sync"
}

excludeRsync

echo "Release completed successfully!"
