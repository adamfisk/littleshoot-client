#!/usr/bin/env bash

function die()
{
  echo $*
  exit 1
}

startDir=`pwd`

function cleanDjango()
{
  echo "Cleaning Django dir"
  cd $startDir
  cd common/django || die "Could not cd"

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
}

RELEASE=release

function mkReleaseDir()
{
  echo "Creating release dir"
  cd $startDir
  #local ZIPS="littleshoot.zip dojo.zip dijit.zip dojox.zip"
  local ZIPS=""
  local FILES="manage.py app.yaml __init__.py settings.py index.yaml"
  local DIRS="static templates bugs common"

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

#cleanDjango || die "Could not clean Django"
mkReleaseDir || die "Could not make release dir"

echo "Updating App Engine Application..."
appcfg.py update $RELEASE 

