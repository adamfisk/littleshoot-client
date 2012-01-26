#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

#./checkJavaScript.bash || die "Bad JavaScript"

startDir=`pwd`

function build()
{
  local basePath=$startDir/../server/static/build/src/main/webapp
  cd $basePath
  cd util
  #test -e shoot.sh || die "Build script does not exist"

  #./shoot.sh || die "Could not build javascript"

#  test -e releaseFix.bash || die "HTML fix script does not exist"
 # ./releaseFix.bash || die "Could not fix HTML"
}

build || die "Could not build static release"

cd $startDir/../server/appengine

#cp static/*.html templates

#./makeZips.bash || die "Could not make zips"
./release.bash || die "Could not release"

#make update || die "Could not update GAE"
cd $startDir

echo "Finished updating site..."
