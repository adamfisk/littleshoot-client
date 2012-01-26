#!/usr/bin/env sh

function die()
{
  echo $*
  exit 1
}

./depends.sh || die "Could not expand dependencies"

function buildMac()
{
  pushd mac
  ./build.sh || die "Could not build Mac"
  popd
}

function buildWin()
{
  pushd windows
  ./build.sh || die "Could not build Windows"
  popd
}

uname -a | grep CYGWIN && buildWin
uname -a | grep Darwin && buildMac
