#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

#test -f lib/libjnltorrent.jnilib || curl -O http://svn.littleshoot.org/maven/libjnltorrent.jnilib

pushd mojo
mvn install -Dmaven.test.skip=true || die "Could not build mojo"
popd
mvn install -Dmaven.test.skip=true

cd client/site
mvn lastbamboo:runscript
./run.sh
