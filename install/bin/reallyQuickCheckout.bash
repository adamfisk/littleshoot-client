#!/bin/bash

pushd ../../../..

INSTALL_DIR=temp_install_dir

cd $INSTALL_DIR
cd trunk

# This is useful if we're testing and aren't doing full checkouts
svn up

echo "Installing all projects..."
if ! mvn install -Dmaven.test.skip=true
then
    echo "Maven build failed"
    popd
    exit 1
fi

popd

exit 0
