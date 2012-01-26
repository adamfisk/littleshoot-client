#!/usr/bin/env bash
function die()
{
  echo $*
  exit 1
}

source constants.bash
#startDir=`pwd`
rhinoBase=rhino1_7R2
function downloadAndUnzip()
{
    echo "Downloading Rhino for build with base $rhinoBase"
    curl -O "ftp://ftp.mozilla.org/pub/mozilla.org/js/$rhinoBase.zip"
    unzip $rhinoBase.zip
}

test -f $rhinoBase.zip || downloadAndUnzip

rhinoJar=$rhinoBase/js.jar
#rhinoJar=../server/static/build/src/main/webapp/$DOJO_VERSION/util/shrinksafe/custom_rhino.jar
#jsDir=$startDir/../server/static/build/src/main/webapp/littleshoot
#jsTemplatesDir=$startDir/../server/appengine/templates
jsDir=$1
jsTemplatesDir=$2
jsCombinedDir=$3

function badJs()
{
  echo "Printing errors"
  java -jar $rhinoJar jslint.js $1
  exit 1
}

function checkFile()
{
  local fileName=$1
  echo "Checking file: $fileName" 
  echo $fileName | grep -v "jquery" || return
  echo $fileName | grep -v "swfobject" || return
  echo $fileName | grep -v "ymp" || return
  echo $fileName | grep -v "littleshootlib" || return
  echo $fileName | grep -v "flowplayer-3." || return
  echo $fileName | grep -v "pure" || return
  # The tooltip one should GET FIXED!!
  echo $fileName | grep -v "easyTooltip" || return
  java -jar $rhinoJar jslint.js $fileName | grep "No problems found in" || badJs $fileName  
}

dirs="$jsTemplatesDir $jsDir $jsCombinedDir"
for d in $dirs
do
  for f in $( ls $d/*.js ); do
    echo "Checking JavaScript file: $f"
    checkFile $f 
  done
done

echo "Completed all checks successfully."
exit 0
