#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

startDir=`pwd`

#./checkJavaScript.bash || die "Bad JavaScript"

function build()
{
  #local basePath=$startDir/../server/static/build/src/main/webapp
  local basePath=../server/static/build/src/main/webapp
  cd $basePath
  cd util
  test -e shoot.sh || die "Build script does not exist"

  ./shoot.sh || die "Could not build javascript"
}

build || die "Could not build static release"

cd $startDir/../server/appengine
pushd tmp/js
cp littleshoot/littleshootlib.js* ../../media || die "Could not copy littleshootlib"
#cp dojo/dojo.js ../../templates || die "Could not copy dojo"
popd

./makeZips.bash

rm -rf tmp

cd $startDir

echo "Finished updating site..."
