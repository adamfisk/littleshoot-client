#!/usr/bin/env bash

function die()
{
  echo $*
  svn revert littleshoot/amazonDevPay.py
  exit 1
}


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

cleanDjango || die "Could not clean django"

