#!/usr/bin/env bash
function die()
{
  echo $*
  exit 1
}

#startDir=`pwd`
#./checkJavaScriptBase.bash $startDir/../server/static/build/src/main/webapp/littleshoot $startDir/../server/appengine/templates $startDir/../server/appengine/media || die "Error checking JavaScript"
./checkJavaScriptBase.bash ../server/static/build/src/main/webapp/littleshoot ../server/appengine/templates ../server/appengine/media || die "Error checking JavaScript"
