#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

startDir=`pwd`

buildDir=$startDir/../server/static/build/src/main/webapp

aeDir=$startDir/../server/static/appengine
subDir=css
workingDir=$aeDir/$subDir


rsync --exclude .svn \
      -avz $buildDir/$subDir/* $workingDir/static || die "Could not sync"

cd $workingDir
make update || die "Could not update"

cd $startDir

echo "Finished updating site..."
