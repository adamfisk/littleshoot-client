#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}


pushd ../../bin
./checkJavaScriptBase.bash ../server/rja/static/js ../server/rja/templates || die "Error in JavaScript!"
popd

appcfg.py update .
