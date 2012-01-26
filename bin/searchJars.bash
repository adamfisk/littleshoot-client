#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

searchTerms=$1
JARS=`ls *.jar`
#echo "Searching jars $JARS"
for x in $JARS
do
   jar tf $x | grep "$searchTerms" && echo "Found '$searchTerms' in jar $x"
done
