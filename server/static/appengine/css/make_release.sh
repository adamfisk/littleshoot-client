#!/bin/sh

# Script to create a "release" subdirectory.  This is a subdirectory
# containing a bunch of symlinks, from which the app can be updated.
# The main reason for this is to import Django from a zipfile, which
# saves dramatically in upload time: statting and computing the SHA1
# for 1000s of files is slow.  Even if most of those files don't
# actually need to be uploaded, they still add to the work done for
# each update.

RELEASE=release
FILES="app.yaml __init__.py main.py settings.py urls.py"
DIRS="static "

# Remove old $RELEASE directory.
rm -rf $RELEASE

# Create new $RELEASE directory.
mkdir $RELEASE

# Create symbolic links.
for x in $FILES $DIRS 
do
    ln -s ../$x $RELEASE/$x
done
