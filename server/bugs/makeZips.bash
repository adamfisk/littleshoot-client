#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

cd static/js

dirs="dojo dijit dojox littleshoot"
for x in $dirs
do
  cd $x || die "Could not cd to $x"
  echo "Building zip for $x"
  zf=../../../$x.zip
  rm $zf
  zip -rv $zf * || die "Could not create zip"
  cd ..
  # We actually delete all the contents because we don't want to 
  # include them in GAE.
  rm -rf $x
done

