#/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

./releaseOsx.bash $1 false || die "Could not release OSX Free"
