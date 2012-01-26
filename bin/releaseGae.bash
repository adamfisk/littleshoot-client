#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

startDir=`pwd`

./updateGaeCss.bash || die "Could not update CSS"
./updateGaeImages.bash || die "Could not update images"
# Add a script to build js to a separate project
./updateGaeSite.bash || die "Could not update site!!"

echo "Finished updating site..."
