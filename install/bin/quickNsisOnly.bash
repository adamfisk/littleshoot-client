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
        echo "Usage: quickNsisOnly.bash version"
        exit $E_BADARGS
fi

APP_VERSION="$1";


INSTALL_DIR=../../../../temp_install_dir

test -d $INSTALL_DIR || die "$INSTALL_DIR not a directory"

cp ../win/LittleShoot.nsi $INSTALL_DIR/trunk/install/win


echo "Moving to client site dir"
pushd $INSTALL_DIR/trunk/client/site

echo "Creaing makensis script..."
mvn lastbamboo:win-dist -DappVersion=$APP_VERSION || die "Could not build windows installer"

cd ../../install/win

./makensis.sh || die "Could not make NSIS installer"

releaseDir=../../../../littleshoot/trunk/client/site

echo "Moving plugin to: $releaseDir"
mv LittleShootPlugin.exe $releaseDir 

popd
exit 0
