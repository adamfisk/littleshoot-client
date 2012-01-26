#!/usr/bin/env bash

source constants.bash

function die()
{
  echo $*
  exit 1
}

# Function for converting a single HTML file from development
# to release, basically removing all individual JavaScript
# imports and replacing them with the single, consolidated
# and compressed file.
function releaseHtml()
{
  local dir=$1
  local file=$2
  echo "Updating file: $file"
  local newFile=$dir/$file
  cp $file $dir || die "Could not copy file"
  cat $file | sed "/DELETEME/d" > $newFile
}

function update()
{
  #local releaseDir=../../../../release/src/main/webapp
  local htmlDir=../../../../../appengine/static
  #echo `ls $releaseDir`
  cp *.class $htmlDir || die "Could not copy class files to $htmlDir"
  #cp images/*.* $releaseDir/images || die "Could not copy images to $releaseDir/images"
  #cp images/sources/* $releaseDir/images/sources || die "Could not copy image sources to $releaseDir"
  #cp images/type/* $releaseDir/images/type || die "Could not copy types to $releaseDir/images/type"
  #cp images/icons/* $releaseDir/images/icons || die "Could not copy icons to $releaseDir/images/icons"
  #cp css/* $releaseDir/css || die "Could not copy css to $releaseDir/css"
  #cp firebug/* $releaseDir/firebug || die "Could not copy firebug to $releaseDir"
  cp *.js $htmlDir || die "Could not copy javascript to $htmlDir"
  cp *.swf $htmlDir || die "Could not copy javascript to $htmlDir"

  local dir=$1
  for file in *.html
  do
    releaseHtml $dir $file  || die "Could not copy"
  done

  #cp $dir/*.html $releaseDir || die "Could not copy html"
  cp $dir/*.html $htmlDir || die "Could not copy html"
}

tmpBase=`basename $0`
tmpDir=`mktemp -d /tmp/${tmpBase}.XXXXXX` || die "Could not create temp dir"

cd ..
update $tmpDir
