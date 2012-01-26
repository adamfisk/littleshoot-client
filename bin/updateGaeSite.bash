#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

#if [ $# -ne 1 ]
#then
#  die "Must specify a comment for svn commit as arg 1. Num args received:  $#"
#fi


echo "Releasing site!!"

#svnComment=$1
#echo "Using SVN comment: $svnComment"

startDir=`pwd`
./localReleaseGae.bash || die "Could not build release"


cd $startDir/../server/appengine

./RELEASE_DO_NOT_COMMIT.bash || die "Could not release"
#./release.bash || die "Could not release"


echo "Finished updating site..."
