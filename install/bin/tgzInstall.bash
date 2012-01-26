#!/usr/bin/env bash
ARGS=1  # One arg to script expected.

function die()
{
  echo $*
  exit 1
}

if [ $# -ne "$ARGS" ]
then
  echo "Usage: tgzInstall.bash version"
  exit 1
fi

APP_VERSION="$1";

BASE_DIR=littleshoot
INSTALL_DIR=temp_install_dir

echo "Moving to client site dir"
cd ~/$INSTALL_DIR/trunk/client/site

echo "Setting up tgz..."
if ! mvn lastbamboo:tgz -DappVersion=$APP_VERSION
then
    echo "Could not create tgz"
    exit 1
fi

cd target/tgz

echo "Removing svn files..."
find . -name "*.svn" | xargs rm -rf

echo "Running tar command -- preserving permissions."
tar czvfp LittleShoot.tgz * 

echo "Copying tgz to upload directory"
cp LittleShoot.tgz ~/$BASE_DIR/trunk/client/site/LittleShootPlugin.tgz || die "Could not copy tgz"

exit 0

