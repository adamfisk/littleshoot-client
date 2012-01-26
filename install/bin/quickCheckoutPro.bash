#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

./quickCheckout.bash true || die "Could not build pro"
