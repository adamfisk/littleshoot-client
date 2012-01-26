#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

function packJars()
{
  for f in $( ls *.jar ); do
    echo "Packing jar: $f"
    pack200 --modification-time=latest --deflate-hint="false" --no-gzip $f.pack $f || die "Could not pack $f"
    rm $f || die "Could not remove $f"
  done
}

function unpackJars()
{
  for f in $( ls *.pack ); do
    echo "Unpacking packed jar: $f"
    local jarName=`echo "${f%.pack}"`
    unpack200 $f $jarName || die "Could not pack $f"
    rm $f || die "Could not delete pack file $f"
  done
}
