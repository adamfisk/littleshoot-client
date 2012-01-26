#!/usr/bin/env sh

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
tmpDir=`mktemp -d /tmp/${tmpBase}.XXXXXX` || die "Could not create temp dir"

#rd=`pwd`/../../../../../release/src/main/webapp/
rd=`pwd`/../../../../../appengine/js/static/

echo "Release dir is: $rd"
test -d $rd || die "Release dir $rd does not exist"

cp embed.profile.js $buildDir/profiles || die "Could not copy profile"

cd $buildDir || die "Could not change directories"
./build.sh loader=xdomain profile=embed cssOptimize=comments.keepLines cssImportIgnore=../dijit.css action=release releaseDir=$tmpDir/ releaseName=js || die "Could not build"

echo "Finished build.  Syncing."
cd $tmpDir || die "Could not move to temp dir!!"

echo `pwd`
echo `ls -la` || die "Could not print directory contents"

rsync -avz $tmpDir/js $rd || die "Could not sync"

echo "Release completed successfully!"
