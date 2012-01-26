#!/usr/bin/env bash
function die()
{
  echo $*
  exit 1
}

echo "Building LimeWire Pro"
./buildLime.bash true || die "Could not build pro"
