#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

source constants.bash

if [ $# -ne "1" ]
then
  echo "Would you like to commit the release version if the build succeeds? (y/n)"
  read doCommit
else
  doCommit = $1
fi



#./checkJavaScript.bash || die "Bad JavaScript"

startDir=`pwd`

function build()
{
  local basePath=$startDir/../server/static/build/src/main/webapp
  cd $basePath
  cd util
  test -e shoot.sh || die "Build script does not exist"
 
  ./shoot.sh || die "Could not build javascript"

  test -e releaseFix.bash || die "HTML fix script does not exist"
  ./releaseFix.bash || die "Could not fix HTML"
}

build || die "Could not build static release"

function commit()
{
  cd $startDir/../server/static/release || die "Could not move to release dir"
  svn add src/main/webapp/*.*
  svn add src/main/webapp/js
  svn add src/main/webapp/images
  svn add src/main/webapp/images/sources
  svn add src/main/webapp/images/sources/*
  svn add src/main/webapp/images/icons
  svn add src/main/webapp/images/icons/*
  svn add src/main/webapp/js/littleshoot
  svn add src/main/webapp/js/littleshoot/*.js
  svn ci -m "Auto updating release build" || die "Could not commit code!"
}

case $doCommit in
y)
  commit || die "Could not build"
  ;;
Y)
  commit || die "Could not build"
  ;;
*)
  echo "OK, just building."
  ;;
esac

