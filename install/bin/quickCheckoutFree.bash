#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

./quickCheckout.bash false || die "Could not build free"
