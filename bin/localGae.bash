#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

startDir=`pwd`

buildDir=$startDir/../server/static/build/src/main/webapp

#aeDir=$startDir/../server/static/appengine
aeMain=$startDir/../server/appengine

#rsync --exclude .svn -avz $buildDir/css/* $aeMain/static/css || die "Could not sync"
#rsync --exclude .svn -avz $buildDir/images/* $aeDir/images/static || die "Could not sync"
#rsync --exclude .svn -avz $buildDir/littleshoot/* $aeMain/static/js/littleshoot || die "Could not sync"
#cp $buildDir/* $aeMain/static 
#cp $buildDir/*.html $aeMain/templates 

cd $buildDir/littleshoot
zip -rv littleshoot.zip *
mv littleshoot.zip $aeMain

cp $buildDir/* $aeMain/static

mkdir $aeMain/static/css
cp $buildDir/css/* $aeMain/static/css

mkdir $aeMain/static/images
cp $buildDir/images/* $aeMain/static/images

cd $aeMain
make serve || die "Could not update"

echo "Finished local release..."
