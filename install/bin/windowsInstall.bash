#!/usr/bin/env bash

echo "Starting Windows install script..."

function die()
{
  echo $*
  exit 1
}

ARGS=1  # One arg to script expected.
E_BADARGS=65

if [ $# -ne "$ARGS" ]
then
        echo "Usage: windowsInstall.bash version"
        exit $E_BADARGS
fi

APP_VERSION="$1";
echo "Building windows installer with app version: $APP_VERSION"

#INSTALL_DIR=lb
 
INSTALL_DIR=../../../../temp_install_dir
#INSTALL_DIR=../../..

pushd $INSTALL_DIR/trunk
svn up

echo "Moving to client site dir"
pushd client/site/

mvn clean

mvn install -Dmaven.test.skip=true
cp target/lastbamboo-*-jar-with-dependencies.jar $INSTALL_DIR/trunk/install/win/LittleShoot.jar

echo "Calling win install prep from dir `pwd`"
mvn lastbamboo:win-install-prep || die "Could not prepare windows install"


echo "About to run lastbamboo:win-launcher with appVersion: $APP_VERSION"
mvn lastbamboo:win-launcher -DappVersion=$APP_VERSION || die "Could not build windows launcher"

# Here we launch JSmooth and we unfortunately have to manually build
# the generated the exe through opening littleshoot.jsmooth and 
# hitting the compile button
jsmoothgen.exe || die "Could not execute jsmooth -- on PATH?"

mvn lastbamboo:win-dist -DappVersion=$APP_VERSION || die "Could not create Windows distribution"

cd ../../install/win
test -f LittleShoot.exe || die "No LittleShoot.exe"
cp ../../lib/jnltorrent.dll . || die "Could not copy dll"
cp ../../lib/nplittleshoot.dll . || die "Could not copy dll"
cp ../../lib/static/*.dll . || die "Could not copy static dlls!"

#./makensis.sh || die "Could not make NSIS installer"

#releaseDir=../../../../littleshoot/trunk/client/site

#echo "Moving plugin to: $releaseDir"
#mv LittleShootPlugin.exe $releaseDir 

popd
popd
exit 0
