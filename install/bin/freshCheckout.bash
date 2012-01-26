#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

pushd ../../../..

INSTALL_DIR=temp_install_dir

mkdir $INSTALL_DIR

echo "Deleting old install directory..."
rm -rf $INSTALL_DIR || die "Could not delete old install dir!"

function checkout()
{
  echo "Checking out repository"
  svn co http://svn.littleshoot.org/svn/littleshoot/trunk $INSTALL_DIR/trunk || die "Could not checkout repo"
}

checkout

cd $INSTALL_DIR
cd trunk

# This is useful if we're testing and aren't doing full checkouts
svn up

echo "Installing all projects..."
./build.bash || die "Could not build app"
# mvn clean install || die "Could not build app"

popd

exit $EXIT_STATUS
