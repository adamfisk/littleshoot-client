#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

cd tmp/js || die "Could not cd into js dir"

dojoVersion=12

#dirs="dojo dijit dojox"
dirs="dojo dijit"
for x in $dirs
do
  #cd $x || die "Could not cd to $x"
  echo "Building zip for $x"
  zf=../../$x$dojoVersion.zip
  rm $zf 
  test -f $zf && die "Could not delete zip file $zf"
  zip -rv $zf $x || die "Could not create zip"
  # We actually delete all the contents because we don't want to 
  # include them in GAE.
  rm -rf $x
done

#dirs="littleshoot"
cd littleshoot || die "Could not cd into littleshoot"
zip -rv ../../../littleshoot.zip *
cd ..

# We now need to make the crazy ...zip file that dojo mistakenly requests from time to time.
# This is in response to errors like:
# "GET /../littleshoot/nls/littleshootlib_en-us.js HTTP/1.1" 404
#zip -rv ../../...zip *
rm -rf littleshoot

#for x in $dirs
#do
#  cd $x || die "Could not cd to $x"
#  echo "Building zip for $x"
  #zf=../../../$x.zip
  #rm $zf
  #zip -rv $zf * || die "Could not create zip"
#  cd ..
  # We actually delete all the contents because we don't want to 
  # include them in GAE.
  #rm -rf $x
#done
