#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

ARGS=1  # One arg to script expected.

if [ $# -ne "$ARGS" ]
then
        echo "Need a version argument"
        exit 1 
fi


version=$1

wget http://littleshoot.s3.amazonaws.com/LittleShootPlugin.dmg
wget http://littleshoot.s3.amazonaws.com/LittleShootPlugin.exe
wget http://littleshoot.s3.amazonaws.com/LittleShootPlugin.tgz

mv LittleShootPlugin.dmg LittleShootPlugin-$version.dmg
mv LittleShootPlugin.exe LittleShootPlugin-$version.exe
mv LittleShootPlugin.tgz LittleShootPlugin-$version.tgz
