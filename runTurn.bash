#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

./build.bash || die "Could not build"
cd common/turn/server/
mvn lastbamboo:runscript
./run.sh 
