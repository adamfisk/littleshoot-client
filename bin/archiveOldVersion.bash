#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

ARGS=1  # One arg to script expected.

if [ $# -ne "$ARGS" ]
then
        echo "Need a version argument"
        exit 1 
fi

version=$1

./runRemote.bash www archive.bash $version
