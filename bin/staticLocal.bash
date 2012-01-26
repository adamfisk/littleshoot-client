#!/usr/bin/env bash

# This is a quick script for locally testing the static server with 
# a locally-running Apache instance. 
function die()
{
  echo $*
  exit 1
}

source constants.bash

baseDir=../server/static/build/src/main/webapp/
mkdir $baseDir/$DOJO_VERSION/littleshoot 
cp $baseDir/littleshoot/*.js $baseDir/$DOJO_VERSION/littleshoot || die "Could not copy js"

serverRoot=/Library/WebServer/Documents/
rsync -avz $baseDir $serverRoot || die "Could not sync"

netstat -na | grep 80 > /dev/null || die "No server running on port 80."

if [ $# -eq 0 ]
then
  #open http://www.littleshoot.org
else
  #open http://www.littleshoot.org/$1
fi
