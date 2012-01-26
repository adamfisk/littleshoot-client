#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}
source constants.bash
startDir=`pwd`

jsDir=$startDir/../server/static/appengine/js
function build()
{
  cd $startDir/../server/static/build/src/main/webapp/util

  baseDir=../$DOJO_VERSION
  echo "Using base dir: $baseDir"
  buildDir=$baseDir/util/buildscripts
  tmpBase=`basename $0`
  tmpDir=`mktemp -d /tmp/${tmpBase}.XXXXXX` || die "Could not create temp dir"

  rd=$jsDir/static/

  echo "Release dir is: $rd"
  test -d $rd || die "Release dir $rd does not exist"

  cd $buildDir || die "Could not change directories"
#  ./build.sh stripConsole=normal copyTests=false loader=xdomain profileFile=../../../util/embed.profile.js cssOptimize=comments.keepLines cssImportIgnore=../dijit.css action=release releaseDir=$tmpDir/ releaseName=js || die "Could not build"
  ./build.sh stripConsole=normal copyTests=false loader=xdomain profileFile=../../../util/embed.profile.js cssOptimize=comments.keepLines cssImportIgnore=../dijit.css action=release releaseDir=$tmpDir/ releaseName=js || die "Could not build"

  echo "Finished build.  Syncing."
  cd $tmpDir || die "Could not move to temp dir!!"

  echo `pwd`
  echo `ls -la` || die "Could not print directory contents"

  # This is defined in constants!!
  excludeRsyncJsLib $tmpDir/js $rd || die "Could not sync"
  #rsync -avz $tmpDir/js $rd || die "Could not sync"

  echo "Release completed successfully!"
}

build || die "Could not build static release"

cd $jsDir 
./makeZips.bash || die "Could not make zips!!"

cd $jsDir
make update || die "Could not update"

cd $startDir

echo "Finished updating site..."
