#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

#mvn install -Dmaven.test.skip=true || die "Could not build JNI lib"
./build.bash || die "Could not build JNI"
cd ../../lib || die "Could not cd"
rm jnl.tgz 

echo "Building tgz"
tar czvf jnl.tgz libjnltorrent.jnilib || die "Could not tgz"

echo "Uploading tgz"
aws -putp littleshoot jnl.tgz || die "Could no upload tgz"

cd -
