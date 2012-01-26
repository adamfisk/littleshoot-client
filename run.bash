#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

cd client/site
mvn lastbamboo:runscript || die "Could not create runscript"
./run.sh
