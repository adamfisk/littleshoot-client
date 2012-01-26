#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

pushd openssl
./build.sh || die "Could not build open ssl"
popd

pushd boost
./build.bash || die "Could not build boost"
popd

pushd libtorrent
./build.sh || die "Could not build libtorrent"
popd

pushd npapi/mozilla/mac
./build.sh || die "Could not build NPAPI"
popd

pushd jni/jlibtorrent || die "Could not move to jni lib"
./build.bash || die "Could not build JNI lib!!"
popd

pushd mojo/lastbamboo-maven-plugin
mvn install || die "Could not build mojo"
popd
mvn install -Dmaven.test.skip=true
