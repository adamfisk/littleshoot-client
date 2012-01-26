#!/usr/bin/env bash

function die()
{
  echo $*
  #svn revert littleshoot/amazonDevPay.py
  #svn revert littleshoot/awsUtils.py
  svn revert settings.py
  exit 1
}

ARGS=3  # One arg to script expected.

if [ $# -ne "$ARGS" ]
then
        echo "Usage: release.bash keyId key productToken"
        exit 1
fi

keyId=$1
key=$2
productToken=$3

#echo "Got key ID: $keyId"
#echo "Got key: $key"
#echo "Got token: $productToken"

#test -d "notthereever" || die "testing!!"

function cleanDjango()
{
  echo "Cleaning Django dir"
  #cd $startDir
  pushd django || die "Could not cd"

  rm -rfv bin  || die "Could remove dir"
  rm -rfv contrib/admin || die "Could remove dir"
  rm -rfv contrib/comments || die "Could remove dir"
  rm -rfv contrib/databrowse || die "Could remove dir"
  rm -rfv contrib/flatpages || die "Could remove dir"
  rm -rfv contrib/gis || die "Could remove dir"
  rm -rfv db/backends/postgresql || die "Could remove dir"
  rm -rfv db/backends/postgresql_psycopg2 || die "Could remove dir"
  rm -rfv db/backends/oracle || die "Could remove dir"
  rm -rfv db/backends/mysql || die "Could remove dir"
  rm -rfv db/backends/sqlite3 || die "Could remove dir"

  local exts="mo po pyc pyo"
  for x in $exts
  do
    find . -name "*.$x" | xargs rm
  done

  find . -name ".svn" | xargs rm -rf
  popd
}

#cleanDjango || die "Could not clean django"


#echo "Add a timestamp? [y/n]"
#read TIMESTAMP
# We don't do this because the checked in version always just uses
# "littleShootTimeStampedResource", and we can't use that.

#case $TIMESTAMP in
#y)
#  addTimeStamp=0
#  ;;
#Y)
#  addTimeStamp=0 || die "Could not modify port"
#  ;;
#*)
#  echo "OK, using default port of 8080.  Proceeding with install."
#  addTimeStamp=1
#  ;;
#esac
pushd ~/littleshoot/trunk/bin
./checkJavaScript.bash || die "Bad JavaScript, Bad!"
popd

startDir=`pwd`

curDate=`date "+date-%Y-%m-%d--time-%H-%M-%S"`

pushd templates
grep uncompressed *.js && die "Looks like you're committing uncompressed code"

svn ci -m 'Auto-committing templates from release script' * || die "commit failed"
echo "Setting timestamped resources to use date: $curDate"

perl -pi -e "s/littleShootTimeStampedResource/$curDate/g" *
grep $curDate * || die "Did not correctly set date?"
perl -pi -e "s/generated_media//g" *.html
popd

pushd media
perl -pi -e "s/littleShootTimeStampedResource/$curDate/g" *
popd

perl -pi -e "s/awsAccessKeyIdToken/$keyId/g" settings.py || die "Could not replace key ID"
perl -pi -e "s,awsSecretAccessKeyToken,$key,g" settings.py || die "Could not replace key"
perl -pi -e "s,awsProductTokenToken,$productToken,g" settings.py || die "Could not replace product token"

pushd littleshoot
#perl -pi -e "s/awsAccessKeyIdToken/$keyId/g" amazonfps.py || die "Could not replace key ID"
#perl -pi -e "s/awsSecretAccessKeyToken/$key/g" amazonfps.py || die "Could not replace key"

#perl -pi -e "s/awsAccessKeyIdToken/$keyId/g" awsUtils.py || die "Could not replace key ID"
#perl -pi -e "s,awsSecretAccessKeyToken,$key,g" awsUtils.py || die "Could not replace key"
popd

svn up

RELEASE=release

function mkReleaseDir()
{
  echo "Creating release dir"
  cd $startDir
  # The path for looking for the dojo localization file gets messed up for 
  # some browsers -- not sure exactly when or where, but it will look for 
  # /../littleshoot/nls/littleshootlib_en-us.js
  # instead of
  # /littleshoot/nls/littleshootlib_en-us.js
  #  
  # That's why we include the mysterious ...zip file.
  local ZIPS="littleshoot.zip dojo12.zip dijit12.zip dojox12.zip"
  local FILES="urls.py manage.py app.yaml index.yaml __init__.py main.py settings.py"
  local DIRS="images static templates littleshoot common _generated_media"

  # Remove old $RELEASE directory.
  rm -rf $RELEASE

  # Create new $RELEASE directory.
  mkdir $RELEASE

  # Create symbolic links.
  for x in $FILES $DIRS $ZIPS
  do
    ln -s ../$x $RELEASE/$x
  done
}

#mkReleaseDir || die "Could not make release dir"

echo "Updating App Engine Application..."
#appcfg.py update $RELEASE 

find . -name "*.bak" | xargs rm
./manage.py update

pushd templates
echo "About to revert timestamped files"
svn revert *
popd

pushd media
echo "About to revert timestamped files"
svn revert tabbedContent.js
popd

svn revert settings.py
 
#pushd littleshoot
#echo "Reverting python files"
#svn revert amazonDevPay.py 
#svn revert awsUtils.py
#popd
