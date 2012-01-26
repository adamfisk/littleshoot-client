#!/usr/bin/env bash
function die()
{
  echo $*
  exit 1
}

echo "Building LimeWire Free"
./buildLime.bash false || die "Could not build free"
