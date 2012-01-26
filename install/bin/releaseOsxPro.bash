#/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

./releaseOsx.bash $1 true || die "Could not release OSX Pro"
