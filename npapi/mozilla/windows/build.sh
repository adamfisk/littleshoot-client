#!/usr/bin/env sh

function die()
{
  echo $*
  exit 1
}

/cygdrive/c/Program\ Files/Microsoft\ Visual\ Studio\ 9.0/VC/vcpackages/vcbuild.exe /rebuild ./littleshoot.vcproj/littleshoot.vcproj || die "Could not build Windows NPAPI"

