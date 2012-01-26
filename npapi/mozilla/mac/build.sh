#!/bin/sh

function die()
{
  echo $*
  exit 1
}

#pushd ../..
#svn up
#./buildBoost.sh || die "Could not build boost!!"
#popd

pushd ..
./depends.sh || die "Could not expand dependencies"
popd


# Clean
xcodebuild clean -alltargets -configuration Release -configuration Debug -project littleshoot.xcodeproj

# Build Release
xcodebuild build -target LittleShootPlugIn -configuration Release -project littleshoot.xcodeproj || die "Could not build xcode!!"

# Build Debug:
#xcodebuild build -alltargets  -configuration Debug -project libtorrent.xcodeproj

