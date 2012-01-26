#!/usr/bin/env bash

echo "Starting Windows install script..."
echo "WARNING: THIS SHOULD ONLY BE USED FOR TESTING"
sleep 2

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

echo "About to svn up"
svn up $INSTALL_DIR/trunk

INSTALL_DIR=../../../../temp_install_dir
test -d $INSTALL_DIR || die "$INSTALL_DIR does not exist"

cp ../win/LittleShoot.nsi $INSTALL_DIR/trunk/install/win || die "Could not copy file"


echo "Moving to client site dir"
pushd $INSTALL_DIR/trunk/client/site/


#mvn clean

mvn lastbamboo:win-install-prep || die "Could not prepare windows install"

if ! mvn lastbamboo:win-launcher -DappVersion=$APP_VERSION
then
    echo "Could not build windows launcher"
    exit 1
fi

# Here we launch JSmooth and we unfortunately have to manually build
# the generated the exe through opening littleshoot.jsmooth and 
# hitting the compile button
jsmoothgen.exe || die "Could not execute jsmooth -- on PATH?"

echo "Creaing makensis script..."
mvn lastbamboo:win-dist -DappVersion=$APP_VERSION || die "Could not build windows installer"

cd ../../install/win

./makensis.sh || die "Could not make NSIS installer"

releaseDir=../../../../littleshoot/trunk/client/site

echo "Moving plugin to: $releaseDir"
mv LittleShootPlugin.exe $releaseDir 

popd
exit 0
