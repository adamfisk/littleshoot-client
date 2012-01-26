#!/bin/bash

function die()
{
  echo $*
  exit 1
}

ARGS=1  # One arg to script expected.

if [ $# -ne "$ARGS" ]
then
        echo "Usage: release.bash version, as in release.bash 0.07"
        exit 1 
fi

echo "We need to make sure we have sudo for a couple of operations"

#sudo ls

APP_VERSION="$1";

echo "Checking out"
./quickCheckout.bash || die "Error checking out or building code!!"

echo "Running OSX install script..."
./osxInstall.bash $APP_VERSION || die "Error building OSX installer"

echo "Built OSX installer"
echo "Building TGZ installer"
./tgzInstall.bash $APP_VERSION || die "Error building TGZ installer"

echo "Built TGZ Installer"

cd ../../client/site

function resetProps()
{
  mkdir ~/.littleshoot
  cp ~/littleshoot.properties ~/.littleshoot || die "Could not copy props"
}

test -f ~/.littleshoot/littleshoot.properties || resetProps
 
echo "Uploading DMG..."
aws -putp littleshoot LittleShootPlugin.dmg || die "Could not upload dmg"

echo "Finished DMG upload.  Uploading TGZ..."
aws -putp littleshoot LittleShootPlugin.tgz || die "Could not upload tgz"

svn copy https://svn.littleshoot.org/svn/littleshoot/trunk https://svn.littleshoot.org/svn/littleshoot/tags/littleshoot-$APP_VERSION-release-osx -m "release tag"

exit $?
